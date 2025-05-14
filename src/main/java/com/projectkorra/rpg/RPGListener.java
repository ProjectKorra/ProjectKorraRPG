package com.projectkorra.rpg;

import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class RPGListener implements Listener {
	@EventHandler
	public void onBendingConfigReload(BendingReloadEvent event) {
		ConfigManager.reloadConfigs();

		new BukkitRunnable() {
			@Override
			public void run() {
				new RPGCommandBase();
				new HelpCommand();
			}
		}.runTaskLater(ProjectKorraRPG.getPlugin(), 20);

		for (WorldEvent worldEvent : WorldEvent.getActiveEvents()) {
			worldEvent.stopEvent();
		}

		ProjectKorraRPG.getPlugin().getModuleManager().enableModules();
	}
}
