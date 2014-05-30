package net.zyuiop.openCities.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import net.zyuiop.openCities.OpenCities;
import net.zyuiop.openCities.data.Ville;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DataReader extends Data {
	

	public DataReader(OpenCities pl) {
		super(pl);
	}
	
	/*
	 * 
	 * Fonctions liées aux VILLES
	 * 
	 */
	
	public Ville getVille(String ville) {
		checkCityFolder(ville);
		
		Ville v = new Ville();
		try {
			BufferedReader lecteur = new BufferedReader(new FileReader(rootDir+"villes/"+ville+"/"+CityFile));
			String line = null;
			try {
				while ((line = lecteur.readLine()) != null) {
					if (!line.equals("")) {
						String[] tab = line.split("=");
						switch (tab[0]) {
							case "nom":
								v.setName(tab[1]);
								break;
							case "maire":
								v.setMaire(tab[1]);
								break;
							case "maire_interim":
								v.setMaireInterim((tab[1] == "null") ? null : tab[1]);
								break;
							case "conseillers":
								if (tab.length > 1 && !tab[1].equals("")) {
									String[] conseillers = tab[1].split(";");
									int i = 0;
									while (i < conseillers.length) {
										v.addConseiller(conseillers[i]);
										i++;
									}
								}
								break;
							case "chunks":
								if (tab.length > 1 && !tab[1].equals("")) {
									String[] coordonnees = tab[1].split(";");
									int i = 0;
									while (i < coordonnees.length) {
										String[] c =  coordonnees[i].substring(1, coordonnees[i].length()-1).split(",");
										v.addChunk(pl.getServer().getWorld("world").getChunkAt(Integer.parseInt(c[0]), (Integer.parseInt(c[1]))));
										i++;
									}
								}
								break;
							case "public":
								v.setPublic(Boolean.parseBoolean(tab[1]));
								break;
							case "compte":
								v.creditPO(Double.parseDouble(tab[1]));
								break;
							case "position":
								
								if (tab.length > 1 && !tab[1].equals("")) { 
									String[] xyz = tab[1].split(";");
									if (xyz.length == 3) {
										Location l = new Location(pl.getServer().getWorld("world"), Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
										if (l != null)
											v.setSpawn(l);
									}
								}									
								break;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Bukkit.getLogger().warning("Erreur durant la lecture de la ville '"+ville+"'");
				return null;
			}
			
		} catch (FileNotFoundException e) {
			pl.getLogger().warning("Erreur de lecture du fichier de la ville "+ville);
			e.printStackTrace();
		} 
		
		v = getParcelles(v);
		v = getInhabitants(v);
		v = getInvit(v);
		
		return v;
	}
	
	public HashMap<String, Ville> getVilles() {
		HashMap<String, Ville> villes = new HashMap<String, Ville>();
		try {
			BufferedReader lecteur = new BufferedReader(new FileReader(rootDir+"villes/villes.list"));
			String line = null;
			while ((line = lecteur.readLine()) != null) {
				if (!line.equals("")) {
					Ville v = getVille(line);
					if (v != null)
						villes.put(v.getName(), v);
				}
			}
			
		} catch (FileNotFoundException e) {
			// Creation du fichier
			DataSaver ds = new DataSaver(pl);
			ds.saveListeVilles(new HashMap<String, Ville>());
			pl.getLogger().info("Création du fichier de liste des villes : terminé.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return villes;
	}
	
	/*
	 * Parcelles
	 */
	
	public Ville getParcelles(Ville v) {
		BufferedReader lecteur = null;
		try {
			lecteur = new BufferedReader(new FileReader(rootDir+"villes/"+v.getName()+"/"+PlotsFile));
			String line = null;
			while ((line = lecteur.readLine()) != null) {
				if (!line.equals("")) {
					v.addParcelle(new Parcelle(line));
				}
			}
			lecteur.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Bukkit.getLogger().warning("Erreur durant la lecture des parcelles de la ville '"+v.getName()+"'");
			return v;
		} finally {
			if (lecteur != null) {
				try { lecteur.close(); } catch (IOException ignore) {  }
			}
		}
			
		return v;
	
	}
	
	/*
	 * Habitants
	 */
	
	public Ville getInhabitants(Ville v) {
		BufferedReader lecteur = null;
		ArrayList<String> l = new ArrayList<String>();
		try {
			lecteur = new BufferedReader(new FileReader(rootDir+"villes/"+v.getName()+"/"+CityMembersFile));
			String line = null;
			while ((line = lecteur.readLine()) != null) {
				if (!line.equals("")) {
					l.add(line);
				}
			}
			lecteur.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			pl.getSaver().saveHabitants(v);
			Bukkit.getLogger().warning("Erreur durant la lecture des habitants de la ville '"+v.getName()+"'");
			return v;
		} finally {
			if (lecteur != null) {
				try { lecteur.close(); } catch (IOException ignore) {  }
			}
		}
		v.setInhabitants(l);
		return v;
	}
	
	/*
	 * Invitations
	 */
	
	public Ville getInvit(Ville v) {
		BufferedReader lecteur = null;
		ArrayList<String> l = new ArrayList<String>();
		try {
			lecteur = new BufferedReader(new FileReader(rootDir+"villes/"+v.getName()+"/"+InvitsFile));
			String line = null;
			while ((line = lecteur.readLine()) != null) {
				if (!line.equals("")) {
					l.add(line);
				}
			}
			lecteur.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			pl.getSaver().saveInvits(v);
			Bukkit.getLogger().warning("Erreur durant la lecture des invitations de la ville '"+v.getName()+"'");
			return null;
		} finally {
			if (lecteur != null) {
				try { lecteur.close(); } catch (IOException ignore) {  }
			}
		}
		v.setInvits(l);
		return v;
	}
	
	
}
