package ongk;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class OngkHenya extends OngkRunner{

	public OngkHenya(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("みんなゆるゆる")) {
			if(!waza[0]) {
				if(CostStamina(50)) {
					Random rnd = new Random();
					switch(rnd.nextInt(4)) {
					case 0:
						og.SendChatToAll(player, "落ち着いていこ～");
						break;
					case 1:
						og.SendChatToAll(player,"一旦きゅーけー");
						break;
					case 2:
						og.SendChatToAll(player, "ゆったりしよーよ～");
						break;
					}
					for(Player p:og.GetPlayers()) {
						if(p != player) {
							og.GetRunnerData(p).AddStamina(-20);
						}
					}
				}
			}
		}else if(itemName.equals("みんなふわふわ")) {
			if(!waza[1]) {
				if(CostStamina(43)) {
					waza[1] = true;
					Random rnd = new Random();
					switch(rnd.nextInt(3)) {
					case 0:
						og.SendChatToAll(player, "みんなぷかぷか！おいでおいで～！");
						break;
					case 1:
						og.SendChatToAll(player,"ふわふわふわーん");
						break;
					case 2:
						og.SendChatToAll(player, "皆で浮いたら楽しいねぇ～");
						break;
					}
					for(Player p:og.GetPlayers()) {
							if(!player.isSneaking() && p == player) {
								continue;
							}
							p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,60,3));
					}
					HashMap<Player,Integer> huwaid = new HashMap<Player,Integer>();
					huwaid.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
						public void run() {
							for(Player p:og.GetLivingPlayers()) {
								if(p != player) {
									Vector v = new Vector(player.getLocation().getX() - p.getLocation().getX(), p.getVelocity().getY(), player.getLocation().getZ() - p.getLocation().getZ());
									og.GetRunnerData(p).Push(v.normalize().multiply(0.4));
								}
							}
						}
					}, 0, 8));
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							Bukkit.getServer().getScheduler().cancelTask(huwaid.get(player));
						}
					},60);
				}
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
					public void run() {
						waza[1] = false;
					}
				},60);
			}
		}
	}
}
