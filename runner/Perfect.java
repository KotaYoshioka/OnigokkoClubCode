package runner;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import oc.OCGame;

public class Perfect extends Runner{

	public Perfect(Player player, Plugin plugin, OCGame ocg, List<Integer> acceID) {
		super(player, plugin, ocg, 5, acceID);
	}

	@Override
	void firstAbility() {
		ocg.getPlayerData(player).force(player.getLocation().getDirection().normalize().multiply(1.8).multiply(ocg.getOni()==player?-1:1), player, "パーフェクトジャンプ");
	}

	@Override
	void secondAbility() {
		for(Entity ent : player.getNearbyEntities(7, 7, 7)) {
			if(ent != player && ent instanceof Player) {
				Player target = (Player)ent;
				if(ocg.containsLivings(target)) {
					Location plo = player.getLocation().clone();
					Location tlo = target.getLocation().clone();
					Vector v = new Vector(plo.getX()-tlo.getX(),plo.getY()-tlo.getY(),plo.getZ()-tlo.getZ()).normalize();
					ocg.getPlayerData(target).force(v.multiply(ocg.getOni()==player?3:-3),player,"パーフェクトインパクト");
				}
			}
		}
	}

	@Override
	void thirdAbility() {
		if(ocg.getOni() == player) {
			ocg.getPlayerData(player).healStamina(100);
		}else {
			player.setAllowFlight(true);
			player.setFlying(true);
			cool[2] = false;
			new BukkitRunnable() {
				public void run() {
					if(live) {
						player.setAllowFlight(false);
					}
					cool[2] = true;
				}
			}.runTaskLater(plugin, 80);
		}
	}

}
