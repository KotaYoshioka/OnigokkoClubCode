package ongk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class OngkLefthand extends OngkRunner{

	public OngkLefthand(Plugin plugin, Player play, OngkGame og, int acce) {
		super(plugin, play, og, acce);
	}

	@Override
	public void CancelEvents() {
		super.CancelEvents();
		PlayerPickupItemEvent.getHandlerList().unregister(this);
		InventoryClickEvent.getHandlerList().unregister(this);
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("オーバータッチ")) {
			if(!waza[0]) {
				if(og.GetOni() == player && og.kyusen == false) {
					if(CostStamina(10)) {
						List<Player> ps = new ArrayList<Player>();
						for(Entity ent:player.getNearbyEntities(3, 3, 3)) {
							if(og.GetLivingPlayers().contains(ent) && ent != player) {
								Player tar = (Player)ent;
								ps.add(tar);
							}
						}
						if(ps.size() >= 1) {
							double distance = 10000;
							Player target = null;
							for(Player p:ps) {
								double nd = player.getLocation().distance(p.getLocation());
								if(distance > nd) {
									target = p;
									distance = nd;
								}
							}
							if(target != null) {
								og.SetOni(target);
							}
						}
					}
				}
			}
		}else 	if(itemName.equals("左手にも")) {
			if(!waza[1]) {
				if(CostStamina(10)) {
					Inventory inv = Bukkit.createInventory(null, 36,"アクセサリー一覧");
					//List<Boolean> pos = DataBases.Getongkdb().GetPossessPlayerAccesoryList(player);
					/*
					for(int i = 0 ; i < OngkData.accesory.length;i++) {
						switch(i) {
						case 2:
						case 13:
						case 15:
						case 20:
						case 21:
							continue;
						}
						ItemStack item = new ItemStack((Material)OngkData.accesory[i][1]);
						ItemMeta itemm = item.getItemMeta();
						itemm.setDisplayName((String)OngkData.accesory[i][0]);
						itemm.setLore(CarpediemMain.MakeDescription((String)OngkData.accesory[i][2], ChatColor.WHITE));
						item.setItemMeta(itemm);
						if(!og.debug && !pos.get(i)) {
							continue;
						}
						inv.setItem(i, item);
					}
					*/
					//DataBases.Getcdb().SetInventoryNumber(player, 125);
					player.openInventory(inv);
				}
			}
		}
	}

	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player player = (Player)e.getWhoClicked();
		if(player != this.player) {
			return;
		}
		if(item == null) {
			return;
		}
		if(!item.hasItemMeta()) {
			return;
		}
		//int in = DataBases.Getcdb().GetInventoryNumber(player);
		int index = e.getRawSlot();
		//DataBases.Getcdb().SetInventoryNumber(player, 0);
		player.closeInventory();
		/*
		if(in == 125) {
			player.sendMessage(OnigokkoMain.OngkText((String)OngkData.accesory[index][0] + "を選択しました。"));
			acce2 = index;
		}
		*/
	}
}
