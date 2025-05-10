package com.projectkorra.rpg.modules.worldevents.util.display;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;

public interface IWorldEventDisplay {
	void startDisplay(WorldEvent event);
	void updateDisplay(WorldEvent event, double progress);
	void stopDisplay(WorldEvent event);
}
