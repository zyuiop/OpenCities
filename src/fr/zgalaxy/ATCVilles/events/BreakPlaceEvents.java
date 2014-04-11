package fr.zgalaxy.ATCVilles.events;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEvent;

import fr.zgalaxy.ATCVilles.ATCVilles;
import fr.zgalaxy.ATCVilles.data.Parcelle;
import fr.zgalaxy.ATCVilles.data.Ville;

public class BreakPlaceEvents implements Listener {
private ATCVilles pl = null;
	
	public BreakPlaceEvents(ATCVilles pl) {
		this.pl = pl;
	}
	
	/*
	 * 
	 * 
	 * Non citoyen = BUG
	 * Citoyen SANS PARCELLE = BUG
	 * 
	 * 
	 */
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		
		if (!e.getBlock().getWorld().getName().equals("world"))
			return;
		
		if (e.getPlayer().getItemInHand().getType() == Material.STICK && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Outil de Parcelles"))
		{	
			e.setCancelled(true);	
			return;
		}
		
		if (!pl.v().canInteract(e.getPlayer(), e.getBlock())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(pl.TAG+ChatColor.DARK_RED+"Vous ne pouvez pas casser de blocs ici.");
			return;
		}	
		
	}
	
	
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (!e.getBlock().getWorld().getName().equals("world") || e.getPlayer().hasPermission(ATCVilles.BypassPerm))
			return;
		
		// Get chunk.
		if (!pl.v().canInteract(e.getPlayer(), e.getBlock())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(pl.TAG+ChatColor.DARK_RED+"Vous ne pouvez pas placer de blocs ici.");
			return;
		}
	}
	
	
	
	
}
