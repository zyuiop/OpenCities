package net.zyuiop.openCities.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.zyuiop.openCities.OpenCities;
import net.zyuiop.openCities.data.DataSaver;
import net.zyuiop.openCities.data.Parcelle;
import net.zyuiop.openCities.data.Ville;
import net.zyuiop.openCities.utils.TextUtils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandParcelle implements CommandExecutor {
private OpenCities plugin = null;
	
	public CommandParcelle(OpenCities pl) {
		plugin = pl;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		DataSaver ds = plugin.getSaver();
		if (sender instanceof Player) {
			if (args.length == 0)
				return false;
			
			Player j = (Player) sender;
			if (args[0].equalsIgnoreCase("help")) {
				
				sender.sendMessage(ChatColor.GOLD+"=========["+ChatColor.BLUE+"ATC Villes"+ChatColor.GOLD+"]=========");
				sender.sendMessage("");
				
				String[] messages = new String[] {
				TextUtils.FormatHelp("parcelle", "tool", "Donne un objet de gestion de création de parcelles."),
				TextUtils.FormatHelp("parcelle", "hauteurmax", "Agrandit la zone sélectionnée de la hauteur 0 à la hauteur 255."),
				TextUtils.FormatHelp("parcelle", "create <nom> <shop|house|hostel> <prix>", "Crée une parcelle sur la zone sélectionnée.(hostel est une parcelle louable au jour IRL)"),
				TextUtils.FormatHelp("parcelle", "list", "Liste les parcelles de la ville. "),
				TextUtils.FormatHelp("parcelle", "supprimer <nom>", "Supprime la parcelle <nom>"),
				TextUtils.FormatHelp("parcelle", "setprix <nom> <prix>", "Change le prix de la parcelle <nom>"),
				TextUtils.FormatHelp("parcelle", "acheter <nom>", "Achète la parcelle <nom> dans la ville "+ChatColor.BOLD+"dans laquelle vous vous trouvez."),
				TextUtils.FormatHelp("parcelle", "ajouter <parcelle> <pseudo>", "Ajoute le citoyen <pseudo> à la parcelle <parcelle>. La parcelle doit vous appartenir."),
				TextUtils.FormatHelp("parcelle", "info <nom>", "Affiche les infos de la parcelle <nom> dans votre ville."),
				TextUtils.FormatHelp("parcelle", "setpermissions <parcelle> <permission> <valeur>", "Commandes de permissions dans une parcelle. (voir /parcelle setpermissions help)"),
				TextUtils.FormatHelp("parcelle", "revendre <nom> <prix>", "Remet en vente la parcelle <nom> pour <prix> et vous crédite de 90% du prix."),
				//TextUtils.FormatHelp("parcelle", "rent <ville> <parcelle>", "Loue la parcelle <parcelle> de type hostel dans la ville <ville>. Vous paierez tous les jours à minuit IRL le prix journée de la parcelle."),
				TextUtils.FormatHelp("parcelle", "leave <ville> <parcelle>", "Termine la location de la parcelle <parcelle> dans la ville <ville>")};

				int page = 1;
				
				if (args.length > 1) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED+"La page entrée n'est pas un nombre.");
						return true;
					}
					
				}
				

				
				int linesPerPage = 5;
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
				
			} else if (args[0].equals("setprix")) {
				if (args.length != 3) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Nombre d'arguments incorrect.");
					return true;
				}
				
				Ville v = plugin.v().citizenOf(j.getName());
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous ne disposez pas de ville.");
					return true;
				} 
				else if (!v.canBuild(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes pas adjoint ou maire dans votre ville.");
					return true;
				}
				
				Parcelle p = v.getParcelle(args[1]);
				if (p == null)  {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : le nom de parcelle entré n'existe pas dans cette ville.");
					return true;
				}
				
				if (p.getProprio() != null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : cette parcelle appartient dèjà a un joueur.");
					return true;
				}
				
				int prix=  0;
				try {
					prix = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : le prix défini n'est pas valide.");
					return true;
				}
				
				p.setPrix(prix);
				v.setParcelle(p);
				plugin.v().updateVille(v.getName(), v);
				plugin.getSaver().saveParcelles(v);
				
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"Le prix de la parcelle a bien été modifié !");
				return true;
				
			}
			else if (args[0].equals("revendre")) {
				if (args.length != 3) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Nombre d'arguments incorrect.");
					return true;
				}
				
				Ville v = plugin.v().citizenOf(j.getName());
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous ne disposez pas de ville.");
					return true;
				} 
				
				Parcelle p = v.getParcelle(args[1]);
				if (p == null)  {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : le nom de parcelle entré n'existe pas dans cette ville.");
					return true;
				}
				
				if (p.getProprio() == null || !p.getProprio().equals(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : cette parcelle appartient a un autre joueur.");
					return true;
				}
				
				int prix=  0;
				try {
					prix = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : le prix défini n'est pas valide.");
					return true;
				}
				
				p.setPrix(prix);
				p.setProprio(null);
				p.setSellTo(j.getName());
				v.setParcelle(p);
				plugin.v().updateVille(v.getName(), v);
				plugin.getSaver().saveParcelles(v);
				
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"La parcelle a bien été mise en vente ! Vous serez crédité de 90% de son prix lorsqu'elle sera vendure.");
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("tool")) {
				
				/*
				 * Donne l'outil de gestion de parcelles 
				 */
				Ville v = plugin.v().citizenOf(j.getName());
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous ne disposez pas de ville.");
					return true;
				} 
				else if (!v.canBuild(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes pas adjoint ou maire dans votre ville.");
					return true;
				}
				else if (j.getItemInHand().getType() != Material.AIR) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"L'outil ne peut pas vous être donné car vous avez un item en main.");
					return true;
				}
				
				
				// Création de l'item
				ItemStack item = new ItemStack(Material.STICK, 1);
				List<String> lores = new ArrayList<String>();
				lores.add("Permet de délimiter une parcelle");
				lores.add("Clic gauche pour le premier point");
				lores.add("Clic droit pour le second point");
				lores.add("Pour de l'aide : "+ChatColor.GREEN+"/parcelle help");
				ItemMeta im = item.getItemMeta();
				im.setLore(lores);
				im.setDisplayName(ChatColor.GOLD+"Outil de Parcelles");
				item.setItemMeta(im);
				
				// Don de l'item
				j.setItemInHand(item);
				j.sendMessage(plugin.TAG+ChatColor.GOLD+"L'outil de création de parcelle vient de vous être donné.");
				return true;
				
			} else if (args[0].equalsIgnoreCase("hauteurmax")) {
				
				// Récupération des points
				Location p1 = plugin.p.getPoint(0, j.getName());
				Location p2 = plugin.p.getPoint(1, j.getName());
				
				if (p1 == null || p2==null || p1.equals(p2)) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Votre sélection n'est pas valide : vous devez sélectionner 2 points différents.");
					return true;
				}
				
				// Update des points
				p1.setY(1);
				p2.setY(255);
				plugin.p.setPoint(0, j.getName(), p1);
				plugin.p.setPoint(1, j.getName(), p2);
				
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"La sélection à été agrandie verticalement.");
				
				return true;
				
			} else if (args[0].equals("create")) {

				if (args.length != 4) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : nombre d'arguments incorrect.");
					return true;
				} 
				
				// Récup arguments :
				String nom = args[1];
				String type = args[2];
				int prix = 0;
				
				Ville v = plugin.v().citizenOf(j.getName());
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous ne disposez pas de ville.");
					return true;
				} 
				else if (!v.canBuild(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes pas adjoint ou maire dans votre ville.");
					return true;
				} else if (v.getParcelles().containsKey(nom)) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : ce nom de parcelle est dèjà utilisé dans votre ville.");
					return true;
				} else if (!type.equals("shop") && !type.equals("house")) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Le type de parcelle doit être 'shop' ou 'house'");
					return true;
				}
				
				try {
					prix = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Argument 'prix' non reconnu. L'argument doit être un nombre entier.");
					return true;
				}
				
				
				
				// Récupération de la sélection.
				Location p1 = plugin.p.getPoint(0, j.getName());
				Location p2 = plugin.p.getPoint(1, j.getName());
				
				if (p1 == null || p2==null || p1.equals(p2)) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Votre sélection n'est pas valide : vous devez sélectionner 2 points différents.");
					return true;
				}
				
				if (p1.getX() == p2.getX() || p1.getY() == p2.getY() || p1.getZ() == p2.getZ()) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Votre sélection n'est pas valide : vos points ne doivent pas être sur le même axe.");
					return true;
				}
				
				double minX = 0;
				double maxX = 0;
				double minY = 0;
				double maxY = 0;
				double minZ = 0;
				double maxZ = 0;
				
				if (p1.getX() > p2.getX()) {
					minX = p2.getX();
					maxX = p1.getX();
				} else {
					minX = p1.getX();
					maxX = p2.getX();
				}
				
				if (p1.getY() > p2.getY()) {
					minY = p2.getY();
					maxY = p1.getY();
				} else {
					minY = p1.getY();
					maxY = p2.getY();
				}
				
				if (p1.getZ() > p2.getZ()) {
					minZ = p2.getZ();
					maxZ = p1.getZ();
				} else {
					minZ = p1.getZ();
					maxZ = p2.getZ();
				}
				
				
				int x = (int) minX;
				while (x < (int) maxX) {
					int y = (int) minY;
					while (y < (int) maxY){
						int z = (int) minZ;
						while (z < (int) maxZ) {
							Location l = new Location(Bukkit.getWorld("world"), x,y,z);
							
							/*
							 * 
							 * EVENTUELEMENT :
							 * Autoriser les sous parcelles dans les projets
							 * 
							 */
							
							
							if (v.getParcelle(l) != null) {
								j.sendMessage(plugin.TAG+ChatColor.RED+"Une partie de votre sélection est dèjà utilisée.");
								return true;
							}
							
							if (!v.hasChunk(l.getChunk()))
							{
								j.sendMessage(plugin.TAG+ChatColor.RED+"Une partie de votre sélection se trouve hors de la ville.");
								return true;
							}
							z++;
						}
						y++;
					}
					x++;
				}
				
				// Redéfinition des points
				Location minP = new Location(Bukkit.getWorld("world"),minX, minY, minZ);
				Location maxP = new Location(Bukkit.getWorld("world"),maxX, maxY, maxZ);
				
				
				// Ajout de la parcelle
				v.addParcelle(new Parcelle(nom, prix, minP, maxP, null, type));
				plugin.v().updateVille(v.getName(), v);
				ds.saveParcelles(v);
				
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"La parcelle a bien été créée.");
				return true;
			}
			else if (args[0].equals("list")) {
				Ville v = plugin.v().citizenOf(j.getName());
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes membre d'aucune ville");
					return true;
				} 
				
				j.sendMessage(plugin.TAG+ChatColor.AQUA+"Liste des parcelles de la ville "+v.getName());
				HashMap<String, Parcelle> parcelles = v.getParcelles();
				
				int visibility = 2;
				if (args.length == 2)
				{
					switch (args[1]) {
					case "free":
						visibility = 1;
						break;
					
					case "used":
						visibility = 0;
						break;
					}
				}
				
				int n = 0;
				j.sendMessage(ChatColor.AQUA+"Tapez "+ChatColor.GOLD+"/parcelle join <parcelle> "+ChatColor.AQUA+"pour rejoindre une parcelle.");
				j.sendMessage("");
				for (String k : parcelles.keySet()) {
					Parcelle p = parcelles.get(k);
					if (p.getProprio() == null && visibility >= 1)
						j.sendMessage("- Parcelle "+ChatColor.GREEN+"en vente"+ChatColor.WHITE+" '"+ChatColor.AQUA+p.getIdClaim()+ChatColor.RESET+"', de type "+ChatColor.GREEN+p.getType()+ChatColor.RESET+" et de prix "+ChatColor.GREEN+""+p.getPrix()+""+ChatColor.RESET+".");
					
					if (p.getProprio() != null && (visibility == 0 || visibility == 2))
						j.sendMessage("- Parcelle '"+ChatColor.AQUA+p.getIdClaim()+ChatColor.RESET+"', de type "+ChatColor.GREEN+p.getType()+ChatColor.RESET+" et possédée par "+ChatColor.GOLD+p.getProprio());
					n++;
				}
				if (n == 0)
					j.sendMessage(ChatColor.RED+"Aucune parcelle n'est disponible dans cette ville.");
				
				return true;
			}
			else if (args[0].equals("supprimer")) {
				Ville v = plugin.v().citizenOf(j.getName());
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : nombre d'arguments incorrect.");
					return true;
				}

				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous ne disposez pas de ville.");
					return true;
				} 
				else if (!v.canBuild(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'êtes pas adjoint ou maire dans votre ville.");
					return true;
				} 	
				if (v.getParcelles().containsKey(args[1])) {
					v.delParcelle(v.getParcelles().get(args[1]));
					j.sendMessage(plugin.TAG+ChatColor.GREEN+"La parcelle a bien été supprimée.");
					plugin.v().updateVille(v.getName(), v);
					ds.saveParcelles(v);
					return true;
				} else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"La parcelle n'existe pas.");
					return true;
				}
			}
			else if (args[0].equals("acheter")) {
				
				
				Ville v = plugin.v().citizenOf(j.getName());
				
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : nombre d'arguments incorrect.");
					return true;
				}
				
				if (!j.getWorld().getName().equals("world")) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous n'êtes pas sur la bonne map.");
					return true;
				}
				
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Vous n'êtes citoyen d'aucune ville.");
					return true;
				}
				
				Ville test = plugin.v().citizenOf(j.getName());
				
					
				
				if (!v.isInhabitant(j.getName()) && test == null)
				{
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Vous n'êtes pas citoyen de cette ville. Rejoignez la avec "+ChatColor.AQUA+"/ville join "+v.getName());
					return true;
				}
				else if (!v.equals(test)) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Vous habitez dans une autre ville.");
					return true;
				}
				
				Parcelle p = v.getParcelle(args[1]);
				if (p == null)  {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : le nom de parcelle entré n'existe pas dans cette ville.");
					return true;
				}
				
				if (p.getProprio() != null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : cette parcelle appartient dèjà a un joueur.");
					return true;
				}
				
				if (plugin.getEco().has(j.getName(), p.getPrix())) {
						plugin.getEco().withdrawPlayer(j.getName(), p.getPrix());
						int prix = p.getPrix();
						if (p.getSellTo() != null) {
							plugin.getEco().depositPlayer(j.getName(), Math.floor(prix*0.9));
							v.creditPO(p.getPrix()*0.1);
							// On divise le reversement
						}
						else 
							v.creditPO(p.getPrix());
						plugin.v().updateVille(v.getName(), v);
				} else {
						j.sendMessage(plugin.TAG+ChatColor.RED+"Vous n'avez pas assez d'argent.");
						return true;
				}
				
				
				p.setProprio(j.getName());
				v.setParcelle(p);
				plugin.v().updateVille(v.getName(), v);
				plugin.getSaver().saveParcelles(v);
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"Vous avez bien acheté la parcelle "+ChatColor.AQUA+p.getIdClaim());
				
				return true;
			} 
			else if (args[0].equals("ajouter")) {
				Ville v = plugin.v().citizenOf(j.getName());
				if (args.length != 3) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : nombre d'arguments incorrect.");
					return true;
				}
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous n'êtes citoyen d'aucune ville.");
					return true;
				}
				
				Parcelle p = v.getParcelle(args[1]);
				
				if (p == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Cette parcelle n'existe pas.");
					return true;
				}
				
				if (!p.getProprio().equals(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Vous n'êtes pas propriétaire de cette parcelle.");
					return true;
				}
					
				if (!v.isInhabitant(args[2])) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : la personne que vous tentez d'ajouter n'est pas citoyen de la ville.");
					return true;
				}
				
				p.addMembre(args[2]);
				v.setParcelle(p);
				plugin.v().updateVille(v.getName(), v);
				
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"Le joueur a bien été ajouté à votre parcelle.");
				
				return true;
				
			}
			else if(args[0].equals("info")) {
				Ville v = plugin.v().citizenOf(j.getName());
				if (args.length != 2) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : nombre d'arguments incorrect.");
					return true;
				}
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous n'êtes citoyen d'aucune ville.");
					return true;
				}
				
				Parcelle p = v.getParcelle(args[1]);
				
				if (p == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Cette parcelle n'existe pas.");
					return true;
				}
				
				if (p.getProprio() == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Cette parcelle est en vente.");
					return true;
				}
				
				sender.sendMessage(plugin.TAG+ChatColor.AQUA+"Affichage des infos de la parcelle "+ChatColor.GREEN+p.getIdClaim()+ChatColor.AQUA+" :");
				sender.sendMessage(ChatColor.AQUA+"Propriétaire : "+ChatColor.GREEN+p.getProprio());
				sender.sendMessage(ChatColor.AQUA+"Membres : "+ChatColor.GREEN+StringUtils.join(p.getMembres(), ChatColor.AQUA+", "+ChatColor.GREEN));
				HashMap<String, Integer> size = p.getSize();
				sender.sendMessage(ChatColor.AQUA+"Taille : "+size.get("x")+"x"+size.get("z"));
				sender.sendMessage(ChatColor.AQUA+"Permissions : ");
				sender.sendMessage(ChatColor.AQUA+" - Ouvrir portes et trappes : "+((p.getAllowDoors()) ? ChatColor.GREEN+"Autorisé" : ChatColor.RED+"Interdit"));
				sender.sendMessage(ChatColor.AQUA+" - Ouvrir portes de clotures : "+((p.getAllowFenceGates()) ? ChatColor.GREEN+"Autorisé" : ChatColor.RED+"Interdit"));
				sender.sendMessage(ChatColor.AQUA+" - Utiliser leviers et boutons : "+((p.getAllowLeversButtons()) ? ChatColor.GREEN+"Autorisé" : ChatColor.RED+"Interdit"));
				return true;
			}
			else if(args[0].equals("setpermissions")) {
				Ville v = plugin.v().citizenOf(j.getName());
				if (args.length == 2) {
					if (args[1].equals("help")) {
						sender.sendMessage(plugin.TAG+ChatColor.GOLD+"Modification de permissions :");
						sender.sendMessage(ChatColor.GOLD+"Utilisation : "+ChatColor.AQUA+"/parcelle setpermissions <nom parcelle> <nom permission> <droit>");
						sender.sendMessage(ChatColor.GOLD+"Permissions disponnibles : (pour voir leur valeur, utilisez le /parcelle info <parcelle>)");
						sender.sendMessage(ChatColor.GOLD+"- "+ChatColor.AQUA+"doors"+ChatColor.GOLD+" : interdit / autorise l'ouverture des portes et trappes");
						sender.sendMessage(ChatColor.GOLD+"- "+ChatColor.AQUA+"gates"+ChatColor.GOLD+" : interdit / autorise l'ouverture des portes de clôture");
						sender.sendMessage(ChatColor.GOLD+"- "+ChatColor.AQUA+"redstone"+ChatColor.GOLD+" : interdit / autorise l'utilisation des leviers et boutons");
						sender.sendMessage("");
						sender.sendMessage(ChatColor.GOLD+"Droits :");
						sender.sendMessage(ChatColor.GREEN+"- autoriser : enabled OU yes OU true OU oui");
						sender.sendMessage(ChatColor.RED+"- refuser : disabled OU no OU false OU non");
						return true;
					}
				}
				
				if (args.length != 4) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : nombre d'arguments incorrect.");
					return true;
				}
				
				if (v == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : vous n'êtes citoyen d'aucune ville.");
					return true;
				}
				
				Parcelle p = v.getParcelle(args[1]);
				
				if (p == null) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Cette parcelle n'existe pas.");
					return true;
				}
				
				if (!p.getProprio().equals(j.getName())) {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : Vous n'êtes pas propriétaire de cette parcelle.");
					return true;
				}
				
				String perm = args[2];
				String val = args[3];
				boolean bval = false;
				if (val.equals("enabled") || val.equals("yes") || val.equals("oui") || val.equals("true"))
					bval = true;
				else if (val.equals("disabled") || val.equals("no") || val.equals("non") || val.equals("false"))
					bval = false;
				else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : cette valeur de droit n'existe pas.");
					return true;
				}
				
				if (perm.equals("doors")) 
					p.setAllowDoors(bval);
				else if (perm.equals("gates"))
					p.setAllowFenceGates(bval);
				else if (perm.equals("redstone"))
					p.setAllowLeversButtons(bval);
				else {
					j.sendMessage(plugin.TAG+ChatColor.RED+"Erreur : la permission entrée n'existe pas.");
					return true;
				}
				v.setParcelle(p);
				plugin.v().updateVille(v.getName(), v);
				j.sendMessage(plugin.TAG+ChatColor.GREEN+"La permission a bien été modifiée.");
				
				return true;
				
			}

		}
		return false;
	}
}
