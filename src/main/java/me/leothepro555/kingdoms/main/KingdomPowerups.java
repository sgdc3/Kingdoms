package me.leothepro555.kingdoms.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
				if(plugin.getChunkKingdom(p.getLocation().getChunk()) != null){
				if(plugin.getChunkKingdom(p.getLocation().getChunk()).equals(plugin.getKingdom(p))){
				if(plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-reduction") > 0){
					event.setDamage(event.getDamage() * (1 - plugin.powerups.getDouble(plugin.getKingdom(p) + ".dmg-reduction")/100));
				
				}
				
				if(this.hasMisUpgrade(plugin.getKingdom(p), "anticreeper")){
					if(event.getCause() == DamageCause.ENTITY_EXPLOSION){
						event.setCancelled(true);
					}
				}
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
				if(plugin.getChunkKingdom(p.getLocation().getChunk()) != null){
				if(plugin.getChunkKingdom(p.getLocation().getChunk()).equals(plugin.getKingdom(p))){
				if(plugin.powerups.getInt(plugin.getKingdom(p) + ".dmg-boost") > 0){
					event.setDamage(event.getDamage() * (1 + plugin.powerups.getDouble(plugin.getKingdom(p) + ".dmg-boost")/100));
					
				}
			}
			}
			}
			
		}
	}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event){
		for(final Block b: event.blockList()){
			if(plugin.getChunkKingdom(b.getChunk()) != null){
				if(event.getEntity() instanceof Creeper){
				if(hasMisUpgrade(plugin.getChunkKingdom(b.getChunk()), "anticreeper")){
					event.blockList().remove(b);
				}
				}else if(event.getEntity() instanceof TNTPrimed){
					if(hasMisUpgrade(plugin.getChunkKingdom(b.getChunk()), "bombshards")){
						final Block block = b;
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							  @SuppressWarnings("deprecation")
							public void run() {
							   b.setType(block.getType());
							   b.setData(block.getData());
							   }
							}, 200);
					}
					if(plugin.getChunkKingdom(b.getChunk()) != null){
					String kingdom = plugin.getChunkKingdom(b.getChunk());
					for(Entity e: event.getEntity().getNearbyEntities(10, 10, 10)){
						if(e instanceof Player){
							Player player = (Player) e;
							if(plugin.hasKingdom(player)){
								
								if(!plugin.getKingdom(player).equals(kingdom)){
								if(((Damageable)player).getHealth() >= 0){	
									player.setHealth(((Damageable)player).getHealth() -1.0);
								}	
								}
								
							}
						}
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
			try{
			if(plugin.hasKingdom(p)){
				if(plugin.getChunkKingdom(p.getLocation().getChunk()) != null){
				if(plugin.getChunkKingdom(p.getLocation().getChunk()).equals(plugin.getKingdom(p))){
				if(plugin.powerups.getInt(plugin.getKingdom(p) + ".regen-boost") > 0){
					event.setAmount(event.getAmount() * (1 + plugin.powerups.getDouble(plugin.getKingdom(p) + ".regen-boost")/100));
					
				}
			}
			}
			}
		}catch(NullPointerException e){
			
		}
		}
	}
	
	public boolean hasMisUpgrade(String kingdom, String upgrade){
		boolean boo = plugin.misupgrades.getBoolean(kingdom + "." + upgrade);
		
		return boo;
	}
	
	@EventHandler
	public void onSoilTrample(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL){
		if(plugin.getChunkKingdom(p.getLocation().getChunk()) != null){
			String kingdom = plugin.getChunkKingdom(p.getLocation().getChunk());
			if(hasMisUpgrade(kingdom, "antitrample")){
				event.setCancelled(true);
				if(plugin.hasKingdom(p)){
			if(kingdom.equals(plugin.getKingdom(p))){
				p.sendMessage(ChatColor.AQUA + "Your soil can't be trampled.");
			}
				}else{
					p.sendMessage(ChatColor.RED + "You cannot trample soil in " + kingdom + "'s land!");
				}
			}
		
		}
	}
	}
	
	@EventHandler
	public void entityDeath(EntityDeathEvent event){
		if(event.getEntity().getKiller() != null){
		Player p = event.getEntity().getKiller();
		if(plugin.hasKingdom(p)){
			if(plugin.getChunkKingdom(p.getLocation().getChunk()) != null){
			if(plugin.getChunkKingdom(p.getLocation().getChunk()).equals(plugin.getKingdom(p))){
			if(hasMisUpgrade(plugin.getKingdom(p), "glory")){
				
				event.setDroppedExp(event.getDroppedExp() * 3);
				
			}
		}
		}
		}
	}
	}
	
	
	
}
