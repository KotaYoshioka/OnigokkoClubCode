package ongk;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Ongkzzz extends OngkRunner{

	public Ongkzzz(Plugin plugin, Player play, OngkGame og, int acce) {
		super(plugin, play, og, acce);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("あくび")) {
			if(!waza[0]) {
				if(CostStamina(10)) {
					for(Entity ent:player.getNearbyEntities(6, 6, 6)) {
						if(og.GetLivingPlayers().contains(ent) & ent != player) {
							Player target = (Player)ent;
							switch(new Random().nextInt(3)) {
							case 0:
								og.SendChatToPlayer(player, target,"一緒に寝よぉ？");
								break;
							case 1:
								og.SendChatToPlayer(player,target,"おやすみ・・・");
								break;
							case 2:
								og.SendChatToPlayer(player, target,"眠くなってきたなぁ・・・");
								break;
							}
							og.GetRunnerData(target).TempSpeed(-1, 100);
						}
					}
					og.GetRunnerData(player).TempSpeed(-1, 300);
					og.GetRunnerData(player).TempJump(-1, 300);
				}
			}
		}else if(itemName.equals("夢遊病")) {
			if(!waza[1]) {
				if(CostStamina(15)) {
					hell = true;
					switch(new Random().nextInt(3)) {
					case 0:
						og.SendChatToAll(player,"もう寝るね・・・(ったく、こいつ、呑気に寝やがって)");
						break;
					case 1:
						og.SendChatToAll(player,"おやすみ・・・(俺様がこいつを守ってやらねぇとなぁ)");
						break;
					case 2:
						og.SendChatToAll(player,"もう疲れた・・・(そろそろ俺様の出番か)");
						break;
					}
					waza[1] = true;
					og.GetRunnerData(player).TempSpeed(-3, 320);
					og.GetRunnerData(player).TempJump(-3, 320);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							for(Player p:og.GetLivingPlayers()) {
								p.sendTitle("スリーピングデビルが現れた！", "殴れ！殺せ！", 10, 60, 10);
							}
							og.GetRunnerData(player).TempSpeed(-(speed * 2), 200);
							og.GetRunnerData(player).TempJump(-(jump * 2), 200);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
								public void run() {
									hell = false;
								}
							},200);
						}
					},60);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							waza[1] = false;
						}
					},360);
				}
			}
		}
	}

}
