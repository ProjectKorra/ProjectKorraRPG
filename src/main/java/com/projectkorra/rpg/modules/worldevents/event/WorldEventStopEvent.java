package com.projectkorra.rpg.modules.worldevents.event;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WorldEventStopEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	private final WorldEvent worldEvent;

	public WorldEventStopEvent(WorldEvent worldEvent) {
		this.worldEvent = worldEvent;
	}

	public WorldEvent getWorldEvent() {
		return worldEvent;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
