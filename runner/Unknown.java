package runner;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import oc.OCGame;

public class Unknown extends Runner{

	public Unknown(Player player, Plugin plugin, OCGame ocg, List<Integer> acceID) {
		super(player, plugin, ocg, 1, acceID);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	void firstAbility() {
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,100,1));
		cool[0] = false;
		for(Player p:ocg.getLivings()) {
			p.hidePlayer(player);
		}
		addSpeed(2,100,player,"インビジブル");
		addJump(2,100,player,"インビジブル");
		new BukkitRunnable() {
			public void run() {
				for(Player p:ocg.getLivings()) {
					p.showPlayer(player);
					cool[0] = true;
				}
			}
		}.runTaskLater(plugin, 100);
	}

	@Override
	void secondAbility() {
		for(Player p:ocg.getLivings()) {
			if(p != player) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,100,1));
				Location plo = p.getLocation();
				plo.setDirection(player.getLocation().getDirection());
				p.teleport(plo);
			}
		}
	}

	@Override
	void thirdAbility() {
		for(Player p:ocg.getLivings()) {
			ocg.getPlayerData(p).silence(200);
		}
	}

}
