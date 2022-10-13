package ongk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class OngkFeet extends OngkRunner{

	public OngkFeet(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("可逆テレポート")) {
			if(!waza[0]) {
				if(CostStamina(30)) {
					waza[0] = true;
					final Location plo = player.getLocation().clone();
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin,new Runnable() {
						public void run() {
							player.teleport(plo);
							waza[0] = false;
						}
					},60);
				}
			}
		}else if(itemName.equals("フリッパ")) {
			List<Player> targets = new ArrayList<Player>();
			for(Entity ent:player.getNearbyEntities(15, 15, 15)) {
				if(ent instanceof Player) {
					Player target = (Player)ent;
					if(target != player && target.getGameMode() != GameMode.SPECTATOR) {
						targets.add(target);
					}
				}
			}
			if(targets.size() >= 1) {
				if(CostStamina(55)) {
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
					switch(new Random().nextInt(3)) {
					case 0:
						og.SendChatToPlayer(player,target,"悪いな。");
						break;
					case 1:
						og.SendChatToPlayer(player,target,"どこにいるんだ。");
						break;
					case 2:
						og.SendChatToPlayer(player, target,"お疲れ様。");
						break;
					}
				}
			}
		}
	}

}
