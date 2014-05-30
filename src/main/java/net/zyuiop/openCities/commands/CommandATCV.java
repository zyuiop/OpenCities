package net.zyuiop.openCities.commands;

import net.zyuiop.openCities.OpenCities;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandATCV implements CommandExecutor {

	private OpenCities plugin = null;
	
	public CommandATCV(OpenCities pl) {
		plugin = pl;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 0) {
			return false;
		}
		
		if (args[0].equals("help")) {
			sender.sendMessage("----------------"+plugin.TAG+"----------------");
			sender.sendMessage(ChatColor.AQUA+"Aide générale ATCVilles");
			sender.sendMessage("Vous ne pouvez pas placer ou casser sur la map principale.");
			sender.sendMessage("Commandes de ville : /ville help");
			sender.sendMessage("Commandes de parcelles : /parcelle help");
			
			return true;
		}
		
		return false;
	}

}
