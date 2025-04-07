package com.projectkorra.rpg.configuration;

import com.projectkorra.projectkorra.configuration.ConfigType;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigManager {

	private static Config rpgConfig;

	private static final ConfigType RPG_DEFAULT = new ConfigType("RPG_Default");

	public ConfigManager() {
		rpgConfig = new Config(new File("RPG_config.yml"));
		configCheck(RPG_DEFAULT);
	}

	public static void configCheck(ConfigType type) {
		FileConfiguration config = rpgConfig.get();

		// MODULE SETUP
		for (Module module : ProjectKorraRPG.getPlugin().getModuleManager().getModules()) {
			String key = "Configuration.Modules." + module.getName() + ".Enabled";
			config.addDefault(key, true);
		}

		config.options().copyDefaults(true);
		rpgConfig.save();
	}

	public static FileConfiguration getConfig() {
		return rpgConfig.get();
	}

	public static void saveConfig() {
		rpgConfig.save();
	}
}
