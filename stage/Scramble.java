package stage;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import oc.OCGame;

public class Scramble extends StageBase{

	public Scramble(Plugin plugin, OCGame ocg,World world) {
		super(plugin, ocg,world);
		windAir();
	}
	
	public void windAir() {
		new BukkitRunnable() {
			public void run() {
				if(stopgimic) {
					this.cancel();
				}
				Location[] ls = {new Vector(712,7,16).toLocation(world),new Vector(708,7,-3).toLocation(world),
						new Vector(689,7,1).toLocation(world),new Vector(693,7,20).toLocation(world)};
				for(Location l:ls) {
					for(int i = 0 ; i < 20 ; i++) {
						Location cl = l.clone();
						cl.add(0,i,0);
						for(Entity ent:world.getNearbyEntities(cl, 2, 2, 2)) {
							if(ent instanceof Player) {
								Player player = (Player)ent;
								if(ocg.containsLivings(player)) {
									player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,20-i,30-i));
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 3);
	}

}
