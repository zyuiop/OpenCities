package net.zyuiop.openCities.utils;

import org.bukkit.ChatColor;

public class TextUtils {
	public static final String FormatHelp(String command, String params, String usage) {
		return "/"+ChatColor.GOLD+command+" "+ChatColor.YELLOW+params+ChatColor.RESET+" : "+ChatColor.WHITE+usage;
	}
}
