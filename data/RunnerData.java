package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class RunnerData {

	//ランナーの名前
	//[0]正式表記
	//[1]読み方
	public static String[][] runnerName = {
			{"SpeeedBoy","スピードボーイ"},
			{"[   ]","アンノウン"},
			{"F→T","フィート"},
			{"KABOOOOM!","ボム"},
			{"□","ストーン"},
			{"100/100","パーフェクト"},
			{"♡☆彡♡","アイドル"}
	};
	
	public static String[] runnerOneDiscription = {
		"超高速","姿を消す","テレポート","爆弾","地形変動","鬼じゃなければ","メロメロでイチコロ"
	};
	
	public static Material[] runnerLooks = {
			Material.LEATHER_BOOTS,Material.BARRIER,Material.ENDER_PEARL,Material.TNT,Material.STONE,Material.GOLD_INGOT,Material.HONEYCOMB
	};
	
	//紹介文
	//[0]一言紹介(複数)
	//[1]紹介文(複数)
	public static String[][][] runnerdis = {
			//SpeedBoy
			{
				{"期待の超新星","目にも止まらぬ速さ","かけっこ大好き少年"},
				{"とにかくすばしっこいぜ！鬼ごっこ向きだよ！ほんと！","誰よりも足が速いんだってよ！こいつ勝ちじゃん！"}
			},
			//[   ]
			{
				{"誰にも見えない","影からの一撃","鬼ごっこ？いや、かくれんぼ"},
				{"あれ、来てる？俺、見えないんだけど...","名前くらい言えよ！失礼だな！"}
			},
			//F→T
			{
				{"テレポートの使い手","そこにはもういない"},
				{"テレポート！？チートじゃねぇかよ！","俺もテレポート使えたらな...帰宅、楽なのに..."}
			},
			//KABOOOOM!
			{
				{"爆薬製造おじさん","全ての爆発の生みの親"},
				{"鬼ごっこだぞ...爆弾って...","爆弾使うってクレイジーだな！...ってかアホだろ...?"}
			},
			//□
			{
				{"石の塊"},
				{"石ってお前...もう人でもないじゃん..."}
			},
			//100/100
			{
				{"完璧を極めた男"},
				{"とんでもなくプライドが高いらしい！ﾒﾝﾄﾞｸｻ..."}
			},
			//♡☆彡♡
			{
				{"みんなをメロメロに"},
				{"意外に図太いらしいぞ！アイドルって大変なんだな...","...あとでサイン貰おう"}
			}
	};
	
	//ステータス
	//[0]初期HP(基準:2)
	//[1]移動速度(基準:3)
	//[2]ジャンプ力(基準:3)
	//[3]スタミナ(基準:3)
	//[4]回復速度(基準:3)
	public static int[][] status = {
			//SpeeedBoy
			{2,5,2,3,1},
			//[  ]
			{2,3,3,2,2},
			//F→T
			{2,2,3,3,4},
			//KABOOOOM!
			{2,4,3,4,3},
			//□
			{2,2,2,3,4},
			//100/100
			{2,3,3,3,3},
			//♡☆彡♡
			{2,4,4,5,3}
	};
	
	//技の名前
	public static String[][][] ability = {
			//SpeeedBoy
			{
				{"スーパースピード","一瞬だけ、とてつもない速さになる。"},
				{"ヴェクターショット","向いている方向に思いっきり吹き飛ぶ。"},
				{"ソニックブーム","一定時間、自分が走っているとき、近くにいる敵は吹き飛ぶ。"}
			},
			//[   ]
			{
				{"インビジブル","一定時間、完全透明になり、移動速度とジャンプ力が上昇する。"},
				{"バーティゴ","全プレイヤー、自分が向いている方向に向くと同時に盲目になる。"},
				{"アノニマスワールド","自分含めた全プレイヤーは、一定時間、技が一切使用できなくなる。"}
			},
			//F→T
			{
				{"テレポート","向いている方向にテレポートする。"},
				{"シャッフル","自分を含め、全プレイヤーの位置をランダムに交換する。"},
				{"リロード","発動から3秒後、発動した場所にテレポートする。鬼の時、対象は自分ではなく自分以外のプレイヤーになる。"}
			},
			//KABOOOM!
			{
				{"ボムショット","爆弾を設置する。一定時間後に爆破し、近くのプレイヤーは吹き飛ぶ。スニークして使用する場合、逆に引き寄せる。"},
				{"リモートボム","発動すると、その場に自分しか見えない煙が発生する。もう一度使用すると、その煙の位置から爆発が発生する。"},
				{"チェイサー","TNTを積んだトロッコを走らせ、最寄の敵を追いかける。トロッコの近くに敵がいる時、トロッコが爆発する。また、一定時間経過することでも爆発する。"}
			},
			//□
			{
				{"パイルアップ","向いている場所に目がけて、地面から石が伸びてくる。"},
				{"フリースロー","石を投げる。ぶつかった相手の移動速度を一時的に低下させる。"},
				{"クラッシュピアス","向いている方向に石のつぶてを飛ばす。ぶつかった相手を進行方向に吹き飛ばす。"}
			},
			//100/100
			{
				{"パーフェクトジャンプ","向いている方向に軽く飛ぶ。鬼の時、逆の方向に飛ぶ。"},
				{"パーフェクトインパクト","近くにいる敵を吹き飛ばす。鬼の時、逆に引き寄せる。"},
				{"オールパーフェクト","一定時間、自由に空を飛べる。鬼の時、空は飛べないが、代わりにスタミナを即時回復する。"}
			},
			//♡☆彡♡
			{
				{"ラブビーム","ハートの光線を飛ばし、当たった相手をメロメロ状態にする。"},
				{"ファンミート","メロメロ状態の敵が何らかの不利益を起こす。"},
				{"応援パワー","メロメロ状態の各敵から、現在の半分の量のスタミナを吸収する。"}
			}
	};
	//技の消費
	public static int[][] abilitysp = {
			//SpeedBoy
			{17,52,65},
			//[  ]
			{46,43,50},
			//F→T
			{15,55,30},
			//KABOOOOM!
			{13,30,55},
			//□
			{15,20,15},
			//100/100
			{15,35,50},
			//♡☆彡♡
			{15,35,60}
	};
	//3つ目の技の開放タイミング
	public static int[] thirdOpen = {
			4,4,4,4,3,7,4
	};
	//技の見た目
	public static Material[][] abilitylooks = {
			//SpeedBoy
			{Material.STICK,Material.NETHER_STAR,Material.ECHO_SHARD},
			//[   ]
			{Material.STICK,Material.NETHER_STAR,Material.ECHO_SHARD},
			//F→T
			{Material.STICK,Material.NETHER_STAR,Material.ECHO_SHARD},
			//KABOOOOM!
			{Material.STICK,Material.NETHER_STAR,Material.ECHO_SHARD},
			//□
			{Material.STICK,Material.NETHER_STAR,Material.ECHO_SHARD},
			//100/100
			{Material.STICK,Material.NETHER_STAR,Material.ECHO_SHARD},
			//♡☆彡♡
			{Material.STICK,Material.NETHER_STAR,Material.ECHO_SHARD}
	};
	//カスタム
	public static String[][][][] customs = {
		//SpeeeedBoy
		{
			//スーパースピード
			{
				{"",""}
			}
		}
	};
	
	static ChatColor getStatusColor(int status) {
		switch(status) {
		case 0:
			return ChatColor.GRAY;
		case 1:
			return ChatColor.AQUA;
		case 2:
			return ChatColor.GREEN;
		case 3:
			return ChatColor.YELLOW;
		case 4:
			return ChatColor.RED;
		case 5:
			return ChatColor.LIGHT_PURPLE;
		default:
			return ChatColor.GOLD;
		}
	}

	public static ItemStack getRunnerLook(int id) {
		ItemStack item = new ItemStack(runnerLooks[id]);
		ItemMeta itemm = item.getItemMeta();
		itemm.setDisplayName(runnerName[id][0]);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "読み方：" + runnerName[id][1]);
		lore.add(ChatColor.WHITE + "\"" + runnerOneDiscription[id] + "\"");
		lore.add(ChatColor.YELLOW + "<ステータス>");
		String[] sts = {"基礎体力","スピード","ジャンプ","スタミナ","回復速度"};
		for(int i = 0 ; i < sts.length ; i++) {
			String s = sts[i];
			lore.add(ChatColor.WHITE + s  + "：" + getStatusColor(status[id][i]) + status[id][i]);
		}
		itemm.setLore(lore);
		item.setItemMeta(itemm);
		return item;
	}
	
	public static List<ItemStack> getAllRunnerLook(){
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(int i = 0 ; i < runnerName.length ; i++) {
			if(i == 4) {
				items.add(new ItemStack(Material.AIR));
				continue;
			}
			items.add(getRunnerLook(i));
		}
		return items;
	}
	
	public static ItemStack getAbility(int id,int index) {
		ItemStack abi = new ItemStack(abilitylooks[id][index]);
		ItemMeta abilitym = abi.getItemMeta();
		abilitym.setDisplayName(ChatColor.YELLOW + ability[id][index][0] + "[" + abilitysp[id][index] + "]");
		List<String> lore = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		char[] cs = ability[id][index][1].toCharArray();
		for(char c:cs) {
			sb.append(c);
			if(sb.toString().length() > 15) {
				lore.add(ChatColor.WHITE + sb.toString());
				sb = new StringBuilder();
			}
		}
		if(sb.toString().length() > 0) {
			lore.add(ChatColor.WHITE + sb.toString());
		}
		abilitym.setLore(lore);
		abi.setItemMeta(abilitym);
		return abi;
	}
	
	public static ItemStack getClock(int id,int time) {
		ItemStack clock = new ItemStack(Material.CLOCK,thirdOpen[id]-time);
		ItemMeta clockm = clock.getItemMeta();
		clockm.setDisplayName(ChatColor.GRAY + ability[id][2][0] + "(使用不可)");
		clock.setItemMeta(clockm);
		return clock;
	}
	
	public static List<ItemStack> getAbilities(int id,boolean force){
		List<ItemStack> abilities = new ArrayList<ItemStack>();
		for(int i = 0 ; i < 2 ; i++) {
			abilities.add(getAbility(id,i));
		}
		if(force) {
			abilities.add(getAbility(id,2));
		}else {
			abilities.add(getClock(id,0));
		}
		return abilities;
	}
	
	public static String getRunnerOnePhrase(int id) {
		Random rnd = new Random();
		int rndm = rnd.nextInt(runnerdis[id][0].length);
		return runnerdis[id][0][rndm];
	}
	
	public static String getRunnerDiscription(int id) {
		Random rnd = new Random();
		int rndm = rnd.nextInt(runnerdis[id][1].length);
		return runnerdis[id][1][rndm];
	}
	
}
