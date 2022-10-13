package ongk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class OngkCreater extends OngkRunner{

	List<Block> bs = new ArrayList<Block>();
	HashMap<Block,Integer> bi = new HashMap<Block,Integer>();
	int createrid;

	public OngkCreater(Plugin plugin, Player play, OngkGame og,int acce) {
		super(plugin, play, og,acce);
		Downtamina();
	}

	@Override
	public void RightClick(String itemName) {
		if(itemName.equals("ブロック設置")) {
			Location plo = player.getLocation();
			plo.add(0,-1,0);
			if(plo.getBlock().getType() == Material.AIR) {
				BlockPut(plo);
			}else {
				Block bb = player.getTargetBlock(null, 1).getLocation().getBlock();
				for(Block b : player.getLineOfSight((HashSet<Material>) null, 10)) {
					if(b.getType() == Material.AIR) {
						bb = b;
					}else {
						break;
					}
				}
				plo = bb.getLocation();
				if(plo != player.getLocation().getBlock().getLocation()) {
					BlockPut(plo);
				}
			}
		}else if(itemName.equals("ブロック破壊")) {
			ResetBlocks(true);
		}
	}

	/*
	public void BlockPut(Location lo) {
		lo.getBlock().setType(Material.OAK_WOOD);
		Block b = lo.getBlock();
		bs.add(b);
		final HashMap<Player,Integer> bpi = new HashMap<Player,Integer>();
		bpi.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				if(!CostStamina(1)) {
					lo.getBlock().setType(Material.AIR);
					Bukkit.getServer().getScheduler().cancelTask(bpi.get(player));
					bs.remove(b);
					bi.remove(b);
				}
			}
		}, 0, 3));
		bi.put(b, bpi.get(player));
	}
	*/

	public void BlockPut(Location lo) {
		lo.getBlock().setType(Material.OAK_WOOD);
		Block b = lo.getBlock();
		bs.add(b);
		blockput = true;
	}

	public void ResetBlocks(boolean t) {
		for(Block b:bs) {
			b.setType(Material.AIR);
			if(t)og.GetRunnerData(player).AddStamina(1);
		}
		bs.clear();
		blockput = false;
	}

	public void Downtamina() {
		createrid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin, new Runnable() {
			public void run() {
				if(bs.size() != 0) {
					if(!CostStamina(1)) {
						ResetBlocks(false);
					}
				}
			}
		}, 0, 2);
	}
}
