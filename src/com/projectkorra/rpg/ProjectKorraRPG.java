package com.projectkorra.rpg;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.ProjectKorra.Methods;
import com.projectkorra.rpg.api.MechaAPI;
import com.projectkorra.rpg.items.CraftingRecipes;

public class ProjectKorraRPG extends JavaPlugin {
	
	public static ProjectKorraRPG plugin;
	public static Logger log;

	@Override
	public void onEnable() {
		ProjectKorraRPG.log = this.getLogger();
		plugin = this;
		new ConfigManager(this);

		new RPGMethods(this);
		
		ConfigManager.configCheck();
		
		DBConnection.engine = getConfig().getString("Storage.engine");
		DBConnection.host = getConfig().getString("Storage.MySQL.host");
		DBConnection.port = getConfig().getInt("Storage.MySQL.port");
		DBConnection.pass = getConfig().getString("Storage.MySQL.pass");
		DBConnection.db = getConfig().getString("Storage.MySQL.db");
		DBConnection.user = getConfig().getString("Storage.MySQL.user");
		
		DBConnection.init();
		
		Bukkit.getServer().getPluginManager().registerEvents(new RPGListener(), this);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new MechaSuitManager(), 0, 1);
		
		Bukkit.getPlayer("sampepere").getInventory().addItem(MechaAPI.createMechaSuit());
		
		try {
	        MetricsLite metrics = new MetricsLite(this);
	        metrics.start();
	    } catch (IOException e) {
	        // Failed to submit the stats :-(
	    }
		
		new CraftingRecipes(this);
		
		
		for(World w : Bukkit.getWorlds()) {
			for(Entity en : w.getEntities()) {
				if(en instanceof IronGolem) {
					if(en.getCustomName().equalsIgnoreCase(Methods.getChiColor() + "Mecha Suit")) {
						RPGListener.unmounted.put((IronGolem)en, en.getLocation());
					}
				}
			}
		}
	}
	
	@Override
	public void onDisable() {
		RPGListener.riding.clear();
		RPGListener.unmounted.clear();
	}
}
