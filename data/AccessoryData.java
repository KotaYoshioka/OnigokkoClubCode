package data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AccessoryData {

	//アクセサリーの情報
	//[0]名前
	//[1]見た目
	//[2]説明文
	//[3]重み
	public static Object[][] accessory = {
			//0
			{"タッチシンドローム",Material.RED_DYE,"鬼が変わる度に自分以外の人のスタミナが25減る。",2},
			{"ハッピーデイ",Material.PINK_DYE,"ランダムで鬼が決定する際、絶対に自分は鬼にならない。",1},
			{"アンハッピーデイ",Material.BLACK_DYE,"ランダムで鬼が決定する際、絶対に自分は鬼になる。",1},
			{"ベクトルサーファー",Material.OAK_SLAB,"自分にかかるプッシュ効果が全て反対方向になる。",1},
			{"チェーンチェーサー",Material.IRON_BARS,"自分が鬼になった瞬間に、15秒間移動速度が2上昇する。",2},
			//5
			{"シックスセンス",Material.GLOWSTONE_DUST,"自分が鬼の時、残り時間60秒で全員光り出す。",2},
			{"バンドエイド",Material.OAK_PRESSURE_PLATE,"最初の周回で自分が鬼で時間切れになった時、ダメージが発生しない。",1},
			{"スーパーストレート",Material.ARROW,"自分が鬼から逃走者に変わった時、スタミナを50回復する。",2},
			{"エクスライフ",Material.LIME_DYE,"基礎移動速度と基礎ジャンプ力を1ずつ下げる。自分の基礎体力を1上げる。",3},
			{"デストロイヤー",Material.TNT,"最初の周回で自分が鬼で時間切れになった時、全員にダメージが発生する。",3},
			//10
			{"レム",Material.HEART_OF_THE_SEA,"立ち止まっている間のスタミナ回復量が上がる。",3},
			{"セーフティブロック",Material.BARRIER,"全逃走者、スニーク中にスタミナを回復できなくなる。",2},
			{"シャウト",Material.ENDER_EYE,"自分のスタミナが切れる度、全プレイヤー17秒発光する。",1},
			{"ミニマムファイティング",Material.WHEAT_SEEDS,"自分の体力が1の時、一時的に能力値が変動する効果は全て「+2」される。",3},
			{"リベンジ",Material.MAGENTA_DYE,"自分が鬼になった5秒後、近くに別のプレイヤーがいた場合、そのプレイヤーをタッチしたことになる。",2},
			//15
			{"ダブルタッチ",Material.GLOW_BERRIES,"タッチした相手にかかる盲目の時間は、通常よりも6秒長くなる。",1},
			{"シンカー",Material.ANVIL,"自分の基礎移動速度が2下がる。",-2}
	};
	
	public static ItemStack getAccessory(int id) {
		ItemStack item = new ItemStack((Material)accessory[id][1]);
		ItemMeta itemm = item.getItemMeta();
		itemm.setDisplayName(ChatColor.YELLOW + (String)accessory[id][0]);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GREEN + "重み：" + (int)accessory[id][3]);
		StringBuilder sb = new StringBuilder();
		char[] discription = ((String)accessory[id][2]).toCharArray();
		for(char c:discription) {
			sb.append(c);
			if(sb.toString().length() > 15) {
				lore.add(ChatColor.WHITE + sb.toString());
				sb = new StringBuilder();
			}
		}
		if(sb.toString().length() > 0) {
			lore.add(ChatColor.WHITE + sb.toString());
		}
		itemm.setLore(lore);
		item.setItemMeta(itemm);
		return item;
	}
	
	public static List<ItemStack> getAllAccessory(){
		List<ItemStack> access = new ArrayList<ItemStack>();
		for(int i = 0 ; i < accessory.length ; i++) {
			access.add(getAccessory(i));
		}
		return access;
	}
}
