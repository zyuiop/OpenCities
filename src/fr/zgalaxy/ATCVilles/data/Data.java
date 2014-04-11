package fr.zgalaxy.ATCVilles.data;

import java.io.File;

import org.bukkit.Bukkit;

import fr.zgalaxy.ATCVilles.ATCVilles;
import fr.zgalaxy.ATCVilles.utils.FileUtils;

public abstract class Data {
	
	protected ATCVilles pl = null;
	protected String rootDir = null;
	protected final String nl = System.getProperty("line.separator");
	
	public Data(ATCVilles plugin) {
		pl = plugin;
		
		String dat =  pl.getDataFolder().toPath().toAbsolutePath().toString();
		rootDir = dat+"/data/";
		
		FileUtils.checkFolders(new String[] {dat, 
				rootDir,
				rootDir+"villes/"});

	}
	
	protected String CityFile = "city.info";
	protected String CityMembersFile = "members.list";
	protected String PlotsFile = "plots.list";
	protected String InvitsFile = "invits.list";
	
	protected void checkCityFolder(String city) {
		FileUtils.checkFolders(new String[] {rootDir+"villes/"+city});
	}
}
