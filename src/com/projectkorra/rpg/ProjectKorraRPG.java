package com.projectkorra.rpg;

import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.EventCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.storage.DBConnection;
import com.projectkorra.rpg.util.MetricsLite;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ProjectKorraRPG extends JavaPlugin {
	
	public static ProjectKorraRPG plugin;
	public static Logger log;

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
		
		if (this.getDataFolder() != null) {
			if (new File(this.getDataFolder(), "projectkorra.db").exists()) {
				importDatabase();
			}
		}
		
		
		Bukkit.getServer().getPluginManager().registerEvents(new RPGListener(), this);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new EventManager(), 0L, 1L);
		
		try {
	        MetricsLite metrics = new MetricsLite(this);
	        metrics.start();
	    } catch (IOException e) {
	        e.printStackTrace();
	        log.info("Failed to load metric stats");
	    }
	}
	
	@Override
	public void onDisable() {
		// Might do something later
	}
	
	public void importDatabase() {
		DBConnection.open();
		ProjectKorraRPG.log.info("Attempting to connect to the database.");
		
		if (!DBConnection.isOpen()) {
			ProjectKorraRPG.log.info("Failed to connect to the database.");
			return;
		}
		if (!DBConnection.sql.tableExists("pk_avatars")) {
			ProjectKorraRPG.log.info("Table no longer exists, import aborted.");
			DBConnection.close();
			return;
		}
		ConcurrentHashMap<String, String> avatars = new ConcurrentHashMap<>();
		ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM pk_avatars WHERE uuid IS NOT NULL");
		
		try {
			while (rs.next()) {
				String uuid = rs.getString(2);
				String elements = rs.getString(4);
				avatars.putIfAbsent(uuid, elements);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			ProjectKorraRPG.log.info("Exception occured. Database closed.");
			DBConnection.close();
			return;
		}
		
		for (String uuid : avatars.keySet()) {
			String elements = avatars.get(uuid);
			if (ConfigManager.avatarConfig.get().contains("Avatar.Past." + uuid))
				ConfigManager.avatarConfig.get().set("Avatar.Past." + uuid, elements);
		}
		
		ConfigManager.avatarConfig.save();
		ProjectKorraRPG.log.info("Import successful, Database closed.");
		DBConnection.close();
	}
}
