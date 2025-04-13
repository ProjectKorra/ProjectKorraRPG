package com.projectkorra.rpg;

import com.projectkorra.rpg.commands.WorldEventCommand;
import com.projectkorra.rpg.listeners.RPGListeners;
import com.projectkorra.rpg.modules.ModuleManager;
import com.projectkorra.rpg.storage.Storage;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;

public class ProjectKorraRPG extends JavaPlugin {
	private static ProjectKorraRPG plugin;
	private ModuleManager moduleManager;

	@Override
	public void onEnable() {
		plugin = this;
		Storage.init();
		moduleManager = new ModuleManager();

		new ConfigManager();

		new RPGCommandBase();

		getServer().getPluginManager().registerEvents(new RPGListeners(), this);
		moduleManager.enableModules();
	}

	@Override
	public void onDisable() {
		moduleManager.disableModules();
	}

	public static ProjectKorraRPG getPlugin() {
		return plugin;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}
}
