package fr.zgalaxy.ATCVilles.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import fr.zgalaxy.ATCVilles.ATCVilles;
import fr.zgalaxy.ATCVilles.data.Ville;
import fr.zgalaxy.ATCVilles.utils.FileUtils;

public class DataSaver extends Data {

	public DataSaver(ATCVilles plugin) {
		super(plugin);
	}
	
	/*
	 * 
	 * VILLES
	 * 
	 */
	
	public void saveVille(Ville v) {
		checkCityFolder(v.getName());
		
		BufferedWriter writer = null;
		try {
			
			// Ecriture des renseignements :			
			writer = new BufferedWriter(new FileWriter(rootDir+"villes/"+v.getName()+"/"+CityFile));
			writer.write("nom="+v.getName()+nl);
			writer.write("maire="+v.getMaire()+nl);
			writer.write("maire_interim="+v.getMaireInterim()+nl);
			writer.write("public="+v.isPublic()+nl);
			writer.write("compte="+v.getCompte()+nl);
			Location spawn = v.getSpawn();
			if (spawn == null) {
				writer.write("position=null"+nl);
			} else {
				Integer[] pos = new Integer[] {spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()};
				writer.write("position="+StringUtils.join(pos, ";")+nl);
			}
			
			// Création des listes de conseillers et de chunks
			ArrayList<String> conseillers = v.getConseillers();

			Vector<int[]> chunks = v.getChunks();
			ArrayList<String> listeChunks = new ArrayList<String>();
			for (int i = 0; i < chunks.size(); i++) {
				int[] c = chunks.get(i);
				listeChunks.add("["+c[0]+","+c[1]+"]");
			}
			
			// Ecriture 
			writer.write("conseillers="+StringUtils.join(conseillers, ";")+nl);
			writer.write("chunks="+StringUtils.join(listeChunks, ";")+nl);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			pl.getLogger().warning("Erreur lors de l'écriture du fichier d'infos de la ville "+v.getName());
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try { writer.close(); } catch (IOException ignore) {  }
			}
		}
		
		// Sauvegardes des autres infos
		saveParcelles(v);
		saveHabitants(v);
		saveInvits(v);
	}
	
	/*
	 * Parcelles
	 */
	
	public void saveParcelles(Ville v) {
		HashMap<String, Parcelle> parcelles = v.getParcelles();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(rootDir+"villes/"+v.getName()+"/"+PlotsFile));
			for (String key : parcelles.keySet()) {
				writer.write(parcelles.get(key).toString()+nl);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			pl.getLogger().warning(">> Erreur lors de l'écriture du fichier de parcelles de "+v.getName()+" <<");
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try { writer.close(); } catch (IOException ignore) {  }
			}
		}
	}
	
	/*
	 * Habitants
	 */
	
	public void saveHabitants(Ville v) {
		ArrayList<String> l = v.getInhabitants();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(rootDir+"villes/"+v.getName()+"/"+CityMembersFile));
			for (String mem : l) {
				writer.write(mem+nl);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			pl.getLogger().warning(">> Erreur lors de l'écriture du fichier de membres de "+v.getName()+" <<");
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try { writer.close(); } catch (IOException ignore) {  }
			}
		}
	}
	
	/*
	 * Invitations
	 */
	
	public void saveInvits(Ville v) {
		ArrayList<String> l = v.getInvits();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(rootDir+"villes/"+v.getName()+"/"+InvitsFile));
			for (String mem : l) {
				writer.write(mem+nl);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			pl.getLogger().warning(">> Erreur lors de l'écriture du fichier d'invitations de "+v.getName()+" <<");
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try { writer.close(); } catch (IOException ignore) {  }
			}
		}
	}
	
	/*
	 * Sauvegarde LISTE VILLES
	 */
	
	public void saveListeVilles(HashMap<String, Ville> villes) {
		String[] liste = villes.keySet().toArray(new String[0]);
		for (int i = 0; i < liste.length; i++) {
			saveVille(villes.get(liste[i]));
		}
		saveListOnly(villes);
	}
	
	public void saveListOnly(HashMap<String, Ville> villes) {
		String[] liste = villes.keySet().toArray(new String[0]);
		pl.getLogger().info("Going to write in "+rootDir+"villes/villes.list");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(rootDir+"villes/villes.list"));
			writer.write(StringUtils.join(liste ,nl));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try { writer.close(); } catch (IOException ignore) {  }
			}
		}
		
	}
	
	/*
	 * Suppression ville
	 */
	
	public void deleteVille(String ville) {
		pl.v().deleveVille(ville);
		this.saveListOnly(pl.v().getVilles());
		deleteDirectory(new File(rootDir+"villes/"+ville+"/"));
	}
	
	/*
	 * Creation ville
	 */
	
	public void nouvelleVille(String ville, String maire, Chunk c, boolean publique) {
		Ville v = new Ville(ville, maire, c, publique);
		saveVille(v);
		pl.v().addVille(v);
		this.saveListOnly(pl.v().getVilles());
	}
	
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
}
