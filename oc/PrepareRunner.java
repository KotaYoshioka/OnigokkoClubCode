package oc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import data.AccessoryData;
import data.RunnerData;

public class PrepareRunner implements Listener{
	Plugin plugin;
	Player player;
	OCGame ocg;
	World world;
	//現在のフェーズを表すもの
	//[0]最初の待機室
	//[1]廊下
	int phase = 0;
	int room = 0;
	//再び入ろうとするときの重複排除用
	boolean delay = false;
	//現在、確認画面にいるかどうか
	boolean nowopen = false;
	//現在、アクセサリーを選んでいるか
	boolean accessory = false;
	List<Integer> selectedAcce = new ArrayList<Integer>();
	int omomi = 0;
	//現在、ランナーを選んでいるか
	boolean runner = false;
	int selectedRunner = 0;
	
	
	public PrepareRunner(Plugin plugin,Player player,OCGame ocg,int roomnumber,World world) {
		this.plugin = plugin;
		this.player = player;
		this.room = roomnumber;
		this.ocg = ocg;
		this.world = world;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void cancelEvents() {
		HandlerList.unregisterAll(this);
	}
	
	/**
	 * 確認画面の表示
	 */
	void openConfirm(String question) {
		Inventory inv = Bukkit.createInventory(null, 9,question);
		ItemStack ok = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemMeta okm = ok.getItemMeta();
		okm.setDisplayName("OK");
		ok.setItemMeta(okm);
		ItemStack no = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta nom = no.getItemMeta();
		nom.setDisplayName("NO");
		no.setItemMeta(nom);
		inv.setItem(2, ok);
		inv.setItem(6, no);
		openInventory(inv);
	}
	
	void scoreboard() {
		List<Player> players = ocg.getPlayers();
		ScoreboardManager sm = Bukkit.getScoreboardManager();
		Scoreboard sb = sm.getNewScoreboard();
		Objective o = sb.registerNewObjective("sb", "dummy", ChatColor.GRAY + "鬼ごっこ倶楽部");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score blank = o.getScore("                       ");
		blank.setScore(players.size() + 4);
		Score labelm = o.getScore(ChatColor.RED + "<現在の選択>");
		labelm.setScore(players.size() + 3);
		Score runner = o.getScore(ChatColor.RED  + RunnerData.runnerName[selectedRunner][0]);
		runner.setScore(players.size() + 2);
		for(int acc : selectedAcce) {
			Score acce = o.getScore(ChatColor.RED  + ((String)AccessoryData.accessory[acc][0]));
			acce.setScore(players.size() + 1);
		}
		Score label = o.getScore("<各プレイヤーの状態>");
		label.setScore(players.size());
		for(int i = 0 ; i < players.size() ;i++) {
			Player player = players.get(i);
			boolean check = ocg.getPrepare(player).getPrepare();
			Score playerdata = o.getScore((check?ChatColor.GREEN:ChatColor.GRAY) + player.getDisplayName() + "：" + (check?"完了":"未完了"));
			playerdata.setScore(i);
		}
		player.setScoreboard(sb);
	}
	
	void openInventory(Inventory inv) {
		player.openInventory(inv);
		nowopen = true;
	}
	
	/**
	 * 廊下にテレポートさせる
	 */
	void teleport() {
		if(phase == 0) {
			player.getInventory().clear();
			for(ItemStack item:RunnerData.getAbilities(selectedRunner,true)) {
				player.getInventory().addItem(item);
			}
		}else {
			player.getInventory().clear();
			ItemStack runner = new ItemStack(Material.CYAN_DYE);
			ItemMeta runnerm = runner.getItemMeta();
			runnerm.setDisplayName("ランナー選択");
			runner.setItemMeta(runnerm);
			ItemStack acce = new ItemStack(Material.RED_DYE);
			ItemMeta accem = acce.getItemMeta();
			accem.setDisplayName("アクセサリー選択");
			acce.setItemMeta(accem);
			player.getInventory().addItem(runner,acce);
		}
		Location[][] ls = {{new Vector(-32,1,3).toLocation(world),new Vector(-32,1,13).toLocation(world),
				new Vector(50,1,13).toLocation(world),new Vector(-32,1,13).toLocation(world)},
				{new Vector(-45,1,0).toLocation(world),new Vector(-45,1,12).toLocation(world),
					new Vector(62,1,15).toLocation(world),new Vector(62,1,5).toLocation(world)}};
		Vector[] r = {new Vector(1,0,0),new Vector(1,0,0),new Vector(-1,0,0),new Vector(-1,0,0)};
		Location l = ls[phase][room].setDirection(r[room]);
		player.teleport(l);
		phase = phase==0?1:0;
		ocg.changePrepare(phase==1);
		player.closeInventory();
		delay = false;
	}
	
	void openList(boolean runner) {
		Inventory inv = Bukkit.createInventory(null, 54,runner?"ランナー一覧":"アクセサリー一覧:残り"+(3-omomi));
		accessory = !runner;
		int counter = 0;
		for(ItemStack item:runner?RunnerData.getAllRunnerLook():AccessoryData.getAllAccessory()) {
			if(!runner && (int)AccessoryData.accessory[counter][3] > (3-omomi) || selectedAcce.contains(counter)) {
				counter++;
				continue;
			}
			inv.setItem(counter, item);
			counter++;
		}
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.getPlayer() == player) {
			int[][] wth = {{-38,-38,55,55},{-35,-35,52,52}};
			if((phase==0?1:-1) * (room<2?1:-1) * player.getLocation().getX() > (phase==0?1:-1) * (room<2?1:-1) * wth[phase][room] && !nowopen && !delay) {
				openConfirm(phase==0?"準備完了ですか？":"待機室に戻りますか？");
			}else if(delay && (phase==0?1:-1) * player.getLocation().getX() < (phase==0?1:-1) * wth[phase][room]) {
				delay = false;
			}
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if(e.getPlayer() == player) {
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				e.setCancelled(true);
				if(phase == 0) {
					Bukkit.getLogger().info("aa");
					if(player.getInventory().getHeldItemSlot() == 0) {
						openList(true);
						runner = true;
					}else if(player.getInventory().getHeldItemSlot() == 1) {
						omomi = 0;
						this.selectedAcce.clear();
						openList(false);
						accessory = true;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(e.getPlayer() == player) {
			nowopen = false;
			accessory = false;
			runner = false;
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getWhoClicked() == player) {
			e.setCancelled(true);
			if(nowopen) {
				Bukkit.getLogger().info("b");
				if(e.getRawSlot()  == 2) {
					teleport();
				}else if(e.getRawSlot() == 6) {
					delay = true;
					player.closeInventory();
				}
			}else if(e.getCurrentItem() != null) {
				if(e.getCurrentItem().getType() != Material.AIR) {
					if(runner) {
						Bukkit.getLogger().info("d");
						selectedRunner = e.getSlot();
						player.closeInventory();
						scoreboard();
					}else if(accessory) {
						int nowomomi = (int)AccessoryData.accessory[e.getRawSlot()][3];
						omomi += nowomomi;
						selectedAcce.add(e.getSlot());
						if(omomi < 3) {
							player.closeInventory();
							openList(false);
						}else {
							player.closeInventory();	
						}
						scoreboard();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() == player) {
			e.setCancelled(true);
		}
	}
	
	/**
	 * 準備完了かどうか
	 */
	boolean getPrepare() {
		return phase==1;
	}
	
	Object[] getRunnerAndAcce() {
		Object[] is = {selectedRunner,selectedAcce};
		return is;
	}
}
