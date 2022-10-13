package ongk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public class OngkSubitem implements Listener{

	List<Integer> items = new ArrayList<Integer>();
	Plugin mainPlugin;
	OngkGame og;

	public OngkSubitem(Plugin plugin, OngkGame nog) {
		mainPlugin = plugin;
		og = nog;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		/*
		for(int i = 0 ; i < OngkData.subitems.length ; i++) {
			items.add(i);
		}
		*/
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				RandomSpawn();
			}
		},200);
	}

	public void CancelEvents() {
		PlayerInteractEvent.getHandlerList().unregister(this);
	}

	public void RandomSpawn() {
		SpawnItemRandom();
		Random rnd = new Random();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
			public void run() {
				if(og.GetStart()) {
					RandomSpawn();
				}
			}
		},rnd.nextInt(700) + 1);
	}

	public void SpawnItemRandom() {
		Random rnd = new Random();
		int spawnitem = items.get(rnd.nextInt(items.size()));
		Location lo = new Vector(rnd.nextInt(70) + 2 , 56 ,  - (rnd.nextInt(145) + 25)).toLocation(og.GetPlayers().get(0).getWorld());
		lo.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, lo, 100, 1, 1, 1, 0.5);
		//ItemStack item = new ItemStack((Material)OngkData.subitems[spawnitem][1]);
		//ItemMeta itemm = item.getItemMeta();
		//itemm.setDisplayName((String)OngkData.subitems[spawnitem][0]);
		//itemm.setLore(OnigokkoMain.MakeDescription(OngkData.runner[(int)OngkData.subitems[spawnitem][2]],(String)OngkData.subitems[spawnitem][3], (String)OngkData.subitems[spawnitem][4]));
		//item.setItemMeta(itemm);
		//lo.getWorld().dropItem(lo, item);
	}

	@EventHandler
	public void PlayerInteract(PlayerInteractEvent e) {
		if(og.GetPlayers().contains(e.getPlayer())) {
			ItemStack item = e.getItem();
			int runner = og.GetRunner(e.getPlayer());
			OngkRunner or = og.GetRunnerData(e.getPlayer());
			Player player = e.getPlayer();
			String name = item.getItemMeta().getDisplayName();
			if(player.getInventory().first(item) == 2) {
				if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if(!player.isSneaking()) {
						if(name.equals("快速ジェットブーツ")) {
							player.getInventory().remove(item);
							if(runner == 0) {
								or.TempSpeed(4, 120);
							}else {
								or.TempSpeed(2, 100);
							}
						}else if(name.equals("ぶっちぎりダッシュ")) {
							player.getInventory().remove(item);
							if(runner == 0) {
								player.setVelocity(player.getLocation().getDirection().multiply(3));
							}else {
								player.setVelocity(player.getLocation().getDirection());
							}
						}else if(name.equals("非常食")) {
							player.getInventory().remove(item);
							if(runner == 1) {
								or.AddStamina(50);
							}else {
								or.AddStamina(25);
							}
						}else if(name.equals("まんぷく飯")) {
							player.getInventory().remove(item);
							if(runner == 1) {
								or.AddStamina(or.maxstaminad);
							}else {
								or.AddStamina(or.maxstaminad);
								or.TempSpeed(-10, 200);
							}
						}else if(name.equals("見えないテクスチャ")) {
							player.getInventory().remove(item);
							if(runner == 2) {
								player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,120,1));
							}else {
								player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,60,1));
							}
						}else if(name.equals("夜更け")) {
							player.getInventory().remove(item);
							if(runner == 2) {
								for(Player p:og.GetPlayers()) {
									if(p != player) {
										p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,60,1));
									}
								}
							}else {
								for(Player p:og.GetPlayers()) {
									p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,60,1));
								}
							}
						}else if(name.equals("不安定テレポート装置")) {
							player.getInventory().remove(item);
							if(runner == 3) {
								List<Player> demop = new ArrayList<Player>();
								for(Player p:og.GetPlayers()) {
									if(p != og.GetOni() && p != player) {
										demop.add(p);
									}
								}
								if(demop.size() != 0) {
									Collections.shuffle(demop);
									player.teleport(demop.get(0).getLocation());
								}
							}else {
								player.teleport(og.GetOni().getLocation());
							}
						}else if(name.equals("フォールアウト")) {
							player.getInventory().remove(item);
							Location plo = player.getLocation().clone();
							if(runner == 3) {
								plo.add(0,6,0);
							}else {
								plo.add(0,3,0);
							}
							player.teleport(plo);
						}else if(name.equals("脱力モード")) {
							player.getInventory().remove(item);
							if(runner == 4) {
								for(Player p:og.GetPlayers()) {
									if(p != player) {
										og.GetRunnerData(p).AddStamina(-15);
									}
								}
							}else {
								for(Player p:og.GetPlayers()) {
									og.GetRunnerData(p).AddStamina(-10);
								}
							}
						}else if(name.equals("結んだ風船")) {
							player.getInventory().remove(item);
							if(runner == 4) {
								for(Player p:og.GetPlayers()) {
									if(p != player) {
										p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,60,3));
									}
								}
							}else {
								for(Player p:og.GetPlayers()) {
									p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,60,3));
								}
							}
						}
					}else {
						int itemid = -1;
						/*
						for(int i = 0 ; i < OngkData.subitems.length ; i++) {
							if(OngkData.subitems[i][0].equals(name)) {
								itemid = i;
								break;
							}
						}
						if(itemid != -1) {
							player.sendMessage(ChatColor.YELLOW + "[テーマ]" +OngkData.runner[(int)OngkData.subitems[itemid][2]]);
							player.sendMessage(ChatColor.WHITE + "(効果)" + (String)OngkData.subitems[itemid][3]);
							player.sendMessage(ChatColor.YELLOW + "(テーマ効果)" + (String)OngkData.subitems[itemid][4]);
						}
						*/
					}
				}
			}
		}
	}

}
