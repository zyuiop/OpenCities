package net.zyuiop.openCities.prompts;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;


public class CityNamePrompt extends ValidatingPrompt {

	@Override
	public String getPromptText(ConversationContext context) {
		// TODO Auto-generated method stub
		return ChatColor.GOLD+" Choisissez un nom pour votre ville";
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		if (input.matches("^[a-zA-Z0-9]{3,15}$"))
			return true;
		else 
			return false;
		
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		context.setSessionData("CityName", input);
		return Prompt.END_OF_CONVERSATION;
	}
	
	@Override
	protected String getFailedValidationText(ConversationContext context, String invalidInput) {
		return ChatColor.AQUA+"[ATCVilles] "+ChatColor.RED+"Le nom entré n'est pas valide (entre 3 et 15 caractères alphanumériques)";
	}

}
