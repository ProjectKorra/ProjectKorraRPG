package com.projectkorra.rpg;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.commands.WorldEventCommand;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.ModuleManager;
import com.projectkorra.rpg.modules.elementassignments.AssignmentManager;
import com.projectkorra.rpg.modules.randomavatar.AvatarManager;
import com.projectkorra.rpg.util.MetricsLite;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class ProjectKorraRPG extends JavaPlugin {

    public static ProjectKorraRPG plugin;
    public static Logger log;
    public static LuckPerms luckPermsAPI;

    private AssignmentManager assignmentManager;
    private AvatarManager avatarManager;
    private ModuleManager moduleManager;

    public static ProjectKorraRPG getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        ProjectKorraRPG.log = this.getLogger();
        plugin = this;
        moduleManager = new ModuleManager();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsAPI = provider.getProvider();
        }
        new ConfigManager();
        moduleManager.enableModules();
        new RPGCommandBase();
        new AvatarCommand();
        new HelpCommand();
        new WorldEventCommand();
        Bukkit.getServer().getPluginManager().registerEvents(new RPGListener(), this);
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
        plugin.getLogger().info("Disabling " + plugin.getName() + "...");
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }


    public AssignmentManager getAssignmentManager() {
        return assignmentManager;
    }

    public void setAssignmentManager(AssignmentManager assignmentManager) {
        this.assignmentManager = assignmentManager;
        ProjectKorraRPG.log.info(assignmentManager.isEnabled() ? "AssignmentManager enabled" : "AssignmentManager disabled");
        ProjectKorraRPG.log.info("AssignmentManager set");
    }

    public AvatarManager getAvatarManager() {
        return avatarManager;
    }

    public void setAvatarManager(AvatarManager avatarManager) {
        ProjectKorraRPG.log.info(avatarManager.isEnabled() ? "AvatarManager enabled" : "AvatarManager disabled");
        this.avatarManager = avatarManager;
    }
}
