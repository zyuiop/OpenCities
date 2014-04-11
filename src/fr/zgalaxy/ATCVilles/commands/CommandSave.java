package fr.zgalaxy.ATCVilles.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.zgalaxy.ATCVilles.ATCVilles;

public class CommandSave implements CommandExecutor {

	private ATCVilles plugin = null;
	
	public CommandSave(ATCVilles pl) {
		plugin = pl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		// TODO Auto-generated method stub
		// Interprete le /atcsave
		sender.sendMessage("Début de la sauvegarde...");
		plugin.getSaver().saveListeVilles(plugin.v().getVilles());
		sender.sendMessage("Sauvegarde terminée !");
		
		return false;
	}

}
