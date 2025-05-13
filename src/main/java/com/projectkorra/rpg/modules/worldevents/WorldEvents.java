package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.listeners.EnhancedBendingListener;
import com.projectkorra.rpg.modules.worldevents.util.schedule.WorldEventScheduler;

public class WorldEvents extends Module {
	public WorldEvents() {
		super("WorldEvents");
	}

	@Override
	public void enable() {
		registerListeners(
				new EnhancedBendingListener()
		);

		registerDefaults();
		registerCommands();
	}

	@Override
	public void disable() {

	}

	void registerCommands() {
		new WorldEventCommand();
	}

	void registerDefaults() {
		WorldEvent.initAllWorldEvents();

		new WorldEventScheduler();
	}

	public Module getModuel() {
		return this;
	}
}
