package com.projectkorra.rpg;

import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.metrics.MetricsLite;
import com.projectkorra.rpg.modules.ModuleManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class ProjectKorraRPG extends JavaPlugin {
	public static ProjectKorraRPG plugin;
	public static LuckPerms luckPermsAPI;

	private ModuleManager moduleManager;

	@Override
	public void onEnable() {
		plugin = this;
		moduleManager = new ModuleManager();

		Bukkit.getServer().getPluginManager().registerEvents(new RPGListener(), this);

		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) {
			luckPermsAPI = provider.getProvider();
		}

		// DEFAULT INITS
		new ConfigManager();
		new RPGCommandBase();
		new HelpCommand();

		// No need to init all other commands here, since we are doing it in ModuleManager.enable()
		moduleManager.enableModules();

		// METRICS
		try {
			new MetricsLite(this).start();
		} catch (IOException e) {
			getLogger().severe("Failed to load metric stats" + e.getMessage());
		}
	}

	@Override
	public void onDisable() {
		moduleManager.disableModules();
	}

	public static ProjectKorraRPG getPlugin() {
		return plugin;
	}

	public static LuckPerms getLuckPermsAPI() {
		return luckPermsAPI;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}
}
