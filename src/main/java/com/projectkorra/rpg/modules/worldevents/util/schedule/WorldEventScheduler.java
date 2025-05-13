package com.projectkorra.rpg.modules.worldevents.util.schedule;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.listeners.WorldEventScheduleListener;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldEventScheduler {
	private final Plugin plugin = ProjectKorraRPG.getPlugin();
	private final Map<WorldEvent, List<WorldEventScheduleStrategy>> strategies = new HashMap<>();

	public WorldEventScheduler() {
		initStrategies();
	}

	private void initStrategies() {
		for (WorldEvent event : WorldEvent.getAllEvents().values()) {
			if (event.getWorld() == null) {
				plugin.getLogger().warning("WorldEvent " + event.getKey() + " has no valid world - skipping scheduling.");
				continue;
			}

			WorldEventScheduleStrategy strategy = WorldEventScheduleStrategyFactory.get(event.getConfig());

			if (strategy == null) {
				plugin.getLogger().info("WorldEvent " + event.getKey() + " using MANUAL scheduling - no auto start.");
				continue;
			}

			strategy.scheduleNext(event, plugin);
			strategies.put(event, Collections.singletonList(strategy));
		}

		plugin.getServer().getPluginManager().registerEvents(new WorldEventScheduleListener(plugin, strategies), plugin);
	}
}
