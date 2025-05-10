package com.projectkorra.rpg.modules.worldevents.util.schedule.strategies;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.schedule.WorldEventScheduleStrategy;
import org.bukkit.plugin.Plugin;

public class EveryInGameDaysStrategy implements WorldEventScheduleStrategy {
	public EveryInGameDaysStrategy(int intervalGameDays, double chance) {
	}

	@Override
	public void scheduleNext(WorldEvent event, Plugin plugin) {

	}
}
