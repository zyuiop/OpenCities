package fr.zgalaxy.ATCVilles.events;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.zgalaxy.ATCVilles.ATCVilles;
import fr.zgalaxy.ATCVilles.Villes;
import fr.zgalaxy.ATCVilles.data.Ville;

public class JoinEventHandler implements Listener {
	private ATCVilles pl = null;
	
	public JoinEventHandler(ATCVilles pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		// 1. Get current chunk
		Chunk c = e.getPlayer().getLocation().getChunk();
		Villes v = pl.v();
		Ville ville = v.villeParChunk(c);
		
		if (ville != null) {
			e.getPlayer().sendMessage(pl.TAG+ChatColor.GOLD+"Bienvenue à "+ville.getName());
		}
		
		ArrayList<String> invits = pl.v().invitedIn(e.getPlayer().getName());
		if (invits != null && invits.size() > 0)
		{
			e.getPlayer().sendMessage(pl.TAG+ChatColor.GREEN+"Vous avez des invitations pour rejoindre des villes : "+ChatColor.AQUA+StringUtils.join(invits, ChatColor.RESET+", "+ChatColor.AQUA));
			e.getPlayer().sendMessage(pl.TAG+ChatColor.GREEN+"Pour accepter une invitation, faites "+ChatColor.AQUA+"/ville join <nom ville>");
			e.getPlayer().sendMessage(pl.TAG+ChatColor.RED+"Pour refuser une invitation, faites "+ChatColor.AQUA+"/ville refuse <nom ville>");
		}
	}
}
