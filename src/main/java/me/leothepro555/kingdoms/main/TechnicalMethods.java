package me.leothepro555.kingdoms.main;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TechnicalMethods implements Listener{
	
	private static Kingdoms plugin;
	
	
	@SuppressWarnings("static-access")
	public TechnicalMethods(Kingdoms plugin){
		this.plugin = plugin;
	}

	public static Location stringToLocation(String key){
        String[] split = key.split(" , ");
        if(split.length == 6){
        Location loc = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        return loc;
        }else{
            return null;
        }
   
   
    }
	
	public static String locationToString(Location loc){
		
		try{
		String key = loc.getWorld().getName() + " , " +  loc.getX() + " , " + loc.getY() + " , " + loc.getZ() + " , " + loc.getPitch() + " , " + loc.getYaw();
		 
		return key;
		}catch(NullPointerException e){
			return "No nexus";
		}
		
		
	}
	
	public static String locationToStringTurret(Location loc){
		
		try{
		String key = loc.getWorld().getName() + " , " +  (int)loc.getX() + " , " + (int)loc.getY() + " , " + (int)loc.getZ() + " , " + 0 + " , " + 0;
		 
		return key;
		}catch(NullPointerException e){
			return "No nexus";
		}
		
		
	}
	
	public static ChatColor randomColor(){
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
	
	public static int randInt(int min, int max) {

	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public static void fireArrow(Location origin, Entity e){
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
	
	
}
