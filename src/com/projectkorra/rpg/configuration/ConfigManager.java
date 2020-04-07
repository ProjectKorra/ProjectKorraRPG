package com.projectkorra.rpg.configuration;

import java.io.File;
import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import com.projectkorra.projectkorra.configuration.ConfigType;

public class ConfigManager {

	private static Config rpgConfig;

	private static final ConfigType RPG_DEFAULT = new ConfigType("RPG_Default");

	public ConfigManager() {
		rpgConfig = new Config(new File("RPG_config.yml"));
		configCheck(RPG_DEFAULT);
	}

	public static void configCheck(ConfigType type) {
		FileConfiguration config = rpgConfig.get();
		
		config.addDefault("ResetLevelOnElementChange", true);

		config.addDefault("Avatar.AvatarStateOnFinalBlow", true);
		config.addDefault("Avatar.CurrentAvatar", "");
		config.addDefault("Avatar.AutoCycle.Enabled", true);
		config.addDefault("Avatar.AutoCycle.Interval", 1800000L);

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
		config.addDefault("SubElementAssign.Percentages.Spiritual", 0.4);
		
		config.addDefault("LightChakraAttributes", Arrays.asList("Knockback::MULTIPLICATION", "Knockup::MULTIPLICATION", "Radius::ADDITION"));
		
		config.addDefault("ChatColors.DEFAULT", "GRAY");
		config.addDefault("ChatColors.NOVICE", "YELLOW");
		config.addDefault("ChatColors.INTERMEDIATE", "AQUA");
		config.addDefault("ChatColors.ADVANCED", "RED");
		config.addDefault("ChatColors.MASTER", "GREEN");
		
		for (EntityType etype : EntityType.values()) {
			if (etype.isAlive()) {
				config.addDefault("MobDrops." + etype.toString() + ".XP", 25);
				config.addDefault("MobDrops." + etype.toString() + ".DefiniteDrops", Arrays.asList("list abilities here"));
				config.addDefault("MobDrops." + etype.toString() + ".RandomChance", 12);
				config.addDefault("MobDrops." + etype.toString() + ".RandomDropTiers", Arrays.asList("NOVICE", "INTERMEDIATE", "ADVANCED", "MASTER"));
			}
		}

		config.addDefault("Storage.engine", "sqlite");

		config.addDefault("Storage.MySQL.host", "localhost");
		config.addDefault("Storage.MySQL.port", 3306);
		config.addDefault("Storage.MySQL.pass", "");
		config.addDefault("Storage.MySQL.db", "minecraft");
		config.addDefault("Storage.MySQL.user", "root");

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
