package com.projectkorra.rpg.configuration;

import com.projectkorra.rpg.ProjectKorraRPG;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private final ProjectKorraRPG plugin;

    private final File file;
    private final FileConfiguration config;

    /**
     * Creates a new {@link Config} with the file being the configuration file.
     *
     * @param file The file to create/load
     */
    public Config(File file) {
        this.plugin = ProjectKorraRPG.plugin;
        this.file = new File(plugin.getDataFolder() + File.separator + file);
        this.config = YamlConfiguration.loadConfiguration(this.file);
        reload();
    }

    /**
     * Creates a file for the {@link FileConfiguration} object. If there are
     * missing folders, this method will try to create them before create a file
     * for the config.
     */
    public void create() {
        if (!file.getParentFile().exists()) {
            try {
                if (file.getParentFile().mkdir()) {
                    plugin.getLogger().info("Generating new directory for " + file.getName() + "!");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to generate directory!" + e.getMessage());
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.getLogger().info("Generating new " + file.getName() + "!");
            } catch (Exception e) {
                plugin.getLogger().info("Failed to generate " + file.getName() + "!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the {@link FileConfiguration} object from the {@link Config}.
     *
     * @return the file configuration object
     */
    public FileConfiguration get() {
        return config;
    }

    /**
     * Reloads the {@link FileConfiguration} object. If the config object does
     * not exist it will run {@link #create()} first before loading the config.
     */
    public void reload() {
        create();
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the {@link FileConfiguration} object.
     * {@code config.options().copyDefaults(true)} is called before saving the
     * config.
     */
    public void save() {
        try {
            config.options().copyDefaults(true);
            config.options().parseComments(true);
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
