package runner;

import java.util.List;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import oc.OCGame;

public class SpeedBoy extends Runner{

	boolean sonic = false;
	
	public SpeedBoy(Player player, Plugin plugin, OCGame ocg, List<Integer> acceID) {
		super(player, plugin, ocg, 0,acceID);
		sonicwave();
	}

	@Override
	void firstAbility() {
		addSpeed(20,10,player,"スーパースピード");
	}

	@Override
	void secondAbility() {
		force(player.getLocation().getDirection().normalize().multiply(3),player,"ヴェクターショット");
	}

	@Override
	void thirdAbility() {
		cool[2] = false;
		sonic = true;
		new BukkitRunnable() {
			public void run() {
				cool[2] = true;
				sonic = false;
			}
		}.runTaskLater(plugin, 200);
	}
	
	void sonicwave() {
		new BukkitRunnable() {
			public void run() {
				if(!live) {
					this.cancel();
					return;
				}
				if(sonic && player.isSprinting()) {
					player.getWorld().spawnParticle(Particle.SWEEP_ATTACK,player.getLocation(),12,0.5,0.5,0.5,0);
					for(Entity ent:player.getNearbyEntities(2.5, 2.5, 2.5)) {
						if(ent instanceof Player && ocg.containsLivings((Player)ent)) {
							Player target = (Player)ent;
							Vector v = player.getLocation().getDirection().normalize().multiply(-2);
							v.setY(2);
							ocg.getPlayerData(target).force(v, player, "ソニックブーム");
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 5);
	}

}
