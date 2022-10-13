package ongk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class OngkSteal extends OngkRunner{

	boolean ha = false;
	Player tarrr;
	List<Item> items = new ArrayList<Item>();
	List<Entity> tnts = new ArrayList<Entity>();
	HashMap<Entity,Boolean> tntbool = new HashMap<Entity,Boolean>();

	public OngkSteal(Plugin plugin, Player play, OngkGame og, int acce) {
		super(plugin, play, og, acce);
	}


	@Override
	public void CancelEvents() {
		super.CancelEvents();
		PlayerPickupItemEvent.getHandlerList().unregister(this);
		ExplosionPrimeEvent.getHandlerList().unregister(this);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("アクセ盗み")) {
			if(!waza[0]) {
				if(CostStamina(29)) {
					waza[0] = true;
					Player tar = null;
					double dis = 10000;
					for(Entity ent:player.getNearbyEntities(5, 5, 5)) {
						if(og.GetLivingPlayers().contains(ent) && ent != player) {
							double ndis = ent.getLocation().distance(player.getLocation());
							if(dis > ndis) {
								dis = ndis;
								tar = (Player)ent;
							}
						}
					}
					if(tar != null) {
						int na = og.GetRunnerData(tar).GetAcce();
						og.GetRunnerData(player).TempAcce(na, 200);
						og.GetRunnerData(tar).TempAcce(-1, 200);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
							public void run() {
								waza[0] = false;
							}
						},200);
						switch(new Random().nextInt(3)) {
						case 0:
							og.SendChatToPlayer(player, tar,"へぇ、良いの持ってんじゃん");
							break;
						case 1:
							og.SendChatToPlayer(player,tar,"あ、落としたよ？貰っちゃうね");
							break;
						case 2:
							og.SendChatToPlayer(player, tar,"ダメだよ～ちゃんと持っとかなきゃ");
							break;
						}
						//player.sendMessage((String)OngkData.accesory[na][0]);
					}else {
						waza[0] = false;
					}
				}
			}
		}else 	if(itemName.equals("個性盗み")) {
			if(!waza[1]) {
				if(CostStamina(35)) {
					waza[1] = true;
					Player tar = null;
					double dis = 10000;
					for(Entity ent:player.getNearbyEntities(5, 5, 5)) {
						if(og.GetLivingPlayers().contains(ent) && ent != player) {
							double ndis = ent.getLocation().distance(player.getLocation());
							if(dis > ndis) {
								dis = ndis;
								tar = (Player)ent;
							}
						}
					}
					if(tar != null && og.GetRunner(tar) != 10 && og.GetRunner(tar) != 8) {
						switch(new Random().nextInt(3)) {
						case 0:
							og.SendChatToPlayer(player, tar,"へぇ、良いの持ってんじゃん");
							break;
						case 1:
							og.SendChatToPlayer(player,tar,"面白い技だね～使わせてよ");
							break;
						case 2:
							og.SendChatToPlayer(player, tar,"個性とっちゃった～どんな気持ち？");
							break;
						}
						ha = true;
						og.GetRunnerData(tar).SetSilence(true);
						tarrr = tar;
						player.getInventory().setItem(1, GetKosei(og.GetRunner(tar)));
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
							public void run() {
								if(ha) {
									Reback();
								}
							}
						},120);
					}else {
						waza[1] = false;
					}
				}
			}
		}else 	if(itemName.equals("ヴェロショット")) {
			int idvelo;
			HashMap<Player,Location> pl = new HashMap<Player,Location>();
			idvelo =Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
				public void run() {
					Location lo = player.getTargetBlock(null, 20).getLocation();
					player.spawnParticle(Particle.VILLAGER_HAPPY, lo, 5,0,0, 0);
					pl.put(player, lo);
				}
			}, 0, 5);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
				public void run() {
					Bukkit.getServer().getScheduler().cancelTask(idvelo);
					HashMap<Player,Integer> idrun = new HashMap<Player,Integer>();
					final Location goal = pl.get(player);
					idrun.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
						public void run() {
							Vector v = new Vector(goal.getX() - player.getLocation().getX(), goal.getY() - player.getLocation().getY() , goal.getZ() - player.getLocation().getZ());
							player.setVelocity(v.normalize().multiply(2));
							Location lo = player.getLocation();
							if(lo.getX() < goal.getX() + 2.5 && lo.getX() > goal.getX() - 2.5 && lo.getY() < goal.getY() + 2.5 && lo.getY() > goal.getY() - 2.5 && lo.getZ() < goal.getZ() + 2.5 && lo.getZ() > goal.getZ() - 2.5) {
								Bukkit.getServer().getScheduler().cancelTask(idrun.get(player));
								return;
							}
						}
					}, 0, 1));
				}
			},20);
			Reback();
		}else if(itemName.equals("食料トス")) {
			ItemStack syokuryo = new ItemStack(Material.COOKED_BEEF);
			ItemMeta syokuryom = syokuryo.getItemMeta();
			syokuryom.setDisplayName("食料");
			syokuryo.setItemMeta(syokuryom);
			Item s = player.getWorld().dropItem(player.getLocation(), syokuryo);
			s.setVelocity(player.getLocation().getDirection());
			items.add(s);
			Reback();
		}else if(itemName.equals("インビジブル")) {
			inv = true;
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,120,1));
			for(Player p:og.GetPlayers()) {
				p.hidePlayer(player);
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
				public void run() {
					inv = false;
					for(Player p:og.GetPlayers()) {
						p.showPlayer(player);
					}
				}
			},120);
			Reback();
		}else if(itemName.equals("可逆テレポート")) {
			final Location plo = player.getLocation().clone();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin,new Runnable() {
				public void run() {
					player.teleport(plo);
				}
			},40);
			Reback();
		}else if(itemName.equals("みんなふわふわ")) {
			for(Player p:og.GetPlayers()) {
					if(!player.isSneaking() && p == player) {
						continue;
					}
					p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,80,3));
			}
			HashMap<Player,Integer> huwaid = new HashMap<Player,Integer>();
			huwaid.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
				public void run() {
					for(Player p:og.GetLivingPlayers()) {
						if(p != player) {
							Vector v = new Vector(player.getLocation().getX() - p.getLocation().getX(), player.getLocation().getY() - p.getLocation().getY(), player.getLocation().getZ() - p.getLocation().getZ());
							og.GetRunnerData(p).Push(v.normalize().multiply(0.4));
						}
					}
				}
			}, 0, 8));
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
				public void run() {
					Bukkit.getServer().getScheduler().cancelTask(huwaid.get(player));
				}
			},80);
			Reback();
		}else if(itemName.equals("急行線")) {
			final int kyuid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
				public void run() {
					player.setVelocity(player.getLocation().getDirection());
				}
			}, 0, 1);
			final HashMap<Player,Integer> kyuid2 = new HashMap<Player,Integer>();
			kyuid2.put(player, 		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
				public void run() {
					int kizyun = 5;
					if(stamina <= kizyun) {
						Bukkit.getServer().getScheduler().cancelTask(kyuid);
						Bukkit.getServer().getScheduler().cancelTask(kyuid2.get(player));
						return;
					}
					AddStamina(-1);
				}
			}, 0, 1));
			Reback();
		}else if(itemName.equals("スピード")) {
			TempSpeed(1,200);
			TempJump(1,200);
			Reback();
		}else if(itemName.equals("狩りの時間")) {
			player.setVelocity(new Vector(0,7.5,0));
			player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
				public void run() {
					final HashMap<Player,Integer> ei = new HashMap<Player,Integer>();
					ei.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
						public void run() {
							if(player.isOnGround()) {
								player.getInventory().setChestplate(new ItemStack(Material.AIR));
								Bukkit.getServer().getScheduler().cancelTask(ei.get(player));
								return;
							}
						}
					}, 0, 1));
				}
			},10);
			Reback();
		}else if(itemName.equals("爆弾シュート")) {
			Entity tnt = player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
			tnt.setVelocity(player.getLocation().getDirection());
			if(player.isSneaking()) {
				tntbool.put(tnt, true);
			}else {
				tntbool.put(tnt, false);
			}
			tnts.add(tnt);
			Reback();
		}else if(itemName.equals("人探し")) {
			 for(Player p: og.GetLivingPlayers()) {
				 if(p == player) {
					 continue;
				 }
				 if(p.hasPotionEffect(PotionEffectType.GLOWING)) {
					 p.removePotionEffect(PotionEffectType.GLOWING);
				 }
				 p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,200,1));
			 }
			Reback();
		}else 	if(itemName.equals("オーバータッチ")) {
			List<Player> ps = new ArrayList<Player>();
			for(Entity ent:player.getNearbyEntities(4, 4, 4)) {
				if(og.GetLivingPlayers().contains(ent) && ent != player) {
					Player tar = (Player)ent;
					ps.add(tar);
				}
			}
			if(ps.size() >= 1) {
				double distance = 10000;
				Player target = null;
				for(Player p:ps) {
					double nd = player.getLocation().distance(p.getLocation());
					if(distance > nd) {
						target = p;
						distance = nd;
					}
				}
				if(target != null) {
					og.SetOni(target);
				}
			}
			Reback();
		}
	}

	public void Reback() {
		ItemStack konu = new ItemStack(Material.CLAY_BALL);
		ItemMeta konum = konu.getItemMeta();
		konum.setDisplayName("個性盗み");
		konu.setItemMeta(konum);
		player.getInventory().setItem(1, konu);
		waza[1] = false;
		ha = false;
		og.GetRunnerData(tarrr).SetSilence(false);
	}

	public ItemStack GetKosei(int runnerid) {
		switch(runnerid) {
		case 0:
			player.sendMessage(ChatColor.GREEN + "飛ぶまでのラグが" + ChatColor.RED + "2→1" + ChatColor.GREEN + "秒に短縮されている。");
			break;
		case 1:
			player.sendMessage(ChatColor.GREEN + "自分で肉を拾った場合のスタミナ回復量が" + ChatColor.RED + "10→40" + ChatColor.GREEN + "に上昇している。");
			break;
		case 2:
			player.sendMessage(ChatColor.GREEN + "透明になれる時間が" + ChatColor.RED + "5→6" + ChatColor.GREEN + "秒に延長されている。");
			break;
		case 3:
			player.sendMessage(ChatColor.GREEN + "元の位置に戻るまでの時間が" + ChatColor.RED + "3→2" + ChatColor.GREEN + "秒に短縮されている。");
			break;
		case 4:
			player.sendMessage(ChatColor.GREEN + "強制浮上させる時間が" + ChatColor.RED + "3→4" + ChatColor.GREEN + "秒に延長されている。");
			break;
		case 5:
			player.sendMessage(ChatColor.GREEN + "鬼でも、スタミナが5残る。");
			break;
		case 6:
			player.sendMessage(ChatColor.GREEN + "副作用がない。");
			break;
		case 7:
			player.sendMessage(ChatColor.GREEN + "より高く飛ぶようになっている。");
			break;
		case 9:
			player.sendMessage(ChatColor.GREEN + "爆破の威力が上がっている。");
			break;
		case 10:
			player.sendMessage(ChatColor.GREEN + "鬼かどうか関係なく、全プレイヤー16秒発光する。");
			break;
		case 11:
			player.sendMessage(ChatColor.GREEN + "タッチの範囲が半径" + ChatColor.RED + "3→4" + ChatColor.GREEN + "ブロックに広がっている。");
			break;
		}
		int[] is = {1,0,0,0,1,0,0,1,0,0,-1,0,0};
		int i = is[runnerid];
		//ItemStack weapon = new ItemStack((Material)OngkData.weapon[runnerid][i][1]);
		//ItemMeta weaponm = weapon.getItemMeta();
		//weaponm.setDisplayName((String)OngkData.weapon[runnerid][i][0]);
		//weaponm.setLore(OnigokkoMain.MakeDescription((String)OngkData.weapon[runnerid][i][3],(int)OngkData.weapon[runnerid][i][2]));
		//weapon.setItemMeta(weaponm);
		//return weapon;
		return null;
	}

	@EventHandler
	public void PlayerPickupItem(PlayerPickupItemEvent e) {
		if(og.GetPlayers().contains(e.getPlayer())) {
			if(e.getItem().getItemStack().getItemMeta().getDisplayName().equals("食料")) {
				if(items.contains(e.getItem())) {
					e.setCancelled(true);
					e.getItem().remove();
					og.GetRunnerData(e.getPlayer()).AddStamina(10);
					if(e.getPlayer() == player) {
						og.GetRunnerData(e.getPlayer()).AddStamina(30);
					}
					if(e.getPlayer() != player) {
						og.GetRunnerData(e.getPlayer()).TempSpeed(-3, 200);
						og.GetRunnerData(e.getPlayer()).TempJump(-1, 200);
					}
					items.remove(e.getItem());
				}
			}
		}
	}

	@EventHandler
	public void ExplosionPrime(ExplosionPrimeEvent e) {
		if(tnts.contains(e.getEntity())) {
			e.setCancelled(true);
			tnts.remove(e.getEntity());
			e.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, e.getEntity().getLocation(), 200,1, 1,1, 0.5);
			for(Entity ent: e.getEntity().getNearbyEntities(8, 8, 8)) {
				if(ent instanceof Player) {
					Player target = (Player)ent;
					Location elo = e.getEntity().getLocation();
					Location tlo = target.getLocation();
					Vector v = new Vector(tlo.getX() - elo.getX(), tlo.getY() - elo.getY(), tlo.getZ() - elo.getZ());
					og.GetRunnerData(target).Push(v.normalize().multiply((tntbool.get(e.getEntity())?-1:1) * 7));
				}
			}
		}
	}
}
