package oc;

import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin{

	@Override
	public void onEnable() {
		super.onEnable();
		getCommand("oc").setExecutor(new OCCommand(this));
	}
}
