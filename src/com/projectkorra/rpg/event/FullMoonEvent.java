package com.projectkorra.rpg.event;

import com.projectkorra.rpg.configuration.ConfigManager;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FullMoonEvent extends Event implements Cancellable{

	public static final HandlerList handlers = new HandlerList();
	
	public String message = ConfigManager.rpgConfig.get().getString("WorldEvents.FullMoon.Message");
	public World world;
	public boolean cancelled;
	
	public FullMoonEvent(World world) {
		this.world = world;
	}
	
	public String getMessage() {
		return message;
	}
	
	public World getWorld() {
		return world;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		// TODO Auto-generated method stub
		this.cancelled = cancelled;
	}

}
