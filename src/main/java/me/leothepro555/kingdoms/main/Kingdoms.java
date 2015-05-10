package me.leothepro555.kingdoms.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import me.leothepro555.kingdoms.events.PlayerChangeChunkEvent;
import me.leothepro555.kingdoms.events.PlayerJoinKingdomEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;


public class Kingdoms extends JavaPlugin implements Listener{

	public boolean hasWorldGuard = false;
	public boolean noRegionClaiming = true;
	public WorldEditTools wet;
	public AsciiCompass compass = new AsciiCompass();
	
	public Kingdoms(){
		
	}
	
	public void onEnable(){
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new KingdomPowerups(this), this);
		pm.registerEvents(new NexusBlockManager(this), this);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		this.kingdoms.options().copyDefaults(false);
		saveKingdoms();
		this.land.options().copyDefaults(false);
		saveClaimedLand();
		this.players.options().copyDefaults(false);
		savePlayers();
		this.powerups.options().copyDefaults(false);
		savePowerups();
		this.misupgrades.options().copyDefaults(false);
		saveMisupgrades();
		int num = 0;
		for(String kingdom: kingdoms.getKeys(false)){
			num++;
			if(kingdoms.getString(kingdom + ".nexus-block") != null){
			if(hasNexus(kingdom)){
			if(stringToLocation(kingdoms.getString(kingdom + ".nexus-block")) != null){
				
			Location loc = stringToLocation(kingdoms.getString(kingdom + ".nexus-block"));
		
			Block b = loc.getBlock();
			b.setMetadata("nexusblock", new FixedMetadataValue(this, "ok."));
		}
		}
		}else{
			kingdoms.set(kingdom + ".nexus-block", 0);
			Bukkit.getLogger().severe(ChatColor.RED + "The kingdom in your config file, " + kingdom + " is corrupted. "
					+ "Did you modify the kingdoms.yml file lately? If " + kingdom + " is not supposed to exist, "
					+ "delete it in the kingdoms.yml file.");
		}
			
			if(misupgrades.get(kingdom + ".anticreeper") == null){
				misupgrades.set(kingdom + ".antitrample", false);
				misupgrades.set(kingdom + ".anticreeper", false);
				misupgrades.set(kingdom + ".nexusguard", false);
				misupgrades.set(kingdom + ".glory", false);
				misupgrades.set(kingdom + ".bombshards", false);
				saveMisupgrades();
			}
			
		}
		Bukkit.getLogger().info("Loaded " + num + " kingdoms.");
		if(getWorldGuard() != null){
			this.hasWorldGuard = true;
			Bukkit.getLogger().info("World Guard found, enabling WorldGuard support.");
			wet = new WorldEditTools(this);
		}else{
			this.hasWorldGuard = false;
			Bukkit.getLogger().info("World Guard not found, disabling WorldGuard support.");
		}
		
		if(getConfig().getBoolean("no-region-claim")){
			this.noRegionClaiming = true;
		}else{
			this.noRegionClaiming = false;
		}
		
		for(String s: getConfig().getStringList("resource-point-trade-blacklist")){
			if(Material.getMaterial(s) != null){
				blacklistitems.add(Material.getMaterial(s));
			}else{
				Bukkit.getLogger().severe(ChatColor.RED + "Your Material, " + s + " typed under the trade blacklist is invalid!");
			}
		}
		
		for(String s: getConfig().getStringList("special-item-cases")){
			
			String[] split = s.split(",");
			if(split.length == 2){
			if(Material.getMaterial(split[0]) != null){
				try{
				specialcaseitems.put(Material.getMaterial(split[0]), Integer.parseInt(split[1]));
				}catch(NumberFormatException e){
					Bukkit.getLogger().severe(ChatColor.RED + "Your Material, " + s + " typed under the trade speciallist is invalid!");
				}
			}else{
				Bukkit.getLogger().severe(ChatColor.RED + "Your Material, " + s + " typed under the trade speciallist is invalid!");
			}
			}else{
				Bukkit.getLogger().severe(ChatColor.RED + "Your Material, " + s + " typed under the trade speciallist is invalid!");
			}
		}
		
		}
	HashMap<Material, Integer> specialcaseitems = new HashMap<Material, Integer>();
	ArrayList<Material> blacklistitems = new ArrayList<Material>();
	HashMap<UUID, Location> click1 = new HashMap<UUID, Location>();
	HashMap<UUID, Location> click2 = new HashMap<UUID, Location>();
	ArrayList<UUID> placingnexusblock = new ArrayList<UUID>();
	HashMap<UUID, Integer> immunity = new HashMap<UUID, Integer>();
	HashMap<UUID, Chunk> champions = new HashMap<UUID, Chunk>();
	HashMap<UUID, String> nexusguards = new HashMap<UUID, String>();
	HashMap<UUID, UUID> duelpairs = new HashMap<UUID, UUID>();
	ArrayList<UUID> adminmode = new ArrayList<UUID>();
	ArrayList<UUID> mapmode = new ArrayList<UUID>();
	ArrayList<UUID> rapidclaiming = new ArrayList<UUID>();
	
	public Plugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    if (plugin == null) {
	        return null; 
	    }
	 
	    return plugin;
	}
	
	public boolean hasWorldEdit(){
		if(Bukkit.getPluginManager().getPlugin("WorldEdit") != null){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("k") ||
				cmd.getName().equalsIgnoreCase("kingdom")||
				cmd.getName().equalsIgnoreCase("kingdoms")){
			if(isValidWorld(p.getWorld())){
			
			if(args.length <= 0){
				p.sendMessage(ChatColor.LIGHT_PURPLE + "===Kingdoms Commands===");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k " + ChatColor.AQUA + "shows all commands");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k nexus " + ChatColor.AQUA + "changes a block into your kingdom's nexus block");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k info " + ChatColor.AQUA + "shows how Kingdoms work");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k join " + ChatColor.AQUA + "joins a Kingdom. Must be invited");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k leave " + ChatColor.AQUA + "leaves your Kingdom.");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k create [kingdom name] " + ChatColor.AQUA + "creates a kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k claim " + ChatColor.AQUA + "claims a patch of land for your kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k unclaim " + ChatColor.AQUA + "unclaims a patch of land from your kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k invade " + ChatColor.AQUA + "challenges the champion of an enemy kingdom for a piece of their land.");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k show [kingdom] " + ChatColor.AQUA + "shows a kingdom's info");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k king [player] " + ChatColor.AQUA + "hands leadership of the kingdom to another player");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k mod [player] " + ChatColor.AQUA + "promotes another player to a mod of your kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k demote [player] " + ChatColor.AQUA + "demotes a mod back to a normal member");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k kick [player] " + ChatColor.AQUA + "kicks a player from your kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k invite [player] " + ChatColor.AQUA + "invites a player to your kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k uninvite [player] " + ChatColor.AQUA + "uninvites a player to your kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k sethome " + ChatColor.AQUA + "sets the home of your kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k home " + ChatColor.AQUA + "Goes to your kingdom home");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k ally [kingdom] " + ChatColor.AQUA + "Sends an ally request to another kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k enemy [kingdom] " + ChatColor.AQUA + "Marks another kingdom as an enemy");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k neutral [kingdom] " + ChatColor.AQUA + "Marks another kingdom as neutral");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "/k disband " + ChatColor.AQUA + "Disbands your kingdom.");
				
				if(p.hasPermission("kingdoms.map")){
					p.sendMessage(ChatColor.LIGHT_PURPLE + "/k map " + ChatColor.AQUA + "Shows a map, showing the land surrounding you.");
				}
				
				if(p.hasPermission("kingdoms.admin")){
					p.sendMessage(ChatColor.RED + "===Admin Commands===");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin toggle " + ChatColor.AQUA + "allows you to harm anyone in any land, and allows you to break/place blocks in any land.");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin disband [kingdom] " + ChatColor.AQUA + "Forcefully disband a kingdom");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin safezone " + ChatColor.AQUA + "claims current piece of land as a safezone.");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin warzone " + ChatColor.AQUA + "claims current piece of land as a warzone.");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin unclaim " + ChatColor.AQUA + "forcefully unclaims a claimed piece of land. Can also be used to unclaim warzones and safezones");
				 	p.sendMessage(ChatColor.DARK_PURPLE + "/k admin show [kingdom/player name] " + ChatColor.AQUA + "shows all info on the specified kingdom/player");
				 	p.sendMessage(ChatColor.DARK_PURPLE + "/k admin rp [kingdom] [amount] " + ChatColor.AQUA + "adds or subtracts resourcepoints from the specified kingdom. To subtract, put a minus '-' in front of the amount. A kingdom's "
				 			+ " rp cannot go below 0");
				}
				
			}else if(args[0].equalsIgnoreCase("admin")){
				if(p.hasPermission("kingdoms.admin")){
		      if(args.length == 1){
					p.sendMessage(ChatColor.RED + "===Admin Commands===");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin toggle " + ChatColor.AQUA + "allows you to harm anyone in any land, and allows you to break/place blocks in any land.");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin disband [kingdom] " + ChatColor.AQUA + "Forcefully disband a kingdom");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin selectsafezone " + ChatColor.AQUA + "Select safezone through worldedit selection. Claims the chunks of the selected blocks. (Region may be larger than expected.)");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin selectwarzone " + ChatColor.AQUA + "Select warzone through worldedit selection. Claims the chunks of the selected blocks. (Region may be larger than expected.)");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin safezone " + ChatColor.AQUA + "claims current piece of land as a safezone.");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin warzone " + ChatColor.AQUA + "claims current piece of land as a warzone.");
					p.sendMessage(ChatColor.DARK_PURPLE + "/k admin unclaim " + ChatColor.AQUA + "forcefully unclaims a claimed piece of land. Can also be used to unclaim warzones and safezones");
				 	p.sendMessage(ChatColor.DARK_PURPLE + "/k admin show [kingdom/player name] " + ChatColor.AQUA + "shows all info on the specified kingdom/player");
				 	p.sendMessage(ChatColor.DARK_PURPLE + "/k admin rp [kingdom] [amount] " + ChatColor.AQUA + "adds or subtracts resourcepoints from the specified kingdom. To subtract, put a minus '-' in front of the amount. A kingdom's "
				 			+ " rp cannot go below 0");
		      }
		      if(args.length >= 2){
		      if(args[1].equalsIgnoreCase("toggle")){
		    	  if(adminmode.contains(p.getUniqueId())){
		    		  adminmode.remove(p.getUniqueId());
		    		  p.sendMessage(ChatColor.RED + "[Kingdoms] Admin mode disabled");
		    	  }else{
		    		  adminmode.add(p.getUniqueId());
		    		  p.sendMessage(ChatColor.GREEN + "[Kingdoms] Admin mode enabled");
		    	  }
		      }else if(args[1].equalsIgnoreCase("safezone")){
		    		  claimSafezoneCurrentPosition(p);
		      }else if(args[1].equalsIgnoreCase("disband")){
	    		  if(args.length == 3){
	    			  if(getConfig().getString(args[2] + ".king") != null){
	    				  disbandKingdom(args[2]);
	    			  }else{
	    				  p.sendMessage(ChatColor.RED + "That kingdom doesn't exist!");
	    			  }
	    		  }else{
	    			  p.sendMessage(ChatColor.RED + "Usage: /k admin disband [kingdom]");
	    		  }
	      }else if(args[1].equalsIgnoreCase("warzone")){
		    		  claimWarzoneCurrentPosition(p);
		    	  
		      }else if(args[1].equalsIgnoreCase("selectsafezone")){
	    		 if(hasWorldEdit()){
	    			 WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	    			 if(worldedit.getSelection(p) != null){
	    				 for(Chunk c: wet.getRegionChunks(p)){
	    					 this.claimSafezoneChunk(c);
	    				 }
	    				 p.sendMessage(ChatColor.GREEN + "Safezone claimed.");
	    			 }else{
	    				 p.sendMessage(ChatColor.RED + "You don't have a worldedit selection.");
	    			 }
	    		 }
		    	  
	      }else if(args[1].equalsIgnoreCase("selectwarzone")){
	    	  if(hasWorldEdit()){
	    			 WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	    			 if(worldedit.getSelection(p) != null){
	    				 for(Chunk c: wet.getRegionChunks(p)){
	    					 this.claimWarzoneChunk(c);
	    				 }
	    				 p.sendMessage(ChatColor.GREEN + "Warzone claimed.");
	    			 }else{
	    				 p.sendMessage(ChatColor.RED + "You don't have a worldedit selection.");
	    			 }
	    		 }
	    	  
      }else if(args[1].equalsIgnoreCase("unclaimselection")){
    	  if(hasWorldEdit()){
 			 WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
 			 if(worldedit.getSelection(p) != null){
 				 for(Chunk c: wet.getRegionChunks(p)){
 					 this.emptyCurrentPosition(c);
 				 }
 				 p.sendMessage(ChatColor.GREEN + "Land in selection force unclaimed.");
 			 }else{
 				 p.sendMessage(ChatColor.RED + "You don't have a worldedit selection.");
 			 }
 		 }
 	  
}else if(args[1].equalsIgnoreCase("unclaim")){
		    	  if(getChunkKingdom(p.getLocation().getChunk()) != null){
		    		  forceUnclaimCurrentPosition(p);
		    	  }
		      }else if(args[1].equalsIgnoreCase("rp")){
		    	  if(args.length == 4){
		    		  if(kingdoms.getKeys(false).contains(args[2])){
		    			  int amount = 0;
		    			  try{
		    				  amount = Integer.parseInt(args[3]);
		    			  }catch(NumberFormatException e){
		    				  p.sendMessage(ChatColor.RED + "Your the resoure point amount must be an Integer!");
		    			  }
		    			  
		    			  if(amount < 0){
		    				 this.minusRP(args[2], amount * -1);
		    				  p.sendMessage(ChatColor.GREEN + "" + amount + " resource points deducted from " + args[2]);
		    			  }else if(amount > 0){
		    				 this.addRP(args[2], amount);
		    				  p.sendMessage(ChatColor.GREEN + "" + amount + " resource points added to " + args[2]);
		    			  }else if(amount == 0){
		    				  this.addRP(args[2], amount);
		    				  p.sendMessage(ChatColor.GREEN + "0 resource points added to " + args[2]);
		    			  }
		    			  
		    			  
		    		  }
		    	  }
		      }else if(args[1].equalsIgnoreCase("show")){
					if(args.length == 3){
						String kingdom = "";
						if(kingdoms.getKeys(false).contains(args[2])){
						kingdom = args[2];	

					}else if(Bukkit.getOfflinePlayer(args[2]) != null){
						if(hasKingdom(Bukkit.getOfflinePlayer(args[2]))){
							kingdom = getKingdom(Bukkit.getOfflinePlayer(args[2]));
						}
					}
						
						String allies = "";
						String tallies = "";
						String enemies = "";
						String tenemies = "";
						
						for(String s:kingdoms.getStringList(kingdom + ".enemies")){
							tenemies = enemies;
							enemies = tenemies +" " + s;
						}
						for(String s:kingdoms.getStringList(kingdom + ".allies")){
							tallies = allies;
							allies = tallies +" " + s;
						}
								
						p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==[" +kingdom+ "]==--=-=-=-=-=");
						p.sendMessage(ChatColor.AQUA + "| King: " + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(kingdom + ".king")))).getName());
						p.sendMessage(ChatColor.AQUA + "| Might: " + kingdoms.getInt(kingdom + ".might"));
						p.sendMessage(ChatColor.AQUA + "|" + " Allies:" + ChatColor.GREEN + allies);
						p.sendMessage(ChatColor.AQUA + "|" + " Enemies:" + ChatColor.RED + enemies);
						p.sendMessage(ChatColor.AQUA + "|" + " Resource Points: " + this.getRp(kingdom));
						p.sendMessage(ChatColor.AQUA + "|" + " Nexus Location: " + locationToString(this.getNexusLocation(kingdom)));
						p.sendMessage(ChatColor.AQUA + "|" + " Home Location: " + kingdoms.getString(kingdom + ".home"));
						p.sendMessage(ChatColor.AQUA + "|");
						p.sendMessage(ChatColor.AQUA + "|     " + ChatColor.UNDERLINE +"Members" );
						p.sendMessage(ChatColor.AQUA + "|");
						p.sendMessage(ChatColor.AQUA + "|" + ChatColor.GREEN + " Online" + ChatColor.WHITE + " | " + ChatColor.RED + "Offline");
					    
						
						String kping = "";
						
			         if(Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(kingdom + ".king")))).isOnline()){
							
							kping = ChatColor.GREEN + "☀";
						}else{
							kping = ChatColor.RED + "☀";
						}
						
						p.sendMessage(ChatColor.AQUA + "| " + kping + "[King]" + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(kingdom + ".king")))).getName() + "");
						
						
						for(String s:kingdoms.getStringList(kingdom + ".members")){

							String ign = players.getString(s + ".ign");
							String ping = "☀";
							if(Bukkit.getOfflinePlayer(UUID.fromString(s)).isOnline()){
								ping = ChatColor.GREEN + "☀";
							}else{
								ping = ChatColor.RED + "☀";
							}
							
							
							
							if(isMod(kingdom, Bukkit.getOfflinePlayer(UUID.fromString(s)))){
								
								p.sendMessage(ChatColor.AQUA + "| " + ping + "[Mod] " + ign);
								
							}else{
								p.sendMessage(ChatColor.AQUA + "| " + ping + ign);
							}
						}
						p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==-==-==--=-=-=-=-=");
						
						
					}
		      }
				}
			}else{
				p.sendMessage(ChatColor.RED + "You don't have the permission to use this command.");
			}
	     	}else if(args[0].equalsIgnoreCase("info")){
				
				p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "======Kingdoms======");
				
				p.sendMessage(ChatColor.LIGHT_PURPLE + "Kingdoms allows a player to create a kingdom.");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "Do /k create [kingdomname] to create your kingdom");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "Once you're done, you can try /k show to see your kingdom's information.");
				p.sendMessage(ChatColor.LIGHT_PURPLE + "This will show your resource points, members of your kingdom, your kingdom's might, enemies and allies."
						+ " You can also do /k show [kingdom name /player name] to gather information on another kingdom. However, their enemies, allies and "
						+ "resource points will be kept secret.");
				
				
				p.sendMessage(ChatColor.LIGHT_PURPLE + "The next step is to place the nexus block somewhere safe. This should be near your kingdom home. "
						+ "The nexus block is the center of your operations: You can convert blocks to resourcepoints, upgrade kingdom bonuses"
						+ " and upgrade champion stats");
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "An enemy can steal up to 20 resource points each mine. Nexus blocks can be raided"
						+ " even if your nexus block is on your location");
				
				p.sendMessage(ChatColor.GOLD + "Kingdom might is how powerful your kingdom is. Might grows as you have more land");
	
				p.sendMessage(ChatColor.GREEN + "Allies can be made with /k ally [kingdom]. Allies can donate"
						+ " to each other's nexuses and cannot damage each other.");
				
				p.sendMessage(ChatColor.GRAY + "Can't trust another kingdom as an ally? Want to remove an enemy from your"
						+ " blacklist? Do /k neutral [kingdom]. This will remove a kingdom from your ally list or enemy list.");
				
				p.sendMessage(ChatColor.RED + "Enemies can be made with /k enemy [kingdom]. Doing so will mark a kingdom as"
						+ " hostile, allowing both sides to harm each other. When an enemy mines your nexus, 20"
						+ " resource points will be lost instead of 10");
				
				
				
				
				p.sendMessage(ChatColor.AQUA + "The members of your kingdom show the number of players in your kingdom. "
						+ "Players can help to collect your resources, build, open chests, furnaces and other amenities. Players can be "
						+ "invited to your kingdom with /k invite [playername] "
						+ "be careful who you invite as spies in your kingdom can do a lot of internal damage.");
				
				p.sendMessage(ChatColor.RED + "To claim another kingdom's land, you will have to duel with their champion with"
						+ " /k invade. If you win, you can claim the land. If you lose, you leave empty-handed. Your champion's strength can be upgraded in the nexus block. "
						+ "When you are online, you can fight your opponent with your champion in an event of an invasion. Your champion "
						+ "will gain better stats, depending on the number of players fighting him.");

			
			
			}else if(args[0].equalsIgnoreCase("create")){
				if(!hasKingdom(p)){
				if(args.length == 2){
	    		if(kingdoms.get(args[1]) == null){
	    			if(!args[1].contains(" ")){
	    				if(!args[1].equalsIgnoreCase("SafeZone")&&
	    						!args[1].equalsIgnoreCase("WarZone")){
					newKingdom(p.getUniqueId(), args[1]);
					p.sendMessage(ChatColor.GREEN + "You created a new kingdom: " + args[1]);
					players.set(p.getUniqueId().toString() + ".kingdom", args[1]);
					savePlayers();
					Bukkit.broadcastMessage(ChatColor.DARK_RED + "A new kingdom, " + args[1] + " has been founded by " + p.getName());
	    			}
	    			}else{
	    			p.sendMessage(ChatColor.RED + "No spaces allowed in kingdom name");
	    		}
	    		}else{
					p.sendMessage(ChatColor.RED + args[1] + " already exists!");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k create [kingdomname]");
				}
			}else{
				p.sendMessage(ChatColor.RED + "You already have a kingdom! You must leave before creating a new Kingdom");
			}
			}else if(args[0].equalsIgnoreCase("nexus")){
				if(hasKingdom(p)){
					if(isKing(p)){
						placingnexusblock.add(p.getUniqueId());
						p.sendMessage(ChatColor.RED + "=======================================");
						p.sendMessage(ChatColor.RED + "");
						p.sendMessage(ChatColor.BLUE + "Right click to replace a clicked block with your nexus. Left click to cancel. Be careful"
								+ " not to click on chests/important blocks.");
						p.sendMessage(ChatColor.RED + "");
						p.sendMessage(ChatColor.RED + "=======================================");
					}else{
						p.sendMessage(ChatColor.RED + "You must be your kingdom's king to set your nexus!");
					}
				}else{
					p.sendMessage(ChatColor.RED + "You don't have a kingdom!");
				}
			}else if(args[0].equalsIgnoreCase("claim")){
				if(hasKingdom(p)){
				
             if(isMod(getKingdom(p), p) || isKing(p)){
				claimCurrentPosition(p);
			}else{
				p.sendMessage(ChatColor.RED + "Your rank is too low to claim land!");
			}
             
				
			}else{
				p.sendMessage(ChatColor.RED + "You are not in a kingdom!");
			}
			}else if(args[0].equalsIgnoreCase("autoclaim")){
				if(hasKingdom(p)){
					
		             if(isMod(getKingdom(p), p) || isKing(p)){
						claimCurrentPosition(p);
						 if(!rapidclaiming.contains(p.getUniqueId())){
			            	 rapidclaiming.add(p.getUniqueId());
			            	 p.sendMessage(ChatColor.GREEN + "Enabled auto-claiming");
			            	 }else{
			            		 rapidclaiming.remove(p.getUniqueId());
			            		 p.sendMessage(ChatColor.RED + "Disabled auto-claiming");
			            	 }
					}else{
						p.sendMessage(ChatColor.RED + "Your rank is too low to claim land!");
					}
		             
		            
		             
						
					}else{
						p.sendMessage(ChatColor.RED + "You are not in a kingdom!");
					}
					}else if(args[0].equalsIgnoreCase("unclaim")){
				if(hasKingdom(p)){
		             if(isMod(getKingdom(p), p) || isKing(p)){			
		            	 if(!this.isNexusChunk(p.getLocation().getChunk())){
						unclaimCurrentPosition(p);
		             }else{
		            	 p.sendMessage(ChatColor.RED + "You can't unclaim your nexus land! You must move your nexus with /k nexus before unclaiming this patch of land!");
		             }
					}else{
						p.sendMessage(ChatColor.RED + "Your rank is too low to claim land!");
					}
					}else{
						p.sendMessage(ChatColor.RED + "You are not in a kingdom!");
					}
					}else if(args[0].equalsIgnoreCase("show")){
				if(args.length == 1){
					if(hasKingdom(p)){
						
				String allies = "";
				String tallies = "";
				String enemies = "";
				String tenemies = "";
				
				for(String s:kingdoms.getStringList(getKingdom(p) + ".enemies")){
					tenemies = enemies;
					enemies = tenemies +" " + s;
				}
				for(String s:kingdoms.getStringList(getKingdom(p) + ".allies")){
					tallies = allies;
					allies = tallies +" " + s;
				}
				
				p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==[" +getKingdom(p)+ "]==--=-=-=-=-=");
				p.sendMessage(ChatColor.AQUA + "| King: " + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(getKingdom(p) + ".king")))).getName());
				p.sendMessage(ChatColor.AQUA + "| Might: " + kingdoms.getInt(getKingdom(p) + ".might"));
				p.sendMessage(ChatColor.AQUA + "|" + " Allies:" + ChatColor.GREEN + allies);
				p.sendMessage(ChatColor.AQUA + "|" + " Enemies:" + ChatColor.RED + enemies);
				p.sendMessage(ChatColor.AQUA + "|");
				p.sendMessage(ChatColor.AQUA + "|     " + ChatColor.UNDERLINE +"Members" );
				p.sendMessage(ChatColor.AQUA + "|");
				p.sendMessage(ChatColor.AQUA + "|" + ChatColor.GREEN + " Online" + ChatColor.WHITE + " | " + ChatColor.RED + "Offline");
			    
				
				String kping = "";
				
	           if(Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(getKingdom(p) + ".king")))).isOnline()){
					
					kping = ChatColor.GREEN + "☀";
				}else{
					kping = ChatColor.RED + "☀";
				}
				
				p.sendMessage(ChatColor.AQUA + "| " + kping + "[King]" + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(getKingdom(p) + ".king")))).getName() + "");
				
				
				for(String s:kingdoms.getStringList(getKingdom(p) + ".members")){

					String ign = players.getString(s + ".ign");
					String ping = "☀";
					if(Bukkit.getOfflinePlayer(UUID.fromString(s)).isOnline()){
						ping = ChatColor.GREEN + "☀";
					}else{
						ping = ChatColor.RED + "☀";
					}
					
					
					
					if(isMod(getKingdom(p), Bukkit.getOfflinePlayer(UUID.fromString(s)))){
						
						p.sendMessage(ChatColor.AQUA + "| " + ping + "[Mod] " + ign);
						
					}else{
						p.sendMessage(ChatColor.AQUA + "| " + ping + ign);
					}
				}
				p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==-==-==--=-=-=-=-=");
				}else{
					p.sendMessage(ChatColor.RED + "You don't have a kingdom");
				}
			}else if(args.length ==2){
				if(kingdoms.getKeys(false).contains(args[1])){
					
					p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==[" +args[1]+ "]==--=-=-=-=-=");
					p.sendMessage(ChatColor.AQUA + "| King: " + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(args[1] + ".king")))).getName());
					p.sendMessage(ChatColor.AQUA + "| Might: " + kingdoms.getInt(args[1] + ".might"));
					p.sendMessage(ChatColor.AQUA + "|");
					p.sendMessage(ChatColor.AQUA + "|     " + ChatColor.UNDERLINE +"Members" );
					p.sendMessage(ChatColor.AQUA + "|");
					p.sendMessage(ChatColor.AQUA + "|" + ChatColor.GREEN + " Online" + ChatColor.WHITE + " | " + ChatColor.RED + "Offline");
				
					
					String kping = "";
					
		           if(Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(args[1] + ".king")))).isOnline()){
						
						kping = ChatColor.GREEN + "☀";
					}else{
						kping = ChatColor.RED + "☀";
					}
					
					p.sendMessage(ChatColor.AQUA + "| " + kping + "[King]" + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(args[1] + ".king")))).getName() + "");
					
					
					for(String s:kingdoms.getStringList(args[1] + ".members")){

						String ign = players.getString(s + ".ign");
						String ping = "☀";
						if(Bukkit.getOfflinePlayer(UUID.fromString(s)).isOnline()){
							ping = ChatColor.GREEN + "☀";
						}else{
							ping = ChatColor.RED + "☀";
						}
						
						
						
						if(isMod(args[1], Bukkit.getOfflinePlayer(UUID.fromString(s)))){
							
							p.sendMessage(ChatColor.AQUA + "| " + ping + "[Mod] " + ign);
							
						}else{
							p.sendMessage(ChatColor.AQUA + "| " + ping + ign);
						}
					}
					p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==-==-==--=-=-=-=-=");
					
				}else{
					if(!args[1].equalsIgnoreCase(p.getName())){
					if(Bukkit.getOfflinePlayer(args[1]) != null){
						
						String kingdom = getKingdom(Bukkit.getOfflinePlayer(args[1]));
						if(this.kingdoms.getKeys(false).contains(kingdom)){
							
								p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==[" +kingdom+ "]==--=-=-=-=-=");
								p.sendMessage(ChatColor.AQUA + "| King: " + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(kingdom + ".king")))).getName());
								p.sendMessage(ChatColor.AQUA + "| Might: " + kingdoms.getInt(kingdom + ".might"));
								p.sendMessage(ChatColor.AQUA + "|");
								p.sendMessage(ChatColor.AQUA + "|     " + ChatColor.UNDERLINE +"Members" );
								p.sendMessage(ChatColor.AQUA + "|");
								p.sendMessage(ChatColor.AQUA + "|" + ChatColor.GREEN + " Online" + ChatColor.WHITE + " | " + ChatColor.RED + "Offline");
							
								
								String kping = "";
								
					           if(Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(kingdom + ".king")))).isOnline()){
									
									kping = ChatColor.GREEN + "☀";
								}else{
									kping = ChatColor.RED + "☀";
								}
								
								p.sendMessage(ChatColor.AQUA + "| " + kping + "[King]" + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(kingdom + ".king")))).getName() + "");
								
								
								for(String s:kingdoms.getStringList(kingdom + ".members")){

									String ign = players.getString(s + ".ign");
									String ping = "☀";
									if(Bukkit.getOfflinePlayer(UUID.fromString(s)).isOnline()){
										ping = ChatColor.GREEN + "☀";
									}else{
										ping = ChatColor.RED + "☀";
									}
									
									
									
									if(isMod(kingdom, Bukkit.getOfflinePlayer(UUID.fromString(s)))){
										
										p.sendMessage(ChatColor.AQUA + "| " + ping + "[Mod] " + ign);
										
									}else{
										p.sendMessage(ChatColor.AQUA + "| " + ping + ign);
									}
								}
								p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==-==-==--=-=-=-=-=");
								
						
							
					}else{
						p.sendMessage(ChatColor.RED + "The player " + args[1] + " does not have a kingdom");
					}
					}else{
						p.sendMessage(ChatColor.RED + "The player or kingdom " + args[1] + " does not exist or is offline");
					}
							}else{
								
								if(hasKingdom(p)){
									p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==[" +getKingdom(p)+ "]==--=-=-=-=-=");
									p.sendMessage(ChatColor.AQUA + "| King: " + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(getKingdom(p) + ".king")))).getName());
									p.sendMessage(ChatColor.AQUA + "| Might: " + kingdoms.getInt(getKingdom(p) + ".might"));
									p.sendMessage(ChatColor.AQUA + "|");
									p.sendMessage(ChatColor.AQUA + "|     " + ChatColor.UNDERLINE +"Members" );
									p.sendMessage(ChatColor.AQUA + "|");
									p.sendMessage(ChatColor.AQUA + "|" + ChatColor.GREEN + " Online" + ChatColor.WHITE + " | " + ChatColor.RED + "Offline");
								
									
									String kping = "";
									
						           if(Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(getKingdom(p) + ".king")))).isOnline()){
										
										kping = ChatColor.GREEN + "☀";
									}else{
										kping = ChatColor.RED + "☀";
									}
									
									p.sendMessage(ChatColor.AQUA + "| " + kping + "[King]" + Bukkit.getOfflinePlayer((UUID.fromString(kingdoms.getString(getKingdom(p) + ".king")))).getName() + "");
									
									
									for(String s:kingdoms.getStringList(getKingdom(p) + ".members")){

										String ign = players.getString(s + ".ign");
										String ping = "☀";
										if(Bukkit.getOfflinePlayer(UUID.fromString(s)).isOnline()){
											ping = ChatColor.GREEN + "☀";
										}else{
											ping = ChatColor.RED + "☀";
										}
										
										
										
										if(isMod(getKingdom(p), Bukkit.getOfflinePlayer(UUID.fromString(s)))){
											
											p.sendMessage(ChatColor.AQUA + "| " + ping + "[Mod] " + ign);
											
										}else{
											p.sendMessage(ChatColor.AQUA + "| " + ping + ign);
										}
									}
									p.sendMessage(ChatColor.AQUA + "=-=-=-=-=--==-==-==--=-=-=-=-=");
									}else{
										p.sendMessage(ChatColor.RED + "You don't have a kingdom");
									}
								
								
							}
				}
			}
			}else if(args[0].equalsIgnoreCase("join")){
				
			if(args.length == 2){	
				if(kingdoms.getKeys(false).contains(args[1])){
				if(p.hasMetadata("kinv " + args[1])){
					if(!hasKingdom(p)){
					joinKingdom(args[1], p);
					p.sendMessage(ChatColor.GREEN + "Joined " + args[1]);
				}else{
					p.sendMessage(ChatColor.RED + "");
				}
				}else{
					p.sendMessage(ChatColor.RED + "You must be invited to join this kingdom. Notify the kingdom's owner or one of the mods.");
				}
			}else{
				p.sendMessage(ChatColor.RED + args[1] + " is not an existant kingdom. Bear in mind that this is case sensitive");
			}
			}else{
				p.sendMessage(ChatColor.RED + "Usage: /k join [kingdom name]");
			}
			}else if(args[0].equalsIgnoreCase("leave")){
				
				if(hasKingdom(p)){
				if(!isKing(p)){
					p.sendMessage(ChatColor.RED + "Left " + getKingdom(p));
					quitKingdom(getKingdom(p), p);
				}else{
					p.sendMessage(ChatColor.RED + "As a king, you must pass on your leadership to another member of the kingdom with /k king [playername], or disband your kingdom with /k disband");
				}
			}else{
				p.sendMessage(ChatColor.RED + "You don't have a kingdom");
			}

			}else if(args[0].equalsIgnoreCase("invite")){
				if(args.length == 2){
					if(Bukkit.getPlayer(args[1]) != null){
						if(getKingdom(Bukkit.getPlayer(args[1])) == null ||
								!getKingdom(Bukkit.getPlayer(args[1])).equals(getKingdom(p))){
						invitePlayer(getKingdom(p), Bukkit.getPlayer(args[1]));
						Bukkit.getPlayer(args[1]).sendMessage(ChatColor.RED + "===========INVITATION==========");
						Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + "");
						Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + p.getName() + " has invited you to join " + getKingdom(p));
						Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + "Do /k join " + getKingdom(p) + " to accept the invite.");
						Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + "");
						Bukkit.getPlayer(args[1]).sendMessage(ChatColor.RED + "===============================");
					p.sendMessage(ChatColor.GREEN + "Invited " + args[1] + " to your kingdom.");
					}else if(getKingdom(Bukkit.getPlayer(args[1])).equals(getKingdom(p))){
						p.sendMessage(ChatColor.RED + "This player is already in your kingdom!");
					}
					}else{
						p.sendMessage(ChatColor.RED + "This player is offline or doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k invite [player]");
				}
			}else if(args[0].equalsIgnoreCase("uninvite")){
				if(args.length == 2){
					if(Bukkit.getPlayer(args[1]) != null){
						deinvitePlayer(getKingdom(p), Bukkit.getPlayer(args[1]));
						Bukkit.getPlayer(args[1]).sendMessage(ChatColor.RED + "Your invitation to " + getKingdom(p) + " has been revoked.");
						p.sendMessage(ChatColor.RED + "Unnvited " + args[1] + " to your kingdom.");
					}else{
						p.sendMessage(ChatColor.RED + "This player is offline or doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k uninvite [player]");
				}
			}else if(args[0].equalsIgnoreCase("kick")){
				if(isKing(p)|| isMod(getKingdom(p), p)){
				if(args.length == 2){
					if(!args[1].equalsIgnoreCase(p.getName())){
					if(Bukkit.getOfflinePlayer(args[1]) != null){
						if(getKingdom(Bukkit.getOfflinePlayer(args[1])).equals(getKingdom(p))){
							if(isMod(getKingdom(p), Bukkit.getOfflinePlayer(args[1])) &&
									!isKing(p)){
								
								p.sendMessage(ChatColor.RED + "Only the King can kick mods!");
								
							}else{
								
								quitKingdom(getKingdom(p), Bukkit.getOfflinePlayer(args[1]));
								p.sendMessage(ChatColor.RED + args[1] + " has been kicked from your kingdom");
								messageKingdomPlayers(getKingdom(p), ChatColor.GREEN + p.getName() + " kicked " + args[1] + " from your kingdom! :O");
								
							}
					}else{
						p.sendMessage(ChatColor.RED + "This player isn't in your kingdom!");
					}
					}else{
						p.sendMessage(ChatColor.RED + "This player doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "You can't kick yourself!");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k kick [player]");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Only kingdom Kings and Mods can kick members!");
				}
			}else if(args[0].equalsIgnoreCase("sethome")){
			if(isKing(p)){
				if(getChunkKingdom(p.getLocation().getChunk()) != null &&
						getChunkKingdom(p.getLocation().getChunk()).equals(getKingdom(p))){
				kingdoms.set(getKingdom(p) + ".home", locationToString(p.getLocation()));
				p.sendMessage(ChatColor.GREEN + "Kingdom home set to your location");
			}else{
				p.sendMessage(ChatColor.RED + "You can't set your kingdom home outside your land!");
			}
				
			}else{
				p.sendMessage(ChatColor.RED + "Only kingdom king can set the kingdom home!");
			}
			}else if(args[0].equalsIgnoreCase("mod")){
				if(isKing(p)){
				if(args.length == 2){
					if(!args[1].equalsIgnoreCase(p.getName())){
					if(Bukkit.getOfflinePlayer(args[1]) != null){
						modPlayer(getKingdom(p), Bukkit.getOfflinePlayer(args[1]).getUniqueId());
						p.sendMessage(ChatColor.GREEN + "Promoted " + args[1] + " to moderator of your kingdom");
					}else{
						p.sendMessage(ChatColor.RED + "This player is offline or doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "The King can't be a moderator!");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k mod [player]");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Only kingdom Kings can promote members!");
				}
			}else if(args[0].equalsIgnoreCase("king")){
				if(isKing(p)){
				if(args.length == 2){
					if(!args[1].equalsIgnoreCase(p.getName())){
					if(Bukkit.getOfflinePlayer(args[1]) != null){
						if(getKingdom(Bukkit.getOfflinePlayer(args[1])).equals(getKingdom(p))){
						kingPlayer(getKingdom(p), Bukkit.getOfflinePlayer(args[1]).getUniqueId());
						p.sendMessage(ChatColor.GREEN + "Passed leadership of your kingdom to " + args[1]);
					}
					}else{
						p.sendMessage(ChatColor.RED + "This player is offline or doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "You already are the king!");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k king [player]");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Only kingdom Kings can pass on their leadership!");
				}
			}else if(args[0].equalsIgnoreCase("demote")){
				if(isKing(p)){
				if(args.length == 2){
					if(Bukkit.getOfflinePlayer(args[1]) != null){
						unModPlayer(getKingdom(p), Bukkit.getOfflinePlayer(args[1]).getUniqueId());
						p.sendMessage(ChatColor.RED + "Demoted " + args[1] + ".");
					}else{
						p.sendMessage(ChatColor.RED + "This player is offline or doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k mod [player]");
				}
			}else{
				p.sendMessage(ChatColor.RED + "Only kingdom Kings can demote moderators!");
			}
			}else if(args[0].equalsIgnoreCase("home")){
				if(hasKingdom(p)){
					if(hasHome(getKingdom(p))){
						if(getChunkKingdom(stringToLocation(kingdoms.getString(getKingdom(p) + ".home")).getChunk()).equals(getKingdom(p))){
							p.sendMessage(ChatColor.GREEN + "Teleporting...");
							p.teleport(stringToLocation(kingdoms.getString(getKingdom(p) + ".home")));

					}else{
						p.sendMessage(ChatColor.RED + "Contact your king to set a new home! Your old home was claimed by enemies!");
					}
					}else{
						p.sendMessage(ChatColor.RED + "Your home has not been set or the spot where your home was is claimed.");
					}
				}
			}else if(args[0].equalsIgnoreCase("ally")){
				if(isKing(p) || isMod(getKingdom(p), p)){
				if(args.length == 2){
					
					String kingdomtoally = "";
					
					if(!this.kingdoms.getKeys(false).contains(args[1])){
						
					if(!args[1].equalsIgnoreCase(p.getName())){
					if(Bukkit.getOfflinePlayer(args[1]) != null){
						
						kingdomtoally = getKingdom(Bukkit.getOfflinePlayer(args[1]));
						if(this.kingdoms.getKeys(false).contains(kingdomtoally)){
							if(!kingdomtoally.equals(getKingdom(p))){
						if(isEnemy(kingdomtoally, p)){
							p.sendMessage(ChatColor.RED + "Be warned that " + kingdomtoally + " may still have your kingdom marked as an enemy, without your knowledge.");
						}
						
						
							allyKingdom(getKingdom(p), kingdomtoally);
							p.sendMessage(ChatColor.GREEN + "You have established an alliance with " + kingdomtoally);
							 messageKingdomPlayers(getKingdom(p), ChatColor.GREEN + "You are now allies with " + kingdomtoally);
							}else{
							p.sendMessage(ChatColor.RED + "You can't ally yourself.");
						}
						}else{
							p.sendMessage(ChatColor.RED + args[1] + " doesn't exist! Be aware that kingdom names are case-sensitive");
						}
						
					}else{
						p.sendMessage(ChatColor.RED + "This player doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "You can't ally yourself.");
				}
					
				}else{
					kingdomtoally = args[1];
					
					if(this.kingdoms.getKeys(false).contains(kingdomtoally)){
						if(!kingdomtoally.equals(getKingdom(p))){
					if(isEnemy(kingdomtoally, p)){
						p.sendMessage(ChatColor.RED + "Be warned that " + kingdomtoally + " may still have your kingdom marked as an enemy, without your knowledge.");
					}
					
					
						allyKingdom(getKingdom(p), kingdomtoally);
						 messageKingdomPlayers(getKingdom(p), ChatColor.GREEN + "You are now allies with " + kingdomtoally);
						p.sendMessage(ChatColor.GREEN + "You have established an alliance with " + kingdomtoally);
					}else{
						p.sendMessage(ChatColor.RED + "You can't ally yourself.");
					}
					
				}else{
					p.sendMessage(ChatColor.RED + args[1] + " doesn't exist! Be aware that kingdom names are case-sensitive");
				}
				}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k ally [kingdom name/ player name]");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Only kingdom Kings and kingdom Mods ally other kingdoms!");
				}
			}else if(args[0].equalsIgnoreCase("enemy")){
				if(isKing(p) || isMod(getKingdom(p), p)){
				if(args.length == 2){
					
					String kingdomtoally = "";
					
					if(!this.kingdoms.getKeys(false).contains(args[1])){
						
					if(!args[1].equalsIgnoreCase(p.getName())){
					if(Bukkit.getOfflinePlayer(args[1]) != null){
						
						kingdomtoally = getKingdom(Bukkit.getOfflinePlayer(args[1]));
						if(this.kingdoms.getKeys(false).contains(kingdomtoally)){
							if(!kingdomtoally.equals(getKingdom(p))){
						if(isAlly(kingdomtoally, p)){
							p.sendMessage(ChatColor.RED + "You have severed alliance ties with " + kingdomtoally + " without their knowledge.");
						    
						}
						
						 messageKingdomPlayers(getKingdom(p), ChatColor.RED + "You are now enemies with " + kingdomtoally);
							enemyKingdom(getKingdom(p), kingdomtoally);
							p.sendMessage(ChatColor.GREEN + "You are now enemies with " + kingdomtoally);
						}else{
							p.sendMessage(ChatColor.RED + "You can't enemy yourself.");
						}
						}else{
							p.sendMessage(ChatColor.RED + args[1] + " doesn't exist! Be aware that kingdom names are case-sensitive");
						}
						
					}else{
						p.sendMessage(ChatColor.RED + "This player doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "You can't enemy yourself.");
				}
					
				}else{
					kingdomtoally = args[1];
					
					if(this.kingdoms.getKeys(false).contains(kingdomtoally)){
						if(!kingdomtoally.equals(getKingdom(p))){
							if(isAlly(kingdomtoally, p)){
								p.sendMessage(ChatColor.RED + "You have severed alliance ties with " + kingdomtoally);
							}
					
					
						enemyKingdom(getKingdom(p), kingdomtoally);
						messageKingdomPlayers(getKingdom(p), ChatColor.RED + "You are now enemies with " + kingdomtoally);
						p.sendMessage(ChatColor.RED + "You are now enemies with " + kingdomtoally);
					}else{
						p.sendMessage(ChatColor.RED + "You can't enemy yourself.");
					}
					
				}else{
					p.sendMessage(ChatColor.RED + args[1] + " doesn't exist! Be aware that kingdom names are case-sensitive");
				}
				}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k enemy [kingdom name/ player name]");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Only kingdom Kings and kingdom Mods enemy other kingdoms!");
				}
			}else if(args[0].equalsIgnoreCase("neutral")){
				if(isKing(p) || isMod(getKingdom(p), p)){
				if(args.length == 2){
					
					String kingdomtoally = "";
					
					if(!this.kingdoms.getKeys(false).contains(args[1])){
						
					if(!args[1].equalsIgnoreCase(p.getName())){
					if(Bukkit.getOfflinePlayer(args[1]) != null){
						
						kingdomtoally = getKingdom(Bukkit.getOfflinePlayer(args[1]));
						if(this.kingdoms.getKeys(false).contains(kingdomtoally)){
							if(!kingdomtoally.equals(getKingdom(p))){
						if(isAlly(kingdomtoally, p)){
							p.sendMessage(ChatColor.RED + "You have severed alliance ties with " + kingdomtoally + " without their knowledge.");
						    
						}
						
						if(isEnemy(kingdomtoally, p)){
							p.sendMessage(ChatColor.RED + "Be warned that " + kingdomtoally + " may still have your kingdom marked as an enemy, without your knowledge.");
						}
						
						
							neutralizeKingdom(getKingdom(p), kingdomtoally);
							p.sendMessage(ChatColor.GREEN + "You are now neutral with " + kingdomtoally);
						}else{
							p.sendMessage(ChatColor.RED + "You can't neutral yourself.");
						}
						}else{
							p.sendMessage(ChatColor.RED + args[1] + " doesn't exist! Be aware that kingdom names are case-sensitive");
						}
						
					}else{
						p.sendMessage(ChatColor.RED + "This player doesn't exist! Bear in mind that this is case sensitive.");
					}
				}else{
					p.sendMessage(ChatColor.RED + "You can't neutral yourself.");
				}
					
				}else{
					kingdomtoally = args[1];
					
					if(this.kingdoms.getKeys(false).contains(kingdomtoally)){
						if(!kingdomtoally.equals(getKingdom(p))){
							if(isAlly(kingdomtoally, p)){
								p.sendMessage(ChatColor.RED + "You have severed alliance ties with " + kingdomtoally + " without their knowledge.");
							}
							
							if(isEnemy(kingdomtoally, p)){
								p.sendMessage(ChatColor.RED + "Be warned that " + kingdomtoally + " may still have your kingdom marked as an enemy, without your knowledge.");
							}
					    messageKingdomPlayers(getKingdom(p), ChatColor.GRAY + "You are now neutral with " + kingdomtoally);
					   
						neutralizeKingdom(getKingdom(p), kingdomtoally);
						
					}else{
						p.sendMessage(ChatColor.RED + "You can't neutral yourself.");
					}
					
				}else{
					p.sendMessage(ChatColor.RED + args[1] + " doesn't exist! Be aware that kingdom names are case-sensitive");
				}
				}
				}else{
					p.sendMessage(ChatColor.RED + "Usage: /k neutral [kingdom name/ player name]");
				}
				}else{
					p.sendMessage(ChatColor.RED + "Only kingdom Kings and kingdom Mods neutral other kingdoms!");
				}
			
				
			}else if(args[0].equalsIgnoreCase("invade")){
			invadeCurrentPosition(p);
				
			}else if(args[0].equalsIgnoreCase("disband")){
				if(hasKingdom(p)){
					if(isKing(p)){
					if(args.length == 1){
						disbandKingdom(getKingdom(p));
						p.sendMessage(ChatColor.RED + "Kingdom Disbanded.");
					}else{
						p.sendMessage(ChatColor.RED + "Usage: /k disband " + ChatColor.BOLD + "to permanently remove your base.");
					}
					}
				}
				
				}else if(args[0].equalsIgnoreCase("map")){
				if(args.length == 1){
				if(p.hasPermission("kingdoms.map")){
				displayMap(p);
			}else{
				p.sendMessage(ChatColor.RED + "Insufficient Permissions!");
			}
			}else{
				if(!mapmode.contains(p.getUniqueId())){
				p.sendMessage(ChatColor.GREEN + "Auto map on");
				displayMap(p);
				mapmode.add(p.getUniqueId());
			}else{
				p.sendMessage(ChatColor.RED + "Auto map off");
				mapmode.remove(p.getUniqueId());
			}
			}
				}else{
				p.sendMessage(ChatColor.RED + "[Kingdoms] Unknown command. Do /k for commands.");
			}
			
		}else{
			p.sendMessage(ChatColor.RED + "Kingdoms is disabled in this world.");
		}
		}

	}else{
		sender.sendMessage("/k can only be used for players!");
	}
		return false;
		
	}
	
	@EventHandler
	public void onChat(PlayerCommandPreprocessEvent event){
		String[] array = event.getMessage().split(" ");
		if(duelpairs.containsValue(event.getPlayer().getUniqueId())){
		if(array[0].startsWith("/")){
				  event.setCancelled(true);
				  event.getPlayer().sendMessage(ChatColor.RED + "You cannot use commands while dueling a champion! If you have already killed the champion or vice versa, simply relog to solve the problem.");
				}
	}
	}
	

	

	
	
	public boolean isValidWorld(World world){
		if(getConfig().getStringList("enabled-worlds").contains(world.getName())){
			return true;
		}else{
			return false;
		}
	}
	
	
	@EventHandler
	public void onPlayerRightclick(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(this.placingnexusblock.contains(p.getUniqueId())){
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
				Location loc = event.getClickedBlock().getLocation();
				try{
				if(hasWorldGuard){
					if(wet.isInRegion(event.getClickedBlock().getLocation())){
						if(noRegionClaiming){
						p.sendMessage(ChatColor.RED + "You can't place your nexus in a region.");
						return;
					}
					}
				}
			}catch(NoClassDefFoundError e){
				Bukkit.getLogger().severe(ChatColor.RED + "Your worldguard is not the latest! Players will still be able to place a nexus in regioned areas! To prevent this, use /k admin safezone or /k admin warzone to protect an area from claiming.");
			}
				if(event.getClickedBlock().getType() != Material.CHEST){
				if(!hasNexus(getKingdom(p))){
			if(getChunkKingdom(loc.getChunk()).equals(getKingdom(p))){
				
				placeNexus(loc, getKingdom(p));
				placingnexusblock.remove(p.getUniqueId());
				p.sendMessage(ChatColor.GREEN + "Nexus placed.");
			}else{
				p.sendMessage(ChatColor.RED + "Your nexus block can only be placed in your own land!");
			}
			}else{
				
				if(getChunkKingdom(loc.getChunk()).equals(getKingdom(p))){
					Location lastnexus = stringToLocation(kingdoms.getString(getKingdom(p) + ".nexus-block"));
					lastnexus.getBlock().setType(Material.AIR);
					lastnexus.getBlock().removeMetadata("nexusblock", this);
					placeNexus(loc, getKingdom(p));
					placingnexusblock.remove(p.getUniqueId());
					p.sendMessage(ChatColor.GREEN + "Nexus moved.");
				}else{
					p.sendMessage(ChatColor.RED + "Your nexus block can only be placed in your own land!");
				}
				
			}
			}else{
				p.sendMessage(ChatColor.RED + "You don't want to replace a chest with your nexus. Nexus placing cancelled");
				placingnexusblock.remove(p.getUniqueId());
			}
			
		}else if(event.getAction() == Action.LEFT_CLICK_BLOCK||
				event.getAction() == Action.LEFT_CLICK_AIR){
			placingnexusblock.remove(p.getUniqueId());
			p.sendMessage(ChatColor.RED + "Nexus placing cancelled.");
		}
		}

		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getType() == Material.WOOD_DOOR ||
					event.getClickedBlock().getType() == Material.IRON_DOOR){
				
				if(getChunkKingdom(event.getClickedBlock().getChunk()) != null){
					if(hasKingdom(p)){
						if(!getChunkKingdom(event.getClickedBlock().getChunk()).equals(getKingdom(p))){
							event.setCancelled(true);
							p.sendMessage(ChatColor.RED + "You may not use doors in " + getChunkKingdom(event.getClickedBlock().getChunk()) + "'s land");
						}
					}else{
						event.setCancelled(true);
						p.sendMessage(ChatColor.RED + "You may not use doors in " + getChunkKingdom(event.getClickedBlock().getChunk()) + "'s land");
					}
					}
				
			}
		}
		
	}
	
	@EventHandler
	public void onChunkChange(PlayerChangeChunkEvent event){
		Player p = event.getPlayer();
		if(mapmode.contains(event.getPlayer().getUniqueId())){
			displayMap(event.getPlayer());
		}
		if(rapidclaiming.contains(event.getPlayer().getUniqueId())){
			claimCurrentPosition(event.getPlayer());
		}
		
		if(getChunkKingdom(event.getToChunk()) != null){
		if(!getChunkKingdom(event.getToChunk()).equals(getChunkKingdom(event.getFromChunk()))){
			if(getChunkKingdom(event.getToChunk()).equals("SafeZone")){
				p.sendMessage(ChatColor.GOLD + "Entering a Safezone. You are now safe from pvp and monsters.");
				return;
			}
			
            if(getChunkKingdom(event.getToChunk()).equals("WarZone")){
				p.sendMessage(ChatColor.RED + "Entering a Warzone! Not the safest place to be.");
            	return;
			}
            
            if(kingdoms.getKeys(false).contains(getChunkKingdom(event.getToChunk()))){
			p.sendMessage(ChatColor.AQUA + "Entering " + ChatColor.YELLOW + getChunkKingdom(event.getToChunk()));
            }else if(!kingdoms.getKeys(false).contains(getChunkKingdom(event.getToChunk()))){
            	if(getChunkKingdom(event.getFromChunk()) != null){
            		p.sendMessage(ChatColor.AQUA + "Entering unoccupied land");
            		}
            	emptyCurrentPosition(event.getToChunk());
            }
		
			
		
	}
	}else{
		if(getChunkKingdom(event.getFromChunk()) != null){
		p.sendMessage(ChatColor.AQUA + "Entering unoccupied land");
		}
	}
		
	}
	
	@EventHandler
	public void onEntityAttack(EntityDamageByEntityEvent event){
		if(isValidWorld(event.getEntity().getWorld())){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if(event.getDamager() instanceof Player){
				Player damager = (Player) event.getDamager();
				
				if(hasKingdom(p)){
					if(getChunkKingdom(p.getLocation().getChunk()) != null){
					if(getChunkKingdom(p.getLocation().getChunk()).equals(getKingdom(p))){
						if(!isEnemy(getKingdom(p),damager)){
							
							event.setCancelled(true);
							damager.sendMessage(ChatColor.RED + "You can't harm members of " + getKingdom(p) + " in their own territory unless your kingdom is an enemy!");
							return;
						}
					}
				}
					if(hasKingdom(damager)){
						if(getKingdom(damager).equals(getKingdom(p))){
							event.setCancelled(true);
							damager.sendMessage(ChatColor.RED + "You can't damage your kingdom members!");
							return;
						}
					}
					
					if(isAlly(getKingdom(p), damager)){
						event.setCancelled(true);
						damager.sendMessage(ChatColor.RED + "You can't damage your allies!");
						return;
					}
					
				}
				
			}
		}
	}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Player p = event.getPlayer();
		if(adminmode.contains(p.getUniqueId())){
			return;
		}
		if(land.getString(chunkToString(event.getBlock().getLocation().getChunk())) != null){
			if(!getChunkKingdom(event.getBlock().getLocation().getChunk()).equals(getKingdom(p))){
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You cannot place blocks in " +getChunkKingdom(event.getBlock().getLocation().getChunk()) + "'s land!");
			}
		}
		
	
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player p = event.getPlayer();
		
		if(event.getBlock().getType() == Material.BEACON){
			if(event.getBlock().hasMetadata("nexusblock")){
				event.setCancelled(true);
				if(!getChunkKingdom(event.getBlock().getLocation().getChunk()).equals(getKingdom(p))){
					String kingdom = getChunkKingdom(event.getBlock().getLocation().getChunk());
					if(hasMisUpgrade(getChunkKingdom(event.getBlock().getLocation().getChunk()), "nexusguard")){
					spawnNexusGuard(kingdom, p.getLocation(), p);
					p.sendMessage(ChatColor.RED + "A nexus guard has been summoned!");
					}
					
					if(!isAlly(getChunkKingdom(event.getBlock().getLocation().getChunk()), p)){
						if(isEnemy(getChunkKingdom(event.getBlock().getLocation().getChunk()), p)){
							int i = minusRP(getChunkKingdom(event.getBlock().getLocation().getChunk()), 20);
							p.sendMessage(ChatColor.GREEN + "Plundered " + i + " resource points!");
							addRP(getKingdom(p), i);
							return;
						}else{
					int i = minusRP(getChunkKingdom(event.getBlock().getLocation().getChunk()), 10);
					p.sendMessage(ChatColor.GREEN + "Plundered " + i + " resource points!");
					if(hasKingdom(p)){
						addRP(getKingdom(p), i);
					}
					return;
						}
				}else{
					p.sendMessage(ChatColor.RED + "You can't steal resourcepoints from an ally!");
				}
				}
			}
		}
		
		
		
		if(land.getString(chunkToString(event.getBlock().getLocation().getChunk())) != null){
			if(!getChunkKingdom(event.getBlock().getLocation().getChunk()).equals(getKingdom(p))){
				if(adminmode.contains(p.getUniqueId())){
					return;
				}
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You cannot break blocks in " +getChunkKingdom(event.getBlock().getLocation().getChunk()) + "'s land!");
			
			}
		}
		

	
	}
	
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent event){
		for(Block b: event.blockList()){
			if(b.getType() == Material.BEACON){
				if(b.hasMetadata("nexusblock")){
					event.blockList().remove(b);
				}
			}
		}
		
		
		
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if(getChunkKingdom(p.getLocation().getChunk()) != null){
				if(getChunkKingdom(p.getLocation().getChunk()).equals("SafeZone")){
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityAttackEntity(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player){
			if(event.getDamager() instanceof Player){
			Player p = (Player) event.getEntity();
			Player damager = (Player) event.getDamager();
			
			if(adminmode.contains(damager.getUniqueId())){
				return;
			}
			if(getChunkKingdom(p.getLocation().getChunk()) != null){
				if(getChunkKingdom(p.getLocation().getChunk()).equals("SafeZone")){
					event.setCancelled(true);
					damager.sendMessage(ChatColor.RED + "You can't damage a player while he is in a safezone");
				return;
				}
			}
			if(getChunkKingdom(damager.getLocation().getChunk()) != null){
				if(getChunkKingdom(damager.getLocation().getChunk()).equals("SafeZone")){
					event.setCancelled(true);
					damager.sendMessage(ChatColor.RED + "You can't damage a player while you are in a safezone");
			return;	
				}
			}
		}
		}
	}
	

	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Player p = event.getPlayer();
		if(event.getTo().getChunk() != event.getFrom().getChunk()){
			
			PlayerChangeChunkEvent pcce = new PlayerChangeChunkEvent(p, event.getFrom().getChunk(), event.getTo().getChunk());
			Bukkit.getServer().getPluginManager().callEvent(pcce);
			

		}
		
	}
	

	
	
	public void newKingdom(UUID king, String tag){
		List<String> list = new ArrayList<String>();
		kingdoms.set(tag + ".king", king.toString());
		kingdoms.set(tag + ".might", 0);
		kingdoms.set(tag + ".nexus-block", 0);
		kingdoms.set(tag + ".home", 0);
		kingdoms.set(tag + ".members", list);
		kingdoms.set(tag + ".mods", list);
		kingdoms.set(tag + ".enemies", list);
		kingdoms.set(tag + ".allies", list);
		kingdoms.set(tag + ".resourcepoints", 5);
		kingdoms.set(tag + ".champion.health", 100);
		kingdoms.set(tag + ".champion.damage", 5);
		kingdoms.set(tag + ".champion.specials", 0);
		kingdoms.set(tag + ".champion.speed", 0);
		kingdoms.set(tag + ".champion.resist", 0);
		saveKingdoms();
		
		powerups.set(tag + ".dmg-reduction", 0);
		powerups.set(tag + ".regen-boost", 0);
		powerups.set(tag + ".dmg-boost", 0);
		powerups.set(tag + ".double-loot-chance", 0);
		savePowerups();
		
		misupgrades.set(tag + ".antitrample", false);
		misupgrades.set(tag + ".anticreeper", false);
		misupgrades.set(tag + ".nexusguard", false);
		misupgrades.set(tag + ".glory", false);
		misupgrades.set(tag + ".bombshards", false);
		saveMisupgrades();
	}
	
	public void disbandKingdom(String tag){
		ArrayList<OfflinePlayer> members = getKingdomMembers(tag);
		getNexusLocation(tag).getBlock().setType(Material.AIR);
		getNexusLocation(tag).getBlock().removeMetadata("nexusblock", this);;
		kingdoms.set(tag, null);
		for(OfflinePlayer p: members){
			players.set(p.getUniqueId().toString() + ".kingdom", "");
			if(p.isOnline()){
				((Player) p).sendMessage(ChatColor.RED + "Your kingdom was disbanded!");
			}
		}
		saveKingdoms();
		savePlayers();
	}
	
	public void modPlayer(String kingdom, UUID uuid){
		List<String> mods = kingdoms.getStringList(kingdom + ".mods");
		if(!mods.contains(uuid.toString())){
		mods.add(uuid.toString());
		kingdoms.set(kingdom + ".mods", mods);
	}
		saveKingdoms();
	}
	
	public void kingPlayer(String kingdom, UUID uuid){
		String oldking = kingdoms.getString(kingdom + ".king");
		modPlayer(kingdom, UUID.fromString(oldking));
		unModPlayer(kingdom, uuid);
		joinKingdom(kingdom, Bukkit.getPlayer(UUID.fromString(oldking)));
		List<String> members = kingdoms.getStringList(kingdom + ".members");
		members.remove(uuid.toString());
		kingdoms.set(kingdom + ".members", members);
		kingdoms.set(kingdom + ".king", uuid.toString());
		Bukkit.broadcastMessage(ChatColor.GOLD + Bukkit.getPlayer(UUID.fromString(oldking)).getName() + " has passed leadership of " + kingdom + " to " + Bukkit.getPlayer(uuid).getName());
		saveKingdoms();
	}
	
	
	public void unModPlayer(String kingdom, UUID uuid){
		List<String> mods = kingdoms.getStringList(kingdom + ".mods");
	if(mods.contains(uuid.toString())){	
		mods.remove(uuid.toString());
	}
		kingdoms.set(kingdom + ".mods", mods);
		saveKingdoms();
	}
	
	public void joinKingdom(String kingdom, Player p){
		PlayerJoinKingdomEvent pcce = new PlayerJoinKingdomEvent(p, kingdom, getKingdom(p));
		Bukkit.getServer().getPluginManager().callEvent(pcce);
		String puuid = p.getUniqueId().toString();
		List<String> members= kingdoms.getStringList(kingdom + ".members");
		members.add(puuid);
		kingdoms.set(kingdom + ".members", members);
		saveKingdoms();
		
		players.set(p.getUniqueId().toString() + ".kingdom", kingdom);
		savePlayers();
		
	}
	
	public void quitKingdom(String kingdom, OfflinePlayer p){
		PlayerJoinKingdomEvent pcce = new PlayerJoinKingdomEvent(p, null, kingdom);
		Bukkit.getServer().getPluginManager().callEvent(pcce);
		if(kingdoms.getStringList(kingdom + ".members").contains(p.getUniqueId().toString())){
		String puuid = p.getUniqueId().toString();
		unModPlayer(kingdom, p.getUniqueId());
		List<String> members= kingdoms.getStringList(kingdom + ".members");
		members.remove(puuid);
		kingdoms.set(kingdom + ".members", members);
		
		
		
		saveKingdoms();
		
		players.set(p.getUniqueId().toString() + ".kingdom", "");
		savePlayers();
	}
	}
	
	public void invitePlayer(String kingdom, Player p){
		p.setMetadata("kinv " + kingdom, new FixedMetadataValue(this, ""));
	}
	
	public void deinvitePlayer(String kingdom, Player p){
		if(p.hasMetadata("kinv " + kingdom)){
		p.removeMetadata("kinv " + kingdom, this);
	}
	}
	
	public int addMight(String kingdom, int addamt){
		kingdoms.set(kingdom + ".might", kingdoms.getInt(kingdom + ".might") + addamt);
		saveKingdoms();
		return kingdoms.getInt(kingdom + ".might");
	}
	
	public int minusMight(String kingdom, int minusamt){
		if(kingdoms.getInt(kingdom + ".might") - minusamt >= 0){
		kingdoms.set(kingdom + ".might", kingdoms.getInt(kingdom + ".might") - minusamt);
		saveKingdoms();
		return minusamt;
		}else{
			int amt = kingdoms.getInt(kingdom + ".might");
			kingdoms.set(kingdom + ".might", 0);
			saveKingdoms();
			return amt;
		}
	}
	
	

	public int minusRP(String kingdom, int minusamt){
		if(kingdoms.getInt(kingdom + ".resourcepoints") - minusamt >= 0){
		kingdoms.set(kingdom + ".resourcepoints", kingdoms.getInt(kingdom + ".resourcepoints") - minusamt);
		saveKingdoms();
		return minusamt;
		}else{
			int amt = kingdoms.getInt(kingdom + ".resourcepoints");
			kingdoms.set(kingdom + ".resourcepoints", 0);
			saveKingdoms();
			return amt;
		}
	}
	
	public int addRP(String kingdom, int addamt){
		kingdoms.set(kingdom + ".resourcepoints", kingdoms.getInt(kingdom + ".resourcepoints") + addamt);
		saveKingdoms();
		return kingdoms.getInt(kingdom + ".resourcepoints");
	}
	
	public boolean hasAmtRp(String kingdom, int amt){
		
		if(kingdoms.getInt(kingdom + ".resourcepoints") >= amt){
			return true;
		}else{
		
		return false;
		}
	}
	
	public int getRp(String kingdom){
		
		return kingdoms.getInt(kingdom + ".resourcepoints");

	}
	
	public void messageKingdomPlayers(String kingdom, String message){
		for(Player p:getKingdomOnlineMembers(kingdom)){
			p.sendMessage(message);
		}
	}
	
	public ArrayList<Player> getKingdomOnlineMembers(String kingdom){
		ArrayList<Player> members = new ArrayList<Player>();
		List<String> uuids = kingdoms.getStringList(kingdom + ".members");
		uuids.add(kingdoms.getString(kingdom + ".king"));
		for(String s:uuids){
			try{
			if(UUID.fromString(s) != null){
			if(Bukkit.getOfflinePlayer(UUID.fromString(s)).isOnline()){
				members.add(Bukkit.getPlayer(UUID.fromString(s)));
			}
		}
		}catch(NullPointerException e){
			
		}
		}
		return members;
		
	}
	
	public ArrayList<Player> getKingdomOfflineMembers(String kingdom){
		ArrayList<Player> members = new ArrayList<Player>();
		List<String> uuids = kingdoms.getStringList(kingdom + ".members");
		uuids.add(kingdoms.getString(kingdom + ".king"));
		for(String s:uuids){
			if(!Bukkit.getOfflinePlayer(UUID.fromString(s)).isOnline()){
				members.add(Bukkit.getPlayer(UUID.fromString(s)));
			}
		}
		return members;
		
	}
	
	public ArrayList<OfflinePlayer> getKingdomMembers(String kingdom){
		ArrayList<OfflinePlayer> members = new ArrayList<OfflinePlayer>();
		List<String> uuids = kingdoms.getStringList(kingdom + ".members");
		uuids.add(kingdoms.getString(kingdom + ".king"));
		for(String s:uuids){
				members.add(Bukkit.getPlayer(UUID.fromString(s)));
			
		}
		return members;
		
	}
	
	public boolean hasNexus(String kingdom){
		if(stringToLocation(kingdoms.getString(kingdom + ".nexus-block")) == null){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean hasHome(String kingdom){
		if(stringToLocation(kingdoms.getString(kingdom + ".home")) == null){
			return false;
		}else{
			return true;
		}
	}

	
	
	public boolean hasKingdom(OfflinePlayer p){
		boolean boo = true;
		if(players.getString(p.getUniqueId() .toString() + ".kingdom") != null){
		if(players.getString(p.getUniqueId() .toString() + ".kingdom").equals("")){
			boo = false;
		}
	}else{
		players.set(p.getUniqueId().toString() + ".ign", p.getName());
		players.set(p.getUniqueId().toString() + ".kingdom", "");
		savePlayers();
		Bukkit.getLogger().info("Added " + p.getName() + "'s info to players.yml");
		boo = false;
	}
		return boo;
		
	}
	
	public String getKingdom(OfflinePlayer p){
		return players.getString(p.getUniqueId().toString() + ".kingdom");
	}
	
	public boolean isMod(String kingdom, OfflinePlayer p){
		boolean boo = false;
		
		if(hasKingdom(p)){
			if(kingdoms.getStringList(kingdom + ".mods").contains(p.getUniqueId().toString())){
				boo = true;
			}else{
				boo = false;
			}
		}else{
			boo = false;
		}
		
		return boo;
	}
	
	public void allyKingdom(String kingdom, String ally){
	

		List<String> enemies = kingdoms.getStringList(kingdom + ".enemies");
		List<String> allies = kingdoms.getStringList(kingdom + ".allies");
	if(enemies.contains(ally)){
		
		enemies.remove(ally);
		
	}
	
	if(allies.contains(ally)){	
		return;
	}else{
		allies.add(ally);
	}
         		kingdoms.set(kingdom + ".allies", allies);
         		kingdoms.set(kingdom + ".enemies", enemies);
         		saveKingdoms();
             
		
		
		
		
	}
	
	
	public void enemyKingdom(String kingdom, String enemy){
		

		List<String> enemies = kingdoms.getStringList(kingdom + ".enemies");
		List<String> allies = kingdoms.getStringList(kingdom + ".allies");
	if(enemies.contains(enemy)){
		
	}else{
		enemies.add(enemy);
	}
	
	if(allies.contains(enemy)){	
		allies.remove(enemy);
	}
	
	List<String> enemyenemies = kingdoms.getStringList(enemy + ".enemies");
	List<String> enemyallies = kingdoms.getStringList(enemy + ".allies");
	
	if(enemyenemies.contains(kingdom)){
		
	}else{
		enemyenemies.add(kingdom);
	}
	
	if(enemyallies.contains(kingdom)){	
		enemyallies.remove(kingdom);
	}
         		kingdoms.set(kingdom + ".allies", allies);
         		kingdoms.set(kingdom + ".enemies", enemies);
         		saveKingdoms();
             
		
		
		
		
	}
	
	public void neutralizeKingdom(String kingdom, String nkingdom){
		List<String> enemies = kingdoms.getStringList(kingdom + ".enemies");
		List<String> allies = kingdoms.getStringList(kingdom + ".allies");
	if(enemies.contains(nkingdom)){	
		
				enemies.remove(nkingdom);
			
		
		
	}
	
	if(allies.contains(nkingdom)){	
				allies.remove(nkingdom);
			
		
	}
		kingdoms.set(kingdom + ".enemies", enemies);
		kingdoms.set(kingdom + ".allies", allies);
		saveKingdoms();
	}
	
	public boolean isKAlly(String kingdom, String ally){
		boolean boo = false;
			if(kingdoms.getStringList(kingdom + ".allies").contains(ally)){
				boo = true;
			}else{
				boo = false;
			
	}
	
		
		return boo;
	}
	
	public boolean isKEnemy(String kingdom, String enemy){
		boolean boo = false;
		if(kingdoms.getStringList(kingdom + ".enemies").contains(enemy)){
			boo = true;
		}else{
			boo = false;
		}
		
		return boo;
	}
	
	public boolean isAlly(String kingdom, OfflinePlayer p){
		boolean boo = false;
		if(hasKingdom(p)){
			if(kingdoms.getStringList(kingdom + ".allies").contains(getKingdom(p))){
				boo = true;
			}else{
				boo = false;
			}
	}
	
		
		return boo;
	}
	
	public boolean isEnemy(String kingdom, OfflinePlayer p){
		boolean boo = false;
		if(hasKingdom(p)){
		if(kingdoms.getStringList(kingdom + ".enemies").contains(getKingdom(p))){
			boo = true;
		}else{
			boo = false;
		}
	}

	
	return boo;
}
	
	public boolean isNexusChunk(Chunk c){
		boolean boo = false;
		
		if(getChunkKingdom(c) != null){
			String kingdom = getChunkKingdom(c);
			if(hasNexus(kingdom)){
			if(getNexusLocation(kingdom).getChunk().equals(c)){
				boo = true;
			}
		}
		}
		
		return boo;
	}
	
	public void placeNexus(Location loc, String kingdom){
		loc.getBlock().setType(Material.BEACON);
		loc.getBlock().setMetadata("nexusblock", new FixedMetadataValue(this, "ok."));
		String sloc = locationToString(loc);
		kingdoms.set(kingdom + ".nexus-block", sloc);
		saveKingdoms();
	}
	
	public void displayMap(Player p){
		String com1 = "";
		String com2 = "";
		String com3 = "";
		ArrayList<String> compass = AsciiCompass.getAsciiCompass(AsciiCompass.getCardinalDirection(p), ChatColor.AQUA, ChatColor.GRAY + "");
		
		com1 = compass.get(0);
		com2 = compass.get(1);
		com3 = compass.get(2);
			String cck = ChatColor.AQUA + "Unoccupied";
			HashMap<String, ChatColor> detected = new HashMap<String, ChatColor>();
			if(getChunkKingdom(p.getLocation().getChunk()) != null){
			    if(getChunkKingdom(p.getLocation().getChunk()).equals(getKingdom(p))){
			    	cck = ChatColor.GREEN + getKingdom(p);
			    }else if(isKEnemy(getKingdom(p), getChunkKingdom(p.getLocation().getChunk()))){
			    	cck = ChatColor.RED + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), ChatColor.RED);
			    }else if(isKAlly(getKingdom(p), getChunkKingdom(p.getLocation().getChunk()))){
			    	cck = ChatColor.LIGHT_PURPLE + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), ChatColor.LIGHT_PURPLE);
			    }else if(getChunkKingdom(p.getLocation().getChunk()).equals("SafeZone")){
			    	cck = ChatColor.GOLD + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), ChatColor.GOLD);
			    }else if(getChunkKingdom(p.getLocation().getChunk()).equals("WarZone")){
			    	cck = ChatColor.RED + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), ChatColor.RED);
			    }else{
			    	ChatColor rc = randomColor();
			    	cck = rc + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), rc);
			    }
			}
			p.sendMessage(ChatColor.AQUA + "======================[" +  cck + ChatColor.AQUA + "]======================");
			String linetext = com1+ ChatColor.WHITE;
			int midx = p.getLocation().getChunk().getX();
			int midz = p.getLocation().getChunk().getZ();
			//South is positive z
			//North is negative z
			//West is negative x
			//East is positive x
			//^
			//|
			//-x
			//-z  ----->
			for (int x = 0; x <= 8; x++) {
				
				if(x == 0){
					linetext = com1;
				}else if(x == 1){
					linetext = com2;
					
				}else if(x == 2){
					linetext = com3;
					
				}else{
				linetext = "";
			}
				for (int z = 0; z <= 45; z++) {
					
					if(z == 45){
						p.sendMessage(linetext);
                        linetext = "";
					}else{
						int chunkxcoord = midx;
						int chunkzcoord = midz;
						if(x <= 4){
							chunkzcoord -= x;
						}else if(x > 5){
							chunkzcoord += x;
						}
						
						if(z <= 21){
						    chunkzcoord += z;	
						}else if(z > 22){
							chunkzcoord -= z;
						}
						Chunk c = p.getWorld().getChunkAt(chunkxcoord, chunkzcoord);
						String chunkx = ChatColor.GRAY + "-";
						if(getChunkKingdom(c) != null){
						    if(getChunkKingdom(c).equals(getKingdom(p))){
						    	chunkx = ChatColor.GREEN + "x";
						    	detected.put(getChunkKingdom(c), ChatColor.GREEN);
						    }else if(isKEnemy(getKingdom(p), getChunkKingdom(c))){
						    	chunkx = ChatColor.RED + "x";
						    	detected.put(getChunkKingdom(c), ChatColor.RED);
						    }else if(isKAlly(getKingdom(p), getChunkKingdom(c))){
						    	chunkx = ChatColor.LIGHT_PURPLE + "x";
						    	detected.put(getChunkKingdom(c), ChatColor.LIGHT_PURPLE);
						    }else if(getChunkKingdom(c).equals("SafeZone")){
						    	chunkx = ChatColor.GOLD + "x";
						    	detected.put(getChunkKingdom(c), ChatColor.GOLD);
						    }else if(getChunkKingdom(c).equals("WarZone")){
						    	chunkx = ChatColor.RED + "x";
						    	detected.put(getChunkKingdom(c), ChatColor.RED);
						    }else{
						    	ChatColor rc = randomColor();
						    	chunkx = rc + getChunkKingdom(c);
						    	detected.put(getChunkKingdom(c), rc);
						    }
						}
						if(x == 8/2 && z == 22){
							chunkx = ChatColor.WHITE + "+";
						}
						
						if(z <= 2){
							if(x <= 2){
								chunkx = "";
							}
						}
						
						linetext += chunkx;
					}
				}
			}
	}
	
	public void betaDisplayMap(Player p){
			String s = "   ";
			String k = "   ";
			String com1 = "";
			String com2 = "";
			String com3 = "";
			String cck = ChatColor.AQUA + "Unoccupied";
			p.sendMessage(ChatColor.AQUA + "========[Map]========");
			ArrayList<String> compass = AsciiCompass.getAsciiCompass(AsciiCompass.getCardinalDirection(p), ChatColor.AQUA, ChatColor.GRAY + "");
			
			com1 = compass.get(0);
			com2 = compass.get(1);
			com3 = compass.get(2);
			
				p.sendMessage(com1);
				p.sendMessage(com2);
				p.sendMessage(com3);
				
			
			HashMap<String, ChatColor> detected = new HashMap<String, ChatColor>();
			if(getChunkKingdom(p.getLocation().getChunk()) != null){
			    if(getChunkKingdom(p.getLocation().getChunk()).equals(getKingdom(p))){
			    	cck = ChatColor.GREEN + getKingdom(p);
			    }else if(isKEnemy(getKingdom(p), getChunkKingdom(p.getLocation().getChunk()))){
			    	cck = ChatColor.RED + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), ChatColor.RED);
			    }else if(isKAlly(getKingdom(p), getChunkKingdom(p.getLocation().getChunk()))){
			    	cck = ChatColor.LIGHT_PURPLE + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), ChatColor.LIGHT_PURPLE);
			    }else if(getChunkKingdom(p.getLocation().getChunk()).equals("SafeZone")){
			    	cck = ChatColor.GOLD + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), ChatColor.GOLD);
			    }else if(getChunkKingdom(p.getLocation().getChunk()).equals("WarZone")){
			    	cck = ChatColor.RED + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), ChatColor.RED);
			    }else{
			    	ChatColor rc = randomColor();
			    	cck = rc + getChunkKingdom(p.getLocation().getChunk());
			    	detected.put(getChunkKingdom(p.getLocation().getChunk()), rc);
			    }
			}
			p.sendMessage(ChatColor.AQUA + "========[" +  cck + ChatColor.AQUA + "]========");
			int looptime = 0;
			s = com1;
			k = "xxx";
		for (int x = -4; x <= 3; x++) {
			for (int z = -11; z <= 10; z++) {
				if(k.length() <= 24){

					String cs = "";
					String ks = "";
					Chunk c = p.getWorld().getChunkAt(x + p.getLocation().getChunk().getX(), z + p.getLocation().getChunk().getX());
					if(!c.equals(p.getLocation().getChunk())){
					if(getChunkKingdom(c) == null){
						cs = ChatColor.GRAY + "-";
						ks = "x";
					}else if(getChunkKingdom(c).equals(getKingdom(p))){
						cs = ChatColor.GREEN + "x";
						ks = "x";
						if(!detected.containsKey(getChunkKingdom(c))){
						detected.put(getChunkKingdom(c), ChatColor.GREEN);
						}
					}else if(isKAlly(getKingdom(p),getChunkKingdom(c))){
						cs = ChatColor.LIGHT_PURPLE + "x";
						ks = "x";
						if(!detected.containsKey(getChunkKingdom(c))){
						detected.put(getChunkKingdom(c), ChatColor.LIGHT_PURPLE);
						}
					}else if(isKEnemy(getChunkKingdom(c), getKingdom(p))){
						cs = ChatColor.RED + "x";
						ks = "x";
						if(!detected.containsKey(getChunkKingdom(c))){
						detected.put(getChunkKingdom(c), ChatColor.RED);
						}
					}else if(getChunkKingdom(c).equals("SafeZone")){
				    	cck = ChatColor.GOLD + getChunkKingdom(c);
				    	detected.put(getChunkKingdom(c), ChatColor.GOLD);
				    }else if(getChunkKingdom(c).equals("WarZone")){
				    	cck = ChatColor.RED + getChunkKingdom(c);
				    	detected.put(getChunkKingdom(c), ChatColor.RED);
				    }else{
						if(!detected.containsKey(getChunkKingdom(c))){
						ChatColor rc = randomColor();
						cs = rc + "x";
						ks = "x";
						detected.put(getChunkKingdom(c), rc);
					}else{
						cs = detected.get(getChunkKingdom(c)) + "x";
						ks = "x";
					}
					}
				}else{
					cs = ChatColor.WHITE + "+";
					
					ks = "x";
				}

					
					s = s + cs;
					k = k + ks;
				}else{
					looptime++;
					p.sendMessage(s);
					if(looptime == 1){
						s = com2;
						k = "xxx";
					}else if(looptime == 2){
						s = com3;
						k = "xxx";
					}else{
					s = "xxx";
					k = "xxx";
					}
					
				}
			}
		}
		String kingdomstring = "";
		for(String x : detected.keySet()){
			kingdomstring = kingdomstring + ChatColor.WHITE + " " + detected.get(x) + x;
		}
		p.sendMessage(ChatColor.AQUA + "Nearby Kingdoms:"  + kingdomstring);
		
	}
	
	public ArrayList<Chunk> getNearbyChunks(Player p){
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		
		
		
		return chunks;
	}
	
	public boolean isKing(Player p){
         boolean boo = false;
		
		if(hasKingdom(p)){
			if(UUID.fromString(kingdoms.getString(getKingdom(p) + ".king")).equals(p.getUniqueId())){
				boo = true;
			}else{
				boo = false;
			}
		}else{
			boo = false;
		}
		
		return boo;
	}
	
	
	public void spawnChampion(String kingdom, Location location, Player p){
		Zombie champion = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		champion.setTarget(p);
		champion.setMaxHealth(this.kingdoms.getDouble(kingdom + ".champion.health"));
		champion.setHealth(this.kingdoms.getDouble(kingdom + ".champion.health"));
		champion.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		
		if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 0){
			champion.getEquipment().setItemInHand(null);
		}else if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 1){
			champion.getEquipment().setItemInHand(new ItemStack(Material.WOOD_SWORD));
		}else if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 2){
			champion.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
		}else if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 3){
			champion.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
		}else if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 4){
			champion.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
		}
		
		champion.getEquipment().setChestplateDropChance(0.0f);
		champion.getEquipment().setItemInHandDropChance(0.0f);
		
		champion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, this.kingdoms.getInt(kingdom + ".champion.speed")));
		
		champions.put(champion.getUniqueId(), location.getChunk());
		duelpairs.put(champion.getUniqueId(), p.getUniqueId());
		
	}
	
	public void spawnNexusGuard(String kingdom, Location location, Player p){
		Zombie champion = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		champion.setTarget(p);
		champion.setMaxHealth(30.0);
		champion.setHealth(30.0);
		champion.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		
		if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 0){
			champion.getEquipment().setItemInHand(null);
		}else if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 1){
			champion.getEquipment().setItemInHand(new ItemStack(Material.WOOD_SWORD));
		}else if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 2){
			champion.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
		}else if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 3){
			champion.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
		}else if(this.kingdoms.getInt(kingdom + ".champion.weapon") == 4){
			champion.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
		}
		
		champion.getEquipment().setChestplateDropChance(0.0f);
		champion.getEquipment().setItemInHandDropChance(0.0f);
		
		champion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, this.kingdoms.getInt(kingdom + ".champion.speed")));
		
		nexusguards.put(champion.getUniqueId(), kingdom);
		
	}
	
	@EventHandler
	public void onMobAttack(final EntityDamageByEntityEvent event){
		if(immunity.containsKey(event.getEntity().getUniqueId())){
			event.setDamage(0.0);
			event.getEntity().getVelocity().multiply(2);
			immunity.remove(event.getEntity().getUniqueId());
			
		}
		
		if(champions.containsKey(event.getEntity().getUniqueId())){
			if(event.getDamager() instanceof Player){
				Player p = (Player) event.getDamager();
			String kingdom = getChunkKingdom(champions.get(event.getEntity().getUniqueId()));
			if(hasKingdom(p)){
				if(getKingdom(p).equals(kingdom)){
					event.setCancelled(true);
					p.sendMessage(ChatColor.RED + "You can't attack your champion!");
				}
			}
		}
			
			int randomnumber = new Random().nextInt(100) + 1;
			int antiknockbackchance = this.kingdoms.getInt(getChunkKingdom(champions.get(event.getEntity().getUniqueId())) + ".champion.resist");
			if(antiknockbackchance >= randomnumber){
				 Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	                 public void run() {
	                     event.getEntity().setVelocity(new Vector());
	                 }
	             }, 1L);
		}
		}
		
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event){
		if(duelpairs.containsValue(event.getPlayer().getUniqueId())){
			for(Entity e:event.getPlayer().getNearbyEntities(30, 30, 30)){
				if(duelpairs.containsKey(e.getUniqueId())){
				if(duelpairs.get(e.getUniqueId()).equals(event.getPlayer().getUniqueId())){
					e.remove();
					champions.remove(e.getUniqueId());
					duelpairs.remove(e.getUniqueId());
				}
				}
				
				if(e instanceof Player){
					Player p = (Player) e;
					p.sendMessage(ChatColor.RED + "The invader " + event.getPlayer().getName() + " has left! Invasion cancelled!");
				}
				
			}
		}
	}
	
	
	
	@EventHandler
	public void onChampionTarget(EntityTargetEvent event){
		if(champions.containsKey(event.getEntity().getUniqueId())){
			if(event.getTarget() instanceof Player){
				Player p = (Player) event.getTarget();
			String kingdom = getChunkKingdom(champions.get(event.getEntity().getUniqueId()));
			if(hasKingdom(p)){
				if(getKingdom(p).equals(kingdom)){
					event.setCancelled(true);
				}
			}
			}
		}else if(nexusguards.containsKey(event.getEntity().getUniqueId())){
			if(event.getTarget() instanceof Player){
				Player p = (Player) event.getTarget();
			String kingdom = nexusguards.get(event.getEntity().getUniqueId());
			if(hasKingdom(p)){
				if(getKingdom(p).equals(kingdom)){
					event.setCancelled(true);
				}
			}
			}
		}
	}
	
	@EventHandler
	public void onSunDamage(EntityDamageEvent event){
		if(champions.get(event.getEntity().getUniqueId()) != null ||
				nexusguards.containsKey(event.getEntity().getUniqueId())){
			if(event.getCause() == DamageCause.FIRE_TICK){
				event.setCancelled(true);
				event.getEntity().setFireTicks(0);
			}
		}
	}
	
	@EventHandler
	public void onChampionDeath(EntityDeathEvent event){
		if(isValidWorld(event.getEntity().getWorld())){
		if(champions.get(event.getEntity().getUniqueId()) != null){
		if(event.getEntity().getKiller() != null){
			Player p = event.getEntity().getKiller();
			if(hasKingdom(p) &&
					!getKingdom(p).equals(getChunkKingdom(champions.get(event.getEntity().getUniqueId())))){
			
			 forceClaimCurrentPosition(champions.get(event.getEntity().getUniqueId()), p);
			 p.sendMessage(ChatColor.GREEN + "Invasion Successful.");
				champions.remove(event.getEntity().getUniqueId());
				duelpairs.remove(event.getEntity().getUniqueId());
			 p.sendMessage(ChatColor.GREEN + "You have successfully conquered " + getChunkKingdom(champions.get(event.getEntity().getUniqueId())) + "'s land. ");
		
			 String attacked = getChunkKingdom(champions.get(event.getEntity().getUniqueId()));
			 Chunk nexuschunk = stringToLocation(kingdoms.getString(attacked + ".nexus-block")).getChunk();
			 
			 if(event.getEntity().getLocation().getChunk().equals(nexuschunk)){
				 
				 kingdoms.set(attacked + ".nexus-block", 0);
				 p.sendMessage(ChatColor.GREEN + attacked + "'s nexus is now under siege by you! ");
				 
					}
			}else{
			event.getEntity().getLastDamageCause().setDamage(0.0);
		}
		}else{
			forceClaimCurrentPosition(champions.get(event.getEntity().getUniqueId()),Bukkit.getPlayer(duelpairs.get(event.getEntity().getUniqueId())));
			Bukkit.getPlayer(duelpairs.get(event.getEntity().getUniqueId())).sendMessage(ChatColor.GREEN + "Invasion Successful.");
			event.getEntity().getKiller().sendMessage(ChatColor.GREEN + "You have successfully conquered " + getChunkKingdom(champions.get(event.getEntity().getUniqueId())) + "'s land. ");
		champions.remove(event.getEntity().getUniqueId());
		duelpairs.remove(event.getEntity().getUniqueId());
		}


		
			
		}else if(nexusguards.containsKey(event.getEntity().getUniqueId())){
			nexusguards.remove(event.getEntity().getUniqueId());
		}
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
		if(duelpairs.containsValue(p.getUniqueId())){
			for(Entity e:p.getNearbyEntities(30, 30, 30)){
				if(duelpairs.containsKey(e.getUniqueId())){
				if(duelpairs.get(e.getUniqueId()).equals(p.getUniqueId())){
					
					e.remove();
					champions.remove(e.getUniqueId());
					duelpairs.remove(e.getUniqueId());
				}
				}
				
				if(e instanceof Player){
					Player plr = (Player) e;
					plr.sendMessage(ChatColor.RED + "The invader " + p.getName() + " has lost to the champion! Invasion failed!");
				}
				
			}
		}
	}
	}
	}
	
	
	
	public void invadeCurrentPosition(Player p){
		Chunk c = p.getLocation().getChunk();
		if(getChunkKingdom(c) != null){
		if(!getChunkKingdom(c).equals(getKingdom(p))){
			if(!getChunkKingdom(c).equals("SafeZone")&&
					!getChunkKingdom(c).equals("WarZone")){
				
			if(!isAlly(getChunkKingdom(c), p)){
			if(hasAmtRp(getKingdom(p), 10)){
				if(!isNexusChunk(c)){
			p.sendMessage(ChatColor.GREEN + "Invading land! " + getChunkKingdom(c) + " is summoning their champion to defend their land!");
			p.sendMessage(ChatColor.RED + "Defeat their champion to gain their land!");
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "BATTLE!");
			this.immunity.put(p.getUniqueId(), 60);
			p.sendMessage(ChatColor.RED + "10 resourcepoints spent");
			spawnChampion(getChunkKingdom(c), p.getLocation(), p);
			minusRP(getKingdom(p), 10);
			}else{
				p.sendMessage(ChatColor.RED + "You can't claim a nexus chunk");
			}
		}else{
			p.sendMessage(ChatColor.RED + "You need at least 10 resource points to attempt an invasion!");
		}
		}else{
			p.sendMessage(ChatColor.RED + "You can't invade an ally!");
		}
			
		}else{
			p.sendMessage(ChatColor.RED + "You can't invade a safezone or a warzone!");
		}
		}else if(getChunkKingdom(c).equals(getKingdom(p))){
			p.sendMessage(ChatColor.AQUA + "You can't invade your own kingdom!");
		}
	}else{
		p.sendMessage(ChatColor.RED + "This land is unoccupied. Do /k claim to claim unoccupied land.");
	}
	}
	
	public void forceClaimCurrentPosition(Chunk c, Player p){
		if(this.land.getString(chunkToString(c)) != null){
			if(!getChunkKingdom(c).equals(getKingdom(p))){
			land.set(chunkToString(c), getKingdom(p));
			saveClaimedLand();
			addMight(getKingdom(p), 5);
		
		}
		}
	}
	
	public Location getNexusLocation(String kingdom){
		Location loc = null;
		
		if(kingdoms.get(kingdom) != null){
			if(hasNexus(kingdom)){
				loc = stringToLocation(kingdoms.getString(kingdom + ".nexus-block"));
			}
		}
		
		return loc;
		
	}
	
	public void claimSafezoneChunk(Chunk c){
		if(this.land.getString(chunkToString(c)) == null){
			land.set(chunkToString(c), "SafeZone");
			saveClaimedLand();
		}
	}
	
	
	public void claimWarzoneChunk(Chunk c){
		if(this.land.getString(chunkToString(c)) == null){
			land.set(chunkToString(c), "WarZone");
			saveClaimedLand();
		}
	}
	
	public void claimSafezoneCurrentPosition(Player p){
		Chunk c = p.getLocation().getChunk();
		if(this.land.getString(chunkToString(c)) == null){
			land.set(chunkToString(c), "SafeZone");
			saveClaimedLand();
			p.sendMessage(ChatColor.GREEN + "Safezone Claimed");
		}else if(getChunkKingdom(c) != null){
			p.sendMessage(ChatColor.RED + "This land is owned by " + getChunkKingdom(c) + ". You can't claim this chunk as safezone.");
		}
	}
	
	public void claimWarzoneCurrentPosition(Player p){
		Chunk c = p.getLocation().getChunk();
		if(this.land.getString(chunkToString(c)) == null){
			land.set(chunkToString(c), "WarZone");
			saveClaimedLand();
			p.sendMessage(ChatColor.GREEN + "Warzone Claimed");
		}else if(getChunkKingdom(c) != null){
			p.sendMessage(ChatColor.RED + "This land is owned by " + getChunkKingdom(c) + ". You can't claim this chunk as warzone.");
		}
	}
	
	public void changeLandOwner(String kingdom, Chunk c){
		
		if(this.land.getString(chunkToString(c)) == null){
			land.set(chunkToString(c), kingdom);
			saveClaimedLand();
		}
	}
	
	public void claimCurrentPosition(Player p){
		Chunk c = p.getLocation().getChunk();
		try{
		if(hasWorldGuard){
			if(wet.isInRegion(p.getLocation())){
				if(noRegionClaiming){
				p.sendMessage(ChatColor.RED + "You can't claim land in a region.");
				return;
			}
			}
		}
	}catch(NoClassDefFoundError e){
		Bukkit.getLogger().severe(ChatColor.RED + "Your worldguard is not the latest! Players will still be able to place a nexus in regioned areas! To prevent this, use /k admin safezone or /k admin warzone to protect an area from claiming.");
	}
		
		if(this.land.getString(chunkToString(c)) == null){
			if(hasAmtRp(getKingdom(p), 5)){
			land.set(chunkToString(c), getKingdom(p));
			saveClaimedLand();
			p.sendMessage(ChatColor.GREEN + "Land Claimed");
			p.sendMessage(ChatColor.RED + "5 resourcepoints spent");
			addMight(getKingdom(p), 5);
			minusRP(getKingdom(p), 5);
		}else{
			p.sendMessage(ChatColor.RED + "You don't have enough resource points.");
		}
		}else if(getChunkKingdom(c).equals(getKingdom(p))){
			p.sendMessage(ChatColor.AQUA + "Your kingdom already owns this land.");
		}else if(!getChunkKingdom(c).equals(getKingdom(p))){
			p.sendMessage(ChatColor.RED + "This land is owned by " + getChunkKingdom(c) + ". Do /k invade to challenge this kingdom's champion.");
		}
	}
	
	public void unclaimCurrentPosition(Player p){
		Chunk c = p.getLocation().getChunk();
		if(getChunkKingdom(c).equals(getKingdom(p))){
			land.set(chunkToString(c), null);
			saveClaimedLand();
			p.sendMessage(ChatColor.RED + "Land Unclaimed");
			p.sendMessage(ChatColor.RED + "5 resourcepoints returned. 5 might lost.");
			minusMight(getKingdom(p), 5);
			addRP(getKingdom(p), 5);
		}else if(this.land.getString(chunkToString(c)) == null){
			p.sendMessage(ChatColor.AQUA + "You can't unclaim land that you don't occupy.");
		}else if(!getChunkKingdom(c).equals(getKingdom(p))){
			p.sendMessage(ChatColor.RED + "This land is owned by " + getChunkKingdom(c) + ". You can't unclaim this land.");
		}
	}
	
	public void emptyCurrentPosition(Chunk c){
		land.set(chunkToString(c), null);
		saveClaimedLand();
	}
	
	public void forceUnclaimCurrentPosition(Player p){
		Chunk c = p.getLocation().getChunk();
		if(!getChunkKingdom(c).equals("")){

			p.sendMessage(ChatColor.RED + "Land Unclaimed");
			if(getChunkKingdom(c).equals("SafeZone")||
					getChunkKingdom(c).equals("WarZone")||
					getChunkKingdom(c).equals("")){
				land.set(chunkToString(c), null);
				saveClaimedLand();
				return;
			}else{
				minusMight(getChunkKingdom(c), 5);
				addRP(getChunkKingdom(c), 5);
				land.set(chunkToString(c), null);
				saveClaimedLand();
				
			
				messageKingdomPlayers(getChunkKingdom(c), p.getName() + " unclaimed your land. Refudning resource points. 5 might lost.");
			return;
			}
			
			
		}else{
			p.sendMessage(ChatColor.AQUA + "You can't unclaim land that is empty");
		}
	}

	
	public String getChunkKingdom(Chunk c){
		String s = null;
		if(c != null){
		if(land.getString(chunkToString(c)) != null){
			s = land.getString(chunkToString(c));
		}
	}
		return s;
	}
	
	public String chunkToString(Chunk c){
		
		String s = "" + c.getX()  +" , "+ c.getZ() +" , "+ c.getWorld().getName();
		
		
		return s;
		
	}
	
	public Chunk stringToChunk(String s){
		Chunk c = null;
		String[] split = s.split(" , ");
		try{
		c = Bukkit.getWorld(split[2]).getChunkAt(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		}catch(NullPointerException e){
			Bukkit.getLogger().severe(ChatColor.RED + "One of the land claim areas is invalid! Did you change the claimedchunks config recently?");
		}catch(NumberFormatException e){
			Bukkit.getLogger().severe(ChatColor.RED + "One of the land claim areas is invalid! Did you change the claimedchunks config recently?");
		}
		return c;
	}
	
	public boolean hasMisUpgrade(String kingdom, String upgrade){
		boolean boo = misupgrades.getBoolean(kingdom + "." + upgrade);
		
		return boo;
	}
	

	
	public Location stringToLocation(String key){
        String[] split = key.split(" , ");
        if(split.length == 6){
        Location loc = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        return loc;
        }else{
            return null;
        }
   
   
    }
	
	public String locationToString(Location loc){
		
		try{
		String key = loc.getWorld().getName() + " , " +  loc.getX() + " , " + loc.getY() + " , " + loc.getZ() + " , " + loc.getPitch() + " , " + loc.getYaw();
		 
		return key;
		}catch(NullPointerException e){
			return "No nexus";
		}
		
		
	}
	
	public ChatColor randomColor(){
		int rand = randInt(0, 6);
		if(rand == 1){
			return ChatColor.DARK_PURPLE;
		}else if(rand == 2){
			return ChatColor.AQUA;
		}else if(rand == 3){
			return ChatColor.DARK_PURPLE;
		}else if(rand == 4){
			return ChatColor.DARK_AQUA;
		}else if(rand == 5){
			return ChatColor.DARK_BLUE;
		}else if(rand == 6){
			return ChatColor.BLUE;
		}else{
		return ChatColor.YELLOW;
		}
	}
	
	public int randInt(int min, int max) {

	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	
	
	
	public File kingdomsfile = new File("plugins/Kingdoms/kingdoms.yml");
	  public FileConfiguration kingdoms = YamlConfiguration.loadConfiguration(this.kingdomsfile);
	  
	  public void saveKingdoms() {
		    try {
		      this.kingdoms.save(this.kingdomsfile);
		      this.kingdoms = YamlConfiguration.loadConfiguration(this.kingdomsfile);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
	  
	  public File playersfile = new File("plugins/Kingdoms/players.yml");
	  public FileConfiguration players = YamlConfiguration.loadConfiguration(this.playersfile);
	  
	  public void savePlayers() {
		    try {
		      this.players.save(this.playersfile);
		      this.players = YamlConfiguration.loadConfiguration(this.playersfile);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
	  
	  public File claimedlandfile = new File("plugins/Kingdoms/claimedchunks.yml");
	  public FileConfiguration land = YamlConfiguration.loadConfiguration(this.claimedlandfile);
	  
	  public void saveClaimedLand() {
		    try {
		      this.land.save(this.claimedlandfile);
		      this.land = YamlConfiguration.loadConfiguration(this.claimedlandfile);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
	  
	  public File powerupsfile = new File("plugins/Kingdoms/powerups.yml");
	  public FileConfiguration powerups = YamlConfiguration.loadConfiguration(this.powerupsfile);
	  
	  public void savePowerups() {
		    try {
		      this.powerups.save(this.powerupsfile);
		      this.powerups = YamlConfiguration.loadConfiguration(this.powerupsfile);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
	  
	  public File misupgradesfile = new File("plugins/Kingdoms/miscellaneousupgrades.yml");
	  public FileConfiguration misupgrades = YamlConfiguration.loadConfiguration(this.misupgradesfile);
	  
	  public void saveMisupgrades() {
		    try {
		      this.misupgrades.save(this.misupgradesfile);
		      this.misupgrades = YamlConfiguration.loadConfiguration(this.misupgradesfile);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		  }
	
	
}