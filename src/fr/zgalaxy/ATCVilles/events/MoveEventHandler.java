package fr.zgalaxy.ATCVilles.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.zgalaxy.ATCVilles.ATCVilles;
import fr.zgalaxy.ATCVilles.data.Parcelle;
import fr.zgalaxy.ATCVilles.data.Ville;

public class MoveEventHandler implements Listener {
	
	private ATCVilles pl = null;
	
	public MoveEventHandler(ATCVilles pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		// 1. Get current chunk
		Location from = e.getFrom();
		Location to = e.getTo();
		
		if (!from.getWorld().getName().equals("world"))
			return;
		
		Ville oldV = pl.v().villeParChunk(from.getChunk());
		Ville v = pl.v().villeParChunk(to.getChunk());
		
		if (v == null && null == oldV)
			return;
		
		if (!e.getFrom().getChunk().equals(e.getTo().getChunk())) {
			if (v != oldV) {
				if (v == null) {
					e.getPlayer().sendMessage(pl.TAG+ChatColor.GOLD+"Vous quittez la ville '"+oldV.getName()+"'");
				} 
				else if (oldV == null) {
					e.getPlayer().sendMessage(pl.TAG+ChatColor.GOLD+"Bienvenue à '"+v.getName()+"'");
				} else {
					e.getPlayer().sendMessage(pl.TAG+ChatColor.GOLD+"Vous quittez la ville '"+oldV.getName()+"'");
					e.getPlayer().sendMessage(pl.TAG+ChatColor.GOLD+"Bienvenue à '"+v.getName()+"'");
				}
				
			}
		}
		
		
		
		Parcelle oldPlot = (oldV != null) ? oldV.getParcelle(from) : null;
		Parcelle newPlot = (v != null) ? v.getParcelle(to) : null;
		
		
		if (oldPlot != newPlot) {
			if (oldPlot == null) {
				welcomeSentence(newPlot, e.getPlayer());
			} else if (newPlot == null) {
				gbieSentence(oldPlot, e.getPlayer());
			} else {
				gbieSentence(oldPlot, e.getPlayer());
				welcomeSentence(newPlot, e.getPlayer());
			}
		}
	}
	
	public void welcomeSentence(Parcelle par, Player p) {
		if (par.getProprio() == null)
			p.sendMessage(pl.TAG+ChatColor.GREEN+"Vous entrez sur une parcelle en vente : "+ChatColor.AQUA+par.getIdClaim()+ChatColor.GREEN+" - Prix "+ChatColor.AQUA+""+par.getPrix());
		else {
			if (par.getType().equals("shop"))
				p.sendMessage(pl.TAG+ChatColor.DARK_GREEN+"Vous entrez dans la boutique de "+ChatColor.AQUA+par.getProprio());
			else if (par.getType().equals("house"))
				p.sendMessage(pl.TAG+ChatColor.DARK_GREEN+"Vous entrez sur la parcelle de "+ChatColor.AQUA+par.getProprio());
			else if (par.getType().equals("hostel"))
				p.sendMessage(pl.TAG+ChatColor.DARK_GREEN+"Vous quittez une chambre d'hôtel.");

		}
	}
	
	public void gbieSentence(Parcelle par, Player p) {
		if (par.getProprio() == null)
			p.sendMessage(pl.TAG+ChatColor.GREEN+"Vous quittez la parcelle en vente "+ChatColor.AQUA+par.getIdClaim());
		else {
			if (par.getType().equals("shop"))
				p.sendMessage(pl.TAG+ChatColor.DARK_GREEN+"Vous quittez la boutique de "+ChatColor.AQUA+par.getProprio());
			else if (par.getType().equals("house"))
				p.sendMessage(pl.TAG+ChatColor.DARK_GREEN+"Vous quittez la parcelle de "+ChatColor.AQUA+par.getProprio());
			else if (par.getType().equals("hostel"))
				p.sendMessage(pl.TAG+ChatColor.DARK_GREEN+"Vous quittez une chambre d'hôtel.");

		}
	}
}
