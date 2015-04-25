package me.leothepro555.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class KingdomPowerups implements Listener{

	private Kingdoms plugin;
	
	public KingdomPowerups(Kingdoms plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityHit(EntityDamageEvent event){
		if(plugin.isValidWorld(event.getEntity().getWorld())){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			
			if(plugin.hasKingdom(p)){
				if(plugin.getChunkKingdom(p.getLocation().getChunk()).equals(plugin.getKingdom(p))){
				if(plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-reduction") > 0){
					event.setDamage(event.getDamage() * (1 - plugin.powerups.getDouble(plugin.getKingdom(p) + ".dmg-reduction")/100));
				
				}
			}
			}
			
		}
	}
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event){
		if(plugin.isValidWorld(event.getEntity().getWorld())){
		if(event.getDamager() instanceof Player){
			Player p = (Player) event.getDamager();
			
			if(plugin.hasKingdom(p)){
				if(plugin.getChunkKingdom(p.getLocation().getChunk()).equals(plugin.getKingdom(p))){
				if(plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-boost") > 0){
					event.setDamage(event.getDamage() * (1 + plugin.powerups.getDouble(plugin.getKingdom(p) + ".dmg-boost")/100));
					
				}
			}
			}
			
		}
	}
	}
	
	@EventHandler
	public void onHealthRegain(EntityRegainHealthEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			
			if(plugin.hasKingdom(p)){
				if(plugin.getChunkKingdom(p.getLocation().getChunk()).equals(plugin.getKingdom(p))){
				if(plugin.powerups.getInt(plugin.getKingdom(p) + ".regen-boost") > 0){
					event.setAmount(event.getAmount() * (1 + plugin.powerups.getDouble(plugin.getKingdom(p) + ".regen-boost")/100));
					
				}
			}
			}
			
		}
	}
	
}
