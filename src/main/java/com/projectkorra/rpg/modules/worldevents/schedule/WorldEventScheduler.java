package com.projectkorra.rpg.modules.worldevents.schedule;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.listeners.WorldEventScheduleListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldEventScheduler {
	private final Plugin plugin = ProjectKorraRPG.getPlugin();
	private final Map<WorldEvent, List<WorldEventScheduleStrategy>> strategies = new HashMap<>();
	private WorldEventScheduleListener worldEventScheduleListener;

	public WorldEventScheduler() {
		initStrategies();
	}

	private void initStrategies() {
		// Clean up any existing strategies before initializing new ones
		cleanup();

		this.plugin.getLogger().info("Initializing WorldEventScheduler...");

		// Process each registered WorldEvent
		for (WorldEvent event : WorldEvent.getAllEvents().values()) {
			if (event.getWorld() == null) {
				this.plugin.getLogger().warning("WorldEvent " + event.getKey() + " has no valid world - skipping scheduling.");
				continue;
			}

			WorldEventScheduleStrategy strategy = WorldEventScheduleStrategyFactory.get(event.getConfig());

			this.strategies.put(event, Collections.singletonList(strategy));
			this.plugin.getLogger().info("Scheduling event: " + event.getKey() + " with strategy: " + strategy.getClass().getSimpleName());
			strategy.scheduleNext(event, this.plugin);
		}

		if (!this.strategies.isEmpty()) {
			this.worldEventScheduleListener = new WorldEventScheduleListener(this.plugin, this.strategies);
			this.plugin.getServer().getPluginManager().registerEvents(this.worldEventScheduleListener, this.plugin);
			this.plugin.getLogger().info("WorldEventScheduler initialized");
		} else {
			this.plugin.getLogger().info("WorldEventScheduler initialized but no events were scheduled");
		}
	}

	public void cleanup() {
		this.plugin.getLogger().info("Cleaning up WorldEventScheduler...");

		// Cancel all existing strategies
		for (List<WorldEventScheduleStrategy> strategyList : this.strategies.values()) {
			for (WorldEventScheduleStrategy strategy : strategyList) {
				strategy.cancelSchedule();
			}
		}
		this.strategies.clear();

		// Unregister listener
		if (this.worldEventScheduleListener != null) {
			HandlerList.unregisterAll(this.worldEventScheduleListener);
			this.worldEventScheduleListener = null;
			this.plugin.getLogger().info("WorldEventScheduler listener unregistered.");
		}

		this.plugin.getLogger().info("WorldEventScheduler cleanup complete");
	}
}
