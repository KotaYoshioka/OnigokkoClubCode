package ongk;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class OngkCommand implements CommandExecutor {

	OngkGame game;
	static Plugin mainPlugin;
	OngkRunner or;

	public OngkCommand(Plugin plugin) {
		mainPlugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(args[0].equals("newgame")) {
				game = new OngkGame(mainPlugin);
			}else if(args[0].equals("debug")) {
				game.SetDebug(true);
			}else if(args[0].equals("join")) {
				if(args[1].equals("@a")) {
					for(Player p:player.getWorld().getPlayers()) {
						game.Join(p);
					}
				}else {
					Player target = Bukkit.getPlayer(args[1]);
					game.Join(target);
				}
			}else if(args[0].equals("start")) {
				game.Start();
			}else if(args[0].equals("qstart")) {
				for(Player p:game.GetPlayers()) {
					if(p.getDisplayName().equals("takosu25")) {
						game.SetRunner(p, Integer.parseInt(args[1]));
					}else {
						//game.SetRunner(p, new Random().nextInt(OngkData.runner.length));
					}
				}
				game.SetStart();
			}else if(args[0].equals("qqstart")) {
				for(Player p:game.GetPlayers()) {
					//game.SetRunner(p, new Random().nextInt(OngkData.runner.length));
				}
				game.SetStart();
			}else if(args[0].equals("lighton")) {
				if(args[1].equals("t")) {
					new OngkStageEffect(mainPlugin,player.getWorld()).LightOn(true);
				}else {
					new OngkStageEffect(mainPlugin,player.getWorld()).LightOn(false);
				}
			}else if(args[0].equals("lightoff")) {
				if(args[1].equals("t")) {
					new OngkStageEffect(mainPlugin,player.getWorld()).LightOff(true);
				}else {
					new OngkStageEffect(mainPlugin,player.getWorld()).LightOff(false);
				}
			}else if(args[0].equals("test")) {
				if(args[1].equals("s")) {
					player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(Integer.parseInt(args[2]));
				}else if(args[1].equals("s")) {
					for(PotionEffect pe:player.getActivePotionEffects()) {
						player.removePotionEffect(pe.getType());
					}
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,10000,Integer.parseInt(args[2])));
				}
			}else if(args[0].equals("sss")) {
				or.CancelEvents();
			}else if(args[0].equals("gachacoin")) {
				//DataBases.Getcdb().AddCoin(player, 1500);
				//CarpediemMain.OpenMainScoreBoard(player);
			}else if(args[0].equals("present")) {
				Player target = Bukkit.getServer().getPlayer(args[1]);
				//DataBases.Getcdb().GotchaPlayerPossess(target, 1, Integer.parseInt(args[2]));
				//target.sendMessage(player.getDisplayName() + "から" + (String)CarpediemData.teleports[Integer.parseInt(args[2])][0] + "のプレゼントをもらったよ！");
			}else if(args[0].equals("ddd")) {
				World world = player.getLocation().getWorld();
				Location lo = new Vector(-32,Integer.parseInt(args[1]),16).toLocation(world);
				StringBuilder sb = new StringBuilder();
				for(int i = 0 ; i < 32 ; i++) {
					for(int j = 0 ; j < 32 ; j++) {
						Location clo = lo.clone();
						clo.add(i, 0, j);
						clo.getBlock().setType(Material.IRON_BLOCK);
						sb.append(clo.getBlock().getType().toString() + ",");
					}
					Bukkit.getServer().getConsoleSender().sendMessage(sb.toString());
					sb = new StringBuilder();
				}
			}else if(args[0].equals("st")) {
				Admission(player,Integer.parseInt(args[1]));
			}else if(args[0].equals("q")) {
				player.sendMessage(player.getLocation().getDirection().getX() + "");
				player.sendMessage(player.getLocation().getDirection().getY() + "");
				player.sendMessage(player.getLocation().getDirection().getZ() + "");
			}
		}
		return true;
	}

	public static void Admission(Player player,int type) {
		World world = Bukkit.getWorld("OngkClub");
		Location[] blo = new Location[2];
		Location startlo = null;
		boolean add = false;
		switch(type) {
		case 0:
			blo[0] = new Vector(27.5,2,14.5).toLocation(world);
			blo[1] = new Vector(27.5,2,13.5).toLocation(world);
			startlo = new Vector(25.5,5,14).toLocation(world);
			break;
		case 1:
			blo[0] = new Vector(27.5,2,3.5).toLocation(world);
			blo[1] = new Vector(27.5,2,2.5).toLocation(world);
			startlo = new Vector(25.5,5,3).toLocation(world);
			break;
		case 2:
			blo[0] = new Vector(-10.5,2,13.5).toLocation(world);
			blo[1] = new Vector(-10.5,2,14.5).toLocation(world);
			startlo = new Vector(-8.5,5,14).toLocation(world);
			add = true;
			break;
		case 3:
			blo[0] = new Vector(-10.5,2,2.5).toLocation(world);
			blo[1] = new Vector(-10.5,2,3.5).toLocation(world);
			startlo = new Vector(-8.5,5,3).toLocation(world);
			add = true;
			break;
		}
		blo[0].getBlock().setType(Material.AIR);
		blo[1].getBlock().setType(Material.AIR);
		final Location sl = startlo;
		final boolean a = add;
		new BukkitRunnable() {
			int progress = 0;
			public void run() {
				Location lo = new Vector(sl.getX()  + ((a?1:-1) * (0.1d * progress)),sl.getY(),sl.getZ()).toLocation(world);
				if(type < 2) {
					lo.setDirection(new Vector(-1,0,0));
				}else {
					lo.setDirection(new Vector(1,0,0));
				}
				player.teleport(lo);
				if(progress >= 40) {
					this.cancel();
					blo[0].getBlock().setType(Material.REDSTONE_BLOCK);
					blo[1].getBlock().setType(Material.REDSTONE_BLOCK);
				}
				progress++;
			}
		}.runTaskTimer(mainPlugin, 0, 1);
	}
}
