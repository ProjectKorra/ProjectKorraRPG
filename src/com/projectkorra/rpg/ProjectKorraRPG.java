package com.projectkorra.rpg;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.rpg.ability.AbilityTiers;
import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.EventCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.storage.RPGStorage;
import com.projectkorra.rpg.util.MetricsLite;
import com.projectkorra.rpg.worldevent.util.EventManager;
import com.projectkorra.rpg.worldevent.util.WorldEventDisplayManager;
import com.projectkorra.rpg.worldevent.util.WorldEventFileManager;

public class ProjectKorraRPG extends JavaPlugin {

	private static ProjectKorraRPG plugin;
	private static Logger log;
	private static EventManager eventManager;
	private static WorldEventFileManager wFileManager;
	private static WorldEventDisplayManager wDisplayManager;
	private static RPGStorage storage;
	private static AbilityTiers tiers;

	@Override
	public void onEnable() {
		ProjectKorraRPG.log = this.getLogger();
		plugin = this;

		new ConfigManager();
		new RPGMethods();
		new RPGCommandBase();
		new AvatarCommand();
		new EventCommand();
		new HelpCommand();
		wFileManager = new WorldEventFileManager();
		wDisplayManager = new WorldEventDisplayManager(this);
		eventManager = new EventManager();
		storage = new RPGStorage();
		tiers = new AbilityTiers();
		RPGMethods.loadAvatarCycle();

		Bukkit.getServer().getPluginManager().registerEvents(new RPGListener(), this);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, eventManager, 0L, 1L);

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		}
		catch (IOException e) {
			e.printStackTrace();
			log.info("Failed to load metric stats");
		}
	}

	@Override
	public void onDisable() {
		wDisplayManager.removeAll();
		RPGMethods.saveAvatarCycle();
	}

	public static ProjectKorraRPG getPlugin() {
		return plugin;
	}

	public static Logger getLog() {
		return log;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static WorldEventFileManager getFileManager() {
		return wFileManager;
	}

	public static WorldEventDisplayManager getDisplayManager() {
		return wDisplayManager;
	}

	public static RPGStorage getStorage() {
		return storage;
	}
	
	public static AbilityTiers getAbilityTiers() {
		return tiers;
	}
}
