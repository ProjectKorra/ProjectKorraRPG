package com.projectkorra.rpg.modules.worldevents.event;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldEventStartEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	private final WorldEvent worldEvent;

	public WorldEventStartEvent(WorldEvent worldEvent) {
		this.worldEvent = worldEvent;
	}

	public WorldEvent getWorldEvent() {
		return worldEvent;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
