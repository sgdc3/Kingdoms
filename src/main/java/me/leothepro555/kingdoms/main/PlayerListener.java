package me.leothepro555.kingdoms.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener{

	private Kingdoms plugin;
	
	public PlayerListener(Kingdoms plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(plugin.players.getString(event.getPlayer().getUniqueId().toString() + ".ign") == null){
			plugin.players.set(event.getPlayer().getUniqueId().toString() + ".ign", event.getPlayer().getName());
			plugin.players.set(event.getPlayer().getUniqueId().toString() + ".kingdom", "");
			plugin.savePlayers();
			Bukkit.getLogger().info("Added " + event.getPlayer().getName() + "'s info to players.yml");
			
	}
		Player p = event.getPlayer();
		if(plugin.isValidWorld(p.getWorld())){
		if(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()) != null){
		 if(!plugin.kingdoms.getKeys(false).contains(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()))){
			 if(!plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()).equals("SafeZone")
					 && !plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()).equals("WarZone")){
         	if(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()) != null){
         		event.getPlayer().sendMessage(ChatColor.AQUA + "Entering unoccupied land");
         		
         	plugin.emptyCurrentPosition(event.getPlayer().getLocation().getChunk());
		 }
         }else{
 			if(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()).equals("SafeZone")){
				p.sendMessage(ChatColor.GOLD + "Entering a Safezone. You are now safe from pvp and monsters.");
				return;
			}
			
            if(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()).equals("WarZone")){
				p.sendMessage(ChatColor.RED + "Entering a Warzone! Not the safest place to be.");
            	return;
			}
            
			p.sendMessage(ChatColor.AQUA + "Entering " + ChatColor.YELLOW + plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()));
            
         }
	}
	}else{
		event.getPlayer().sendMessage(ChatColor.AQUA + "Entering unoccupied land");
	}
	}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event){
		Player p = event.getPlayer();
		if(plugin.isValidWorld(p.getWorld())){
			if(event.getFrom().getChunk() != event.getTo().getChunk()){
		if(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()) != null){
			 if(!plugin.kingdoms.getKeys(false).contains(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()))){
				 if(!plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()).equals("SafeZone")
						 && !plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()).equals("WarZone")){
	         	if(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()) != null){
	         		event.getPlayer().sendMessage(ChatColor.AQUA + "Entering unoccupied land");
	         		
	         	plugin.emptyCurrentPosition(event.getPlayer().getLocation().getChunk());
			 }
	         }else{
	 			if(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()).equals("SafeZone")){
					p.sendMessage(ChatColor.GOLD + "Entering a Safezone. You are now safe from pvp and monsters.");
					return;
				}
				
	            if(plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()).equals("WarZone")){
					p.sendMessage(ChatColor.RED + "Entering a Warzone! Not the safest place to be.");
	            	return;
				}
	            
				p.sendMessage(ChatColor.AQUA + "Entering " + ChatColor.YELLOW + plugin.getChunkKingdom(event.getPlayer().getLocation().getChunk()));
	            
	         }
		}
		}else{
			event.getPlayer().sendMessage(ChatColor.AQUA + "Entering unoccupied land");
		}
		}
	}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerSendMessage(AsyncPlayerChatEvent event){
		if(plugin.isValidWorld(event.getPlayer().getWorld())){
			if(plugin.hasKingdom(event.getPlayer())){
				String format = "[" + plugin.getKingdom(event.getPlayer()) + "] " + event.getFormat();
				event.setFormat(format);
			}
		}
	}
	
}
