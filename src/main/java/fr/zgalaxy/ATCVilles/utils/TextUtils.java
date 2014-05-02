package fr.zgalaxy.ATCVilles.utils;

import org.bukkit.ChatColor;

public class TextUtils {
	public static final String FormatHelp(String command, String params, String usage) {
		return "/"+ChatColor.AQUA+command+" "+ChatColor.BOLD+params+ChatColor.RESET+" : "+ChatColor.GREEN+usage;
	}
}
