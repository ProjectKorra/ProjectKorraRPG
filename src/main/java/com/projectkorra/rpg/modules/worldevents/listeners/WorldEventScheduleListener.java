package com.projectkorra.rpg.modules.worldevents.listeners;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldEventScheduleListener implements Listener {
	private final WorldEventScheduler scheduler;

	public WorldEventScheduleListener(WorldEventScheduler scheduler) {
		this.scheduler = scheduler;
	}

	@EventHandler
	public void onWorldEventStart(WorldEventStartEvent event) {
		scheduler.setEventActive(event.getWorldEvent(), true);
	}

	@EventHandler
	public void onWorldEventStop(WorldEventStopEvent event) {
		WorldEvent worldEvent = event.getWorldEvent();

		// Mark the event as inactive
		scheduler.setEventActive(worldEvent, false);

		// Reschedule it
		scheduler.rescheduleEvent(worldEvent);
	}
}
