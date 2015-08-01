package me.leothepro555.kingdoms.events;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangeChunkEvent extends Event {

	private Player p;
	private Chunk from;
	private Chunk to;
	private static final HandlerList handlers = new HandlerList();
	public PlayerChangeChunkEvent(Player p, Chunk from, Chunk to){
		this.p = p;
		this.from = from;
		this.to = to;
	}
	
	 public HandlerList getHandlers() {
	        return handlers;
	    }
	 
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
	
	public Player getPlayer(){
		return p;
	}
	
	public Chunk getFromChunk(){
		return from;
	}

	public Chunk getToChunk(){
		return to;
	}
}
