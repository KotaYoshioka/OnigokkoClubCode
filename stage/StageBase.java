package stage;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import oc.OCGame;

public class StageBase {

	Plugin plugin;
	OCGame ocg;
	World world;
	boolean stopgimic = false;
	
	public StageBase(Plugin plugin,OCGame ocg,World world) {
		this.plugin = plugin;
		this.ocg = ocg;
		this.world = world;
	}
	
	public void stop() {
		stopgimic = true;
	}
}
