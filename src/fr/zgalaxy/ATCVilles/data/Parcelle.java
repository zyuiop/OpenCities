package fr.zgalaxy.ATCVilles.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/*
 * Gestion de parcelles
 * Stockage des données d'une parcelle et lecture
 * 
 */

public class Parcelle {
	
	private String idClaim;
	private int prix;
	private Location p1;
	private Location p2;
	private String proprio;
	private String claimType;
	private ArrayList<String> membres;
	private boolean allowDoors = true;
	private boolean allowFenceGates = true;
	private boolean allowLeversButtons = true;
	private String sellTo = null;
	
	public Parcelle(String line) {
		
		// Décriptage de la ligne
		
		String[] lines = line.split(",");
		this.idClaim = lines[0];
		this.prix = (int) Integer.decode(lines[1]);
		this.proprio = (lines[4].equals(null)) ? null : lines[4];
		this.claimType = lines[5];
		
		String[] point1 = lines[2].substring(1, lines[2].length()-1).split(";");
		String[] point2 = lines[3].substring(1, lines[3].length()-1).split(";");
		
		this.p1 = new Location(Bukkit.getWorld("world"), (double) Double.parseDouble(point1[0]), (double) Double.parseDouble(point1[1]), (double) Double.parseDouble(point1[2]));
		this.p2 = new Location(Bukkit.getWorld("world"), (double) Double.parseDouble(point2[0]), (double) Double.parseDouble(point2[1]), (double) Double.parseDouble(point2[2]));
		this.membres = new ArrayList<String>();
		for (String m : lines[6].substring(1, lines[6].length()-1).split(";")) {
			membres.add(m);
		}
		
		if (lines.length < 8)
			return;
		
		this.allowDoors = Boolean.parseBoolean(lines[7]);
		this.allowFenceGates = Boolean.parseBoolean(lines[8]);
		this.allowLeversButtons = Boolean.parseBoolean(lines[9]);
	
		if (lines.length < 11)
			return;
		
		this.sellTo = lines[10]; // Sellto = personne qui touchera le montant de la vente.
	}
	
	public void setSellTo(String s) {
		sellTo = s;
	}
	
	public Parcelle(String nom, int prix, Location p1, Location p2, String proprio, String claimType, ArrayList<String> membres, boolean allowD, boolean allowFen, boolean allowLev, String sellTo) {
		this.idClaim = nom;
		this.prix = prix;
		this.p1 = p1;
		this.p2 = p2;
		this.proprio = proprio;
		this.claimType = claimType;
		this.membres = membres;
		allowDoors = allowD;
		allowFenceGates = allowFen;
		allowLeversButtons = allowLev; 
		
		this.sellTo = sellTo;
	}
	
	public Location getP1() {
		return p1;
	}
	
	public Location getP2() {
		return p2;
	}
	
	public Parcelle(String nom, int prix, Location p1, Location p2, String proprio, String claimType) {
		this(nom,prix,p1,p2,proprio,claimType, new ArrayList<String>(), true, false, true, null);
	}
	
	public String getIdClaim() {
		return idClaim;
	}
	
	public String toString() {
		String ret = idClaim+","+Integer.toString(prix)+",";
		ret+="["+Double.toString(p1.getX())+";"+Double.toString(p1.getY())+";"+Double.toString(p1.getZ())+"],";
		ret+="["+Double.toString(p2.getX())+";"+Double.toString(p2.getY())+";"+Double.toString(p2.getZ())+"],";
		ret+=proprio+","+claimType+",[";
		ret+=StringUtils.join(membres,";");
		ret+="],"+allowDoors+","+allowFenceGates+","+allowLeversButtons+","+sellTo;
		return ret;
	}
	
	public boolean isInPlot(Location loc) {
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
				
		if (x > p1.getX() && x < p2.getX() && y > p1.getY() && y < p2.getY() && z > p1.getZ() && z < p2.getZ()) {
			return true;
		}
		return false;
	}
	
	public String getProprio() {
		if (proprio == null || proprio.equals("null"))
			return null;
		return proprio;
	}
	
	public int getPrix() {
		return prix;
	}
	
	public void setPrix(int prix) {
		this.prix = prix;
	}
	
	public String getType() {
		return claimType;
	}

	public ArrayList<String> getMembres() {
		return membres;
	}
	
	public void addMembre(String m) {
		membres.add(m);
	}
	
	public boolean isMember(String pseudo) {
		if (proprio.equals(pseudo))
			return true;
		if (membres.contains(pseudo))
			return true;
		return false;
	}
	
	
	public void setProprio(String prop) {
		this.proprio = prop;
	}
	
	
	/*
	 * Allow / Disallow
	 */
	
	public void setAllowDoors(boolean v) {
		allowDoors = v;
	}
	
	public void setAllowFenceGates(boolean v) {
		allowFenceGates = v;
	}
	
	public void setAllowLeversButtons(boolean v) {
		allowLeversButtons = v;
	}
	
	public boolean getAllowDoors() {
		return allowDoors;
	}
	
	public boolean getAllowFenceGates() {
		return allowFenceGates;
	}
	
	public boolean getAllowLeversButtons() {
		return allowLeversButtons;
	}
	
	
	public ArrayList<Material> getForbiden() {
		ArrayList<Material> m = new ArrayList<Material>();
		if (!allowDoors) {
			m.add(Material.TRAP_DOOR);
			m.add(Material.WOOD_DOOR);
			m.add(Material.WOODEN_DOOR);
		}
		
		if (!allowFenceGates) {
			m.add(Material.FENCE_GATE);
		}
		
		if (!allowLeversButtons) {
			m.add(Material.LEVER);
			m.add(Material.WOOD_BUTTON);
			m.add(Material.STONE_BUTTON);
		}
		return m;
	}
	
	public String getSellTo() {
		return this.sellTo;
	}
	
	public void delMember(String m) {
		membres.remove(m);
	}
	
	public HashMap<String, Integer> getSize() {
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		ret.put("x", p2.getBlockX() - p1.getBlockX());
		ret.put("z", p2.getBlockZ() - p1.getBlockZ());
		return ret;
	}
}
