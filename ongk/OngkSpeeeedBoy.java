package ongk;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class OngkSpeeeedBoy extends OngkRunner{

	public OngkSpeeeedBoy(Plugin plugin, Player play,OngkGame og,int acce) {
		super(plugin, play,og,acce);
	}

	@Override
	public void CancelEvents() {
		super.CancelEvents();
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("スーパースピード")) {
			if(!waza[0]) {
				if(CostStamina(17)) {
					switch(new Random().nextInt(3)) {
					case 0:
						og.SendChatToAround(player, 10,"飛ばしてくぜっ！");
						break;
					case 1:
						og.SendChatToAround(player,10,"いくぜいくぜー！！");
						break;
					case 2:
						og.SendChatToAround(player, 10,"最高！！");
						break;
					}
					waza[0] = true;
					AddSpeed(20);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							AddSpeed(-20);
							waza[0] = false;
						}
					},10);
				}
			}
		}else if(itemName.equals("ヴェロショット")) {
			if( !waza[1]) {
				if(CostStamina(52)) {
					waza[1] = true;
					int idvelo;
					HashMap<Player,Location> pl = new HashMap<Player,Location>();
					idvelo =Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
						public void run() {
							Location lo = player.getTargetBlock(null, 20).getLocation();
							player.spawnParticle(Particle.VILLAGER_HAPPY, lo, 5,0,0, 0);
							pl.put(player, lo);
						}
					}, 0, 5);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							switch(new Random().nextInt(3)) {
							case 0:
								og.SendChatToAround(player, 10,"じゃぁね～！");
								break;
							case 1:
								og.SendChatToAround(player,10,"気持ちいい～！！");
								break;
							case 2:
								og.SendChatToAround(player, 10,"最高ー！！！");
								break;
							}
							Bukkit.getServer().getScheduler().cancelTask(idvelo);
							HashMap<Player,Integer> idrun = new HashMap<Player,Integer>();
							final Location goal = pl.get(player);
							idrun.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
								public void run() {
									Vector v = new Vector(goal.getX() - player.getLocation().getX(), goal.getY() - player.getLocation().getY() , goal.getZ() - player.getLocation().getZ());
									player.setVelocity(v.normalize().multiply(2));
									Location lo = player.getLocation();
									if(lo.getX() < goal.getX() + 2.5 && lo.getX() > goal.getX() - 2.5 && lo.getY() < goal.getY() + 2.5 && lo.getY() > goal.getY() - 2.5 && lo.getZ() < goal.getZ() + 2.5 && lo.getZ() > goal.getZ() - 2.5) {
										Bukkit.getServer().getScheduler().cancelTask(idrun.get(player));
										waza[1] = false;
										return;
									}
								}
							}, 0, 1));
						}
					},40);
				}
			}
		}
	}
}
