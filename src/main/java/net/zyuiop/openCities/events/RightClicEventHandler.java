package net.zyuiop.openCities.events;

import net.zyuiop.openCities.OpenCities;
import net.zyuiop.openCities.data.Ville;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class RightClicEventHandler implements Listener {
	
	
	private OpenCities pl = null;
	
	public RightClicEventHandler(OpenCities pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() == Material.STICK && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Outil de Parcelles")) {
			Action ac =  e.getAction();
			if (ac != Action.LEFT_CLICK_BLOCK && ac != Action.RIGHT_CLICK_BLOCK)
				return;
			
			Player j = e.getPlayer();
			
			Ville v = pl.v().citizenOf(j.getName());
			if (v == null) {
				j.sendMessage(pl.TAG+ChatColor.RED+"Vous ne disposez pas de ville.");
				return;
			} 
			else if (!v.canBuild(j.getName())) {
				j.sendMessage(pl.TAG+ChatColor.RED+"Vous n'êtes pas adjoint ou maire dans votre ville.");
				return;
			}	
			if (!e.getClickedBlock().getWorld().getName().equals("world")) {
				e.getPlayer().sendMessage(pl.TAG+ChatColor.RED+"Cet outil ne peut pas être utilisé ici.");
				return;
			}
			
			if (pl.v().villeParChunk(e.getClickedBlock().getChunk()) != v) {
				e.getPlayer().sendMessage(pl.TAG+ChatColor.RED+"Vous n'êtes pas dans votre ville.");
				return;
			}
			
			if (ac == Action.LEFT_CLICK_BLOCK) {
				pl.p.setPoint(0, e.getPlayer().getName(), e.getClickedBlock().getLocation());
				e.getPlayer().sendMessage(pl.TAG+ChatColor.LIGHT_PURPLE+"Premier bloc sélectionné.");
			} else if (ac == Action.RIGHT_CLICK_BLOCK) {
				pl.p.setPoint(1, e.getPlayer().getName(), e.getClickedBlock().getLocation());
				e.getPlayer().sendMessage(pl.TAG+ChatColor.LIGHT_PURPLE+"Second bloc sélectionné.");
			} 
			return;
		}
		
		Block clicked = e.getClickedBlock();
		if (clicked == null)
			return;
		
		if (!clicked.getWorld().getName().equals("world"))
			return;
		
		if (!pl.v().canInteractEnv(e.getPlayer(), clicked)) {
			e.getPlayer().sendMessage(pl.TAG+ChatColor.DARK_RED+"Vous ne pouvez pas intéragir avec ceci.");
			e.setCancelled(true);
		}
		
		
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (e.getPlayer().getItemInHand().getType().equals(Material.LEASH)) {
			Entity target = e.getRightClicked();
			if (target == null)
				return;
			
			Location loc = target.getLocation();
			if (loc == null)
				return;
			
			if (!loc.getWorld().getName().equals("world"))
				return;
			
			if (!pl.v().canInteractEntity(e.getPlayer(), e.getRightClicked())) {
				e.getPlayer().sendMessage(pl.TAG+ChatColor.DARK_RED+"Vous ne pouvez utiliser de laisse ici.");
				e.setCancelled(true);
			}
		}
	}
}
