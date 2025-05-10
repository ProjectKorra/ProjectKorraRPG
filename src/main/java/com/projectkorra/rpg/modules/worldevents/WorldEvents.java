package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.listeners.EnhancedBendingListener;
import com.projectkorra.rpg.modules.worldevents.util.schedule.WorldEventScheduleStrategy;
import com.projectkorra.rpg.modules.worldevents.util.schedule.WorldEventScheduler;
import org.bukkit.event.Listener;

public class WorldEvents extends Module {
	public WorldEvents() {
		super("WorldEvents");
	}

	@Override
	public void enable() {
		registerListeners(
				new EnhancedBendingListener()
		);
		registerCommands();
		registerDefaults();
	}

	@Override
	public void disable() {

	}

	void registerListeners(Listener... l) {
		for (Listener listener : l) {
			ProjectKorraRPG.getPlugin().getServer().getPluginManager().registerEvents(listener, ProjectKorraRPG.getPlugin());
		}
	}

	void registerCommands() {
		new WorldEventCommand();
	}

	void registerDefaults() {
		WorldEvent.initAllWorldEvents();

		new WorldEventScheduler();
	}
}
