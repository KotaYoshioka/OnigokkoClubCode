package ongk;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OngkUnknown extends OngkRunner{

	public OngkUnknown(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
	}

	@Override
	public void CancelEvents() {
		super.CancelEvents();
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("インビジブル")) {
			if(!waza[0]) {
				if(CostStamina(37)) {
					waza[0] = true;
					inv  = true;
					player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,100,1));
					for(Player p:og.GetPlayers()) {
						p.hidePlayer(player);
					}
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							waza[0] = false;
							inv = false;
							for(Player p:og.GetPlayers()) {
								p.showPlayer(player);
							}
						}
					},100);
				}
			}
		}else if(itemName.equals("バーティゴ")) {
			if(!waza[1]) {
				if(CostStamina(49)) {
					switch(new Random().nextInt(2)) {
					case 0:
						og.SendChatToAll(player,"おい、どこを見てるんだ？");
						break;
					case 1:
						og.SendChatToAll(player,"なんだ、ろくに歩けもしないのか");
						break;
					}
					inv = true;
					waza[1] = true;
					for(Player p:og.GetPlayers()) {
						if(p != player) {
							p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1));
							Location plo = p.getLocation();
							plo.setDirection(player.getLocation().getDirection());
							p.teleport(plo);
						}
					}
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							waza[1] = false;
							inv = false;
						}
					},100);
				}
			}
		}
	}

}
