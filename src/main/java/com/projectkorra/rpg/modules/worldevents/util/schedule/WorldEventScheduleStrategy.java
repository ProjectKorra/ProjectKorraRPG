package com.projectkorra.rpg.modules.worldevents.util.schedule;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.plugin.Plugin;

public interface WorldEventScheduleStrategy {
	void scheduleNext(WorldEvent event, Plugin plugin);
}
