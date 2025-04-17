package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.listeners.EnhancedBendingListener;

public class WorldEvents extends Module {
	public WorldEvents() {
		super("WorldEvents");
	}

	@Override
	public void enable() {
		WorldEvent.initAllWorldEvents();
		ProjectKorraRPG.getPlugin().getServer().getPluginManager().registerEvents(new EnhancedBendingListener(), ProjectKorraRPG.getPlugin());
		new WorldEventCommand();
	}

	@Override
	public void disable() {

	}
}
