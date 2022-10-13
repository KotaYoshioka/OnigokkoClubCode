package ongk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;


public class OngkPlusone extends OngkRunner{

	List<Item> items = new ArrayList<Item>();

	public OngkPlusone(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
	}

	@Override
	public void CancelEvents() {
		super.CancelEvents();
		PlayerPickupItemEvent.getHandlerList().unregister(this);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("スピード")) {
			if(CostStamina(4)) {
				switch(new Random().nextInt(3)) {
				case 0:
					og.SendChatToAround(player, 5,"ア\"ア\"ア\"ア\"ア\"ア\"");
					break;
				case 1:
					og.SendChatToAround(player,5,"いいいいねええええ");
					break;
				case 2:
					og.SendChatToAround(player, 5,"・・・");
					break;
				}
				TempSpeed(1,200);
				TempJump(1,200);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
					public void run() {
						TempSpeed(-2,200);
						TempJump(-2,200);
					}
				},200);
			}
		}else if(itemName.equals("あれ")) {
				if(CostStamina(50)) {
					if(items.size() >= 1) {
						items.get(0).remove();
						items.clear();
					}
					switch(new Random().nextInt(3)) {
					case 0:
						og.SendChatToAround(player, 10,"飛ぼうぜ？");
						break;
					case 1:
						og.SendChatToAround(player,10,"これこれこれこれ！！！");
						break;
					case 2:
						og.SendChatToAround(player, 10,"お前もやれよぉ");
						break;
					}
					ItemStack item = new ItemStack(Material.SUGAR);
					ItemMeta itemm = item.getItemMeta();
					itemm.setDisplayName("あれ");
					item.setItemMeta(itemm);
					Item drop = player.getWorld().dropItem(player.getLocation(), item);
					drop.setVelocity(player.getLocation().getDirection().multiply(2));
					items.add(drop);
				}
			}
	}


	@EventHandler
	public void PlayerPickupItem(PlayerPickupItemEvent e) {
		if(og.GetPlayers().contains(e.getPlayer())) {
			if(items.contains(e.getItem())) {
				e.setCancelled(true);
				e.getItem().remove();
				items.clear();
				OngkRunner ort = og.GetRunnerData(e.getPlayer());
				ort.TempSpeed(15, 200);
				ort.TempJump(15, 200);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
					public void run() {
						ort.AddStamina(-1000);
					}
				},200);
			}
		}
	}
}
