package net.zyuiop.openCities.commands;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.zyuiop.openCities.OpenCities;
import net.zyuiop.openCities.PalierVille;
import net.zyuiop.openCities.Villes;
import net.zyuiop.openCities.data.DataSaver;
import net.zyuiop.openCities.data.Parcelle;
import net.zyuiop.openCities.data.Ville;
import net.zyuiop.openCities.prompts.CityNamePrompt;
import net.zyuiop.openCities.utils.TextUtils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandVille implements CommandExecutor {

	private OpenCities plugin = null;
	private double createCity = 0;
	
	public CommandVille(OpenCities pl) {
		plugin = pl;
		createCity = pl.getConfig().getDouble("ville-creation");
		pl.getLogger().info("---> Argent pour créer une ville : "+createCity);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		DataSaver ds = plugin.getSaver();
		if (sender instanceof Player) {
			if (args.length == 0)
				return false;
			
			Player j = (Player) sender;
			if (args[0].equalsIgnoreCase("help")) {
				int page = 1;
				if (args.length > 1) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED+"La page entrée n'est pas un nombre.");
						return true;
					}
					
				}
				
				page-=1;
				sender.sendMessage(ChatColor.GOLD+"=========["+ChatColor.BLUE+"ATC Villes"+ChatColor.GOLD+"]=========");
				
				
				String[] messages = new String[] {
					TextUtils.FormatHelp("ville", "addadjoint <pseudo>", "Rend le joueur <pseudo> adjoint."),
					TextUtils.FormatHelp("ville", "agrandir", "Agrandit votre ville sur votre position. Vous devez dèjà avoir une ville."),
					TextUtils.FormatHelp("ville", "compte", "Affiche le compte en PO de votre ville"),
					TextUtils.FormatHelp("ville", "create <nom ville> <private|public>", "Crée une ville de nom <nom ville> sur le chunk actuel pour "+createCity+"PO."),
					TextUtils.FormatHelp("ville", "deladjoint <pseudo>", "Enlève l'adjoint <pseudo>."),
					TextUtils.FormatHelp("ville", "don <somme>", "Donne <somme> PO à votre ville."),
					TextUtils.FormatHelp("ville", "goto [ville]", "Vous téléporte vers le spawn de votre ville ou de la ville [ville]."),
					TextUtils.FormatHelp("ville", "info [nom ville]", "Affiche les infos de votre ville."),
					TextUtils.FormatHelp("ville", "invit <pseudo>", "Invite le joueur <pseudo> dans votre ville."),
					TextUtils.FormatHelp("ville", "join <nom ville>", "Rejoint la ville <nom ville>. Si la ville est privée, il vous faut être invité."),
					TextUtils.FormatHelp("ville", "paliers", "Affiche les différents paliers de villes."),
					TextUtils.FormatHelp("ville", "refuse <nom ville>", "Refuse l'invitation de la ville <nom ville>. "),
					TextUtils.FormatHelp("ville", "rename <nom ville>", "Renomme votre ville en <nom ville>"),
					TextUtils.FormatHelp("ville", "setspawn", "Place le spawn de votre ville sur votre position."),
					TextUtils.FormatHelp("ville", "settype <private|public>", "Change le type de votre ville")
				};
				
				int from = page*5;
				int to = from+5;
				int nbPages = (int) Math.ceil(messages.length/5);
				
				if ((page+1) < 1 || (page+1) > nbPages) {
					sender.sendMessage("Erreur : la page d'aide demandée n'existe pas.");
				}
				
				
				int i = from;
				while (i < to && i< messages.length-1) {
					i++;
					sender.sendMessage("- "+messages[i]);
				}
				
				sender.sendMessage("------ Aide : page "+(page+1)+" / "+nbPages+" ------");
				
				return true;
				
			}
			else if (args[0].equals("kick")) {
				if (!j.isOp())
					return true;
				// Commande admin
				Ville v = null;
				if (args.length != 3) {
					return true;
				}
					v = plugin.v().getVille(args[1]);
					if (v == null)  {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Cette ville n'existe pas.");
						return true;
					}
				 
				
				String jo = args[2];
				HashMap<String, Parcelle> plots = v.getParcelles();
				Set<String> plist = plots.keySet();
				ArrayList<Parcelle> modified = new ArrayList<Parcelle>();
				Parcelle p = null;
				for (String k : plist) {
					p = v.getParcelles().get(k);
					if (p.getProprio() != null && p.getProprio().equals(jo)) {
						Parcelle n = new Parcelle(p.getIdClaim(), p.getPrix(), p.getP1(), p.getP2(), null, p.getType());
						modified.add(n);
					} 
					
					if (p.isMember(jo)) {
						p.delMember(jo);
						modified.add(p);
					}
					
				}
				
				for (Parcelle par : modified)
					v.setParcelle(par);
				
				v.delConseiller(jo);
				v.delInhabitant(jo);				
				return true;
			}
			else if (args[0].equals("setspawn")) {
				Ville v = plugin.v().citizenOf(j.getName());
							
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous ne disposez d'aucune ville.");
					return true;
				}
				else if (!v.canBuild(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous n'êtes pas adjoint dans votre ville.");
					return true;
				}
				
				Location l = j.getLocation();
				if (!j.getWorld().getName().equals("world")) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous n'êtes pas sur la map principale.");
					return true;
				}
				
				Chunk c = l.getChunk();
				if (plugin.v().villeParChunk(c) == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Cette position n'est pas dans votre ville.");
					return true;
				}
								
				v.setSpawn(l);
				plugin.v().updateVille(v.getName(), v);
				plugin.getSaver().saveVille(v);
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez bien modifié le spawn de votre ville.");
				return true;
			}
			else if (args[0].equals("goto")) {
				Ville v = null;
				if (args.length == 2) {
					v = plugin.v().getVille(args[1]);
					if (v == null)  {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Cette ville n'existe pas.");
						return true;
					}
				} else {
					v = plugin.v().citizenOf(j.getName());
				}
				
				
				if (v != null) {
					Location spawn = v.getSpawn();
					if (spawn == null) {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : cette ville n'a pas de spawn.");
						return true;
					}
					j.sendMessage(plugin.TAG+ChatColor.GOLD+"Téléportation en cours...");
					j.teleport(spawn);
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes citoyen d'aucune ville.");
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("create")) {
				// 1. La personne n'a pas de ville ?
				if (args.length != 3) {
					j.sendMessage(plugin.TAG+"Arguments incorrects");
					return true;
				}
				
				String vname = args[1];
				String type = args[2];
				
				
				if (plugin.getEco().has(j.getName(), createCity)) {
					plugin.getEco().withdrawPlayer(j.getName(), createCity);
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'avez pas assez d'argent.");
					return true;
				}
				
				/*
				 * Vérifications VILLE / MAP
				 */
				
				if (!j.getWorld().getName().equals("world")) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous ne pouvez pas créer de villes sur cette map.");
					return true;
				} 
				else if (!j.getWorld().getName().equals("world")) 
				{
					j.sendMessage(ChatColor.AQUA+"[ATCVilles] "+ChatColor.RED+"Vous ne pouvez pas créer de villes sur cette map.");
					return true;
				} 
				else if (plugin.v().citizenOf(j.getName()) != null)
				{
						j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Vous êtes dèjà membre d'une ville.");
						return true;
				}
				else if (plugin.v().villeParChunk(j.getLocation().getChunk()) != null) 
				{
            		j.sendMessage(plugin.TAG+ChatColor.RED+"Ce chunk est dèjà occupé par une autre ville.");
            		return true;
            	}
				/*
				 * Vérifications PARAM. NOM VILLE
				 */
				else if (!vname.matches("^[a-zA-Z0-9]{3,15}$")) 
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Le nom de ville n'est pas valide (entre 3 et 15 caractères alphanumériques)");
            		return true;
				}
				else if (plugin.v().getVille(vname) != null) {
            		j.sendMessage(plugin.TAG+ChatColor.RED+"Une ville de ce nom existe dèjà.");
            		return true;
            	}
				/*
				 * Vérifications PARAM. TYPE
				 */
				else if (!type.equals("public") && !type.equals("private")) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Le type de ville n'est pas valide (private : accessible sur invitation ou public : accessible librement).");
            		return true;
				}
				else {
					/* Création de la ville */
					Chunk chunk = j.getWorld().getChunkAt(j.getLocation());
					plugin.getSaver().nouvelleVille(vname, j.getName(), chunk, (type.equals("public")) ? true : false);
					j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez bien créé la ville "+vname+" !");
					plugin.getServer().broadcastMessage(ChatColor.GOLD+j.getName()+" vient de créer la ville "+vname);
					return true;
				}
				
				
			} else if (args[0].equals("rename")) {
				
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+"Arguments incorrects");
					return true;
				}
				
				Ville v = plugin.v().villeParMaire(j.getName());
				
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous ne disposez d'aucune ville.");
					return true;
				}
				
				String oldName = v.getName();
				String vname = args[1];
				
				if (!vname.matches("^[a-zA-Z0-9]{3,15}$")) 
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Le nom de ville n'est pas valide (entre 3 et 15 caractères alphanumériques)");
            		return true;
				}
				else if (plugin.v().getVille(vname) != null) {
            		j.sendMessage(plugin.TAG+ChatColor.RED+"Une ville de ce nom existe dèjà.");
            		return true;
            	}
				else
				{
					v.setName(vname);
					plugin.v().updateVille(oldName, v);
					plugin.getSaver().deleteVille(oldName);
					j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez renommé votre ville en "+ChatColor.AQUA+vname+ChatColor.GREEN+" !");
					return true;
				}
				
				
			} else if (args[0].equals("settype")) {
				
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+"Arguments incorrects");
					return true;
				}
				
				Ville v = plugin.v().villeParMaire(j.getName());
				
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous ne disposez d'aucune ville.");
					return true;
				}
				
				String type = args[1];
				
				if (!type.equals("public") && !type.equals("private")) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Le type de ville n'est pas valide (private : accessible sur invitation ou public : accessible librement).");
            		return true;
				}
				else
				{
					v.setPublic((type.equals("public") ? true : false));
					plugin.v().updateVille(v.getName(), v);
					j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez bien rendu votre ville "+ChatColor.AQUA+(type.equals("public") ? "publique" : "privée")+ChatColor.GREEN+" !");
					return true;
				}
				
				
			} else if (args[0].equalsIgnoreCase("agrandir")) {
				Ville v = plugin.v().citizenOf(j.getName());
				Chunk c = j.getWorld().getChunkAt(j.getLocation());
				
				if (j.getWorld().getName().equals("world") == false) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous ne pouvez pas avoir de ville sur cette map.");
					return true;
				}
				
				
				
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous ne disposez d'aucune ville.");
					return true;
				}
				else if (!v.canBuild(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous n'êtes pas adjoint dans votre ville.");
					return true;
				}
				PalierVille p = plugin.v().getPalier(v);
				
				if (v.getNbChunks() >= p.maxChunks) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : votre ville ne peut pas être plus grande.");
					return true;
				}
				
				if (plugin.v().villeParChunk(c) != null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : ce terrain est dèjà utilisé.");
					return true;
				}
				int x = j.getLocation().getBlockX();
				int z = j.getLocation().getBlockZ();
				Villes m = plugin.v();
				World w = j.getWorld();
				
				
				if (v.equals(m.villeParChunk(w.getChunkAt(new Location(w, x+16,64,z)))) || v.equals(m.villeParChunk(w.getChunkAt(new Location(w, x-16,64,z)))) || v.equals(m.villeParChunk(w.getChunkAt(new Location(w, x,64,z+16)))) || v.equals(m.villeParChunk(w.getChunkAt(new Location(w, x,64,z-16))))) {
					// Validation
					
					if (!v.debitPO(p.chunkPrice)) {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : votre ville n'a pas assez d'argent");
						return true;
					}
						
					v.addChunk(c);
					m.updateVille(v.getName(), v);
					j.sendMessage(plugin.TAG+ChatColor.GREEN+"Le chunk a été ajouté à votre ville.");
					return true;
					//
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : ce terrain n'est pas adjacent à votre ville.");
					return true;
				}
			} 
			else if (args[0].equals("invit")) {
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+"Arguments incorrects");
					return true;
				}
				
				Ville v = plugin.v().citizenOf(j.getName());
							
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous ne disposez d'aucune ville.");
					return true;
				}
				else if (!v.canBuild(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous n'êtes pas adjoint dans votre ville.");
					return true;
				}
				
				if (v.isPublic()) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"La ville est publique.");
            		return true;
				}
				
				String joueur = args[1];
				if (plugin.v().citizenOf(joueur) != null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Le joueur est dèjà citoyen d'une ville.");
					return true;
				}	
				if (v.isInvited(joueur)) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Le joueur est dèjà invité.");
            		return true;
				}
				if (v.isInhabitant(joueur))
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Le joueur est dèjà citoyen de la ville.");
					return true;
				}	
				
				if (!joueur.matches("^[a-zA-Z0-9&@_-]{3,20}$")) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Le joueur n'a pas été trouvé.");
            		return true;
				}
				
				if (Bukkit.getPlayer(joueur) != null) {
					Bukkit.getPlayer(joueur).sendMessage(plugin.TAG+ChatColor.GOLD+"Vous avez été invité à rejoindre "+v.getName());
					Bukkit.getPlayer(joueur).sendMessage(plugin.TAG+ChatColor.GREEN+"Pour accepter l'invitation, faites "+ChatColor.AQUA+"/ville join "+v.getName());
					Bukkit.getPlayer(joueur).sendMessage(plugin.TAG+ChatColor.RED+"Pour refuser l'invitation, faites "+ChatColor.AQUA+"/ville refuse "+v.getName());
				}
				
				v.addInvit(joueur);
				plugin.v().updateVille(v.getName(), v);
				plugin.getSaver().saveInvits(v);
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez invité "+ChatColor.AQUA+joueur+ChatColor.GREEN+" dans votre ville.");
				return true;
				
			}
			else if (args[0].equals("join")) {
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Arguments incorrects");
					return true;
				}
				
				Ville v = plugin.v().getVille(args[1]);
				if (v == null)
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Cette ville n'existe pas.");
					return true;
				}	
				if (plugin.v().citizenOf(j.getName()) != null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous êtes dèjà citoyen d'une ville.");
					return true;
				}	
				
				if (v.isInhabitant(j.getName()))
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous êtes dèjà citoyen de cette ville.");
					return true;
				}	
				else if (v.isInvited(j.getName())) {
					if (!v.isPublic()) {
						v.delInvit(j.getName());
						plugin.getSaver().saveInvits(v);
					}
					PalierVille pa = plugin.v().getPalier(v);
					v.addInhabitant(j.getName());
					plugin.getSaver().saveHabitants(v);
					j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous êtes désormais membre de la ville. Vous pouvez maintenant acheter une parcelle à l'intérieur.");
					ArrayList<String> membres = v.getInhabitants();
					for (String m : membres) {
						Player p = Bukkit.getPlayer(m);
						if (p != null) {
							p.sendMessage(plugin.TAG+ChatColor.GREEN+"Le joueur "+ChatColor.AQUA+j.getName()+ChatColor.GREEN+" a rejoint votre ville !");
							if (v.canBuild(m) && plugin.v().getPalier(v) != pa)
								p.sendMessage(plugin.TAG+ChatColor.GOLD+"Votre ville est maintenant au palier "+ChatColor.AQUA+plugin.v().getPalier(v).nom+ChatColor.GOLD+" !");
						}
					}
 					
					return true;
				}
				else 
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes pas invité dans cette ville.");
					return true;
				}
			}
			else if (args[0].equals("refuse")) {
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+"Arguments incorrects");
					return true;
				}
				
				Ville v = plugin.v().getVille(args[1]);
				if (v.isInhabitant(j.getName()))
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous êtes dèjà citoyen de cette ville.");
					return true;
				}	
				else if (!v.isPublic() && v.isInvited(j.getName())) {
					v.delInvit(j.getName());
					plugin.getSaver().saveInvits(v);
					j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez bien refusé l'invitation de la ville.");
					return true;
				}
				else 
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes pas invité dans cette ville.");
					return true;
				}
			}
			else if (args[0].equals("compte")) {
				Ville v = null;
				if (args.length == 2) {
					v = plugin.v().getVille(args[1]);
					if (v == null)  {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Cette ville n'existe pas.");
						return true;
					}
				} else {
					v = plugin.v().citizenOf(j.getName());
				}
				
				
				if (v != null) {
					j.sendMessage(plugin.TAG+ChatColor.GREEN+"La ville de "+v.getName()+" a "+ChatColor.AQUA+""+v.getCompte()+" POs");
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes citoyen d'aucune ville.");
				}
				return true;
			}
			else if (args[0].equals("info")) {
				Ville v = null;
				if (args.length == 2) {
					v = plugin.v().getVille(args[1]);
					if (v == null)  {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Cette ville n'existe pas.");
						return true;
					}
				} else {
					v = plugin.v().citizenOf(j.getName());
				}
				
				
				if (v != null) {
					PalierVille p = plugin.v().getPalier(v);
					
					int missing = plugin.v().getPalierSup(v.getNbInhabitants()).minHabs - v.getNbInhabitants();
					j.sendMessage(plugin.TAG+"Ville : "+ChatColor.GREEN+v.getName()+ChatColor.WHITE+", niveau "+ChatColor.GOLD+p.nom+ChatColor.WHITE+", chunks "+ChatColor.GREEN+v.getNbChunks()+ChatColor.DARK_GREEN+"/"+ChatColor.GREEN+p.maxChunks+ChatColor.WHITE+", prix chunk "+ChatColor.GREEN+p.chunkPrice+ChatColor.WHITE+" POs, niveau suivant dans "+ChatColor.RED+""+missing+""+ChatColor.RESET+" habitants. Cette ville est "+((v.isPublic()) ? ChatColor.GREEN+"publique" : ChatColor.RED+"privée"));
					j.sendMessage(ChatColor.AQUA+"Conseillers : "+ChatColor.GREEN+ChatColor.ITALIC+v.getMaire()+ChatColor.RESET+", "+ChatColor.GREEN+StringUtils.join(v.getConseillers(), ChatColor.WHITE+", "+ChatColor.GREEN));
					j.sendMessage(ChatColor.AQUA+"Habitants : "+ChatColor.DARK_GREEN+ChatColor.ITALIC+v.getMaire()+ChatColor.RESET+", "+ChatColor.DARK_GREEN+StringUtils.join(v.getInhabitants(), ChatColor.WHITE+", "+ChatColor.DARK_GREEN));
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes citoyen d'aucune ville.");
				}
				return true;
			}
			else if (args[0].equals("list")) {
				HashMap<String, Ville> v = plugin.v().getVilles();
				j.sendMessage(plugin.TAG+ChatColor.AQUA+"Liste des villes du serveur : ");
				j.sendMessage(ChatColor.AQUA+"Tapez "+ChatColor.GOLD+"/ville join <ville> "+ChatColor.AQUA+"pour rejoindre une ville.");
				j.sendMessage(ChatColor.AQUA+"Tapez "+ChatColor.GOLD+"/ville info <ville> "+ChatColor.AQUA+"pour plus d'infos.");
				j.sendMessage("");
				for (String k : v.keySet()) {
					Ville vi = v.get(k);
					j.sendMessage("- "+ChatColor.GREEN+vi.getName()+ChatColor.AQUA+" (ville "+((vi.isPublic()) ? ChatColor.GREEN+"publique" : ChatColor.RED+"privée")+ChatColor.AQUA+", maire "+ChatColor.GREEN+vi.getMaire()+ChatColor.AQUA+")");
				}
				return true;
			}
			else if (args[0].equals("paliers")) {
				j.sendMessage(plugin.TAG+ChatColor.AQUA+"Liste des palliers du serveur : ");
				for (PalierVille p : plugin.v().getPaliers()) {
					j.sendMessage(" - Palier "+ChatColor.GOLD+p.nom+ChatColor.WHITE+", autorise "+ChatColor.GREEN+""+p.maxChunks+""+ChatColor.WHITE+" chunks (coutant chacun "+ChatColor.GOLD+""+p.chunkPrice+" POs"+ChatColor.RESET+") et nécessite "+ChatColor.GREEN+""+p.minHabs+""+ChatColor.WHITE+" habitants minimum.");
				}
				return true;
			}
			else if (args[0].equals("addadjoint")) {
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Arguments incorrects");
					return true;
				}
				
				String pseudo = args[1];								
				Ville v = plugin.v().citizenOf(j.getName());
				if (v != null) {
					if (v.canBuild(j.getName())) {
						if (v.isInhabitant(pseudo)) {
							if (v.canBuild(pseudo)) {
								j.sendMessage(plugin.TAG+ChatColor.RED+"Le joueur est dèjà conseiller dans votre ville.");
								return true;
							}
							v.addConseiller(pseudo);
							plugin.v().updateVille(v.getName(), v);
							plugin.getSaver().saveVille(v);
							j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez bien ajouté "+ChatColor.AQUA+pseudo+ChatColor.GREEN+" en conseiller dans votre ville.");
						} else {
							j.sendMessage(plugin.TAG+ChatColor.RED+"Le joueur n'appartient pas à votre ville.");
						}
					} else {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'avez pas le droit de faire cela.");
					}
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes citoyen d'aucune ville.");
				}
				return true;
			}
			else if (args[0].equals("deladjoint")) {
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Arguments incorrects");
					return true;
				}
				
				String pseudo = args[1];								
				Ville v = plugin.v().citizenOf(j.getName());
				if (v != null) {
					if (v.canBuild(j.getName())) {
						if (v.canBuild(pseudo)) {
							v.delConseiller(pseudo);
							plugin.v().updateVille(v.getName(), v);
							plugin.getSaver().saveVille(v);
							j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez bien enlevé le conseiller "+ChatColor.AQUA+pseudo+ChatColor.GREEN+" de votre ville.");
						} else {
							j.sendMessage(plugin.TAG+ChatColor.RED+"Le joueur n'est pas conseiller dans votre ville.");
						}
					} else {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'avez pas le droit de faire cela.");
					}
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes citoyen d'aucune ville.");
				}
				return true;
			}
			//sender.sendMessage("- "+TextUtils.FormatHelp("ville", "addadjoint <pseudo>", "Rend le joueur <pseudo> adjoint."));
			//sender.sendMessage("- "+TextUtils.FormatHelp("ville", "deladjoint <pseudo>", "Enlève l'adjoint <pseudo>."));
			else if (args[0].equals("don")) {
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+"Arguments incorrects");
					return true;
				}
				
				double money = 0;
				try {
					money = Double.parseDouble(args[1]);
				} catch (NumberFormatException e) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"La valeur entrée n'est pas valide.");
					return true;
				}
				
								
				Ville v = plugin.v().citizenOf(j.getName());
				if (v != null) {
					if (plugin.getEco().has(j.getName(), money)) {
						plugin.getEco().withdrawPlayer(j.getName(), money);
						v.creditPO(money);
						plugin.v().updateVille(v.getName(), v);
						j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez bien donné "+ChatColor.BLUE+""+money+" POs "+ChatColor.GREEN+" a votre ville.");
					} else {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'avez pas assez d'argent.");
					}
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes citoyen d'aucune ville.");
				}
				return true;
			}
			else if (args[0].equals("reset")) {
				j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : cette commande est désactivée.");
				return true;
				
				/*Ville v = plugin.v().villeParMaire(j.getName());
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous ne disposez d'aucune ville.");
					return true;
				}
				
				Vector<int[]> chunks = v.getChunks();
				int i = 0;
				while (i < chunks.size()) {
					int[] c = chunks.get(i);
					Bukkit.getWorld("world").regenerateChunk(c[0], c[1]);
					i++;
					
				}
				
				plugin.v().deleveVille(v.getName());
				plugin.getServer().broadcastMessage(plugin.TAG+ChatColor.RED+""+j.getName()+" a supprimé sa ville.");
				plugin.getSaver().deleteVille(v.getName());
				
				return true;*/
			}
		

		
		/*if (command.getName().equalsIgnoreCase("ville")) {
			if (args[0].equalsIgnoreCase("createcity")) {
				if (args.length < 5)
					return false;
				ds.nouvelleVille(args[1], args[2], plugin.getServer().getWorld("world").getChunkAt(Integer.parseInt(args[3]), Integer.parseInt(args[4])));
			} else if (args[0].equalsIgnoreCase("addchunk")) {
				if (args.length < 4)
					return false;
				Ville v = plugin.getVilles().get(args[1]);
				v.addChunk(plugin.getServer().getWorld("world").getChunkAt(Integer.parseInt(args[2]), Integer.parseInt(args[3])));
				plugin.updateVille(v.getName(), v);
			} else if (args[0].equalsIgnoreCase("addcons")) {
				if (args.length < 4)
					return false;
				Ville v = plugin.getVilles().get(args[1]);
				Vector<String> conseiller = new Vector<String>();
				conseiller.add(args[2]);
				conseiller.add(args[3]);
				v.addConseiller(conseiller);
				plugin.updateVille(v.getName(), v);
			} else if (args[0].equalsIgnoreCase("afficher")) {
				if (args.length < 3)
					return false;
				Ville v = plugin.getVilles().get(args[1]);
				plugin.getLogger().info("Affichage de la ville");
				plugin.getLogger().info("NOM="+v.getName());
				plugin.getLogger().info("MAIRE="+v.getMaire());
				plugin.getLogger().info("MAIRE-INT="+v.getMaireInterim());
				

			
			}
		}*/
		
		}
		return false;
	}

}
