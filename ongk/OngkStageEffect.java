package ongk;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class OngkStageEffect {
	World world;
	Plugin mainPlugin;

	OngkStageEffect(Plugin plugin,World w){
		world = w;
		mainPlugin = plugin;
	}

	public void LightOn(boolean stepbystep) {
		if(stepbystep) {
			StepByStepLightOn();
			return;
		}
		OutsideLight(true);
		InsideLight(true);
		CeilingLight(true);
	}

	private void StepByStepLightOn() {
			OutsideLight(true);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
				public void run() {
					InsideLight(true);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							CeilingLight(true);
						}
					},10);
				}
			},10);
	}

	public void LightOff(boolean stepbystep) {
		if(stepbystep) {
			StepByStepLightOff();
			return;
		}
		OutsideLight(false);
		InsideLight(false);
		CeilingLight(false);
	}

	private void StepByStepLightOff() {
			OutsideLight(false);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
				public void run() {
					InsideLight(false);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							CeilingLight(false);
						}
					},10);
				}
			},10);
	}

	public void OutsideLight(boolean true_on) {
		Vector[] vs = {new Vector(105,2,125),new Vector(105,2,109),new Vector(89,2,109),new Vector(89,2,125)};
		for(int i = 0 ; i < 4 ; i++) {
			Location lo = vs[i].toLocation(world);
			if(true_on) {
				lo.getBlock().setType(Material.REDSTONE_BLOCK);
			}else {
				lo.getBlock().setType(Material.AIR);
			}
		}
	}

	public void InsideLight(boolean true_on) {
		Location lo = new Vector(97,3,117).toLocation(world);
		if(true_on) {
			lo.getBlock().setType(Material.REDSTONE_BLOCK);
		}else {
			lo.getBlock().setType(Material.AIR);
		}
	}

	public void CeilingLight(boolean true_on) {
		Location l = new Vector(99,12,119).toLocation(world);
		for(int x = 0 ; x < 5 ; x++) {
			for(int z = 0 ; z < 5 ; z++) {
				Location lo = l.clone();
				lo.add(-x, 0, -z);
				if(true_on) {
					lo.getBlock().setType(Material.REDSTONE_BLOCK);
				}else {
					lo.getBlock().setType(Material.AIR);
				}
			}
		}
	}
}
