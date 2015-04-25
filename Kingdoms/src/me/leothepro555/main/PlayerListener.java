package me.leothepro555.main;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
			plugin.players.set(event.getPlayer().getUniqueId().toString() + ".rank", 0);
			plugin.savePlayers();
			Bukkit.getLogger().info("Added " + event.getPlayer().getName() + "'s info to players.yml");
	}
	}
	
}
