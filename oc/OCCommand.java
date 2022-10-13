package oc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class OCCommand implements CommandExecutor {

	Plugin plugin;
	
	OCGame ocg;
	
	ArmorStand as;
	
	public OCCommand(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(args[0].equals("newgame")) {
				ocg = new OCGame(plugin);
			}else if(args[0].equals("join")) {
				List<Player> players = new ArrayList<Player>();
				for(Player p:Bukkit.getOnlinePlayers()) {
					players.add(p);
				}
				Collections.shuffle(players);
				for(Player p :players) {
					ocg.join(p);
				}
			}else if(args[0].equals("start")) {
				ocg.prepare();
			}else if(args[0].equals("rotate")) {
				Vector v = player.getLocation().getDirection();
				player.sendMessage("x:" + v.getX() + ",y:" + v.getY() + ",z:" + v.getZ());
			}else if(args[0].equals("info")) {
				Location l = player.getLocation().clone();
				l.add(0,-1,0);
				as = (ArmorStand)player.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
				if(args[1].equals("1")) {
					
					as.setCustomName(ChatColor.GREEN + "準備完了");
				}else if(args[1].equals("2")) {
					as.setCustomName(ChatColor.GREEN + "待機室へ戻る");
				}
				as.setCustomNameVisible(true);
				as.setGravity(false);
				as.setVisible(false);
				as.setRemoveWhenFarAway(false);
			}else if(args[0].equals("defo")) {
				as.remove();
			}
		}
		return true;
	}

}
