package com.projectkorra.rpg.configuration;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import com.projectkorra.projectkorra.configuration.ConfigType;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;

public class ConfigManager {

	public static Config rpgConfig;
	public static Config avatarConfig;
	public static Map<EntityType, Config> mobsConfigs;

	public static final ConfigType AVATARS = new ConfigType("RPG_Avatars");
	public static final ConfigType RPG_DEFAULT = new ConfigType("RPG_Default");
	public static final ConfigType MOB_CONFIG = new ConfigType("RPG_Mob");

	public ConfigManager() {
		rpgConfig = new Config(new File("RPG_config.yml"));
		avatarConfig = new Config(new File("RPG_avatars.yml"));
		File directory = new File(ProjectKorraRPG.plugin.getDataFolder(), "/Mob_Files/");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		for (EntityType t : RPGMethods.bendingMobs) {
			mobsConfigs.put(t, new Config(new File(directory, "Mob_" + t.name())));
		}
		configCheck(RPG_DEFAULT);
		configCheck(AVATARS);
	}

	public static void configCheck(ConfigType type) {
		FileConfiguration config;
		if (type == RPG_DEFAULT) {
			config = rpgConfig.get();

			config.addDefault("WorldEvents.FullMoon.Enabled", true);
			config.addDefault("WorldEvents.FullMoon.Factor", 3.0);
			config.addDefault("WorldEvents.FullMoon.Message", "A full moon is rising, empowering waterbending like never before.");

			config.addDefault("WorldEvents.LunarEclipse.Enabled", true);
			config.addDefault("WorldEvents.LunarEclipse.Frequency", 40);
			config.addDefault("WorldEvents.LunarEclipse.Message", "A lunar eclipse is out! Waterbenders are temporarily powerless.");

			config.addDefault("WorldEvents.SolarEclipse.Enabled", true);
			config.addDefault("WorldEvents.SolarEclipse.Frequency", 20);
			config.addDefault("WorldEvents.SolarEclipse.Message", "A solar eclipse is out! Firebenders are temporarily powerless.");

			config.addDefault("WorldEvents.SozinsComet.Enabled", true);
			config.addDefault("WorldEvents.SozinsComet.Frequency", 100);
			config.addDefault("WorldEvents.SozinsComet.Factor", 5.0);
			config.addDefault("WorldEvents.SozinsComet.Message", "Sozin's Comet is passing overhead! Firebending is now at its most powerful.");
			config.addDefault("WorldEvents.SozinsComet.EndMessage", "Sozin's Comet has passed.");

			config.addDefault("Abilities.AvatarStateOnFinalBlow", true);

			config.addDefault("ElementAssign.Enabled", true);
			config.addDefault("ElementAssign.Default", "None");
			config.addDefault("ElementAssign.Percentages.Earth", 0.205);
			config.addDefault("ElementAssign.Percentages.Water", 0.205);
			config.addDefault("ElementAssign.Percentages.Air", 0.205);
			config.addDefault("ElementAssign.Percentages.Fire", 0.205);
			config.addDefault("ElementAssign.Percentages.Chi", 0.18);

			config.addDefault("SubElementAssign.Enabled", true);
			config.addDefault("SubElementAssign.Percentages.Blood", 0.01);
			config.addDefault("SubElementAssign.Percentages.Combustion", 0.3);
			config.addDefault("SubElementAssign.Percentages.Flight", 0.01);
			config.addDefault("SubElementAssign.Percentages.Healing", 0.2);
			config.addDefault("SubElementAssign.Percentages.Ice", 1);
			config.addDefault("SubElementAssign.Percentages.Lava", 0.3);
			config.addDefault("SubElementAssign.Percentages.Lightning", 0.5);
			config.addDefault("SubElementAssign.Percentages.Metal", 0.7);
			config.addDefault("SubElementAssign.Percentages.Plant", 0.7);
			config.addDefault("SubElementAssign.Percentages.Sand", 1);
			config.addDefault("SubElementAssign.Percentages.SpiritualProjection", 0.4);

			config.addDefault("Storage.engine", "sqlite");

			config.addDefault("Storage.MySQL.host", "localhost");
			config.addDefault("Storage.MySQL.port", 3306);
			config.addDefault("Storage.MySQL.pass", "");
			config.addDefault("Storage.MySQL.db", "minecraft");
			config.addDefault("Storage.MySQL.user", "root");

			config.options().copyDefaults(true);
			rpgConfig.save();
		} else if (type == AVATARS) {
			//Nothing needs to be set in the avatars.yml yet
		} else if (type == MOB_CONFIG) {
			for (EntityType t : RPGMethods.bendingMobs) {
				config = mobsConfigs.get(t).get();
				
				config.addDefault("Enabled", true);
				config.addDefault("SummonChance", 50);
				config.addDefault("BendingFrequency", 5);
				
				config.addDefault("Elements.Air.Enabled", true);
				config.addDefault("Elements.Air.Chance", 25);
				config.addDefault("Elements.Air.Skills", Arrays.asList("AirSwipe"));
				config.addDefault("Elements.Earth.Enabled", true);
				config.addDefault("Elements.Earth.Chance", 25);
				config.addDefault("Elements.Earth.Skills", Arrays.asList("EarthBlast"));
				config.addDefault("Elements.Fire.Enabled", true);
				config.addDefault("Elements.Fire.Chance", 25);
				config.addDefault("Elements.Fire.Skills", Arrays.asList("FireBlast"));
				config.addDefault("Elements.Water.Enabled", true);
				config.addDefault("Elements.Water.Chance", 25);
				config.addDefault("Elements.Water.Skills", Arrays.asList("WaterBlast"));
				
				config.options().copyDefaults(true);
				mobsConfigs.get(t).save();
			}
		}
	}
}
