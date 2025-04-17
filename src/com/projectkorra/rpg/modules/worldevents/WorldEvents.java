package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.listeners.EnhancedBendingListener;
import org.bukkit.Bukkit;

public class WorldEvents extends Module {
	public WorldEvents() {
		super("WorldEvents");
	}

	@Override
	public void enable() {
		ProjectKorraRPG plugin = ProjectKorraRPG.getPlugin();

		WorldEvent.initAllWorldEventsAsync()
				.thenRun(() -> Bukkit.getScheduler().runTask(plugin, () -> {
					Bukkit.getPluginManager().registerEvents(
							new EnhancedBendingListener(),
							plugin
					);

					new WorldEventCommand();
				}));
	}

	@Override
	public void disable() {

	}
}
