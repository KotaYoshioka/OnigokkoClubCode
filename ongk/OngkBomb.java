package ongk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class OngkBomb extends OngkRunner{

	List<Entity> tnts = new ArrayList<Entity>();
	HashMap<Entity,Boolean> tntbool = new HashMap<Entity,Boolean>();

	public OngkBomb(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
	}

	@Override
	public void CancelEvents() {
		super.CancelEvents();
		ExplosionPrimeEvent.getHandlerList().unregister(this);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("爆弾シュート")) {
			if(CostStamina(27)) {
				switch(new Random().nextInt(3)) {
				case 0:
					og.SendChatToAround(player,7,"あひゃひゃ！ばーくはーーつ！！");
					break;
				case 1:
					og.SendChatToAround(player,7,"いーっぱいとんじゃえ！！！");
					break;
				case 2:
					og.SendChatToAround(player, 7,"爆発するよぉ！離れてぇ！あはは！");
					break;
				}
				Entity tnt = player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
				tnt.setVelocity(player.getLocation().getDirection());
				if(player.isSneaking()) {
					tntbool.put(tnt, true);
				}else {
					tntbool.put(tnt, false);
				}
				tnts.add(tnt);
			}
		}else if(itemName.equals("体内爆弾")) {
			if(og.GetOni() != player) {
				if(!waza[1]) {
					if(CostStamina(75)) {
						waza[1] = true;
						player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,100,1));
						bomb = true;
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
							public void run() {
								waza[1] = false;
								bomb = false;
							}
						},100);
					}
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
			for(Entity ent: e.getEntity().getNearbyEntities(6, 6, 6)) {
				if(ent instanceof Player) {
					Player target = (Player)ent;
					Location elo = e.getEntity().getLocation();
					Location tlo = target.getLocation();
					Vector v = new Vector(tlo.getX() - elo.getX(), tlo.getY() - elo.getY(), tlo.getZ() - elo.getZ());
					og.GetRunnerData(target).Push(v.normalize().multiply((tntbool.get(e.getEntity())?-1:1) * 6));
				}
			}
		}
	}

}
