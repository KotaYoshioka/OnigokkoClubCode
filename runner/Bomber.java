package runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import oc.OCGame;

public class Bomber extends Runner{

	List<Entity> tnts = new ArrayList<Entity>();
	HashMap<Entity,Boolean> tntbool = new HashMap<Entity,Boolean>();
	
	boolean bodyBomb = false;
	
	Location blo = null;
	
	public Bomber(Player player, Plugin plugin, OCGame ocg, List<Integer> acceID) {
		super(player, plugin, ocg, 3, acceID);
	}

	@Override
	void firstAbility() {
		Entity tnt = player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
		tnt.setVelocity(player.getLocation().getDirection());
		if(player.isSneaking()) {
			tntbool.put(tnt, true);
		}else {
			tntbool.put(tnt, false);
		}
		tnts.add(tnt);
	}

	@Override
	void secondAbility() {
		if(blo == null) {
			blo = player.getLocation().clone();
			new BukkitRunnable() {
				public void run() {
					if(!live || blo == null) {
						this.cancel();
						return;
					}
					player.spawnParticle(Particle.ASH,blo,10,0.2,0.2,0.2,0);
				}
			}.runTaskTimer(plugin, 0, 5);
		}else {
			blo.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, blo, 200,1, 1,1, 0.5);
			for(Entity ent:blo.getWorld().getNearbyEntities(blo, 8, 8, 8)) {
				if(ent instanceof Player) {
					Player target = (Player)ent;
					if(ocg.containsLivings(target)) {
						Location tlo = target.getLocation();
						Vector v = new Vector(tlo.getX() - blo.getX(), tlo.getY() - blo.getY(), tlo.getZ() - blo.getZ());
						ocg.getPlayerData(target).force(v.normalize().multiply(6),player,"リモートボム");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent e) {
		if(tnts.contains(e.getEntity())) {
			e.setCancelled(true);
			tnts.remove(e.getEntity());
			e.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, e.getEntity().getLocation(), 200,1, 1,1, 0.5);
			for(Entity ent: e.getEntity().getNearbyEntities(11, 11, 11)) {
				if(ent instanceof Player) {
					Player target = (Player)ent;
					Location elo = e.getEntity().getLocation();
					Location tlo = target.getLocation();
					Vector v = new Vector(tlo.getX() - elo.getX(), tlo.getY() - elo.getY(), tlo.getZ() - elo.getZ());
					ocg.getPlayerData(target).force(v.normalize().multiply((tntbool.get(e.getEntity())?-1:1) * 6), player, "ボムショット");
					if(target != player) {
						ocg.getPlayerData(target).addSpeed(-1, 300, player, "爆風");
						ocg.getPlayerData(target).addJump(-1, 300, player, "爆風");
					}
				}
			}
		}
	}
	
	public boolean getBomb() {
		return bodyBomb;
	}

	@Override
	void thirdAbility() {
		Player tar = null;
		double dis = 999999;
		for(Player p:ocg.getLivings()) {
			if(p == player) {
				continue;
			}
			Location tlo = p.getLocation();
			double d = player.getLocation().distance(tlo);
			if(d < dis) {
				d = dis;
				tar = p;
			}
		}
		if(tar != null) {
			Minecart cart = (Minecart)player.getWorld().spawnEntity(player.getLocation(),EntityType.MINECART_TNT);
			Player target = tar;
			new BukkitRunnable() {
				public void run() {
					if(!cart.isValid()) {
						this.cancel();
						return;
					}
					Location tlo = target.getLocation();
					Location clo = cart.getLocation();
					Vector v = new Vector(tlo.getX()-clo.getX(),tlo.getY()-clo.getY(),tlo.getZ()-clo.getZ());
					cart.setVelocity(v.normalize().multiply(2));
					for(Entity ent: cart.getNearbyEntities(3, 3, 3)) {
						if(ent instanceof Player && ent != player) {
							explo(cart);
							break;
						}
					}
				}
			}.runTaskTimer(plugin, 0, 1);
			new BukkitRunnable() {
				public void run() {
					explo(cart);
				}
			}.runTaskLater(plugin, 200);
		}
	}
	
	void explo(Minecart cart) {
		Location clo = cart.getLocation();
		for(Entity ent: cart.getNearbyEntities(7, 7, 7)) {
			if(ent instanceof Player) {
				Player target = (Player)ent;
				if(ocg.containsLivings(target)) {
					Location tlo = target.getLocation();
					Vector vs = new Vector(tlo.getX() - clo.getX(), tlo.getY() - clo.getY(), tlo.getZ() - clo.getZ());
					ocg.getPlayerData(target).force(vs, player, "チェイサー");
				}
			}
		}
		cart.remove();
	}

}
