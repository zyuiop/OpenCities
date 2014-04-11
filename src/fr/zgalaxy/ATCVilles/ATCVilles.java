package fr.zgalaxy.ATCVilles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.permissions.DefaultPermissions;

import net.milkbowl.vault.economy.Economy;
import fr.zgalaxy.ATCVilles.commands.CommandATCV;
import fr.zgalaxy.ATCVilles.commands.CommandParcelle;
import fr.zgalaxy.ATCVilles.commands.CommandVille;
import fr.zgalaxy.ATCVilles.data.*;
import fr.zgalaxy.ATCVilles.events.BreakPlaceEvents;
import fr.zgalaxy.ATCVilles.events.BukketEvent;
import fr.zgalaxy.ATCVilles.events.JoinEventHandler;
import fr.zgalaxy.ATCVilles.events.MoveEventHandler;
import fr.zgalaxy.ATCVilles.events.RightClicEventHandler;
import fr.zgalaxy.ATCVilles.events.UseItemEvent;

public class ATCVilles extends JavaPlugin {
	@Override
	public void onEnable() {
		v = new Villes(this);
		p = new Players(this);
		
		this.saveDefaultConfig();
		
		getCommand("ville").setExecutor(new CommandVille(this));
		getCommand("parcelle").setExecutor(new CommandParcelle(this));
		getCommand("atcv").setExecutor(new CommandATCV(this));
		
		getServer().getPluginManager().registerEvents(new MoveEventHandler(this), this);
		getServer().getPluginManager().registerEvents(new JoinEventHandler(this), this);
		getServer().getPluginManager().registerEvents(new RightClicEventHandler(this), this);
		getServer().getPluginManager().registerEvents(new BreakPlaceEvents(this), this);
		getServer().getPluginManager().registerEvents(new BukketEvent(this), this);
		getServer().getPluginManager().registerEvents(new UseItemEvent(this), this);
		
		if (!setupEconomy() ) {
            getLogger().severe("Vault n'a pas été trouvé.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
       	
	}
	
	private Economy econ = null;
	
	public Economy getEco() {
		return econ;
	}
	
	private boolean setupEconomy() {
	        if (getServer().getPluginManager().getPlugin("Vault") == null) {
	            return false;
	        }
	        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	        if (rsp == null) {
	            return false;
	        }
	        econ = rsp.getProvider();
	        return econ != null;
	}
	
	public static Permission BypassPerm = DefaultPermissions.registerPermission(new Permission("bypass","Evite les restrictions de construction ou destruction", PermissionDefault.OP));
	
	protected Villes v = null;
	public Players p = null;
	
	
	public String TAG = ChatColor.AQUA+"[???Villes] "+ChatColor.RESET;
	
	public Villes v() {
		return this.v;
	}
	
	public static ATCVilles instance = null;
	
	public void onDisable() {
		getSaver().saveListeVilles(v.getVilles());
	}
	
	public void resetVilles() {
		this.v = new Villes(this);
	}
	
	
	public DataSaver getSaver() {
		return new DataSaver(this);
	}
	
	public DataReader getReader() {
		return new DataReader(this);
	}
}
