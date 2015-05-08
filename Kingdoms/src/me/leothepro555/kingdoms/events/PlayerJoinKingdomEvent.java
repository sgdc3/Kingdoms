package me.leothepro555.kingdoms.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinKingdomEvent extends Event{
   
	private OfflinePlayer p;
	private String kingdom;
	private String before;
	private static final HandlerList handlers = new HandlerList();
	
	public PlayerJoinKingdomEvent(OfflinePlayer p, String kingdom, String before){
		this.p = p;
		this.kingdom = kingdom;
		this.before = before;
	}
	
	 public HandlerList getHandlers() {
	        return handlers;
	    }
	 
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }

		public OfflinePlayer getPlayer() {
			return p;
		}

		public String getKingdom() {
			return kingdom;
		}

		public String getBefore() {
			return before;
		}



	    
	    
	    
	    
	
}
