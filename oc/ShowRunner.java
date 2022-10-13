package oc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class ShowRunner implements Listener{

	Player player;
	Plugin plugin;
	World world;
	int number;
	Location current;
	boolean nowShow = false;
	
	public ShowRunner(Plugin plugin,Player player,World world,int number) {
		this.player = player;
		this.plugin = plugin;
		this.world = world;
		this.number = number;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void saveCurrentLocation() {
		if(!nowShow) {
			current = player.getLocation().clone();	
		}else {
			nowShow = !nowShow;
		}
	}
	
	public void cancelEvents() {
		HandlerList.unregisterAll(this);
	}
	
	/**
	 * 暗室にテレポート
	 */
	public void teleportBlackRoom() {
		Location[] ls = {new Vector(-8.5,31,-16.5).toLocation(world),new Vector(-8,31,34).toLocation(world),
				new Vector(17.2,31,-17.5).toLocation(world),new Vector(20.9,31,33.5).toLocation(world)};
		player.teleport(ls[number]);
	}
	
	public void teleportExLocation() {
		player.teleport(current);
	}
	
	public void teleportStage() {
		player.teleport(new Vector(8.5,8,8.5).toLocation(world));
	}
	
	public void nowShow(boolean show) {
		nowShow = show;
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() == player) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if(e.getPlayer() == player) {
			e.setCancelled(true);
		}
	}
}
