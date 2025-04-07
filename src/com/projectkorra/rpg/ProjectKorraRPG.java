package com.projectkorra.rpg;

import com.projectkorra.rpg.modules.manager.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;

public class ProjectKorraRPG extends JavaPlugin {

	private static ProjectKorraRPG plugin;
	private ModuleManager moduleManager;

	@Override
	public void onEnable() {
		plugin = this;
		moduleManager = new ModuleManager();

		new ConfigManager();
		new RPGCommandBase();

		moduleManager.enableModules();
	}

	@Override
	public void onDisable() {
		plugin.getLogger().info("Disabling " + plugin.getName() + "...");
	}

	public static ProjectKorraRPG getPlugin() {
		return plugin;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}
}
