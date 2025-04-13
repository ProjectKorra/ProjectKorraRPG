package com.projectkorra.rpg.listeners;

import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.rpg.configuration.Config;
import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RPGListeners implements Listener {
	@EventHandler
	public void onBendingConfigReload(BendingReloadEvent event) {
		for (Config config : ConfigManager.getAllConfigs()) {
			config.reload();
		}
	}
}
