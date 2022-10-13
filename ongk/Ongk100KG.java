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

public class Ongk100KG extends OngkRunner{

	List<Item> items = new ArrayList<Item>();

	public Ongk100KG(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
	}

	@Override
	public void CancelEvents() {
		super.CancelEvents();
		PlayerPickupItemEvent.getHandlerList().unregister(this);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("食料トス")) {
			if(!waza[0]) {
				if(CostStamina(15)) {
					ItemStack syokuryo = new ItemStack(Material.COOKED_BEEF);
					ItemMeta syokuryom = syokuryo.getItemMeta();
					syokuryom.setDisplayName("食料");
					syokuryo.setItemMeta(syokuryom);
					Item s = player.getWorld().dropItem(player.getLocation(), syokuryo);
					s.setVelocity(player.getLocation().getDirection());
					items.add(s);
				}
			}
		}else if(itemName.equals("体質変化")) {
			if(!waza[1]) {
				if(CostStamina(25)) {
					waza[1] = true;
					switch(new Random().nextInt(3)) {
					case 0:
						og.SendChatToAround(player,10,"少しは痩せれたかなぁ？");
						break;
					case 1:
						og.SendChatToAround(player,10,"なんか軽くなった気がするよ！");
						break;
					case 2:
						og.SendChatToAround(player, 10,"えっほ！えっほ！");
						break;
					}
					if(stamina <= 20) {
						TempSetMaxStamina(20,200);
						TempSpeed(5,200);
						TempJump(5,200);
					}else if(stamina <= 40) {
						TempSetMaxStamina(40,200);
						TempSpeed(4,200);
						TempJump(4,200);
					}else if(stamina <= 60) {
						TempSetMaxStamina(60,200);
						TempSpeed(3,200);
						TempJump(3,200);
					}else if(stamina <= 80) {
						TempSetMaxStamina(80,200);
						TempSpeed(2,200);
						TempJump(2,200);
					}else if(stamina <= 100) {
						TempSetMaxStamina(100,200);
						TempSpeed(1,200);
						TempJump(1,200);
					}
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							waza[1] = false;
 						}
					},200);
				}
			}
		}
	}

	@EventHandler
	public void PlayerPickupItem(PlayerPickupItemEvent e) {
		if(og.GetPlayers().contains(e.getPlayer())) {
			if(e.getItem().getItemStack().getItemMeta().getDisplayName().equals("食料")) {
				if(items.contains(e.getItem())) {
					e.setCancelled(true);
					e.getItem().remove();
					og.GetRunnerData(e.getPlayer()).AddStamina(10);
					if(e.getPlayer() != player) {
						og.GetRunnerData(e.getPlayer()).TempSpeed(-3, 200);
						og.GetRunnerData(e.getPlayer()).TempJump(-1, 200);
					}
					items.remove(e.getItem());
				}
			}
		}
	}

}
