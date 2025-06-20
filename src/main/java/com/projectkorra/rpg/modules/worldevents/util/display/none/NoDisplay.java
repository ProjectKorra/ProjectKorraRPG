package com.projectkorra.rpg.modules.worldevents.util.display.none;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.display.IWorldEventDisplay;

public class NoDisplay implements IWorldEventDisplay {
	@Override
	public void startDisplay(WorldEvent event) {
		// MAYBE LOG
	}

	@Override
	public void updateDisplay(WorldEvent event, double progress) {
		// MAYBE LOG
	}

	@Override
	public void stopDisplay(WorldEvent event) {
		// MAYBE LOG
	}
}
