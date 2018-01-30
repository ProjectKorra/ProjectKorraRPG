package com.projectkorra.rpg;

import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.EventCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.rpg.util.MetricsLite;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
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
}
