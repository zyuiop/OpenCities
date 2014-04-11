package fr.zgalaxy.ATCVilles.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.zgalaxy.ATCVilles.ATCVilles;

public class CommandLoad implements CommandExecutor {

	private ATCVilles plugin = null;
	
	public CommandLoad(ATCVilles pl) {
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
