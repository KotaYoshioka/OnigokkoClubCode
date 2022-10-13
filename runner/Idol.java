package runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import oc.OCGame;

public class Idol extends Runner{

	public Idol(Player player, Plugin plugin, OCGame ocg, List<Integer> acceID) {
		super(player, plugin, ocg, 6, acceID);
	}

	@Override
	void firstAbility() {
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
						if(ocg.containsLivings(target)) {
							ocg.getPlayerData(target).love(500);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 1);
		new BukkitRunnable() {
			public void run() {
				as.remove();
			}
		}.runTaskLater(plugin, 100);
	}

	@Override
	void secondAbility() {
		List<Player> targets = new ArrayList<Player>();
		for(Player p:ocg.getLivings()) {
			if(p != player && ocg.getPlayerData(p).getLove()) {
				targets.add(p);
			}
		}
		int index = new Random().nextInt(11);
		String cause = "";
		for(Player p:targets) {
			switch(index) {
			case 0:
			case 5:
			case 9:
				p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,400,1));
				cause = "皆、発光させちゃえ！";
				break;
			case 1:
			case 6:
			case 10:
				p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,200,3));
				cause = "皆、飛んじゃえ！";
				break;
			case 2:
			case 7:
				ocg.getPlayerData(p).addSpeed(-1, 300, player, "ファンミート");
				cause = "皆、遅くなっちゃえ！";
				break;
			case 3:
			case 8:
				ocg.getPlayerData(p).addJump(-1, 300, player, "ファンミート");
				cause = "皆、跳べなくなっちゃえ！";
				break;
			case 4:
				p.teleport(player.getLocation().clone());
				cause = "皆、集まれ！";
				break;
			}
		}
		player.sendMessage(cause);
	}

	@Override
	void thirdAbility() {
		int heal = 0;
		for(Player p:ocg.getLivings()) {
			if(p != player && ocg.getPlayerData(p).getLove()) {
				int stamina = ocg.getPlayerData(p).getStamina();
				int amount = (int)stamina/2;
				heal += amount;
				ocg.getPlayerData(p).reduceStamina(amount);
			}
		}
		if(heal > 0) {
			ocg.getPlayerData(player).healStamina(heal);
		}
	}

}
