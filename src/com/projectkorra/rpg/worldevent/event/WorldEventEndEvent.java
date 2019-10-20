package com.projectkorra.rpg.worldevent.event;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.projectkorra.rpg.worldevent.WorldEvent;

public class WorldEventEndEvent extends Event implements Cancellable {

	public static final HandlerList HANDLERS = new HandlerList();

	public boolean cancel;
	public World world;
	public WorldEvent event;

	public WorldEventEndEvent(World world, WorldEvent event) {
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
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
