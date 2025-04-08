package com.projectkorra.rpg;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.avatar.AvatarManager;
import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.EventCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.elementassign.AssignmentManager;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.util.MetricsLite;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ProjectKorraRPG extends JavaPlugin {
	
	public static ProjectKorraRPG plugin;
	public static Logger log;
	public static LuckPerms luckPermsAPI;
	private Map<String, Element> elementMap;
	private AssignmentManager assignmentManager;
	private AvatarManager avatarManager;

	@Override
	public void onEnable() {
		ProjectKorraRPG.log = this.getLogger();
		plugin = this;
		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (provider != null) {
			luckPermsAPI = provider.getProvider();
		}
		setElementMap(grabElements());
		new ConfigManager();
		assignmentManager = new AssignmentManager();
		avatarManager = new AvatarManager();
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
	private Map<String, Element> grabElements() {
		Element[] elements = Element.getAllElements();
		Element[] subElements = Element.getAllSubElements();
		// Return these combined into a Hash
		Map<String, Element> elementMap = new HashMap<>();
		for (Element e : elements) {
			if (e == null) continue;
			elementMap.put(e.getName().toLowerCase(), e);
		}
		for (Element e : subElements) {
			if (e == null) continue;
			// Don't overwrite the main elements
			if (elementMap.containsKey(e.getName().toLowerCase())) {
				// Append _sub if it already exists to avoid overwriting main elements
				elementMap.put(e.getName().toLowerCase() + "_sub", e);
			} else {
				elementMap.put(e.getName().toLowerCase(), e);
			}
		}
		return elementMap;
	}

	@Override
	public void onDisable() {
		// Might do something later
	}

	public Map<String, Element> getElementMap() {
		return elementMap;
	}

	public void setElementMap(Map<String, Element> elementMap) {
		this.elementMap = elementMap;
	}

	public AssignmentManager getAssignmentManager() {
		return assignmentManager;
	}

	public void setAssignmentManager(AssignmentManager assignmentManager) {
		this.assignmentManager = assignmentManager;
	}

	public AvatarManager getAvatarManager() {
		return avatarManager;
	}

	public void setAvatarManager(AvatarManager avatarManager) {
		this.avatarManager = avatarManager;
	}
}
