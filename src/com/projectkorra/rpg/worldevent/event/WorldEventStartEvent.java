package com.projectkorra.rpg.worldevent.event;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.projectkorra.rpg.worldevent.WorldEvent;

public class WorldEventStartEvent extends Event implements Cancellable {

	public static final HandlerList handlers = new HandlerList();
	public boolean cancel = false;
	public World world;
	public WorldEvent event;

	public WorldEventStartEvent(World world, WorldEvent event) {
		this.world = world;
		this.event = event;
	}

	public World getWorld() {
		return world;
	}

	public WorldEvent getEvent() {
		return event;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
