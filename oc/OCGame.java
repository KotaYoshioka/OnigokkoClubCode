package oc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import data.RunnerData;
import runner.Bomber;
import runner.Feet;
import runner.Idol;
import runner.Perfect;
import runner.Runner;
import runner.SpeedBoy;
import runner.Stone;
import runner.Unknown;
import stage.Scramble;
import stage.StageBase;

public class OCGame implements Listener{

	Plugin plugin;
	World world;
	//プレイヤーデータ
	List<Player> players = new ArrayList<Player>();
	//生存プレイヤー
	List<Player> livings = new ArrayList<Player>();
	//観戦
	List<Player> observer = new ArrayList<Player>();
	//データ保管
	HashMap<Player,PrepareRunner> prepares = new HashMap<Player,PrepareRunner>();
	HashMap<Player,ShowRunner> shows = new HashMap<Player,ShowRunner>();
	HashMap<Player,Runner> playerdata = new HashMap<Player,Runner>();
	//一時保持
	HashMap<Player,Integer> selectKits = new HashMap<Player,Integer>();
	HashMap<Player,List<Integer>> selectAcce = new HashMap<Player,List<Integer>>();
	
	////ゲーム本編関係
	//鬼のプレイヤー
	Player oni = null;
	//何巡目か
	int phase = 0;
	//最大時間
	int maxTime;
	//現在時間
	int time;
	//現在タッチできる時間か
	boolean touchable = false;
	//ステージデータ
	StageBase stageData;
	//タイマーゲージ
	BossBar bb;
	
	public OCGame(Plugin plugin) {
		this.plugin = plugin;
		this.world = Bukkit.getWorld("OngkClub");
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		bb = Bukkit.createBossBar("制限時間：" + time, BarColor.RED, BarStyle.SOLID);
	}
	
	////ゲーム本編
	void showBossBar() {
		for(Player p:players) {
			bb.addPlayer(p);
		}
	}
	void closeBossBar() {
		bb.removeAll();
	}
	/**
	 * 現在の生存プレイヤーの中からランダムに一人プレイヤーを決める。
	 */
	void randomSelect() {
		bb.setProgress(1);
		bb.setColor(BarColor.GREEN);
		phase++;
		List<Player> rnd = new ArrayList<Player>();
		for(Player p:livings) {
			if(!playerdata.get(p).hasAcce(1)) {
				rnd.add(p);
			}
			playerdata.get(p).timeCheck(phase);
		}
		//アンハッピーデイの効果の有無
		boolean unhappy = false;
		for(Player p:livings) {
			if(playerdata.get(p).hasAcce(2)) {
				if(!unhappy) {
					rnd.clear();
					unhappy = true;
				}
				rnd.add(p);
			}
		}
		//仮に何らかの要因で鬼候補が一人もいない場合に全員を入れ直す
		if(rnd.size() == 0) {
			for(Player p:livings) {
				rnd.add(p);
			}
		}
		Collections.shuffle(rnd);
		Player oniKoho = rnd.get(0);
		//TODO ランダムセレクト排除処理等があれば...
		setOni(oniKoho,null);
		startTimeCounter();
	}
	
	void setOni(Player player,Player explayer) {
		if(playerdata.get(player) instanceof Bomber) {
			Bomber bomber = (Bomber)playerdata.get(player);
			if(bomber.getBomb()) {
				explayer.setVelocity(player.getLocation().getDirection().normalize().multiply(7));
				return;
			}
		}
		//鬼の変化に起因して発生するアクセサリーの処理
		for(Player p:players) {
			//ACCE タッチシンドローム
			if(playerdata.get(p).hasAcce(0)) {
				for(Player pl:players) {
					if(pl != p) {
						playerdata.get(pl).reduceStamina(25);
					}
				}
			}
		}
		//ACCE チェーンチェーサー
		if(playerdata.get(player).hasAcce(4)) {
			playerdata.get(player).addSpeed(2, 300, player, "チェーンチェーサー");
		}
		addTimeCounter(15);
		int blindnessTime = 100;
		//ACCE ダブルタッチ
		if(explayer != null) {
			if(playerdata.get(explayer).hasAcce(15)) {
				blindnessTime += 120;
			}
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,blindnessTime,2));
		playerdata.get(player).addSpeed(-10, 100, null,"鬼の代償");
		oni = player;
		sendTitleToAll(ChatColor.RED + oni.getDisplayName(),"こいつが鬼だ！");
		touchable = true;
		setAllScoreboard();
		//ACCE リベンジ
		if(playerdata.get(player).hasAcce(14)) {
			new BukkitRunnable() {
				public void run() {
					for(Entity ent:player.getNearbyEntities(7, 7, 7)){
						if(livings.contains(ent)) {
							if(ent != player) {
								setOni((Player)ent,player);
							}
						}
					}
				}
			}.runTaskLater(plugin, 105);	
		}
	}
	
	void setAllScoreboard() {
		for(Player p:players) {
			playerdata.get(p).scoreboard();
		}
	}
	
	/**
	 * 呼ばれた時点で新たなタイマーを開始させる。
	 * これ以降の時間操作はtimeを直接操作してもらう。
	 */
	void startTimeCounter() {
		maxTime = livings.size() * 30;
		time = maxTime;
		bb.setTitle(ChatColor.GREEN + "制限時間：" + time);
		bb.setColor(BarColor.GREEN);
		reduceTimeCounter();
	}
	void reduceTimeCounter() {
		new BukkitRunnable() {
			public void run() {
				time--;
				bb.setProgress(((double)time)/((double)maxTime));
				bb.setColor(time>=30?BarColor.GREEN:(time<=10?BarColor.RED:BarColor.YELLOW));
				bb.setTitle((time>=30?ChatColor.GREEN:(time<=10?ChatColor.RED:ChatColor.YELLOW)) + "制限時間：" + time);
				if(time == 0) {
					timeOver();
				}else {
					int goal = playerdata.get(oni).hasAcce(5)?60:30;
					if(time <= goal) {
						if(time == goal) {
							sayMaster("残り"+ goal + "秒！お前ら全員光って見えるようになるぞ！");
						}
						for(Player p:livings) {
							p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,100,1));
						}
					}
					reduceTimeCounter();	
				}
			}
		}.runTaskLater(plugin, 20);
	}
	void addTimeCounter(int add) {
		time += add;
		if(time > maxTime) {
			time = maxTime;
		}
	}
	/**
	 * 時間切れ
	 */
	void timeOver() {
		touchable = false;
		//ACCE バンドエイド
		if(phase != 1 || !playerdata.get(oni).hasAcce(6)) {
			playerdata.get(oni).damage(1);	
		}
		//ACCE デストロイヤー
		if(phase == 1 && playerdata.get(oni).hasAcce(9)) {
			for(Player p:livings) {
				if(p != oni) {
					playerdata.get(p).damage(1);
				}
			}
		}
		oni.setVelocity(new Vector(0,10,0));
		oni.getWorld().spawnParticle(Particle.EXPLOSION_HUGE,oni.getLocation(),100,0.5,0.5,0.5,0.6);
		sendTitleToAll(ChatColor.RED + oni.getDisplayName() + "にダメージ！",ChatColor.YELLOW + "時間切れ！");
		String[] itawari = {"痛そうだな...おい...","あちゃちゃ...","間に合わなかったか..."};
		sayMaster(itawari[new Random().nextInt(itawari.length)]);
		if(playerdata.get(oni).getHP() <= 0) {
			oni.setGameMode(GameMode.SPECTATOR);
			playerdata.get(oni).live(false);
		}
		new BukkitRunnable() {
			public void run() {
				if(playerdata.get(oni).getHP() <= 0) {
					defeat(oni);
				}else {
					randomSelect();
				}
			}
		}.runTaskLater(plugin, 100);
	}
	/**
	 * 誰かが敗退したとき
	 * @param player
	 */
	void defeat(Player player) {
		livings.remove(player);
		sendTitleToAll(ChatColor.DARK_RED + player.getDisplayName() + "、敗退！","残り" + livings.size() + "人");
		String[] gj = {"ここで、" + player.getDisplayName() + "が敗退だ！",player.getDisplayName() + "、お疲れ。いい戦いだったぜ！"};
		sayMaster(gj[new Random().nextInt(gj.length)]);
		new BukkitRunnable() {
			public void run() {
				if(livings.size() == 1) {
					gameOver();
				}else {
					randomSelect();
				}
			}
		}.runTaskLater(plugin, 100);
	}
	/**
	 * ゲーム終了(優勝者発表)
	 */
	void gameOver() {
		Player winner = livings.get(0);
		playerdata.get(winner).live(false);
		closeBossBar();
		sendTitleToAll(ChatColor.YELLOW + winner.getDisplayName() + "の優勝！","おめでとう！");
		String[] ngri = {"お前ら！最高の戦いだったぜ！","今日もいい試合が見れたぜ！お疲れさん！"};
		sayMaster(ngri[new Random().nextInt(ngri.length)]);
		new BukkitRunnable() {
			public void run() {
				gameEnd();
			}
		}.runTaskLater(plugin, 150);
	}
	/**
	 * ゲーム終了に際する後処理
	 */
	void gameEnd() {
		HandlerList.unregisterAll(this);
		for(Player p:players) {
			playerdata.get(p).cancelEvents();
		}
		stageData.stop();
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(players.contains(e.getEntity())) {
			if(players.contains(e.getDamager())) {
				if(!touchable)return;
				Player toucher = (Player)e.getDamager();
				Player touched = (Player)e.getEntity();
				if(toucher == oni && !toucher.hasPotionEffect(PotionEffectType.BLINDNESS)) {
					//ACCE スーパーストレート
					if(playerdata.get(toucher).hasAcce(7)) {
						playerdata.get(toucher).healStamina(50);
					}
					setOni(touched,toucher);
				}
			}
		}
	}

	
	////準備段階
	/**
	 * 全プレイヤーをそれぞれの待機室にテレポートして、キャラ選択を行わせる。
	 */
	public void prepare() {
		sendTitleToAll("準備時間","ランナーとアクセサリーを選んでくれ");
		//アイテムの準備
		ItemStack runner = new ItemStack(Material.CYAN_DYE);
		ItemMeta runnerm = runner.getItemMeta();
		runnerm.setDisplayName("ランナー選択");
		runner.setItemMeta(runnerm);
		ItemStack acce = new ItemStack(Material.RED_DYE);
		ItemMeta accem = acce.getItemMeta();
		accem.setDisplayName("アクセサリー選択");
		acce.setItemMeta(accem);
		//各プレイヤーを待機室にテレポート
		Location[] ls = {new Vector(-45,1,0).toLocation(world),new Vector(-45,1,12).toLocation(world),
				new Vector(62,1,15).toLocation(world),new Vector(62,1,5).toLocation(world)};
		for(int i = 0 ; i < players.size() ; i++) {
			Player player = players.get(i);
			player.setGameMode(GameMode.SURVIVAL);
			player.setFoodLevel(20);
			player.setHealth(20);
			player.teleport(ls[i]);
			player.getInventory().clear();
			player.getInventory().addItem(runner,acce);
			for(PotionEffect pe:player.getActivePotionEffects()) {
				player.removePotionEffect(pe.getType());
			}
			prepares.put(player, new PrepareRunner(plugin,player,this,i,world));
		}
		alterScoreboardForPrepare();
		//観戦プレイヤーのスペクテイター化
		for(Player p: observer) {
			p.setGameMode(GameMode.SPECTATOR);
		}
	}

	
	public void changePrepare(boolean check) {
		alterScoreboardForPrepare();
		if(check) {
			checkPrepare();
		}
	}
	/**
	 * それぞれのプレイヤーが既に準備完了か確認する
	 */
	void checkPrepare() {
		for(Player p:players) {
			if(!prepares.get(p).getPrepare()) {
				return;
			}
		}
		sendTitleToAll(ChatColor.YELLOW + "試合が始まる...","お前ら！入場して来い！");
		for(int i = 0 ; i < players.size() ; i++) {
			Player p = (Player)players.get(i);
			p.closeInventory();
			p.getInventory().clear();
			Object[] is = prepares.get(p).getRunnerAndAcce();
			selectKits.put(p, (int)is[0]);
			List<Integer> acces = new ArrayList<Integer>();
			acces.addAll((List<Integer>)is[1]);
			selectAcce.put(p, acces);
			prepares.get(p).cancelEvents();
			shows.put(p, new ShowRunner(plugin,p,world,i));
		}
		new BukkitRunnable() {
			public void run() {
				entrance();
			}
		}.runTaskLater(plugin, 120);
	}
	
	/**
	 * 全プレイヤー入場
	 */
	void entrance() {
		Location[] redL = {new Vector(-11.5,2,2.5).toLocation(world),new Vector(-11.5,2,13.5).toLocation(world),
				new Vector(28.5,2,13.5).toLocation(world),new Vector(28.5,2,2.5).toLocation(world)};
		//前半：X増える
		Location[] firL = {new Vector(-18.5,5,3).toLocation(world),new Vector(-18.5,5,14).toLocation(world),
				new Vector(35.5,5,14).toLocation(world),new Vector(35.5,5,3).toLocation(world)};
		Vector[] r = {new Vector(1,0,0),new Vector(1,0,0),new Vector(-1,0,0),new Vector(-1,0,0)};
		for(int i = 0 ; i < players.size() ; i++) {
			Player player = players.get(i);
			player.teleport(firL[i].clone().setDirection(r[i]));
			final int fi = i;
			//行進
			for(int m = 1;m <= 30 ; m++) {
				final int fm = m;
				new BukkitRunnable() {
					public void run() {
						Location l = firL[fi].clone();
						l.add((fi<2?0.19:-0.19) * fm, 0, 0);
						l.setDirection(r[fi]);
						player.teleport(l);
						if(fm == 30) {
							for(int n = 1;n <= 30 ; n++) {
								final int fn = n;
								new BukkitRunnable() {
									public void run() {
										Location ls = firL[fi].clone();
										ls.add(fi<2?8:-8,0,0);
										ls.add((fi<2?0.19:-0.19) * fn, 0, 0);
										ls.setDirection(r[fi]);
										player.teleport(ls);
									}
								}.runTaskLater(plugin, fn);
							}
						}
					}
				}.runTaskLater(plugin, m);
			}
			//いい感じのタイミングで開くドア
			new BukkitRunnable() {
				public void run() {
					Location l = redL[fi].clone();
					l.getBlock().setType(Material.AIR);
					l.add(0,0,1);
					l.getBlock().setType(Material.AIR);
				}
			}.runTaskLater(plugin, 15);
			//いい感じのタイミングで閉まるドア
			new BukkitRunnable() {
				public void run() {
					Location l = redL[fi].clone();
					l.getBlock().setType(Material.REDSTONE_BLOCK);
					l.add(0,0,1);
					l.getBlock().setType(Material.REDSTONE_BLOCK);
				}
			}.runTaskLater(plugin, 45);
		}
		new BukkitRunnable() {
			public void run() {
				introduce();
			}
		}.runTaskLater(plugin, 65);
	}
	
	/**
	 * 紹介するフェーズ
	 */
	void introduce() {
		int wait = sayMaster("今宵もバカ共が鬼ごっこしに集まったぜ！","今日の参加人数は..." + ChatColor.YELLOW + players.size() + ChatColor.GREEN + "人！熱いじゃねぇか！",
				"早速だが、ランナー達を紹介していくぜ！");
		new BukkitRunnable() {
			public void run() {
				for(int i = 0 ; i < players.size() ; i++) {
					final int fi = i;
					new BukkitRunnable() {
						public void run() {
							Player player = (Player)players.get(fi);
							introduceUnit(player,fi);
						}
					}.runTaskLater(plugin, 300 * i);
				}
				new BukkitRunnable() {
					public void run() {
						start();
					}
				}.runTaskLater(plugin, 300 * players.size());
			}
		}.runTaskLater(plugin, wait);
	}
	/**
	 * 指定したプレイヤーを紹介する
	 * @param player
	 * @param time
	 */
	void introduceUnit(Player player,int time) {
		allPlayerToBlackroom();
		sayMaster("えーっと、" + (time==0?"最初は...":(time==players.size()-1?"最後は...":"次は...")));
		shows.get(player).nowShow(true);
		new BukkitRunnable() {
			public void run() {
				onlyPlayerShow(player);
				sayMaster(RunnerData.getRunnerOnePhrase(selectKits.get(player)) + "！" + RunnerData.runnerName[selectKits.get(player)][0] + "だ！！！" + ChatColor.GRAY + "(" + player.getDisplayName() + ")",
						RunnerData.getRunnerDiscription(selectKits.get(player)));
			}
		}.runTaskLater(plugin, 60);
	}
	
	void start() {
		sayMaster("よし、これで全員紹介したな！");
		allPlayerToBlackroom();
		new BukkitRunnable() {
			public void run() {
				allPlayerReturnToLobby();
				int delay = sayMaster("それじゃぁいよいよ鬼ごっこスタートだ！","会場までお前ら、一気に飛ばすぜ！");
				new BukkitRunnable() {
					public void run() {
						sendTitleToAll(ChatColor.YELLOW + "もうすぐ始まるぞ...","好きな場所に行って備えろ！");
						sayMaster("落ちたら、そのままスタートだからな！");
						showBossBar();
						registerStageData(0);
						for(Player p:players) {
							shows.get(p).cancelEvents();
							livings.add(p);
							registerPlayerData(p);
							playerdata.get(p).giveAbility();
							Location l = new Vector(702,86,9).toLocation(world);
							Random rnd = new Random();
							l.add(rnd.nextInt(18) * (rnd.nextBoolean()?1:-1),0,rnd.nextInt(20) * (rnd.nextBoolean()?1:-1));
							p.teleport(l);
							p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,200,1));
						}
						for(Player p:observer) {
							Location l = new Vector(702,86,9).toLocation(world);
							p.teleport(l);
						}
						new BukkitRunnable() {
							public void run() {
								sendTitleToAll(ChatColor.RED + "鬼ごっこ開始","");
								sayMaster("スターーーーート！！！！！");
								new BukkitRunnable() {
									public void run() {
										randomSelect();
									}
								}.runTaskLater(plugin, 100);
							}
						}.runTaskLater(plugin, 200);
					}
				}.runTaskLater(plugin, delay);
			}
		}.runTaskLater(plugin, 60);
		
	}
	
	void registerStageData(int stageid) {
		//TODO ステージを複数個作る場合のstageIDとの対応
		stageData = new Scramble(plugin,this,world);
	}
	
	void registerPlayerData(Player player) {
		int id = selectKits.get(player);
		List<Integer> acce = selectAcce.get(player);
		switch(id) {
		case 0:
			playerdata.put(player, new SpeedBoy(player,plugin,this,acce));
			break;
		case 1:
			playerdata.put(player, new Unknown(player,plugin,this,acce));
			break;
		case 2:
			playerdata.put(player, new Feet(player,plugin,this,acce));
			break;
		case 3:
			playerdata.put(player, new Bomber(player,plugin,this,acce));
			break;
		case 4:
			playerdata.put(player,new Stone(player,plugin,this,acce));
			break;
		case 5:
			playerdata.put(player, new Perfect(player,plugin,this,acce));
			break;
		case 6:
			playerdata.put(player,new Idol(player,plugin,this,acce));
			break;
		}
	}
	
	void allPlayerToBlackroom() {
		for(Player p:players) {
			shows.get(p).saveCurrentLocation();
			shows.get(p).teleportBlackRoom();
		}
	}
	void allPlayerReturnToLobby() {
		for(Player p:players) {
			shows.get(p).teleportExLocation();
		}
	}
	void onlyPlayerShow(Player player) {
		for(Player p:players) {
			if(player == p) {
				shows.get(p).teleportStage();
			}else {
				shows.get(p).teleportExLocation();
			}
		}
	}
	
	/**
	 * 指定したプレイヤーをゲームに参加させる
	 * @param player
	 */
	public void join(Player player) {
		if(!players.contains(player)) {
			if(players.size() < 4) {
				players.add(player);
				player.sendMessage(ChatColor.YELLOW + "鬼ごっこ倶楽部に参加しました！");
			}else {
				observer.add(player);
				player.sendMessage(ChatColor.GREEN + "鬼ごっこ倶楽部に観戦で参加します！");
			}
		}
	}
	
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		if(players.contains(e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if(e.getEntity().getType() == EntityType.FALLING_BLOCK) {
			e.setCancelled(true);
			return;
		}
		if(e.getBlock().getType() == Material.AIR) {
			return;
		}
		e.setCancelled(true);
	}
	
	////Getter,Setter メッセージ等
	public void sendMessageToAll(String message) {
		for(Player p:players) {
			p.sendMessage(message);
		}
		for(Player p:observer) {
			p.sendMessage(message);
		}
	}
	public int sayMaster(String... messages) {
		int counter = 0;
		for(final String message:messages) {
			new BukkitRunnable() {
				public void run() {
					sayMaster(message);
				}
			}.runTaskLater(plugin, counter * 100);
			counter++;
		}
		return counter*100;
	}
	public void sayMaster(String message) {
		for(Player p:players) {
			p.sendMessage(ChatColor.GREEN + "<司会者>" + message);
		}
		for(Player p:observer) {
			p.sendMessage(ChatColor.GREEN + "<司会者>" + message);
		}
	}
	public void sendTitleToAll(String main,String sub) {
		sendTitleToAll(main,sub,20,100,20);
	}
	public void sendTitleToAll(String main,String sub,int in,int mid,int out) {
		for(Player p:players) {
			p.sendTitle(main, sub,in,mid,out);
		}
	}
	
	public List<Player> getPlayers(){
		return players;
	}
	public PrepareRunner getPrepare(Player player) {
		return prepares.get(player);
	}
	
	public void alterScoreboardForPrepare() {
		for(Player p:players) {
			prepares.get(p).scoreboard();
		}
	}
	
	public List<Player> getLivings(){
		return livings;
	}
	public boolean containsLivings(Player player) {
		return livings.contains(player);
	}
	
	public Runner getPlayerData(Player player) {
		return playerdata.get(player);
	}
	
	public Player getOni() {
		return oni;
	}
}
