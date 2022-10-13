package runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import oc.OCGame;

public class Stone extends Runner{

	public Stone(Player player, Plugin plugin, OCGame ocg,  List<Integer> acceID) {
		super(player, plugin, ocg,4, acceID);
	}

	@Override
	void firstAbility() {
		Location l = getEyeLocation(player,8);
		if(l.getBlock().getType() == Material.AIR) {
			List<Location> los = new ArrayList<Location>();
			los.add(l.clone());
			for(int i = 0 ; i < 40 ; i++) {
				l.add(0,-1,0);
				Object[] result = buildCheck(l);
				if((boolean)result[1]) {
					return;
				}
				List<Location> bs = (List<Location>)result[0];
				if(bs.size() > 0) {
					for(Location ls :bs) {
						los.add(ls.clone());
					}
				}else {
					break;
				}
			}
			Collections.reverse(los);
			for(int i = 0 ; i < los.size() ; i++) {
				final Location cl = los.get(i);
				new BukkitRunnable() {
					public void run() {
						if(cl.getBlock().getType() == Material.AIR) {
							cl.getBlock().setType(Material.STONE);
							new BukkitRunnable() {
								public void run() {
									cl.getBlock().setType(Material.AIR);
								}
							}.runTaskLater(plugin, 120);
						}
					}
				}.runTaskLater(plugin, i);
			}
		}
	}
	
	Object[] buildCheck(Location l) {
		boolean check = false;
		List<Location> checks = new ArrayList<Location>();
		Location lo = l.clone();
		if(lo.getBlock().getType() == Material.AIR) {
			checks.add(lo.clone());
		}else if(lo.getBlock().getType() == Material.STONE) {
			check = true;
		}
		lo.add(1,0,0);
		if(lo.getBlock().getType() == Material.AIR) {
			checks.add(lo.clone());
		}else if(lo.getBlock().getType() == Material.STONE) {
			check = true;
		}
		lo.add(-2,0,0);
		if(lo.getBlock().getType() == Material.AIR) {
			checks.add(lo.clone());
		}else if(lo.getBlock().getType() == Material.STONE) {
			check = true;
		}
		lo.add(1,0,1);
		if(lo.getBlock().getType() == Material.AIR) {
			checks.add(lo.clone());
		}else if(lo.getBlock().getType() == Material.STONE) {
			check = true;
		}
		lo.add(0,0,-2);
		if(lo.getBlock().getType() == Material.AIR) {
			checks.add(lo.clone());
		}else if(lo.getBlock().getType() == Material.STONE) {
			check = true;
		}
		Object[] result = {checks,check};
		return result;
	}
	
	boolean containsStone(Location l) {
		Location lo = l.clone();
		if(lo.getBlock().getType() == Material.STONE) {
			return true;
		}
		lo.add(1,0,0);
		if(lo.getBlock().getType() == Material.STONE) {
			return true;
		}
		lo.add(-2,0,0);
		if(lo.getBlock().getType() == Material.STONE) {
			return true;
		}
		lo.add(1,0,1);
		if(lo.getBlock().getType() == Material.STONE) {
			return true;
		}
		lo.add(0,0,-2);
		if(lo.getBlock().getType() == Material.STONE) {
			return true;
		}
		return false;
	}
	
	Location getEyeLocation(Player player,int delay) {
		Location ll = player.getLocation().clone();
		Block bl = null;
		for(Block b : player.getLineOfSight((HashSet<Material>) null, delay)) {
			if(b.getType() == Material.AIR) {
				bl = b;
			}else {
				break;
			}
		}
		if(bl != null) {
			ll = bl.getLocation().clone();
		}
		return ll;
	}

	@Override
	void secondAbility() {
		FallingBlock st = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.STONE.createBlockData());
		st.setDropItem(false);
		Vector v = player.getLocation().getDirection().normalize().multiply(2).clone();
		st.setVelocity(v);
		new BukkitRunnable() {
			public void run() {
				if(!st.isValid()) {
					this.cancel();
					return;
				}
				for(Entity ent:st.getNearbyEntities(1.5, 1.5, 1.5)) {
					if(ent instanceof Player) {
						Player target = (Player)ent;
						if(target != player && ocg.containsLivings(target)) {
							ocg.getPlayerData(target).addSpeed(-1, 120, player, "フリースロー");
							st.remove();
							break;
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 3);
	}

	@Override
	void thirdAbility() {
		ArmorStand as = (ArmorStand)player.getWorld().spawnEntity(player.getLocation(),EntityType.ARMOR_STAND);
		as.setVisible(false);
		Vector v = player.getLocation().getDirection().normalize();
		new BukkitRunnable() {
			public void run() {
				if(!as.isValid()) {
					this.cancel();
					return;
				}
				as.setVelocity(v);
				as.getWorld().spawnParticle(Particle.HEART, as.getLocation(),6,0.5,0.5,0.5,0.5);
				for(Entity ent: as.getNearbyEntities(1.5, 1.5, 1.5)) {
					if(ent != player && ent instanceof Player) {
						Player target = (Player)ent;
						ocg.getPlayerData(target).force(v.clone().multiply(2), player, "クラッシュピアス");
						as.remove();
						break;
					}
				}
			}
		}.runTaskTimer(plugin, 0, 1);
		new BukkitRunnable() {
			public void run() {
				if(as.isValid()) {
					as.remove();
				}
			}
		}.runTaskLater(plugin, 70);
	}

}
