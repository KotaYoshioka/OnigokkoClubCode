package runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import oc.OCGame;

public class Feet extends Runner{

	public Feet(Player player, Plugin plugin, OCGame ocg, List<Integer> acceID) {
		super(player, plugin, ocg, 2, acceID);
	}

	@Override
	void firstAbility() {
		/*
		if(!player.isSneaking()) {
			final Location plo = player.getLocation().clone();
			cool[0] = false;
			new BukkitRunnable() {
				public void run() {
					player.teleport(plo);
					cool[0] = true;
				}
			}.runTaskLater(plugin, 60);
		}else {
			for(Player p:ocg.getLivings()) {
				if(p != player) {
					Location plo = p.getLocation();
					cool[0] = false;
					new BukkitRunnable() {
						public void run() {
							p.teleport(plo);
							cool[0] = true;
						}
					}.runTaskLater(plugin, 30);
				}
			}
		}
		*/
		Location l = getEyeLocation(player,14);
		player.teleport(l);
	}

	@Override
	void secondAbility() {
		/*
		List<Player> targets = new ArrayList<Player>();
		for(Entity ent:player.getNearbyEntities(18, 18, 18)) {
			if(ent instanceof Player) {
				Player target = (Player)ent;
				if(target != player && target.getGameMode() != GameMode.SPECTATOR) {
					targets.add(target);
				}
			}
		}
		if(targets.size() >= 1) {
			double dis = 100000;
			Player target = null;
			for(Player p:targets) {
				double ndis = player.getLocation().distance(p.getLocation());
				if(dis > ndis) {
					target = p;
					dis = ndis;
				}
			}
			Location tlo = target.getLocation().clone();
			target.teleport(player.getLocation());
			player.teleport(tlo);
		}
		*/
		List<Player> targets = new ArrayList<Player>();
		targets.addAll(ocg.getLivings());
		Collections.shuffle(targets);
		List<Location> tlos = new ArrayList<Location>();
		for(Player p: targets) {
			tlos.add(p.getLocation().clone());
		}
		for(int i = 0 ; i < targets.size() ; i++) {
			Player t = targets.get(i);
			int index = i + 1;
			if(index == targets.size()) {
				index = 0;
			}
			t.teleport(tlos.get(index));
		}
	}

	@Override
	void thirdAbility() {
		cool[2] = false;
		if(ocg.getOni() != player) {
			Location l = player.getLocation().clone();
			new BukkitRunnable() {
				public void run() {
					player.teleport(l);
					cool[2] = true;
				}
			}.runTaskLater(plugin, 60);
		}else {
			HashMap<Player,Location> plo = new HashMap<Player,Location>();
			for(Player p: ocg.getLivings()) {
				if(p != player) {
					plo.put(p, p.getLocation().clone());
				}
			}
			new BukkitRunnable() {
				public void run() {
					for(Player p:plo.keySet()) {
						p.teleport(plo.get(p));
					}
					cool[2] = true;
				}
			}.runTaskLater(plugin, 60);
		}
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
}
