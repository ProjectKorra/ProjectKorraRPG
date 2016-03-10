package com.projectkorra.rpg.event;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldSunSetEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	
	private World world;
	
	public WorldSunSetEvent(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}
	
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
