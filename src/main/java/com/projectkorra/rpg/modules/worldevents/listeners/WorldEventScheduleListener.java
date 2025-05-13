package com.projectkorra.rpg.modules.worldevents.listeners;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduleStrategy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class WorldEventScheduleListener implements Listener {
	private final Plugin plugin;
	private final Map<WorldEvent, List<WorldEventScheduleStrategy>> strategies;

	public WorldEventScheduleListener(Plugin plugin, Map<WorldEvent, List<WorldEventScheduleStrategy>> strategies) {
		this.plugin = plugin;
		this.strategies = strategies;
	}

	@EventHandler
	public void onWorldEventStop(WorldEventStopEvent event) {
		WorldEvent worldEvent = event.getWorldEvent();
		List<WorldEventScheduleStrategy> eventStrategies = this.strategies.get(worldEvent);

		if (eventStrategies != null && !eventStrategies.isEmpty()) {
			this.plugin.getLogger().info("WorldEvent " + worldEvent.getKey() + " stopped, rescheduling...");

			for (WorldEventScheduleStrategy strat : eventStrategies) {
				strat.scheduleNext(worldEvent, this.plugin);
			}
		};
	}
}
