package com.projectkorra.rpg.modules.worldevents.util.schedule.strategies;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.schedule.WorldEventScheduleStrategy;
import org.bukkit.plugin.Plugin;

public class EveryRealWorldDaysStrategy implements WorldEventScheduleStrategy {
	public EveryRealWorldDaysStrategy(int intervalDays, double chance) {
	}

	@Override
	public void scheduleNext(WorldEvent event, Plugin plugin) {

	}
}
