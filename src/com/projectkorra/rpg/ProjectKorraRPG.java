package com.projectkorra.rpg;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class ProjectKorraRPG extends JavaPlugin {
	
	public static ProjectKorraRPG plugin;
	public static Logger log;

	@Override
	public void onEnable() {
		ProjectKorraRPG.log = this.getLogger();
		plugin = this;
		
		new RPGMethods(this);
		
		try {
	        MetricsLite metrics = new MetricsLite(this);
	        metrics.start();
	    } catch (IOException e) {
	        // Failed to submit the stats :-(
	    }
		
		/*
		 *TODO Register Listeners
		 *Create DBConnection class / Register it
		 *ConfigManager
		 *config.yml
		 */
	}
	
	@Override
	public void onDisable() {
		
	}
}
