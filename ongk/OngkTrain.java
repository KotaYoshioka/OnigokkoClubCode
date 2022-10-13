package ongk;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class OngkTrain extends OngkRunner{

	public OngkTrain(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("急行線")) {
			if(this.stamina >= 20) {
				if(!waza[0]) {
					switch(new Random().nextInt(1)) {
					case 0:
						og.SendChatToAll(player, "発車致します");
						break;
					}
					Kyuko();
					waza[0] = true;
				}
			}
		}else if(itemName.equals("燃料補給")) {
			if(player.isOnGround()) {
				if(og.GetOni() == player) {
					AddStamina(40);
				}
			}
		}
	}

	public void Kyuko() {
		final int kyuid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				player.setVelocity(player.getLocation().getDirection());
			}
		}, 0, 1);
		final HashMap<Player,Integer> kyuid2 = new HashMap<Player,Integer>();
		kyuid2.put(player, 		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				int kizyun = 5;
				if(og.GetOni() == player) {
					kizyun = 0;
				}
				if(stamina <= kizyun) {
					Bukkit.getServer().getScheduler().cancelTask(kyuid);
					Bukkit.getServer().getScheduler().cancelTask(kyuid2.get(player));
					waza[0] = false;
					return;
				}
				AddStamina(-1);
			}
		}, 0, 1));
	}
}
