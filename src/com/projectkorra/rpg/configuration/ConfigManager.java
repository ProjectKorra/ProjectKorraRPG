package com.projectkorra.rpg.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.rpg.ProjectKorraRPG;

public class ConfigManager {
	
	static ProjectKorraRPG plugin;
	
	public ConfigManager(ProjectKorraRPG plugin) {
		ConfigManager.plugin = plugin;
		configCheck();
	}
	
	public static void configCheck() {
		FileConfiguration config = ProjectKorraRPG.plugin.getConfig();
		
		config.addDefault("WorldEvents.FullMoon.Enabled", true);
		config.addDefault("WorldEvents.FullMoon.Factor", 3.0);
		config.addDefault("WorldEvents.FullMoon.Element", "Water");
		config.addDefault("WorldEvents.FullMoon.Message", "A full moon is rising, empowering waterbending like never before.");
				
		config.addDefault("WorldEvents.LunarEclipse.Enabled", true);
		config.addDefault("WorldEvents.LunarEclipse.Frequency", 40);
		config.addDefault("WorldEvents.LunarEclipse.Element", "Water");
		config.addDefault("WorldEvents.LunarEclipse.Message", "A lunar eclipse is out! Waterbenders are temporarily powerless.");
		
		config.addDefault("WorldEvents.SolarEclipse.Enabled", true);
		config.addDefault("WorldEvents.SolarEclipse.Frequency", 20);
		config.addDefault("WorldEvents.SolarEclipse.Element", "Fire");
		config.addDefault("WorldEvents.SolarEclipse.Message", "A solar eclipse is out! Firebenders are temporarily powerless.");

		config.addDefault("WorldEvents.SozinsComet.Enabled", true);
		config.addDefault("WorldEvents.SozinsComet.Frequency", 100);
		config.addDefault("WorldEvents.SozinsComet.Factor", 5.0);
		config.addDefault("WorldEvents.SozinsComet.Element", "Fire");
		config.addDefault("WorldEvents.SozinsComet.Message", "Sozin's Comet is passing overhead! Firebending is now at its most powerful.");
		config.addDefault("WorldEvents.SozinsComet.EndMessage", "Sozin's Comet has passed.");
		
		config.addDefault("Abilities.AvatarStateOnFinalBlow", true);
		
		config.addDefault("ElementAssign.Enabled", false);
		config.addDefault("ElementAssign.Default", "None");
		config.addDefault("ElementAssign.Percentages.Earth", 0.205);
		config.addDefault("ElementAssign.Percentages.Water", 0.205);
		config.addDefault("ElementAssign.Percentages.Air", 0.205);
		config.addDefault("ElementAssign.Percentages.Fire", 0.205);
		config.addDefault("ElementAssign.Percentages.Chi", 0.18);
		
		config.addDefault("Storage.engine", "sqlite");

		config.addDefault("Storage.MySQL.host", "localhost");
		config.addDefault("Storage.MySQL.port", 3306);
		config.addDefault("Storage.MySQL.pass", "");
		config.addDefault("Storage.MySQL.db", "minecraft");
		config.addDefault("Storage.MySQL.user", "root");
		
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}

}
