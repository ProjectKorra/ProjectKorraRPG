package com.projectkorra.rpg;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.MySQL;
import com.projectkorra.rpg.ability.AbilityTiers;
import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.ChakraCommand;
import com.projectkorra.rpg.commands.EventCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.LevelBarCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.commands.ScrollCommand;
import com.projectkorra.rpg.commands.UnlockCommand;
import com.projectkorra.rpg.commands.UnlockedCommand;
import com.projectkorra.rpg.commands.WhoCommand;
import com.projectkorra.rpg.commands.XPCommand;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.player.RPGPlayer;
import com.projectkorra.rpg.storage.RPGStorage;
import com.projectkorra.rpg.util.MetricsLite;
import com.projectkorra.rpg.worldevent.EventManager;
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
		initDatabase();
		new RPGMethods();
		enableCommands();
		
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
		RPGPlayer.saveAll();
	}
	
	private void enableCommands() {
		new RPGCommandBase();
		new AvatarCommand();
		new EventCommand();
		new HelpCommand();
		new XPCommand();
		new ChakraCommand();
		new WhoCommand();
		new UnlockCommand();
		new LevelBarCommand();
		new ScrollCommand();
		new UnlockedCommand();
	}
	
	private void initDatabase() {
		if (!DBConnection.sql.tableExists("rpg_players")) {
			DBConnection.sql.modifyQuery(replace("CREATE TABLE rpg_players (id INTEGER PRIMARY KEY <autoinc>, uuid <text>(36) UNIQUE NOT NULL, xp INTEGER, air INTEGER, earth INTEGER, fire INTEGER, water INTEGER, light INTEGER);"));
		}
		
		if (!DBConnection.sql.tableExists("rpg_ability_ids")) {
			DBConnection.sql.modifyQuery(replace("CREATE TABLE rpg_ability_ids (id INTEGER PRIMARY KEY <autoinc>, name <text>(255) UNIQUE NOT NULL);"));
		}
		
		if (!DBConnection.sql.tableExists("rpg_player_abilities")) {
			DBConnection.sql.modifyQuery("CREATE TABLE rpg_player_abilities (player_id INTEGER REFERENCES rpg_players(id), ability_id INTEGER REFERENCES rpg_ability_ids(id), PRIMARY KEY (player_id, ability_id));");
		}
	}
	
	private String replace(String base) {
		String s = base;
		
		if (DBConnection.sql instanceof MySQL) {
			s = s.replace("<autoinc>", "AUTO_INCREMENT");
			s = s.replace("<text>", "varchar");
		} else {
			s = s.replace("<autoinc>", "AUTOINCREMENT");
			s = s.replace("<text>", "TEXT");
		}
		
		return s;
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
