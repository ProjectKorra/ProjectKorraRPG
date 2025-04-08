package com.projectkorra.rpg.configuration;

import com.projectkorra.projectkorra.configuration.ConfigType;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigManager {

	private static Config config;
	private static Config language;

	private static final ConfigType DEFAULT = new ConfigType("Default");
	private static final ConfigType LANGUAGE = new ConfigType("Language");

	public ConfigManager() {
		config = new Config(new File("config.yml"));
		language = new Config(new File("language.yml"));

		configCheck(DEFAULT);
		configCheck(LANGUAGE);
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
		}
	}

	public static FileConfiguration getConfig() {
		return config.get();
	}

	public static void saveConfig() {
		config.save();
	}
}
