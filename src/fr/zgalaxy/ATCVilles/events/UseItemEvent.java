package fr.zgalaxy.ATCVilles.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import fr.zgalaxy.ATCVilles.ATCVilles;

public class UseItemEvent implements Listener {
	
	private ATCVilles pl = null;
	
	public UseItemEvent(ATCVilles pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void shear(PlayerShearEntityEvent e) {
		Block b = Bukkit.getServer().getWorld("world").getBlockAt(e.getEntity().getLocation());
		
		if (!b.getWorld().getName().equals("world") || e.getPlayer().hasPermission(ATCVilles.BypassPerm))
			return;
		
		
		
		// Get chunk.
		if (!pl.v().canInteract(e.getPlayer(), b)) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(pl.TAG+ChatColor.DARK_RED+"Vous ne pouvez pas utiliser de cisailles ici.");
			return;
		}
		
		
	}
	
	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent e) {
		Block b = Bukkit.getServer().getWorld("world").getBlockAt(e.getEntity().getLocation());
		Player p = null;
		
		if (e.getDamager() instanceof Player)
			p = (Player) e.getDamager();
		else
			return;
		
		if (!b.getWorld().getName().equals("world") || p.hasPermission(ATCVilles.BypassPerm))
			return;
		
		// Get chunk.
		if (!pl.v().canInteractEntity(p, e.getEntity())) {
			e.setCancelled(true);
			p.sendMessage(pl.TAG+ChatColor.DARK_RED+"Vous ne pouvez pas attaquer ces animaux.");
			return;
		}
	}
}
