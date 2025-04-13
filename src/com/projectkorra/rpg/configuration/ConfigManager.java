package com.projectkorra.rpg.configuration;

import com.projectkorra.projectkorra.configuration.ConfigType;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
	private static Config config;
	private static Config language;
	private static Config sozinsComet;

	private static final ConfigType DEFAULT = new ConfigType("Default");
	private static final ConfigType LANGUAGE = new ConfigType("Language");
	private static ConfigType WORLDEVENTS = new ConfigType("WorldEvents");

	public ConfigManager() {
		init();
	}

	private void init() {
		config = new Config(new File("config.yml"));
		language = new Config(new File("language.yml"));
		sozinsComet = new Config(new File("WorldEvents/sozinscomet.yml"));

		configCheck(DEFAULT);
		configCheck(LANGUAGE);
		configCheck(WORLDEVENTS);
	}

	public static void configCheck(ConfigType type) {
		FileConfiguration config;
		if (type == DEFAULT) {
			config = ConfigManager.config.get();

			// MODULE SETUP
			for (Module module : ProjectKorraRPG.getPlugin().getModuleManager().getModules()) {
				String key = "Configuration.Modules." + module.getName() + ".Enabled";
				config.addDefault(key, true);
			}

			// ---------------------------------- WORLD EVENTS ----------------------------------



			// ----------------------------------------------------------------------------------



			// ------------------------------------ LEVELING ------------------------------------



			// ----------------------------------------------------------------------------------



			// ---------------------------------- AVATAR CYCLE ----------------------------------



			// ----------------------------------------------------------------------------------



			// -------------------------------- RANDOM ELEMENTS ---------------------------------


			// MAIN ELEMENTS
			config.addDefault("Configuration.Modules.RandomElements.Element.Hybrid", true);
			config.addDefault("Configuration.Modules.RandomElements.Element.Chi.Chance", 0);
			config.addDefault("Configuration.Modules.RandomElements.Element.Water.Chance", 25);
			config.addDefault("Configuration.Modules.RandomElements.Element.Earth.Chance", 25);
			config.addDefault("Configuration.Modules.RandomElements.Element.Fire.Chance", 25);
			config.addDefault("Configuration.Modules.RandomElements.Element.Air.Chance", 25);

			// WATER-SUB
			config.addDefault("Configuration.Modules.RandomElements.Element.Water.Blood.Chance", 1);
			config.addDefault("Configuration.Modules.RandomElements.Element.Water.Ice.Chance", 75);
			config.addDefault("Configuration.Modules.RandomElements.Element.Water.Healing.Chance", 50);
			config.addDefault("Configuration.Modules.RandomElements.Element.Water.Plant.Chance", 50);

			// EARTH-SUB
			config.addDefault("Configuration.Modules.RandomElements.Element.Earth.Lava.Chance", 5);
			config.addDefault("Configuration.Modules.RandomElements.Element.Earth.Metal.Chance", 35);
			config.addDefault("Configuration.Modules.RandomElements.Element.Earth.Sand.Chance", 85);

			// FIRE-SUB
			config.addDefault("Configuration.Modules.RandomElements.Element.Fire.Lightning.Chance", 25);
			config.addDefault("Configuration.Modules.RandomElements.Element.Fire.Combustion.Chance", 5);
			config.addDefault("Configuration.Modules.RandomElements.Element.Fire.BlueFire.Chance", 5);

			// AIR-SUB
			config.addDefault("Configuration.Modules.RandomElements.Element.Air.Flight.Chance", 1);
			config.addDefault("Configuration.Modules.RandomElements.Element.Air.Spiritual.Chance", 50);


			// ---------------------------------------------------------------------------------


			config.options().copyDefaults(true);
			ConfigManager.config.save();
		} else if (type == LANGUAGE) {
			config = language.get();

			config.options().copyDefaults(true);
			language.save();
		} else if (type == WORLDEVENTS) {
			config = sozinsComet.get();

			config.addDefault("Title", "&cSozins Comet");
			config.addDefault("Duration", 5000);
			config.addDefault("Color", "RED");

			config.options().copyDefaults(true);
			sozinsComet.save();
		}
	}

	public static FileConfiguration getFileConfig() {
		return config.get();
	}

	public static Config getConfig() {
		return config;
	}

	public static List<Config> getAllConfigs() {
		List<Config> configs = new ArrayList<>();
		configs.add(config);
		configs.add(language);
		configs.add(sozinsComet);
		return configs;
	}
}
