package ongk;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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

public class OngkGame implements Listener{
	private List<Player> players = new ArrayList<Player>();
	private List<Player> livingplayers = new ArrayList<Player>();
	private HashMap<Player,Integer> ranking = new HashMap<Player,Integer>();
	private int nowrank;
	private HashMap<Player,Integer> runner = new HashMap<Player,Integer>();
	public HashMap<Player,Integer> acce = new HashMap<Player,Integer>();
	public HashMap<Player,Boolean> allok = new HashMap<Player,Boolean>();
	private boolean safety = false;
	private Plugin mainPlugin;
	private int id;
	private World world;
	private boolean start = false;
	public boolean kyusen = false;
	private Player oni;
	private int timer;
	private double timermax = 120.0;
	public int letitendtime = 0;
	private int timerid;
	private int rooptime = 0;
	public boolean debug = false;
	private HashMap<Player,OngkRunner> po = new HashMap<Player,OngkRunner>();
	private Timestamp ts;
	private BossBar bb;

	public OngkGame(Plugin plugin) {
		mainPlugin = plugin;
		world = Bukkit.getWorld("OngkClub");
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void Start() {
		//最初のメッセージ
		SendTitleToAll(debug?"鬼ごっこのデバッグをしよう！":"鬼ごっこが始まりそう！",debug?"ゆるくいこうぜ～":"準備しようぜ～");
		if(debug) {
			SendMessageToAll(ChatColor.YELLOW,"※注意事項","デバッグ戦では、全ランナー・アクセサリーを使用できます。","今回の試合はデータには残りません。","また、今回の試合によりコインは獲得できません。");
		}
		//プレイヤーの初期化
		for(Player p:players) {
			runner.put(p, -1);
			acce.put(p, -1);
			allok.put(p, false);
		}
		OngkGame ogg = this;
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				//全員にランナー選択をさせる
				OngkInventory oi = new OngkInventory(mainPlugin,ogg,debug);
				for(Player p:players) {
					p.getInventory().clear();
					oi.GiveSelectItems(p);
				}
				id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
					public void run() {
						boolean check = true;
						for(Player p:players) {
							if(allok.get(p) == false) {
								check = false;
								break;
							}
						}
						if(check) {
							Bukkit.getServer().getScheduler().cancelTask(id);
							oi.CancelEvents();
							for(Player p:players) {
								//DataBases.Getcdb().SetInventoryNumber(p, 0);
								p.getInventory().clear();
								p.sendTitle("鬼ごっこが始まるぞ！", "みんな集まれ！",10,80,10);
							}
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
								public void run() {
									ShowTime();
								}
							},100);
						}
					}
				}, 0, 7);
			}
		},60);
	}

	//準備OK/NOの設定
	public void SetOK(Player player) {
		allok.put(player, !allok.get(player));
		boolean check = true;
		for(Player p:players) {
			if(!allok.get(p)) {
				check = false;
				break;
			}
		}
		if(check) {
			for(Player p:players) {
				p.getInventory().clear();
			}
			SendTitleToAll("鬼ごっこが始まるぞ！","みんな集まれ！");
			new BukkitRunnable() {
				public void run() {
					ShowTime();
				}
			}.runTaskLater(mainPlugin, 100);
		}
	}

	//[0]A,[1]B,[2]C,[3]D
	public void Admission(Player player,int type) {
		Location[] blo = new Location[2];
		Location startlo = null;
		boolean add = false;
		switch(type) {
		case 0:
			blo[0] = new Vector(27.5,2,14.5).toLocation(world);
			blo[1] = new Vector(27.5,2,13.5).toLocation(world);
			startlo = new Vector(25.5,5,14).toLocation(world);
			break;
		case 1:
			blo[0] = new Vector(27.5,2,3.5).toLocation(world);
			blo[1] = new Vector(27.5,2,2.5).toLocation(world);
			startlo = new Vector(25.5,5,3).toLocation(world);
			break;
		case 2:
			blo[0] = new Vector(-10.5,2,13.5).toLocation(world);
			blo[1] = new Vector(-10.5,2,14.5).toLocation(world);
			startlo = new Vector(-8.5,5,14).toLocation(world);
			add = true;
			break;
		case 3:
			blo[0] = new Vector(-10.5,2,2.5).toLocation(world);
			blo[1] = new Vector(-10.5,2,3.5).toLocation(world);
			startlo = new Vector(-8.5,5,3).toLocation(world);
			add = true;
			break;
		}
		blo[0].getBlock().setType(Material.AIR);
		blo[1].getBlock().setType(Material.AIR);
		int progress = 0;
		final Location sl = startlo;
		final boolean a = add;
		new BukkitRunnable() {
			public void run() {
				Location lo = new Vector(sl.getX()  + ((a?1:-1) * (0.1d * progress)),sl.getY(),sl.getZ()).toLocation(world);
				player.teleport(lo);
				if(progress >= 40) {
					this.cancel();
					blo[0].getBlock().setType(Material.REDSTONE_BLOCK);
					blo[1].getBlock().setType(Material.REDSTONE_BLOCK);
				}
			}
		}.runTaskTimer(mainPlugin, 0, 1);
	}
	//A
	//27.5,2,14.5と13.5:ブロック
	//25.5,5,14からx 23.5にかけて
	//B
	//27.5,2,3.5と2.5
	//25.5,5,3から23.5にかけて
	//C
	//-10.5,2,13.5と14.5
	//-8.5,5,14から-6.5にかけて
	//D
	//-10.5,2,2.5と3.5
	//-8.5,5,3から-6.5にかけて

	public void ShowTime() {
		for(Player p:players) {
			p.teleport(new Vector(98,4,111).toLocation(world));
		}
		OngkStageEffect ose = new OngkStageEffect(mainPlugin,world);
		ose.LightOff(false);
		OwnerSendMessageToAll("お前ら！今回もよく集まってくれた！");
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				OwnerSendMessageToAll("参加人数は、えーっと。" + players.size() + "人！これは面白くなりそうだな！");
				nowrank = players.size();
				ose.LightOn(true);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
					public void run() {
						OwnerSendMessageToAll("鬼ごっこ開始前にまずは、熱きランナー達を紹介するぜ！");
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
							public void run() {
								IntroduceOurSelf();
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
									public void run() {
										OwnerSendMessageToAll("これで、全員紹介できたかな？よしっ！それじゃぁ鬼ごっこ始めるぜ！！");
										Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
											public void run() {
												SetStart();
											}
										},100);
									}
								},200 * players.size());
							}
						},70);
					}
				},100);
			}
		},80);
	}

	public void IntroduceOurSelf() {
		for(int i = 0 ; i < players.size() ; i++) {
			final int ii = i;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
				public void run() {
					Introduce(players.get(ii));
				}
			},200 * i);
		}
	}

	public void Introduce(Player p) {
		Location lo = p.getLocation().clone();
		p.teleport(new Vector(97,5,117).toLocation(world));
		//OwnerSendMessageToAll(OngkData.runnertop[runner.get(p)] + OngkData.runner[runner.get(p)] + "(" + p.getDisplayName() + ")だ！！");
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				//OwnerSendMessageToAll(OngkData.runnerdis[runner.get(p)]);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
					public void run() {
						p.teleport(lo);
					}
				},100);
			}
		},100);
	}

	public void BarVisible() {
		for(Player p:players) {
			bb.addPlayer(p);
		}
	}

	public void BarInvisible() {
		for(Player p:players) {
			bb.removePlayer(p);
		}
	}

	public void SetStart() {
		ts = new Timestamp(System.currentTimeMillis());
		bb = Bukkit.createBossBar("爆発まで",BarColor.RED, BarStyle.SOLID);
		bb.setProgress(1);
		BarVisible();
		for(Player p:players) {
			p.teleport(new Vector(30,61,-111).toLocation(world));
			p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,150,10));
			po.put(p, OngkRunnerSelect.GiveRunnerSet(mainPlugin, p, this, runner.get(p),acce.get(p)));
			livingplayers.add(p);
		}
		timermax = players.size() * 30;
		timer = (int)timermax;
		OwnerSendMessageToAll("よっしゃ！みんな散らばれ散らばれ！すぐ落ちちまうぞ！");
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				SendTitleToAll("ゲーム開始だぁ！","逃げろ！追え！逃げろ！");
				OwnerSendMessageToAll("あ、落ちた？スタート！スタートだ！！");
				start = true;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
					public void run() {
						RandamOni();
						ReduceTimer();
					}
				},40);
			}
		},155);
		SetScoreboard();
	}

	public void SetScoreboard() {
		for(Player p:players) {
			ScoreboardManager sbm = Bukkit.getScoreboardManager();
			Scoreboard sb = sbm.getNewScoreboard();
			Objective o = sb.registerNewObjective("RunnerHP", "dummy","鬼ごっこ倶楽部");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			Score oni = o.getScore(ChatColor.DARK_RED + "現在の鬼：未定");
			oni.setScore(players.size() + 1);
			Score s = o.getScore(ChatColor.WHITE + "<プレイヤー一覧(HP)>");
			s.setScore(players.size());
			for(int i = 0 ; i < players.size() ; i++) {
				Player pp = players.get(i);
				Score ph = o.getScore(ChatColor.WHITE + pp.getDisplayName() + "：" + po.get(pp).GetHP());
				ph.setScore(i);
			}
			p.setScoreboard(sb);
			po.get(p).SetSpeed(po.get(p).GetSpeed());
			po.get(p).SetJump(po.get(p).GetJump());
		}
	}

	public void Death(Player player) {
		livingplayers.remove(player);
		player.setGameMode(GameMode.SPECTATOR);
		ranking.put(player, nowrank);
		nowrank--;
		if(runner.get(player) == 8) {
			OngkCreater oc = (OngkCreater)po.get(player);
			oc.ResetBlocks(false);
			Bukkit.getServer().getScheduler().cancelTask(oc.createrid);
		}
		if(livingplayers.size() == 1) {
			GameSet();
		}else {
			SendTitleToAll(player.getDisplayName()+"離脱！" , "おつかれちゃんだね！");
		}
	}

	public void RandamOni() {
		List<Player> rp = new ArrayList<Player>();
		boolean unlucky = false;
		for(Player ps:livingplayers) {
			if(po.get(ps).HasAcce(4)) {
				unlucky = true;
				rp.add(ps);
			}
		}
		if(unlucky) {
			Collections.shuffle(rp);
			SetOni(rp.get(0));
		}else {
			for(Player ps : livingplayers) {
				if(!po.get(ps).HasAcce(5)) {
					rp.add(ps);
				}
			}
			if(rp.size() != 0) {
				Collections.shuffle(rp);
				SetOni(rp.get(0));
			}else {
				rp = livingplayers;
				Collections.shuffle(rp);
				SetOni(rp.get(0));
			}
		}
	}

	public void SetOni(Player player) {
		for(Player p:players) {
			Scoreboard sb = p.getScoreboard();
			Objective o = sb.getObjective("RunnerHP");
			String exoni;
			if(oni == null) {
				exoni = "未定";
			}else {
				exoni = oni.getDisplayName();
			}
			sb.resetScores(ChatColor.DARK_RED + "現在の鬼：" + exoni);
			Score newoni = o.getScore(ChatColor.DARK_RED + "現在の鬼：" + player.getDisplayName());
			newoni.setScore(players.size() + 1);
			p.setScoreboard(sb);
			if(po.get(p).HasAcce(0)) {
				for(Player pp:livingplayers) {
					if(pp != p) {
						po.get(pp).AddStamina(-20);
					}
				}
			}
		}
		oni = player;
		SendTitleToAll(player.getDisplayName(),"こいつが鬼だ！");
		if(!po.get(player).HasAcce(9)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,10));
		}
		kyusen = true;
		po.get(player).TempSpeed(-20, 100);
		AddTimer(15);
		if(po.get(player).HasAcce(7)) {
			po.get(player).TempSpeed(2, 500);
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				kyusen = false;
			}
		},100);
	}

	public void Flash() {
		for(Player p:livingplayers) {
			if(p.hasPotionEffect(PotionEffectType.GLOWING)) {
				p.removePotionEffect(PotionEffectType.GLOWING);
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,20,1));
		}
	}

	public void ReduceTimer() {
		rooptime++;
		timerid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				timer--;
				bb.setProgress((timer/timermax * 1.0));
				if(po.get(oni).HasAcce(8)) {
					if(timer <= 40 && timer >= 20) {
						Flash();
					}
				}
				if(timer <= 20) {
					Flash();
				}
				if(timer == 60) {
					SendMessageToAll("あと60秒です。");
				}else if(timer == 30) {
					SendMessageToAll("あと30秒です。");
				}else if(timer == 10) {
					SendMessageToAll("あと10秒です。");
				}else if(timer == 5) {
					SendMessageToAll("あと5秒です。");
				}else if(timer == 3) {
					SendMessageToAll("あと3秒です。");
				}else if(timer == 2) {
					SendMessageToAll("あと2秒です。");
				}else if(timer == 1) {
					SendMessageToAll("あと1秒です。");
				}else if(timer == 0) {
					if(po.get(oni).HasAcce(21) && po.get(oni).extratime) {
						po.get(oni).extratime = false;
						AddTimer(30);
						return;
					}else if(po.get(oni).HasAcce(10) && rooptime == 1) {
						SendTitleToAll(oni.getDisplayName() + "にダメ・・・あれ？","食らってないぞ！コイツ！");
					}else {
						if(po.get(oni).GetHP() != 1) {
							SendTitleToAll(oni.getDisplayName() + "にダメージ！","これは痛い！");
							po.get(oni).Push(new Vector(0,10,0));
						}
						po.get(oni).AddHP(-1);
						oni.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, oni.getLocation(),100, 2, 2, 2);
						if(po.get(oni).HasAcce(12)) {
							for(Entity e:oni.getNearbyEntities(5, 5, 5)) {
								if(e instanceof Player) {
									if(livingplayers.contains(e)) {
										Player victim = (Player)e;
										if(victim != oni) {
											po.get(victim).AddSpeed(-1);
										}
									}
								}
							}
						}
					}
					Bukkit.getServer().getScheduler().cancelTask(timerid);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							if(start) {
								RandamOni();
								timermax = (30.0 * livingplayers.size()) - letitendtime;
								if(timermax < 30) {
									timermax = 30.0;
								}
								timer = (int)timermax;
								bb.setProgress(1);
								ReduceTimer();
							}
						}
					},100);
				}
			}
		}, 0, 20);
	}

	public void CancelEvents() {
		EntityDamageEvent.getHandlerList().unregister(this);
		EntityDamageByEntityEvent.getHandlerList().unregister(this);
	}

	public void GameSet() {
		SendTitleToAll(livingplayers.get(0).getDisplayName() + "の勝ち！","良い戦いだったぜ！お前ら！");
		ranking.put(livingplayers.get(0), 1);
		if(runner.get(livingplayers.get(0)) == 7 || runner.get(livingplayers.get(0)) == 9) {
			if(!debug) {
				//DataBases.Getongkdb().SetPossessPlayer(livingplayers.get(0), runner.get(livingplayers.get(0)), true);
				//livingplayers.get(0).sendMessage(OnigokkoMain.OngkText(OngkData.runner[runner.get(livingplayers.get(0))] + "を獲得しました！"));
			}
		}
		CancelEvents();
		start = false;
		for(Player p:players) {
			po.get(p).CancelEvents();
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				BarInvisible();
				for(Player p:players) {
					//CarpediemMain.ResetPlayer(p);
					if(!debug) {
						CoinResult(p);
						//DataBases.Getongkdb().SetBattle(p,runner.get(p),po.get(p).GetAcce(),ranking.get(p),ts);
					}
				}
			}
		},140);
	}

	public void CoinResult(Player p) {
		SendMessageToPlayer(p,"<<<鬼ごっこ倶楽部コイン報酬>>>");
		int maxcoin = 70 * players.size();
		maxcoin = maxcoin - (50 * (ranking.get(p) - 1));
		SendMessageToPlayer(p,"順位" + ranking.get(p) + "位：" + maxcoin + "C");
		int lefthp = po.get(p).GetHP();
		int lefthpcoin = lefthp * 100;
		if(lefthpcoin > 0) {
			SendMessageToPlayer(p,"残りHPボーナス：" + lefthpcoin + "C");
		}
		int roopcoin = rooptime * 20;
		SendMessageToPlayer(p,"激戦ボーナス：" + roopcoin);
		int result = maxcoin + lefthpcoin + roopcoin;
		//SendMessageToPlayer(p,"合計獲得コイン：" + result + "C");
		//DataBases.Getcdb().AddCoin(p, result);
	}

	public void Join(Player player) {
		if(players.contains(player)) {
			player.sendMessage(OnigokkoMain.OngkText("既に鬼ごっこ倶楽部に参加しています。"));
		}else {
			player.sendMessage(OnigokkoMain.OngkText("鬼ごっこ倶楽部に参加しました。"));
			players.add(player);
		}
	}

	public void Leave(Player player) {
		if(players.contains(player)) {
			player.sendMessage(OnigokkoMain.OngkText("鬼ごっこ倶楽部から退出しました。"));
			players.remove(player);
		}else {
			player.sendMessage(OnigokkoMain.OngkText("鬼ごっこ倶楽部に参加していません。"));
		}
	}

	@EventHandler
	public void EntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player player = (Player)e.getEntity();
			if(players.contains(player)) {
				if(e.getCause() == DamageCause.FALL) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void EntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player player = (Player)e.getDamager();
			Player target = (Player)e.getEntity();
			if(this.GetRunnerData(target).hell) {
				GetRunnerData(target).Push(player.getLocation().getDirection().multiply(2));
				if(GetRunnerData(target).hp >= 2) {
					GetRunnerData(target).AddHP(-1);
				}else {
					GetRunnerData(target).TempSpeed(-3, 10);
				}
			}
			if(oni == player && players.contains(target) && !kyusen) {
				if(po.get(target).bomb) {
					po.get(player).Push(target.getLocation().getDirection().multiply(4));
				}else {
					if(po.get(target).HasAcce(24) && target.isSneaking() && po.get(target).GetJump() >= 1) {
						po.get(target).AddJump(-1);
						double distance = 10000;
						Player newtaget = null;
						for(Player p:livingplayers) {
							if(p != target) {
								double nd = p.getLocation().distance(target.getLocation());
								if(distance > nd) {
									newtaget = p;
									distance = nd;
								}
							}
						}
						if(newtaget != null) {
							SetOni(newtaget);
							return;
						}
					}
					SetOni(target);
					if(po.get(player).HasAcce(11)) {
						po.get(player).AddStamina(40);
					}
					if(po.get(target).HasAcce(11)) {
						po.get(target).AddStamina(20);
					}
					if(po.get(target).HasAcce(19)) {
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
							public void run() {
								for(Entity ent:target.getNearbyEntities(10, 10, 10)) {
									if(ent == player) {
										target.teleport(player);
									}
								}
							}
						},100);
					}
					if(po.get(player).HasAcce(22)) {
						if(po.get(player).GetHP() < po.get(target).GetHP()) {
							if(target.hasPotionEffect(PotionEffectType.BLINDNESS)) {
								target.removePotionEffect(PotionEffectType.BLINDNESS);
							}
							target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,200,1));
						}
					}
				}
			}
		}
	}

	public int GetTimer() {
		return timer;
	}

	public void AddTimer(int add) {
		int newtimer = timer + add;
		if(newtimer >= timermax) {
			newtimer = (int)timermax;
		}
		int zo = newtimer - timer;
		if(zo > 0) {
			SendMessageToAll("時間が" + zo + "秒増えました。");
		}else if(zo < 0) {
			SendMessageToAll("時間が" + -zo + "秒減りました。");
		}
		timer = newtimer;
	}

	public void SetTimer(int set) {
		timer = set;
	}
	public List<Player> GetPlayers(){
		return players;
	}

	public List<Player> GetLivingPlayers(){
		return livingplayers;
	}

	public int GetRunner(Player player) {
		return runner.get(player);
	}

	public void SendTitleToAll(String title,String subtitle) {
		for(Player p:players) {
			p.sendTitle(title, subtitle,10,80,10);
		}
	}

	public void SendMessageToAll(ChatColor cc,String message) {
		for(Player p:players) {
			p.sendMessage(cc + message);
		}
	}

	public void SendMessageToAll(ChatColor cc,String... message) {
		for(String s:message) {
			for(Player p:players) {
				p.sendMessage(cc + s);
			}
		}
	}

	public void SendMessageToAll(String message) {
		for(Player p:players) {
			p.sendMessage(ChatColor.DARK_RED + "[鬼ごっこ倶楽部]" + message);
		}
	}

	public void OwnerSendMessageToAll(String message) {
		for(Player p:players) {
			p.sendMessage(ChatColor.GREEN + "<鬼ごっこ司会者>" + message);
		}
	}

	public void SendMessageToPlayer(Player player,String message) {
		player.sendMessage(ChatColor.DARK_RED + "[鬼ごっこ倶楽部]" + message);
	}

	public void SendChatToAround(Player player,int around,String message) {
		for(Entity e:player.getNearbyEntities(around, around, around)) {
			if(livingplayers.contains(e)) {
				Player targ = (Player )e;
				//arg.sendMessage(OngkData.cc[GetRunner(player)] + "[" + OngkData.runner[GetRunner(player)] + "]" + message);
			}
		}
		//player.sendMessage(OngkData.cc[GetRunner(player)] + "[" + OngkData.runner[GetRunner(player)] + "]" + message);
	}

	public void SendChatToAll(Player player,String message) {
		for(Player targ:players) {
			//targ.sendMessage(OngkData.cc[GetRunner(player)] + "[" + OngkData.runner[GetRunner(player)] + "]" + message);
		}
	}

	public void SendChatToPlayer(Player player,Player target,String message) {
		//target.sendMessage(OngkData.cc[GetRunner(player)] + "[" + OngkData.runner[GetRunner(player)] + "]" + message);
		//player.sendMessage(OngkData.cc[GetRunner(player)] + "[" + OngkData.runner[GetRunner(player)] + "]" + message);
	}

	public Player GetOni() {
		return oni;
	}

	public void SetRunner(Player player,int ids) {
		runner.put(player, ids);
	}

	public OngkRunner GetRunnerData(Player player) {
		return po.get(player);
	}

	public boolean GetStart() {
		return start;
	}

	public void SetDebug(boolean newset) {
		debug = newset;
	}

	public boolean GetSafety() {
		return safety;
	}

	public void SetSafety(boolean safety) {
		this.safety = safety;
	}
}
