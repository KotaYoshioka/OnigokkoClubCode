package ongk;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class OngkRunner implements Listener {

	Player player;
	Plugin mainPlugin;
	//スタミナの表示id
	int id;
	//走っている間スタミナが減るid
	int id2;
	//しばらく走ってないかどうか
	boolean runn = false;
	int id3 = 0;
	//スタミナの回復id
	int id4;
	//スタミナ0の代償
	boolean zero = false;
	int id5;
	//動いていない証明
	boolean moven = false;
	int stamina;
	int maxstamina;
	int hp;
	int staminaheal;
	int speed;
	int jump;
	//元の数値
	int speedd;
	int jumpd;
	int maxstaminad;
	int staminaheald;
	boolean[] waza = new boolean[2];
	OngkGame og;
	boolean inv = false;
	boolean bomb = false;
	boolean extratime = true;
	boolean blockput = false;
	boolean hell = false;
	//アクセサリー
	int acce = -1;
	int acce2 = -1;
	//サイレンス
	boolean silence = false;

	public OngkRunner(Plugin plugin, Player play, OngkGame og, int acce) {
		player = play;
		this.og = og;
		mainPlugin = plugin;
		this.acce = acce;
		int runnerid = og.GetRunner(player);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		//hp = OngkData.states[runnerid][0] + (HasAcce(13) ? 1 : 0);
		//maxstamina = OngkRunnerSelect.StaminaChanger(OngkData.states[runnerid][3]);
		//staminaheal = OngkData.states[runnerid][4];
		stamina = maxstamina;
		maxstaminad = maxstamina;
		//speedd = OngkData.states[runnerid][1] + (HasAcce(13) ? -2 : 0);
		if (HasAcce(15))speedd = 1;
		//jumpd = OngkData.states[runnerid][2] + (HasAcce(2)? 2 : 0) + (HasAcce(13) ? -2 : 0) + (HasAcce(20) ? -2 : 0);
		staminaheald = staminaheal;
		if (HasAcce(15)) {
			FirstSetSpeed(1);
		} else {
			//FirstSetSpeed(OngkData.states[runnerid][1] + (HasAcce(13)? -2 : 0));
		}
		//FirstSetJump(OngkData.states[runnerid][2] + (HasAcce(2) ? 2 : 0) + (HasAcce(13) ? -2 : 0) + (HasAcce(20) ? -2 : 0));
		ShowStamina();
		DownStamina();
		HealStamina();
		player.getInventory().clear();
		/*
		for (int i = 0; i < OngkData.weapon[runnerid].length; i++) {
			player.getInventory().addItem(OngkData.GetWeapon(runnerid, i));
		}
		*/
		for (int i = 3; i < 36; i++) {
			player.getInventory().setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
		}
		for (int i = 0; i < waza.length; i++) {
			waza[i] = false;
		}
		if (HasAcce(16)) {
			og.SetSafety(true);
		}
	}

	public void CancelEvents() {
		PlayerMoveEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
		Bukkit.getServer().getScheduler().cancelTask(id);
		Bukkit.getServer().getScheduler().cancelTask(id2);
		Bukkit.getServer().getScheduler().cancelTask(id4);
		if (id3 == 0) {
			Bukkit.getServer().getScheduler().cancelTask(id3);
		}

	}

	public void ShowStamina() {
		id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				StringBuilder gage = new StringBuilder();
				int wari = maxstamina / 20;
				int slafive = stamina / wari;
				gage.append(ChatColor.WHITE + "スタミナ：");
				for (int i = 0; i < 20; i++) {
					if (i < slafive) {
						gage.append(ChatColor.YELLOW + "|");
					} else {
						gage.append(ChatColor.GRAY + "|");
					}
				}
				gage.append(ChatColor.WHITE + "[" + stamina + "/" + maxstamina + "]");
				BaseComponent[] component = TextComponent.fromLegacyText(gage.toString());
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
			}
		}, 0, 1);
	}

	public void DownStamina() {
		id2 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				if (player.isSprinting()) {
					AddStamina(-1);
					runn = true;
					if (id3 != 0) {
						Bukkit.getServer().getScheduler().cancelTask(id3);
					}
					id3 = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							runn = false;
							id3 = 0;
						}
					}, 40);
				}
			}
		}, 0, 13);
	}

	public void HealStamina() {
		id4 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				if (!blockput) {
					if (!inv) {
						if (!runn) {
							if (!moven) {
								AddStamina((HasAcce(14) ? 3 : 2));
							} else if (player.isSneaking()) {
								AddStamina((og.GetSafety() ? 0 : 1));
							}
						}
					}
				}
			}
		}, 0, 10 - staminaheal - (og.GetOni() == player ? 2 : 0));
	}

	@EventHandler
	public void PlayerInteract(PlayerInteractEvent e) {
		if (e.getPlayer() == player) {
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (!silence) {
					RightClick(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName());
				}
			}
		}
	}

	public void RightClick(String itemName) {
	}

	public void SetStamina(int set) {
		if (set > maxstamina) {
			set = maxstamina;
		}
		stamina = set;
	}

	public void AddStamina(int add) {
		int news = stamina + add;
		if (news > maxstamina) {
			news = maxstamina;
		} else if (news < 0) {
			news = 0;
		}
		if (news == 0) {
			if (!zero) {
				Stan(5, "スタミナ切れ");
				if (HasAcce(17)) {
					for (Player p : og.GetLivingPlayers()) {
						if (p != player) {
							p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 160, 2));
						}
					}
				}
			}
		}
		stamina = news;
	}

	public boolean CostStamina(int cost) {
		if (stamina - cost <= 0) {
			return false;
		} else {
			AddStamina(-cost);
		}
		return true;
	}

	public void Stan(int stantimeforseconds, String cause) {
		zero = true;
		player.sendTitle(ChatColor.RED + cause, stantimeforseconds + "秒動けません。", 0, stantimeforseconds * 20, 10);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				zero = false;
			}
		}, stantimeforseconds * 20);
	}

	public void PreventMove() {
		id5 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				if (zero) {
					double y = player.getVelocity().getY();
					if (y > 0) {
						y = -y;
					}
					player.setVelocity(new Vector(0, y, 0));
				} else {
					Bukkit.getServer().getScheduler().cancelTask(id5);
					return;
				}
			}
		}, 0, 1);
	}

	@EventHandler
	public void PlayerMove(PlayerMoveEvent e) {
		if (player == e.getPlayer()) {
			if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY()
					|| e.getFrom().getZ() != e.getTo().getZ()) {
				if (zero) {
					Location loc = e.getFrom();
					double y = e.getTo().getY();
					if (e.getFrom().getY() < e.getTo().getY()) {
						y = e.getFrom().getY();
					}
					Location nl = new Vector(loc.getX(), y, loc.getZ()).toLocation(player.getWorld());
					e.getPlayer().teleport(nl.setDirection(e.getTo().getDirection()));
				}
				if (!moven) {
					moven = true;
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							moven = false;
						}
					}, 10);
				}
			}
		}
	}

	public void SetMaxStamina(int set) {
		maxstamina = set;
		if (stamina > maxstamina) {
			stamina = maxstamina;
		}
	}

	public void AddMaxStamina(int add) {
		int news = maxstamina + add;
		if (news < 0) {
			maxstamina = 0;
		}
		maxstamina = add;
		if (stamina > maxstamina) {
			stamina = maxstamina;
		}
	}

	public void TempSetMaxStamina(int set, int delayfortick) {
		SetMaxStamina(set);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				SetMaxStamina(maxstaminad);
			}
		}, delayfortick);
	}

	public int GetHP() {
		return hp;
	}

	public void SetHP(int hp) {
		this.hp = hp;
	}

	public void AddHP(int add) {
		if (add < 0) {
			for (Player p : og.GetPlayers()) {
				if (og.GetRunnerData(p).GetAcce() == 15) {
					og.GetRunnerData(p).AddSpeed(1);
				} else if (og.GetRunnerData(p).GetAcce() == 23) {

				}
			}
		}
		int exhp = hp;
		int news = hp + add;
		hp = news;
		AlterScoreboardForHP(exhp);
		if (news == 0) {
			og.Death(player);
		}
	}

	public void AlterScoreboardForHP(int exhp) {
		for (Player p : og.GetPlayers()) {
			Scoreboard sb = p.getScoreboard();
			Objective o = sb.getObjective("RunnerHP");
			int score = o.getScore(player.getDisplayName()).getScore();
			sb.resetScores(ChatColor.WHITE + player.getDisplayName() + "：" + exhp);
			Score sc = o.getScore(ChatColor.WHITE + player.getDisplayName() + "：" + hp);
			sc.setScore(score);
			p.setScoreboard(sb);
		}
	}

	public void AlterSpeed(int exspeed) {
		if (player.hasPotionEffect(PotionEffectType.SPEED)) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
		if (player.hasPotionEffect(PotionEffectType.SLOW)) {
			player.removePotionEffect(PotionEffectType.SLOW);
		}
		if (speed > 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, speed - 1));
		} else if (speed < 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, -speed - 1));
		}
		Scoreboard sb = player.getScoreboard();
		sb.resetScores(ChatColor.WHITE + "SPD：" + ZogenScore(exspeed, speedd));
		Objective obj = sb.getObjective("RunnerHP");
		Score sspd = obj.getScore(ChatColor.WHITE + "SPD：" + ZogenScore(speed, speedd));
		sspd.setScore(og.GetPlayers().size() + 4);
		player.setScoreboard(sb);
	}

	public void FirstAlterSpeed() {
		if (player.hasPotionEffect(PotionEffectType.SPEED)) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
		if (player.hasPotionEffect(PotionEffectType.SLOW)) {
			player.removePotionEffect(PotionEffectType.SLOW);
		}
		if (speed > 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, speed - 1));
		} else if (speed < 0) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, -speed - 1));
		}
	}

	public String ZogenScore(int now, int moto) {
		ChatColor cc = ChatColor.WHITE;
		String uon = "";
		if (now < moto) {
			cc = ChatColor.BLUE;
			uon = "(" + (moto - now) + "↓)";
		} else if (now > moto) {
			cc = ChatColor.RED;
			uon = "(" + (now - moto) + "↑)";
		}
		return cc + "" + now + uon;
	}

	public void Push(Vector v) {
		player.setVelocity(v.multiply((HasAcce(6) ? -1 : 1)));
	}

	public int GetSpeed() {
		return speed;
	}

	public void FirstSetSpeed(int spd) {
		speed = spd;
		FirstAlterSpeed();
	}

	public void SetSpeed(int spd) {
		int exspeed = speed;
		speed = spd;
		AlterSpeed(exspeed);
	}

	public int AddSpeed(int spd) {
		int exspeed = speed;
		//アクセサリー「アンダーリミッター」の効果
		if (HasAcce(1)) {
			if (speed + spd <= -3) {
				spd = -2 - speed;
			}
		}
		speed = speed + spd;
		if (HasAcce(20) && speed >= 10) {
			Push(player.getLocation().getDirection().multiply(2));
		}
		AlterSpeed(exspeed);
		return spd;
	}

	public void TempSpeed(int addspd, int timerfortick) {
		if (HasAcce(18) && GetHP() == 1) {
			addspd = addspd + 2;
		}
		int spp = AddSpeed(addspd);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				AddSpeed(-spp);
			}
		}, timerfortick);
	}

	public void AlterJump(int exjump) {
		if (player.hasPotionEffect(PotionEffectType.JUMP)) {
			player.removePotionEffect(PotionEffectType.JUMP);
		}
		if (jump > -1) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, jump));
		}
		Scoreboard sb = player.getScoreboard();
		sb.resetScores(ChatColor.WHITE + "JMP：" + ZogenScore(exjump, jumpd));
		Objective obj = sb.getObjective("RunnerHP");
		Score sspd = obj.getScore(ChatColor.WHITE + "JMP：" + ZogenScore(jump, jumpd));
		sspd.setScore(og.GetPlayers().size() + 3);
		player.setScoreboard(sb);
	}

	public void FirstAlterJump() {
		if (player.hasPotionEffect(PotionEffectType.JUMP)) {
			player.removePotionEffect(PotionEffectType.JUMP);
		}
		if (jump > -1) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, jump));
		}
	}

	public int GetJump() {
		return jump;
	}

	public void FirstSetJump(int jmp) {
		jump = jmp;
		FirstAlterJump();
	}

	public void SetJump(int jmp) {
		int exjump = jump;
		jump = jmp;
		AlterJump(exjump);
	}

	public int AddJump(int jmp) {
		int exjump = jump;
		if (HasAcce(3) ) {
			if (jmp + jump >= 10) {
				jmp = 9 - jump;
			}
		}
		jump = jmp + jump;
		AlterJump(exjump);
		return jmp;
	}

	public void TempJump(int addjmp, int delayfortick) {
		int jmpp = AddJump(addjmp);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				AddJump(-jmpp);
			}
		}, delayfortick);
	}

	public int GetHEA() {
		return staminaheal;
	}

	public void SetAcce(int ac) {
		acce = ac;
	}

	public int GetAcce() {
		return acce;
	}

	public void TempAcce(int ac, int delayfortick) {
		int accex = acce;
		acce = ac;
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				acce = accex;
			}
		}, delayfortick);
	}

	public void TempSilence(int delayfortick) {
		if (!silence) {
			silence = true;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
				public void run() {
					silence = false;
				}
			}, delayfortick);
		}
	}

	public void SetSilence(boolean newset) {
		silence = newset;
	}

	public boolean HasAcce(int checknumber) {
		if (acce == checknumber || acce2 == checknumber) {
			return true;
		}
		return false;
	}
}
