package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.event.Listener;

public abstract class Module {
    private final ProjectKorraRPG plugin;
    private final String name;

    public Module(ProjectKorraRPG plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract void enable();

    public abstract void disable();

    public void registerListeners(Listener... l) {
        for (Listener listener : l) {
            this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
        }
    }

    public ProjectKorraRPG getPlugin() {
        return plugin;
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return ConfigManager.getDefaultFileConfig().getBoolean("Modules." + getName() + ".Enabled");
    }
}
