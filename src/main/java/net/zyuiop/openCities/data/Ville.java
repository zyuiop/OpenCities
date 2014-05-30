package net.zyuiop.openCities.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

public class Ville {
	protected String name = null;
	protected String maire = null;
	protected String maire_interim = null;
	protected double compte = 0;
	protected Vector<int[]> chunks = new Vector<int[]>();
	protected ArrayList<String> conseillers = new ArrayList<String>();
	protected Location spawn = null;
	
	
	/*
	 * Constructeurs
	 */
	public Ville() {
	}

	public Ville(String name, String maire, Chunk chunk, boolean publique) {
		this.name = name;
		this.maire = maire;
		int[] ch = new int[] {chunk.getX(), chunk.getZ()};
		chunks.add(ch);
		this.publique = publique;
	}
	
	public Ville(String name, String maire, String maire_interim, ArrayList<String> conseillers, Vector<int[]> chunks, boolean publique, double compte, Location spawn) {
		this.name = name;
		this.maire = maire;
		this.maire_interim = maire_interim;
		this.conseillers = conseillers;
		this.chunks = chunks;
		this.publique = publique;
		this.compte = compte;
		this.spawn = spawn;
	}
	
	public int getNbInhabitants() {
		return inhabitants.size()+1;
	}
	
	public int getNbChunks() {
		return chunks.size();
	}
	
	public boolean setSpawn(Location spawn) {
		if (!spawn.getWorld().getName().equals("world"))
			return false;
		this.spawn = spawn;
		return true;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	/*
	 * Compte
	 */
	
	public double getCompte() {
		return compte;
	}
	
	public boolean debitPO(double amount) {
		if (compte - amount >= 0)
		{
			compte-=amount;
			return true;
		}
		return false;
	}
	
	public void creditPO(double amount) {
		compte += amount;
	}
	
	/*
	 * Public	
	 */
	protected boolean publique = true;
	
	public boolean isPublic() {
		return publique;
	}
	
	public void setPublic(boolean publique) {
		this.publique = publique;
	}
	
	/*
	 * Parcelles
	 */
	
	protected HashMap<String, Parcelle> parcelles = new HashMap<String, Parcelle>();
	
	public void addParcelle(Parcelle p) {
		parcelles.put(p.getIdClaim(), p);
	}
	
	public void setParcelle(Parcelle p) {
		delParcelle(p);
		addParcelle(p);
	}
	
	public void delParcelle(Parcelle p) {
		parcelles.remove(p.getIdClaim());
	}
	
	public HashMap<String, Parcelle> getParcelles() {
		return parcelles;
	}
	
	public Parcelle getParcelle(Location loc) {
		if (!loc.getWorld().getName().equals("world"))
			return null;
		
		HashMap<String, Parcelle> plots = getParcelles();
		for (String k : plots.keySet()) {
			Parcelle p = plots.get(k);
			if (p.isInPlot(loc))
				return p;
		}
		return null;
	}
	
	public Parcelle getParcelle(String p) {
		if (parcelles.containsKey(p))
			return parcelles.get(p);
		return null;
	}	
	

	/*
	 * Citoyens
	 */
	
	protected ArrayList<String> inhabitants = new ArrayList<String>();
	
	public void setInhabitants(ArrayList<String> habs) {
		inhabitants = habs;
		
	}
	
	public boolean isInhabitant(String pseudo) {
		if (maire.equals(pseudo))
			return true;
		return inhabitants.contains(pseudo);
	}
	
	public ArrayList<String> getInhabitants() {
		return inhabitants;
	}
	
	public void addInhabitant(String player) {
		inhabitants.add(player);
	}
	
	public void delInhabitant(String player) {
		if (inhabitants.contains(player))
			inhabitants.remove(player);
	}
	
	/*
	 * Invitations
	 */
	
	protected ArrayList<String> invits = new ArrayList<String>();
	
	public void setInvits(ArrayList<String> habs) {
		invits = habs;	
	}
	
	public boolean isInvited(String pseudo) {
		if (isInhabitant(pseudo))
			return false;	
		
		if (publique)
			return true;
		
		return invits.contains(pseudo);
	}
	
	public ArrayList<String> getInvits() {
		return invits;
	}
	
	public void addInvit(String player) {
		invits.add(player);
	}
	
	public void delInvit(String player) {
		if (invits.contains(player))
			invits.remove(player);
	}
	
	/*
	 * Droits d'intéraction
	 */
	
	public boolean canBuild(String player) {
		if (maire.equals(player))
			return true;
		else if (conseillers.contains(player)) 
			return true;
		return false;
	}
	
	
	public ArrayList<Material> getForbiden() {
		ArrayList<Material> m = new ArrayList<Material>();
		m.add(Material.CHEST);
		m.add(Material.FURNACE);
		m.add(Material.HOPPER);
		m.add(Material.HOPPER_MINECART);
		m.add(Material.ANVIL);
		m.add(Material.BEACON);
		m.add(Material.DISPENSER);
		m.add(Material.DROPPER);
		m.add(Material.TRAPPED_CHEST);
		m.add(Material.BURNING_FURNACE);
		return m;
	}
	
	/*
	 * Getters et setters
	 */
	
	
	public String getName() {
		return name;
	}
	
	public String getMaire() {
		return maire;
	}
	
	public Vector<int[]> getChunks() {
		return chunks;
	}
	
	public String getMaireInterim() {
		return maire_interim;
	}
	
	public ArrayList<String> getConseillers() {
		return conseillers;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMaire(String maire) {
		this.maire = maire;
	}
	
	public void setMaireInterim(String name) {
		this.maire_interim = name;
	}
	
	public void addConseiller(String c) {
		this.conseillers.add(c);
	}
	
	public void addChunk(Chunk c) {
		chunks.add(new int[] {c.getX(), c.getZ()});
	}
	
	public boolean delConseiller(String c) {
		int i = 0;
		if (conseillers.contains(c))
		{
			conseillers.remove(c);
			return true;
		}
		return false;
	}
	
	public boolean delConseiller(List<String> c) {
		return this.conseillers.remove(c);
	}
	
	public boolean hasChunk(Chunk c) {
		int i = 0;
		while (i < chunks.size()) {
			int[] cc = chunks.get(i);
			if (cc[0] == c.getX() && cc[1] == c.getZ()) {
				return true;
			}
			i++;
		}
		return false;
	}
	
}
