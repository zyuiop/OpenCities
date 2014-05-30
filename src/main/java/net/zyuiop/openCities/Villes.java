package net.zyuiop.openCities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import net.zyuiop.openCities.data.Parcelle;
import net.zyuiop.openCities.data.Ville;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Villes {
	protected HashMap<String, Ville> listeVilles;
	protected OpenCities plugin;
	protected ArrayList<PalierVille> paliers;
	
	public Villes(OpenCities pl) {
		pl.getLogger().info("Load");
		listeVilles = pl.getReader().getVilles();
		plugin = pl;
		
		// Init types de villes.
		paliers = new ArrayList<PalierVille>();
		
		
		pl.getLogger().info(" -- Chargement des paliers de villes -- ");
		Set<String> keys = pl.getConfig().getConfigurationSection("villes").getKeys(false);
		for (String k : keys) {
			PalierVille p = new PalierVille();
			ConfigurationSection c = pl.getConfig().getConfigurationSection("villes").getConfigurationSection(k);
			
			if (c == null) {
				continue;
			}
			
			pl.getLogger().info(" ---> [Palier "+k+"] <--- ");
			
			p.minHabs = c.getInt("min-hab");
			pl.getLogger().info(" -----> Habitants min "+p.minHabs);
			p.maxChunks = c.getInt("max-chunks");
			pl.getLogger().info(" -----> Chunks max "+p.maxChunks);
			p.maxTax = c.getInt("max-tax");
			pl.getLogger().info(" -----> Impots max "+p.maxTax);
			p.chunkPrice = c.getInt("chunk-price");
			pl.getLogger().info(" -----> Prix chunk "+p.chunkPrice);
			p.nom = k;
			
			paliers.add(p);
		}
	}
	
	/*
	 * Paliers
	 */
	
	public ArrayList<PalierVille> getPaliers() {
		return paliers;
	}
	
	public PalierVille getPalier(Ville v) {
		int habs = v.getInhabitants().size() +1;
		PalierVille p = new PalierVille();
		for (PalierVille palier : paliers) {
			if (palier.minHabs <= habs && palier.minHabs > p.minHabs) // Palier plus grand mais OK
				p = palier;
		}
		return p;
	}
	
	public PalierVille getPalierSup(int habs) {
		
		PalierVille p = new PalierVille();
		for (PalierVille palier : paliers) {
			if (palier.minHabs > habs && (p.minHabs == 0 || palier.minHabs < p.minHabs)) // Palier plus serré
				p = palier;
		}
		return p;
	}
	
	/*
	 * Villes
	 */
	
	public void addVille(Ville v) {
		Bukkit.getLogger().info("Append : "+v.getName());
		listeVilles.put(v.getName(), v);
	}
	public HashMap<String, Ville> getVilles() {
		return listeVilles;
	}
	public void updateVille(String name, Ville v) {
		listeVilles.remove(name);
		addVille(v);
		plugin.getSaver().saveVille(v);
	}
	
	public void deleveVille(String name) {
		if (listeVilles.containsKey(name))
			listeVilles.remove(name);
	}
	
	public Ville villeParMaire(String maire) {
		String[] liste = getVilles().keySet().toArray(new String[0]);
		for (String i : liste) {
			Ville v = getVilles().get(i);
			if (v.getMaire().equals(maire)) {
				return v;
			}
		}
		
		// Pas trouvé ?
		for (String i : liste) {
			Ville v = getVilles().get(i);
			if (v.getMaireInterim() == maire) {
				return v;
			}
		}
		return null;
	}
	
	public Ville villeParChunk(Chunk c) {
		String[] liste = getVilles().keySet().toArray(new String[0]);
		for (String i : liste) {
			Ville v = getVilles().get(i);
			if (v.hasChunk(c)) {
				return v;
			}
		}
		return null;
	}
	
	public Ville getVille(String ville) {
		if (!getVilles().containsKey(ville))
			return null;
		return getVilles().get(ville);
	}
	
	public Ville citizenOf(String playerName) {
		String[] liste = getVilles().keySet().toArray(new String[0]);
		for (String i : liste) {
			Ville v = getVilles().get(i);
			if (v.isInhabitant(playerName)) {
				return v;
			}
		}
		return null;
	}
	
	/*
	 * Fonctions pour les parcelles
	 * 
	 */
	
	public Parcelle getParcelle(String ville, String idParcelle) {
		return getVille(ville).getParcelles().get(idParcelle);
	}
	
	/*
	 * Invits
	 */
	
	public ArrayList<String> invitedIn(String playerName) {
		if (citizenOf(playerName) != null)
			return null;
		
		ArrayList<String> invited = new ArrayList<String>();
		String[] liste = getVilles().keySet().toArray(new String[0]);
		for (String i : liste) {
			Ville v = getVilles().get(i);
			if (v.isPublic() == false && v.isInvited(playerName)) {
				invited.add(v.getName());
			}
		}
		return invited;
	}
	
	public boolean canInteract(Player p, Block b) {
		
		return canInteract(p, b.getLocation());
	}
	
	public boolean canInteract(Player p, Location l) {
		Chunk c = l.getChunk();
		Ville v = villeParChunk(c);
		
		if (p.hasPermission(OpenCities.BypassPerm))
			return true;
		
		if (v == null)
			return false;
		
		if (v.canBuild(p.getName()))
			return true;
		
		if (!v.isInhabitant(p.getName()))
			return false;
		
			
		Parcelle par = v.getParcelle(l);
		
		if (par == null || par.getProprio() == null)
		{
			if (v.canBuild(p.getName()))
				return true;
			return false;
		}
		
		
		
		if (!par.isMember(p.getName())) {
			return false;
		}
		else if (par.isMember(p.getName())) {
			return true;
		}
		
		
		return false;
	}
	
	public boolean canInteractEnv(Player p, Block b) {
		Chunk c = b.getChunk();
		Ville v = villeParChunk(c);
		
		
		
		if (p.hasPermission(OpenCities.BypassPerm))
			return true;
		
		if (v == null)
			return true;
		
		if (v.canBuild(p.getName()))
			return true;
		
		
		
		Parcelle par = v.getParcelle(b.getLocation());
		
		if (par == null || par.getProprio() == null)
		{
			if (v.getForbiden().contains(b.getType())) { // Forbiden globaux (Chests...)
				return false;
			}
			return true;
		}
		
		if (par.isMember(p.getName()))
			return true;
		
		if (v.getForbiden().contains(b.getType())) { // Forbiden globaux (Chests...)
			return false;
		}
		
		if (!par.isMember(p.getName())) {
			if (par.getForbiden().contains(b.getType())) // Forbiden de la parcelle (Levers...)
				return false;
			return true;
		}
		return true;
	}
	
	public boolean canInteractEntity(Player p, Entity e) {
		Chunk c = e.getLocation().getChunk();
		Ville v = villeParChunk(c);
		
		if (p.hasPermission(OpenCities.BypassPerm))
			return true;
		
		if (v == null)
			return true;
		
		if (v.canBuild(p.getName()))
			return true;
		
		if (!c.getWorld().getName().equals("world"))
			return true;
		
		ArrayList<EntityType> allowed = new ArrayList<EntityType>();
		allowed.add(EntityType.BLAZE);
		allowed.add(EntityType.CAVE_SPIDER);
		allowed.add(EntityType.CREEPER);
		allowed.add(EntityType.ENDERMAN);
		allowed.add(EntityType.MAGMA_CUBE);
		allowed.add(EntityType.ZOMBIE);
		allowed.add(EntityType.WITHER);
		allowed.add(EntityType.WITHER_SKULL);
		allowed.add(EntityType.WITCH);
		allowed.add(EntityType.SLIME);
		allowed.add(EntityType.SILVERFISH);
		allowed.add(EntityType.SKELETON);
		allowed.add(EntityType.SPIDER);
		if (allowed.contains(e.getType()))
			return true;
		
		Parcelle par = v.getParcelle(e.getLocation());
		
		if (par == null)
		{
			return false;
		}
		
		if (par.isMember(p.getName()))
			return true;
		
		return false;
	}
}
