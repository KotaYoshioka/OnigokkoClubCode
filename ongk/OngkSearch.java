package ongk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OngkSearch extends OngkRunner{

	boolean kotei = false;
	HashMap<Player,Integer> koteids = new HashMap<Player,Integer>();

	public OngkSearch(Plugin plugin, Player play, OngkGame og, int acce) {
		super(plugin, play, og, acce);
	}

	@Override
	public void CancelEvents() {
		super.CancelEvents();
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("人探し")) {
			if(!waza[0]) {
				if(CostStamina(25)) {
					waza[0] = true;
					 for(Player p: og.GetLivingPlayers()) {
						 if(p == player) {
							 continue;
						 }
						 if(p.hasPotionEffect(PotionEffectType.GLOWING)) {
							 p.removePotionEffect(PotionEffectType.GLOWING);
						 }
						 p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,(og.GetOni() == p?320:200),1));
					 }
					switch(new Random().nextInt(3)) {
					case 0:
						og.SendChatToAll(player,"あはは・・・見えてるよ・・・");
						break;
					case 1:
						og.SendChatToAll(player,"かわいいねぇ・・・おびえてるの・・・？");
						break;
					case 2:
						og.SendChatToAll(player,"そんなとこにいたんだ・・・まっててね・・・");
						break;
					}
					 Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						 public void run() {
							 waza[0] = false;
						 }
					 },200);
				}
			}
		}else if(itemName.equals("特定・固定")) {
			if(!waza[1]) {
				waza[1] = true;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
					public void run() {
						waza[1] = false;
					}
				},5);
				if(!kotei) {
					if(stamina >= 1) {
						if(!player.isSneaking()) {
							int range = 50;
							Set<Entity> entities = new HashSet<Entity>();
							for(Block b : player.getLineOfSight((HashSet<Material>) null, range)) {
								for(Entity ent : b.getWorld().getNearbyEntities(b.getLocation(), 1, 1, 1)) {
									if(!entities.contains(ent)) {
										entities.add(ent);
									}
								}
							}
							for(Iterator<Entity> it = entities.iterator(); it.hasNext();) {
								Entity entity = it.next();
								if(entity == player) {
									continue;
								}
								if(entity instanceof Player) {
									if(og.GetLivingPlayers().contains(entity)) {
										blockput = true;
										Player p = (Player)entity;
										KoteiPlayer((Player)p);
										switch(new Random().nextInt(3)) {
										case 0:
											og.SendChatToPlayer(player, p,"そこで大人しくしててね・・・えへへ・・・");
											break;
										case 1:
											og.SendChatToPlayer(player,p,"うごけない・・・？ようやく近づけるね・・・");
											break;
										case 2:
											og.SendChatToPlayer(player, p,"まって・・・まってよぉ・・・");
											break;
										}
									}
								}
							}
						}else {
							for(Player p:og.GetLivingPlayers()) {
								if(p != player) {
									if(p.hasPotionEffect(PotionEffectType.GLOWING)) {
										blockput = true;
										KoteiPlayer(p);
									}
								}
							}
						}
					}
				}else {
					kotei = false;
					for(Player p : koteids.keySet()) {
						Bukkit.getServer().getScheduler().cancelTask(koteids.get(p));
						og.GetRunnerData(p).zero = false;
					}
					blockput = false;
					koteids.clear();
				}
			}
		}
	}

	public void KoteiPlayer(Player target) {
		kotei = true;
		og.GetRunnerData(target).zero = true;
		koteids.put(target, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				AddStamina(-1);
				if(stamina <= 0) {
					og.GetRunnerData(target).zero = false;
					kotei = false;
					blockput = false;
					Bukkit.getServer().getScheduler().cancelTask(koteids.get(target));
				}
			}
		}, 0, 1));
	}
}
