package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.listeners.EnhancedBendingListener;
import org.bukkit.Bukkit;

public class WorldEvents extends Module {
	public WorldEvents() {
		super("WorldEvents");
	}

	@Override
	public void enable() {
		Bukkit.getPluginManager().registerEvents(new EnhancedBendingListener(), ProjectKorraRPG.getPlugin());
	}

	@Override
	public void disable() {

	}
}
