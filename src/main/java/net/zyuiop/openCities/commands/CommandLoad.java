package net.zyuiop.openCities.commands;

import net.zyuiop.openCities.OpenCities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandLoad implements CommandExecutor {

	private OpenCities plugin = null;
	
	public CommandLoad(OpenCities pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		// TODO Auto-generated method stub
		// Interprete le /atcsave
		sender.sendMessage("Chargement des villes...");
		plugin.resetVilles();
		sender.sendMessage("Chargement terminé !");
		
		return false;
	}

}
