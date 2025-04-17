package com.projectkorra.rpg;

import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.WorldEvents;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.listeners.RPGListener;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;
import com.projectkorra.rpg.modules.ModuleManager;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import com.projectkorra.rpg.storage.Storage;
import com.projectkorra.rpg.util.MetricsLite;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public class ProjectKorraRPG extends JavaPlugin {
	
	public static ProjectKorraRPG plugin;
	public static Logger log;
	public static LuckPerms luckPermsAPI;

	private AssignmentManager assignmentManager;
	private AvatarManager avatarManager;
	private ModuleManager moduleManager;

	@Override
	public void onEnable() {
		plugin = this;
		moduleManager = new ModuleManager();
		Storage.init();
		setLog(ProjectKorraRPG.getPlugin().getLogger());

		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

		if (provider != null) {
			luckPermsAPI = provider.getProvider();
		}

		new ConfigManager();

		this.initializeCommands();
		
		Bukkit.getServer().getPluginManager().registerEvents(new RPGListener(), this);

		try {
	        new MetricsLite(this).start();
	    } catch (IOException e) {
	        getLogger().severe("Failed to load metric stats" + e.getMessage());
	    }

		moduleManager.enableModules();
	}

	@Override
	public void onDisable() {
		moduleManager.disableModules();
	}

	private void initializeCommands() {
		new RPGCommandBase();
		new AvatarCommand();
		new HelpCommand();
	}

	public static ProjectKorraRPG getPlugin() {
		return plugin;
	}

	public static Logger getLog() {
		return log;
	}

	public static LuckPerms getLuckPermsAPI() {
		return luckPermsAPI;
	}

	public AssignmentManager getAssignmentManager() {
		return assignmentManager;
	}

	public AvatarManager getAvatarManager() {
		return avatarManager;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public static void setPlugin(ProjectKorraRPG plugin) {
		ProjectKorraRPG.plugin = plugin;
	}

	public static void setLog(Logger log) {
		ProjectKorraRPG.log = log;
	}

	public static void setLuckPermsAPI(LuckPerms luckPermsAPI) {
		ProjectKorraRPG.luckPermsAPI = luckPermsAPI;
	}

	public void setAssignmentManager(AssignmentManager assignmentManager) {
		this.assignmentManager = assignmentManager;
	}

	public void setAvatarManager(AvatarManager avatarManager) {
		this.avatarManager = avatarManager;
	}

	public void setModuleManager(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}
}
