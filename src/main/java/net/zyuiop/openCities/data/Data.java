package net.zyuiop.openCities.data;

import java.io.File;

import net.zyuiop.openCities.OpenCities;
import net.zyuiop.openCities.utils.FileUtils;

import org.bukkit.Bukkit;

public abstract class Data {
	
	protected OpenCities pl = null;
	protected String rootDir = null;
	protected final String nl = System.getProperty("line.separator");
	
	public Data(OpenCities plugin) {
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
