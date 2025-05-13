package com.projectkorra.rpg;

import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.randomavatar.commands.AvatarCommand;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
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
				new AvatarCommand();
				new HelpCommand();
				new WorldEventCommand();
			}
		}.runTaskLater(ProjectKorraRPG.getPlugin(), 20);

		for (WorldEvent worldEvent : WorldEvent.getActiveEvents()) {
			worldEvent.stopEvent();
		}

		ProjectKorraRPG.getPlugin().getModuleManager().enableModules();
	}
}
