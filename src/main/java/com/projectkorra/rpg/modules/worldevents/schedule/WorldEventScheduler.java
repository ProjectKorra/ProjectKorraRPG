package com.projectkorra.rpg.modules.worldevents.schedule;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.listeners.WorldEventScheduleListener;
import com.projectkorra.rpg.modules.worldevents.schedule.storage.ScheduleStorage;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldEventScheduler {
	private final Plugin plugin = ProjectKorraRPG.getPlugin();
	private final Map<WorldEvent, ScheduledEventContext> scheduledEvents = new ConcurrentHashMap<>();
	private WorldEventScheduleListener worldEventScheduleListener;
	private final ScheduleStorage scheduleStorage;

	public WorldEventScheduler(WorldEventScheduleListener worldEventScheduleListener, ScheduleStorage scheduleStorage) {
		this.worldEventScheduleListener = worldEventScheduleListener;
		this.scheduleStorage = scheduleStorage;

		initSchedules();
	}

	/**
	 * Initialize all world event schedules
	 */
	private void initSchedules() {
		// Clean up any existing strategies before initializing
		cleanup();

		this.plugin.getLogger().info("Initializing WorldEventScheduler...");

		// Process each registered WorldEvent
		for (WorldEvent event : WorldEvent.getAllEvents().values()) {
			try {
				scheduleEvent(event);
			} catch (Exception e) {
				this.plugin.getLogger().severe("Failed to schedule event: " + event.getKey() + e.getMessage());
			}
		}
	}

	/**
	 * Schedule a single world event
	 */
	private void scheduleEvent(WorldEvent event) {
		cancelEvent(event);

		// Create a new strategy based on WorldEvent configuration
		WorldEventScheduleStrategy scheduleStrategy = WorldEventScheduleStrategyFactory.get(event.getConfig(), this.scheduleStorage);

		// Store the event context
		ScheduledEventContext context = new ScheduledEventContext(event, scheduleStrategy);
		this.scheduledEvents.put(event, context);

		this.plugin.getLogger().info("Scheduling event: " + event.getKey() + " with strategy: " + scheduleStrategy.getClass().getSimpleName());

		// Start the schedule
		scheduleStrategy.scheduleNext(event, this.plugin);
		context.setActive(true);
	}

	/**
	 * Reschedule an event after it has stopped
	 */
	public void rescheduleEvent(WorldEvent event) {
		ScheduledEventContext context = this.scheduledEvents.get(event);
		if (context != null) {
			plugin.getLogger().info("Rescheduling event: " + event.getKey());
			context.getStrategy().scheduleNext(context.getEvent(), this.plugin);;
		}
	}

	/**
	 * Cancel a scheduling for a specific event
	 */
	public void cancelEvent(WorldEvent event) {
		ScheduledEventContext context = this.scheduledEvents.get(event);
		if (context != null) {
			context.getStrategy().cancelSchedule();
			context.setActive(false);
			plugin.getLogger().info("Cancelled schedule for event: " + event.getKey());
		}
	}

	/**
	 * Set an event as active or inactive
	 */
	public void setEventActive(WorldEvent event, boolean active) {
		ScheduledEventContext context = this.scheduledEvents.get(event);
		if (context != null) {
			context.setActive(active);
		}
	}

	/**
	 * Check if an event is currently scheduled
	 */
	public boolean isEventScheduled(WorldEvent event) {
		ScheduledEventContext context = this.scheduledEvents.get(event);
		return context != null && context.isActive();
	}

	/**
	 * Clean up all scheduled events
	 */
	public void cleanup() {
		this.plugin.getLogger().info("Cleaning up WorldEventScheduler...");

		// Cancel all existing strategies
		for (ScheduledEventContext context : scheduledEvents.values()) {
			try {
				context.getStrategy().cancelSchedule();
			} catch (Exception e) {
				this.plugin.getLogger().severe("Failed to cancel strategy for event: " + context.getEvent().getKey() + e.getMessage());
			}
		}

		scheduledEvents.clear();

		// Unregister listener
		if (this.worldEventScheduleListener != null) {
			HandlerList.unregisterAll(this.worldEventScheduleListener);
			this.worldEventScheduleListener = null;
		}
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public Map<WorldEvent, ScheduledEventContext> getScheduledEvents() {
		return scheduledEvents;
	}

	public WorldEventScheduleListener getWorldEventScheduleListener() {
		return worldEventScheduleListener;
	}

	public ScheduleStorage getScheduleStorage() {
		return scheduleStorage;
	}

	/**
	 * ScheduledEventContext keeps all scheduling information about a single world event
	 */
	private static class ScheduledEventContext {
		private final WorldEvent event;
		private final WorldEventScheduleStrategy strategy;
		private boolean active = false;

		public ScheduledEventContext(WorldEvent event, WorldEventScheduleStrategy strategy) {
			this.event = event;
			this.strategy = strategy;
		}

		public WorldEvent getEvent() {
			return event;
		}

		public WorldEventScheduleStrategy getStrategy() {
			return strategy;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}
	}
}
