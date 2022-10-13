package ongk;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class OngkInventory implements Listener{
	Plugin mainPlugin;
	OngkGame og;
	boolean debug;
	HashMap<Player,Boolean> cools = new HashMap<Player,Boolean>();

	public OngkInventory(Plugin plugin,OngkGame ogg,boolean deb) {
		mainPlugin = plugin;
		og = ogg;
		debug = deb;
		for(Player p:og.GetPlayers()) {
			cools.put(p, true);
		}
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void CancelEvents() {
		InventoryClickEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
	}

	public void GiveSelectItems(Player player) {
		ItemStack runners = new ItemStack(Material.CYAN_DYE);
		ItemMeta runnersm = runners.getItemMeta();
		runnersm.setDisplayName("ランナー選択");
		runners.setItemMeta(runnersm);
		ItemStack accesorys = new ItemStack(Material.MAGENTA_DYE);
		ItemMeta accesorysm = accesorys.getItemMeta();
		accesorysm.setDisplayName("アクセサリー選択");
		accesorys.setItemMeta(accesorysm);
		ItemStack ok = new ItemStack(Material.GRAY_DYE);
		ItemMeta okm = ok.getItemMeta();
		okm.setDisplayName("準備完了");
		ok.setItemMeta(okm);
		player.getInventory().addItem(runners,accesorys,ok);
	}

	public void ShowRunners(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27,"ランナー一覧");
		String[] st = {"HP","SPD","JMP","STM","HEA"};
		//List<Boolean> posses = DataBases.Getongkdb().GetPossessPlayerRunnerList(player);
		/*
		for(int i = 0 ; i < OngkData.runner.length;i++) {
			boolean possess = posses.get(i);
			ItemStack item = new ItemStack(OngkData.ms[i]);
			ItemMeta itemm = item.getItemMeta();
			itemm.setDisplayName(OngkData.runner[i]);
			List<String> ls = new ArrayList<String>();
			ls.add(ChatColor.WHITE + "(" + OngkData.yomi[i] + ")");
			ls.add(ChatColor.GREEN + "-" + OngkData.cause[i]);
			ls.add(ChatColor.GRAY + "\"" + OngkData.dis[i] + "\"");
			if(OngkData.cause[i].equals("ゲリラランナー") && !possess && !debug) {
				ls.add(ChatColor.YELLOW + "ゲリラで使用可能！");
				ls.add(ChatColor.YELLOW + "このランナーを使用して勝利");
				ls.add(ChatColor.YELLOW + "するとゲット！");
			}
			for(int j = 0 ; j < st.length; j++) {
				ls.add(ChatColor.WHITE + st[j] + "：" + OngkData.states[i][j]);
			}
			itemm.setLore(ls);
			item.setItemMeta(itemm);
			if(!debug && !possess) {
				if(OngkData.cause[i].equals("ゲリラランナー")) {
					Random rnd = new Random();
					if(rnd.nextInt(70) >= 2) {
						continue;
					}
				}else if(!OngkData.cause[i].equals("初期ランナー")) {
					continue;
				}
			}
			inv.setItem(i, item);
		}
		*/
		player.openInventory(inv);
	}

	public void ShowAcces(Player player) {
		Inventory inv = Bukkit.createInventory(null, 36,"アクセサリー一覧");
		/*
		List<Boolean> pos = DataBases.Getongkdb().GetPossessPlayerAccesoryList(player);
		for(int i = 0 ; i < OngkData.accesory.length;i++) {
			ItemStack item = new ItemStack((Material)OngkData.accesory[i][1]);
			ItemMeta itemm = item.getItemMeta();
			itemm.setDisplayName((String)OngkData.accesory[i][0]);
			itemm.setLore(CarpediemMain.MakeDescription((String)OngkData.accesory[i][2], ChatColor.WHITE));
			item.setItemMeta(itemm);
			if(!debug && !pos.get(i)) {
				continue;
			}
			inv.setItem(i, item);
		}
		*/
		player.openInventory(inv);
	}

	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player player = (Player)e.getWhoClicked();
		if(item == null) {
			return;
		}
		if(!item.hasItemMeta()) {
			return;
		}
		String itemName = item.getItemMeta().getDisplayName();
		//int in = DataBases.Getcdb().GetInventoryNumber(player);
		int index = e.getRawSlot();
		/*
		if(in >= 500 && in <= 599) {
			switch(in) {
			case 500:
				og.SetRunner(player, index);
				DataBases.Getcdb().SetInventoryNumber(player, 0);
				player.closeInventory();
				player.sendMessage(OnigokkoMain.OngkText(OngkData.runner[index] + "を選択しました。"));
				break;
			case 501:
				og.acce.put(player, index);
				DataBases.Getcdb().SetInventoryNumber(player, 0);
				player.closeInventory();
				player.sendMessage(OnigokkoMain.OngkText((String)OngkData.accesory[index][0] + "を選択しました。"));
				break;
			}
		}
		*/
	}

	@EventHandler
	public void PlayerInteract(PlayerInteractEvent e) {
		if(og.GetPlayers().contains(e.getPlayer())) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Player player = e.getPlayer();
				ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
				if(item.getItemMeta().getDisplayName().equals("ランナー選択")) {
					if(og.allok.get(player)) {
						player.sendMessage(OnigokkoMain.OngkText("準備完了状態のため、選択画面を開くことができません。"));
					}else {
						ShowRunners(player);
						//DataBases.Getcdb().SetInventoryNumber(player, 500);
					}
				}else if(item.getItemMeta().getDisplayName().equals("アクセサリー選択")) {
					if(og.allok.get(player)) {
						player.sendMessage(OnigokkoMain.OngkText("準備完了状態のため、選択画面を開くことができません。"));
					}else {
						ShowAcces(player);
						//DataBases.Getcdb().SetInventoryNumber(player, 501);
					}
				}else if(item.getItemMeta().getDisplayName().equals("準備完了") && cools.get(player)) {
					cools.put(player, false);
					player.getInventory().remove(item);
					ItemStack wait = new ItemStack(Material.PINK_DYE);
					ItemMeta waitm = wait.getItemMeta();
					waitm.setDisplayName("ちょっと待った");
					wait.setItemMeta(waitm);
					player.getInventory().addItem(wait);
					og.allok.put(player,true);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							cools.put(player, true);
						}
					},5);
				}else if(item.getItemMeta().getDisplayName().equals("ちょっと待った") && cools.get(player)) {
					cools.put(player, false);
					player.getInventory().remove(item);
					ItemStack ok = new ItemStack(Material.GRAY_DYE);
					ItemMeta okm = ok.getItemMeta();
					okm.setDisplayName("準備完了");
					ok.setItemMeta(okm);
					player.getInventory().addItem(ok);
					og.allok.put(player,false);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainPlugin, new Runnable() {
						public void run() {
							cools.put(player, true);
						}
					},5);
				}
			}
		}
	}
}
