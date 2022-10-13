package ongk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class OngkRunnerSelect {
	public static void ShowRunnerChooser(Player player) {
		Inventory inv = Bukkit.createInventory(null, 9,"ランナー選択");
		/*
		for(int i = 0 ; i < OngkData.runner.length ; i++) {
			ItemStack runner = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
			ItemMeta runnerm = runner.getItemMeta();
			runnerm.setDisplayName(OngkData.runner[i]);
			List<String> runnerlore = new ArrayList<String>();
			runnerlore.add(OngkData.yomi[i]);
			runnerm.setLore(runnerlore);
			runner.setItemMeta(runnerm);
			inv.setItem(i, runner);
		}
		*/
		player.openInventory(inv);
	}

	public static OngkRunner GiveRunnerSet(Plugin mainPlugin,Player player, OngkGame game,int id,int acce) {
		switch(id) {
		case 0:
			return new OngkSpeeeedBoy(mainPlugin,player,game,acce);
		case 1:
			return new Ongk100KG(mainPlugin,player,game,acce);
		case 2:
			return new OngkUnknown(mainPlugin,player,game,acce);
		case 3:
			return new OngkFeet(mainPlugin,player,game,acce);
		case 4:
			return new OngkHenya(mainPlugin,player,game,acce);
		case 5:
			return new OngkTrain(mainPlugin,player,game,acce);
		case 6:
			return new OngkPlusone(mainPlugin,player,game,acce);
		case 7:
			return new OngkVampire(mainPlugin,player,game,acce);
		case 8:
			return new OngkCreater(mainPlugin,player,game,acce);
		case 9:
			return new OngkBomb(mainPlugin,player,game,acce);
		case 10:
			return new OngkSteal(mainPlugin,player,game,acce);
		case 11:
			return new OngkSearch(mainPlugin,player,game,acce);
		case 12:
			return new OngkLefthand(mainPlugin,player,game,acce);
		case 13:
			return new Ongkzzz(mainPlugin,player,game,acce);
		}
		return null;
	}

	public static int StaminaChanger(int i) {
		return 60 + (20 * (i - 1));
	}
}
