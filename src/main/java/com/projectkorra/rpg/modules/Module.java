package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.event.Listener;

public abstract class Module {
    private final ProjectKorraRPG plugin = ProjectKorraRPG.getPlugin();
    private final String name;

    public Module(String name) {
        this.name = name;
    }

    public abstract void enable();

    public abstract void disable();

    public void registerListeners(Listener... l) {
        for (Listener listener : l) {
            ProjectKorraRPG.getPlugin().getServer().getPluginManager().registerEvents(listener, ProjectKorraRPG.getPlugin());
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
