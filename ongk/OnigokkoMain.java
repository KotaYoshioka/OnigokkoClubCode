package ongk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;


public class OnigokkoMain extends JavaPlugin{

	@Override
	public void onEnable() {
		getCommand("ongk").setExecutor(new OngkCommand(this));
	}

	public static String OngkText(String text) {
		return ChatColor.DARK_RED + "[鬼ごっこ倶楽部]" + text;
	}

	public static List<String> MakeDescription(String s,int syohi){
		List<String> ls = new ArrayList<String>();
		ls.add(ChatColor.YELLOW + "消費スタミナ：" + syohi);
		StringBuilder sb = new StringBuilder();
		char[] cs = s.toCharArray();
		for(char c:cs) {
			sb.append(c);
			if(sb.length() >= 15) {
				ls.add(ChatColor.WHITE + sb.toString());
				sb = new StringBuilder();
			}
		}
		if(sb.length() >= 1) {
			ls.add(ChatColor.WHITE + sb.toString());
		}
		return ls;
	}

	public static List<String> MakeDescription(String theme,String ippan,String senmon){
		List<String> ls = new ArrayList<String>();
		ls.add(ChatColor.YELLOW + "テーマ" + theme);
		StringBuilder sb = new StringBuilder();
		char[] cs = ippan.toCharArray();
		for(char c:cs) {
			sb.append(c);
			if(sb.length() >= 15) {
				ls.add(ChatColor.WHITE + sb.toString());
				sb = new StringBuilder();
			}
		}
		if(sb.length() >= 1) {
			ls.add(ChatColor.WHITE + sb.toString());
		}
		ls.add(ChatColor.YELLOW + "<テーマ効果>");
		sb = new StringBuilder();
		char[] cs2 = senmon.toCharArray();
		for(char c:cs2) {
			sb.append(c);
			if(sb.length() >= 15) {
				ls.add(ChatColor.YELLOW + sb.toString());
				sb = new StringBuilder();
			}
		}
		if(sb.length() >= 1) {
			ls.add(ChatColor.YELLOW + sb.toString());
		}
		return ls;
	}
}
