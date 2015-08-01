package me.leothepro555.kingdoms.main;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event){
		
		ArrayList<Block> creeperblock = new ArrayList<Block>();
		
		for(final Block b: event.blockList()){
			if(plugin.getChunkKingdom(b.getChunk()) != null){
				if(event.getEntity() instanceof Creeper){
				if(hasMisUpgrade(plugin.getChunkKingdom(b.getChunk()), "anticreeper")){
					creeperblock.add(b);
				}
				}else{
					if(hasMisUpgrade(plugin.getChunkKingdom(b.getChunk()), "bombshards")){
						final Material mat = b.getType();
						final byte data = b.getData();
						if(b.getState() instanceof Hopper){
							
						}else{
						if(mat != Material.CHEST&&
								mat != Material.DISPENSER&&
								mat != Material.DROPPER&&
								mat != Material.DIAMOND_BLOCK&&
								mat != Material.IRON_BLOCK&&
								mat != Material.GOLD_BLOCK&&
								mat != Material.EMERALD_BLOCK){
							b.setType(Material.AIR);
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							  
							public void run() {
							   b.setType(mat);
							   b.setData(data);
							   }
							}, randInt(2000,4000));
						
					}
					}
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
		
		for(Block block:creeperblock){
			event.blockList().remove(block);
		}
		Entity q = event.getEntity();
		if(hasMisUpgrade(plugin.getChunkKingdom(q.getLocation().getChunk()), "bombshards")){
		if(plugin.getChunkKingdom(q.getLocation().getChunk()) != null){
		String kingdom = plugin.getChunkKingdom(q.getLocation().getChunk());
		for(Entity e: event.getEntity().getNearbyEntities(10, 10, 10)){
			if(e instanceof Player){
				Player player = (Player) e;
				if(plugin.hasKingdom(player)){
					
					if(!plugin.getKingdom(player).equals(kingdom)){
					if((((Damageable)player).getHealth() - 1.0) >= 0){	
						player.setHealth(((Damageable)player).getHealth() -1.0);
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
	
	
	
	public int randInt(int min, int max) {

	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	
}
