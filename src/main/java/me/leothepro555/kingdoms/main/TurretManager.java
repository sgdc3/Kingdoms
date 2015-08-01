package me.leothepro555.kingdoms.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TurretManager implements Listener{

	
	public Kingdoms plugin;
	
	public TurretManager(Kingdoms plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		final Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getPlayer().getItemInHand() != null){
				if(event.getPlayer().getItemInHand().getItemMeta() != null){
					if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null){
						if(event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Arrow Turret")){
			        if(event.getBlockFace() == BlockFace.UP &&
			        		event.getClickedBlock().getType() == Material.FENCE){
			        	if(plugin.getChunkKingdom(p.getLocation().getChunk()) != null){
			        		if(plugin.hasKingdom(p)){
			        		if(plugin.getChunkKingdom(p.getLocation().getChunk()).equals(plugin.getKingdom(p))){
				        		p.setItemInHand(null);
			        			final Block turret = event.getClickedBlock().getRelative(0, 1, 0);
			        			turret.setType(Material.SKULL);
			        			turret.setData((byte) 1);
			        			plugin.turrets.set(locationToString(turret.getLocation()), plugin.getKingdom(p));
			        			plugin.saveTurrets();
			        			 new BukkitRunnable(){
			        				 @Override
			        			  public void run(){
			        					 if(plugin.turrets.getString(locationToString(turret.getLocation())) != null){
			        					for(Entity e:getNearbyEntities(turret.getLocation(), 7)){
			        						Location origin = turret.getRelative(0, 1, 0).getLocation();
			        							if(e instanceof LivingEntity){
			        								if(e instanceof Player){
			        									if(((Player) e).getGameMode() == GameMode.SURVIVAL||
			        											((Player) e).getGameMode() == GameMode.ADVENTURE){
			        									if(!plugin.turrets.getString(locationToString(turret.getLocation())).equals(plugin.getKingdom((Player)e))){
			        										fireArrow(origin, e);
			        										break;
			        									}
			        									}
			        								}else if(e instanceof Wolf){
			        									if(((Wolf) e).isTamed()){
			        										Player owner = (Player) ((Wolf) e).getOwner();
			        										if(!plugin.turrets.getString(locationToString(turret.getLocation())).equals(plugin.getKingdom(owner))){
			        											fireArrow(origin, e);
			        											break;
			        										}
			        									}else{
			        										fireArrow(origin, e);
			        										break;
			        									}
			        								}else if(!plugin.champions.containsKey(e.getUniqueId())){
			        									fireArrow(origin, e);
			        									break;
			        								}
			        							}
			        						}
			        				 }else{
			        					 this.cancel();
			        				 }
			        					 
	    						   }
	    						    
	    						   }.runTaskTimer(plugin, 0L, 20L);
	    						   
				        	}else{
					        	p.sendMessage(ChatColor.RED + "You can only place turrets in your lands");
					        }
			        	}else{
				        	p.sendMessage(ChatColor.RED + "You need to be in a kingdom to place turrets");
				        }
			        	}else{
				        	p.sendMessage(ChatColor.RED + "You can only place turrets in your land");
				        }
			        	
			        }else{
			        	p.sendMessage(ChatColor.RED + "You can only place turrets on top of fences.");
			        }
			       }
					}
			}
		}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()){
			return;
		}
		if(event.getBlock().getType() == Material.SKULL){
			if(plugin.turrets.getKeys(false).contains(locationToString(event.getBlock().getLocation()))){
				event.getBlock().setType(Material.AIR);
				ItemStack i1 = new ItemStack(Material.RECORD_9);
				ItemMeta i1m = i1.getItemMeta();
				i1m.setDisplayName(ChatColor.AQUA + "Arrow Turret");
				ArrayList<String> i1l = new ArrayList<String>();
				i1l.add(ChatColor.GREEN + "Rapidly fires at anything other than");
				i1l.add(ChatColor.GREEN + "kingdom members. One target at a time.");
				i1l.add(ChatColor.GREEN + "Works best on a flat space");
				i1l.add(ChatColor.BLUE + "Range: 7 blocks");
				i1l.add(ChatColor.BLUE + "Damage: 4 hearts/shot");
				i1l.add(ChatColor.BLUE + "Attack Speed: 1/sec");
				i1l.add(ChatColor.BLUE + "Targets: One random target in range");
				i1m.setLore(i1l);
				i1.setItemMeta(i1m);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), i1);
				plugin.turrets.set(locationToString(event.getBlock().getLocation()), null);
				plugin.saveTurrets();
     		}
		}
	}
	
	public void fireArrow(Location origin, Entity e){
		Vector to = e.getLocation().add(0, 1, 0).toVector();
		Vector from = origin.toVector();
		Vector direction = to.subtract(from);
		direction.multiply(3); 
		direction.normalize();
		final Arrow a = origin.getWorld().spawn(origin, Arrow.class);
		a.setVelocity(direction);
		 new BukkitRunnable(){
			 @Override
		  public void run(){
				 
				 if(a.isOnGround()||a.isDead()||!a.isValid()){
					 a.remove();
					 this.cancel();
				 }
				 
			 }
			 }.runTaskTimer(plugin, 0L, 20L);
	}
	
	public static List<Entity> getNearbyEntities(Location where, int range) {
		List<Entity> found = new ArrayList<Entity>();
		 
		for (Entity entity : where.getWorld().getEntities()) {
		if (isInBorder(where, entity.getLocation(), range)) {
		found.add(entity);
		}
		}
		return found;
		}
	
	public static boolean isInBorder(Location center, Location notCenter, int range) {
		int x = center.getBlockX(), z = center.getBlockZ();
		int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ();
		 
		if (x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range)) {
		return false;
		}
		return true;
		}
	
	public Location stringToLocation(String key){
        String[] split = key.split(" , ");
        if(split.length == 6){
        Location loc = new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), 0,0);
        return loc;
        }else{
            return null;
        }
   
   
    }
	
	public String locationToString(Location loc){
		
		try{
		String key = loc.getWorld().getName() + " , " +  (int)loc.getX() + " , " + (int)loc.getY() + " , " + (int)loc.getZ() + " , " + 0 + " , " + 0;
		 
		return key;
		}catch(NullPointerException e){
			return "No nexus";
		}
		
		
	}
	
}
