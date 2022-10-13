package runner;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import data.RunnerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import oc.OCGame;

public abstract class Runner implements Listener{

	Player player;
	Plugin plugin;
	OCGame ocg;
	//ランナーのID
	int runnerID;
	
	//ステータス
	//基礎値
	int baseHp;
	int baseSpeed;
	int baseJump;
	int baseStamina;
	int baseHeal;
	//現在値
	int hp;
	int speed;
	int jump;
	int stamina;
	int heal = -100;
	//走らない計測
	int dr = 0;
	//止まる処理
	boolean stop = false;
	//動いていない
	boolean moven = false;
	//スタミナがゼロで動けない
	boolean zero = false;
	//クリック重複避け
	boolean doubleClick = false;
	//現在所持するアクセサリー
	List<Integer> acce = new ArrayList<Integer>();
	//クールダウン
	boolean[] cool = {true,true,true};
	//メロメロ状態
	boolean love = false;
	//生存状態か
	boolean live = true;
	//3つ目の技が使えるか
	boolean third = false;
	//サイレンス
	boolean silence = false;
	
	public Runner(Player player,Plugin plugin,OCGame ocg,int runnerID, List<Integer> acceID) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		//前提変数
		this.player = player;
		this.plugin = plugin;
		this.ocg = ocg;
		this.runnerID = runnerID;
		setAcce(acceID);
		//基礎ステータスの設定
		this.baseHp = RunnerData.status[runnerID][0];
		this.baseSpeed = RunnerData.status[runnerID][1];
		this.baseJump = RunnerData.status[runnerID][2];
		this.baseStamina = 60 + (20 * (RunnerData.status[runnerID][3] - 1));
		this.baseHeal = RunnerData.status[runnerID][4];
		//ACCE エクスライフ
		if(hasAcce(8)) {
			this.baseHp += 1;
			this.baseSpeed -= 1;
			this.baseJump -= 1;
		}
		//ACCE シンカー
		if(hasAcce(16)) {
			this.baseSpeed -= 2;
		}
		setHP(baseHp);
		setSpeed(baseSpeed);
		setJump(baseJump);
		stamina = baseStamina;
		heal = baseHeal;
		//ループ処理呼び出し
		showStamina();
		downStamina();
		healStamina();
		scoreboard();
	}
	
	public void cancelEvents() {
		HandlerList.unregisterAll(this);
		stop = true;
	}
	
	/**
	 * キットに応じた技を渡す
	 */
	public void giveAbility() {
		for(ItemStack item:RunnerData.getAbilities(runnerID,false)) {
			player.getInventory().addItem(item);
		}
	}
	
	public void timeCheck(int time) {
		if(RunnerData.thirdOpen[runnerID] <= time) {
			player.getInventory().setItem(2, RunnerData.getAbility(runnerID, 2));
			third = true;
		}else if(RunnerData.thirdOpen[runnerID] > time) {
			player.getInventory().setItem(2, RunnerData.getClock(runnerID, time));
		}
	}
	
	/**
	 * 基本的な全スコアボード処理
	 */
	public void scoreboard() {
		if(heal == -100) {
			return;
		}
		ScoreboardManager sbm = Bukkit.getScoreboardManager();
		Scoreboard sb = sbm.getNewScoreboard();
		Objective o = sb.registerNewObjective("MP", "dummy",ChatColor.RED + "鬼ごっこ倶楽部");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score blank = o.getScore("                 ");
		blank.setScore(9);
		Score rabeloni = o.getScore(ChatColor.RED + "現在の鬼");
		rabeloni.setScore(8);
		Score oni = o.getScore(ocg.getOni()==null?"-----":ocg.getOni().getDisplayName());
		oni.setScore(7);
		Score hps = o.getScore(ChatColor.GREEN + "HP:" + hp + "/" + baseHp);
		hps.setScore(6);
		Score status = o.getScore("<ステータス>");
		status.setScore(5);
		String[] statuss = {"HP","SPD","JMP","ST","HE"};
		int[] nows = {hp,speed,jump,stamina,heal};
		int[] bases = {baseHp,baseSpeed,baseJump,baseStamina,baseHeal};
		for(int i = 0 ; i < 5 ; i++) {
			if(i == 0 || i == 3) {
				continue;
			}
			int sa = nows[i] - bases[i];
			Score ds = o.getScore((sa==0?ChatColor.WHITE:(sa>0?ChatColor.YELLOW:ChatColor.AQUA))+statuss[i] + "：" + nows[i] + (sa!=0?"(" + (sa<0?"↓":"↑") + sa + ")":""));
			ds.setScore(4-i);
		}
		player.setScoreboard(sb);
	}
	
	/**
	 * 変わり続けるスタミナの表示
	 */
	void showStamina() {
		new BukkitRunnable() {
			public void run() {
				if(stop) {
					this.cancel();
					return;
				}
				StringBuilder gage = new StringBuilder();
				int wari = baseStamina / 20;
				int slafive = stamina / wari;
				gage.append(ChatColor.WHITE + "スタミナ：");
				for (int i = 0; i < 20; i++) {
					if (i < slafive) {
						gage.append(ChatColor.YELLOW + "|");
					} else {
						gage.append(ChatColor.GRAY + "|");
					}
				}
				gage.append(ChatColor.WHITE + "[" + stamina + "/" + baseStamina + "]");
				BaseComponent[] component = TextComponent.fromLegacyText(gage.toString());
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
				
				if(love && live) {
					player.getWorld().spawnParticle(Particle.HEART,player.getLocation(),2,0.3,0.3,0.3,0.2);
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	/**
	 * 走ることで定期的にスタミナが減り続ける
	 */
	void downStamina() {
		new BukkitRunnable() {
			public void run() {
				if(stop) {
					this.cancel();
					return;
				}
				if(player.isSprinting()) {
					dr = 0;
					reduceStamina(1);
					if(live && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(),15,0.1,0.1,0.1,0.1);
					}
				}else {
					dr++;
				}
				downStamina();
			}
		}.runTaskLater(plugin, 13);
	}
	
	/**
	 * 永続的なスタミナの回復
	 */
	void healStamina() {
		new BukkitRunnable() {
			public void run() {
				if(stop) {
					this.cancel();
					return;
				}
				if(!moven) {
					if(dr >= 3) {
						int acceheal = 0;
						//ACCE レム
						if(hasAcce(10)) {
							acceheal = 1;
						}
						healStamina((ocg.getOni()==player?3:2) + acceheal);
					}
				}else if(player.isSneaking()) {
					//ACCE セーフティブロック
					boolean safety = false;
					for(Player p:ocg.getLivings()) {
						if(ocg.getPlayerData(p).hasAcce(11)) {
							safety = true;
							break;
						}
					}
					if(!safety)healStamina(ocg.getOni()==player?2:1);
				}
				healStamina();
			}
		}.runTaskLater(plugin, 16 - (heal * 2) - (ocg.getOni()==player?5:0));
	}
	
	
	/**
	 * ダメージを食らうときの処理
	 * @param amount
	 */
	public void damage(int amount) {
		if(hp - amount < 0) {
			setHP(0);
		}else {
			setHP(hp - amount);
		}
	}

	
	/**
	 * スタミナの回復
	 * @param amount
	 */
	public void healStamina(int amount) {
		stamina += amount;
		if(stamina > baseStamina) {
			stamina = baseStamina;
		}
	}
	
	/**
	 * ただ、スタミナを減少させる場合
	 * @param amount
	 */
	public void reduceStamina(int amount) {
		stamina -= amount;
		if(stamina < 0) {
			stamina = 0;
			if(!zero) {
				zeroStamina();	
			}
		}
	}
	
	/**
	 * 体力が０になってしまった時の処理
	 */
	void zeroStamina() {
		zero = true;
		player.sendTitle(ChatColor.RED + "スタミナ切れ！",ChatColor.YELLOW + "しばらく動けない...",10,80,10);
		//ACCE シャウト
		if(hasAcce(12)) {
			for(Player p:ocg.getLivings()) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,340,1));
			}
		}
		new BukkitRunnable() {
			public void run() {
				zero = false;
			}
		}.runTaskLater(plugin, 100);
	}
	
	/**
	 * 技などでスタミナを消費する場合
	 * @param amount
	 * @return
	 */
	protected boolean costStamina(int amount) {
		if(stamina - amount < 0) {
			return false;
		}else {
			stamina -= amount;
			return true;
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if(e.getPlayer() == player) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				e.setCancelled(true);
				if(!doubleClick && cool[player.getInventory().getHeldItemSlot()]) {
					if(silence) {
						player.sendMessage(ChatColor.RED + "「アノニマスワールド」の効果で発動できない！");
						return;
					}
					if(player.getInventory().getHeldItemSlot() < 2) {
						if(costStamina(RunnerData.abilitysp[runnerID][player.getInventory().getHeldItemSlot()])) {
							doubleClick();
							if(player.getInventory().getHeldItemSlot()==0) {
								firstAbility();
							}else if(player.getInventory().getHeldItemSlot()==1){
								secondAbility();
							}
						}
					}else if(player.getInventory().getHeldItemSlot() == 2) {
						if(third) {
							if(costStamina(RunnerData.abilitysp[runnerID][player.getInventory().getHeldItemSlot()])) {
								doubleClick();
								thirdAbility();
							}
						}
					}
					
				}
			}
		}
	}
	
	void doubleClick() {
		doubleClick = true;
		new BukkitRunnable() {
			public void run() {
				doubleClick = false;
			}
		}.runTaskLater(plugin, 5);
	}
	
	abstract void firstAbility();
	
	abstract void secondAbility();
	
	abstract void thirdAbility();
	
	@EventHandler
	public void PlayerMove(PlayerMoveEvent e) {
		if (player == e.getPlayer()) {
			if(e.getFrom().getY() != e.getTo().getY()) {
				if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
					if (!moven) {
						moven = true;
						new BukkitRunnable() {
							public void run() {
								moven = false;
							}
						}.runTaskLater(plugin, 10);
					}
					if(zero) {
						e.setCancelled(true);	
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() == player) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSprint(PlayerToggleSprintEvent e) {
		if(e.getPlayer() == player) {
			if(!player.isSprinting()) {
				if(live && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(),40,0.5,0.5,0.5,0.8);
				}
			}
		}
	}
	
	//GetterとSetter
	/**
	 * 体力の取得
	 * @return
	 */
	public int getHP() {
		return hp;
	}
	/**
	 * 体力の変更あった場合
	 * @param newHP
	 */
	public void setHP(int newHP) {
		hp = newHP;
		player.setPlayerListName((hp==0?ChatColor.STRIKETHROUGH + "" + ChatColor.GRAY:(hp==1?ChatColor.YELLOW:ChatColor.GREEN)) + player.getDisplayName() + "：" + hp);
		scoreboard();
	}
	
	/**
	 * 移動速度の更新
	 */
	public void alterSpeed() {
		if(player.hasPotionEffect(PotionEffectType.SPEED)) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
		if(player.hasPotionEffect(PotionEffectType.SLOW)) {
			player.removePotionEffect(PotionEffectType.SLOW);
		}
		if(speed > 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,9999999,speed - 1));
		}else if(speed < 0){
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,9999999,-speed - 1));
		}
		scoreboard();
	}
	/**
	 * 移動速度を取得する
	 * @return
	 */
	public int getSpeed() {
		return speed;
	}
	/**
	 * 移動速度を変化させる
	 * @param speed
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
		alterSpeed();
	}
	/**
	 * 一時的に移動速度を変化させる
	 * @param add
	 * @param timeForTicks
	 * @param causer
	 * @param cause
	 */
	public void addSpeed(int add,int timeForTicks,Player causer,String cause) {
		int changed = add;
		//ACCE ミニマムファイティング
		if(hasAcce(13) && hp == 1) {
			changed += 2;
		}
		this.speed += changed;
		alterSpeed();
		final int fchanged = changed;
		new BukkitRunnable() {
			public void run() {
				speed -= fchanged;
				alterSpeed();
			}
		}.runTaskLater(plugin, timeForTicks);
	}
	
	/**
	 * ジャンプ力を取得できる
	 * @return
	 */
	public int getJump() {
		return jump;
	}
	/**
	 * ジャンプ力を新たに設定することができる。
	 * @param jump
	 */
	public void setJump(int jump) {
		this.jump = jump;
		alterJump();
	}
	/**
	 * ジャンプ力の更新
	 */
	public void alterJump() {
		if(player.hasPotionEffect(PotionEffectType.JUMP)) {
			player.removePotionEffect(PotionEffectType.JUMP);
		}
		if(jump > -1) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,99999999,jump));
		}
		scoreboard();
	}
	
	/**
	 * 一時的に移動速度を変化させる
	 * @param add
	 * @param timeForTicks
	 * @param causer
	 * @param cause
	 */
	public void addJump(int add,int timeForTicks,Player causer,String cause) {
		int changed = add;
		//ACCE ミニマムファイティング
		if(hasAcce(13) && hp == 1) {
			changed += 2;
		}
		this.jump += changed;
		alterJump();
		final int fchanged = changed;
		new BukkitRunnable() {
			public void run() {
				jump -= fchanged;
				alterJump();
			}
		}.runTaskLater(plugin, timeForTicks);
	}
	
	/**
	 * プレイヤーを強制的に移動させる
	 * @param v
	 * @param causer
	 * @param cause
	 */
	public void force(Vector v,Player causer,String cause) {
		//ACCE ベクトルサーファー
		if(hasAcce(3)) {
			v.multiply(-1);
		}
		player.setVelocity(v);
	}
	
	/**
	 * アクセサリーの取得
	 * @return
	 */
	public List<Integer> getAcce(){
		return acce;
	}
	
	/**
	 * 指定のアクセサリーを含んでいるか否か
	 * @param acceID
	 * @return
	 */
	public boolean hasAcce(int acceID) {
		return acce.contains(acceID);
	}
	
	/**
	 * アクセサリーを追加する
	 * @param acceID
	 */
	public void addAcce(int acceID) {
		acce.add(acceID);
	}
	
	/**
	 * 新たにアクセサリーを持たせる（上書き）
	 * @param acceID
	 */
	public void setAcce(List<Integer> acceID) {
		acce.clear();
		acce.addAll(acceID);
	}
	
	public void love(int delayForTicks) {
		if(!love) {
			love = true;
			new BukkitRunnable() {
				public void run() {
					love = false;
				}
			}.runTaskLater(plugin, delayForTicks);
		}
	}
	
	public boolean getLove() {
		return love;
	}
	
	public void live(boolean live) {
		this.live = live;
	}
	
	public void silence(int delayForTicks) {
		if(silence) {
			return;
		}
		silence = true;
		player.sendMessage(ChatColor.RED + "「アノニマスワールド」に入った！");
		new BukkitRunnable() {
			public void run() {
				silence = false;
				player.sendMessage(ChatColor.GREEN + "「アノニマスワールド」が終わった！");
			}
		}.runTaskLater(plugin, delayForTicks);
	}
	
	public int getStamina() {
		return stamina;
	}
}
