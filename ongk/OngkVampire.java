package ongk;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class OngkVampire extends OngkRunner{

	public OngkVampire(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("狩りの時間")) {
			if(!waza[0]) {
				if(CostStamina(54)) {
					switch(new Random().nextInt(3)) {
					case 0:
						og.SendChatToAround(player, 10,"さぁ、狩りの時間だ");
						break;
					case 1:
						og.SendChatToAround(player,10,"逃げたって無駄だ");
						break;
					case 2:
						og.SendChatToAround(player, 10,"誰も逃れられない");
						break;
					}
					waza[0] = true;
					player.setVelocity(new Vector(0,5.5,0));
					player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							final HashMap<Player,Integer> ei = new HashMap<Player,Integer>();
							ei.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
								public void run() {
									if(player.isOnGround()) {
										waza[0] = false;
										player.getInventory().setChestplate(new ItemStack(Material.AIR));
										Bukkit.getServer().getScheduler().cancelTask(ei.get(player));
										return;
									}
								}
							}, 0, 1));
						}
					},10);
				}
			}
		}else if(itemName.equals("吸血")) {
			if(CostStamina(20)) {
				for(Entity ent:player.getNearbyEntities(9, 9, 9)) {
					if(ent instanceof Player) {
						Player target = (Player)ent;
						if(target != player && og.GetPlayers().contains(target)) {
							og.GetRunnerData(target).AddStamina(-10);
							og.GetRunnerData(player).AddStamina(10);
							switch(new Random().nextInt(8)) {
							case 0:
								og.SendChatToPlayer(player, target,"どうした？立ち上がれないか？");
								break;
							case 1:
								og.SendChatToPlayer(player,target,"もっとだ、もっとくれ");
								break;
							case 2:
								og.SendChatToPlayer(player, target,"・・・");
								break;
							}
						}
					}
				}
			}
		}
	}

}
