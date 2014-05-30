package net.zyuiop.openCities.events;

import net.zyuiop.openCities.OpenCities;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BukketEvent implements Listener {
	
	private OpenCities pl = null;
	
	public BukketEvent(OpenCities pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onBukket(PlayerBucketFillEvent e) {
		if (!e.getBlockClicked().getWorld().getName().equals("world") || e.getPlayer().hasPermission(OpenCities.BypassPerm))
			return;
		
		// Get chunk.
		if (!pl.v().canInteract(e.getPlayer(), e.getBlockClicked())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(pl.TAG+ChatColor.DARK_RED+"Vous ne pouvez pas utiliser de seau ici.");
			return;
		}
	}
	
	@EventHandler
	public void onBukket(PlayerBucketEmptyEvent e) {
		if (!e.getBlockClicked().getWorld().getName().equals("world") || e.getPlayer().hasPermission(OpenCities.BypassPerm))
			return;
		
		// Get chunk.
		if (!pl.v().canInteract(e.getPlayer(), e.getBlockClicked())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(pl.TAG+ChatColor.DARK_RED+"Vous ne pouvez pas utiliser de seau ici.");
			return;
		}
	}
}
