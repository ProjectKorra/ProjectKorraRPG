package com.projectkorra.rpg.worldevent.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.worldevent.WorldEvent;

public class WorldEventFile {

	protected File file;
	protected FileConfiguration config;
	protected WorldEvent event = null;

	public WorldEventFile(String name) {
		this(new File(new File(ProjectKorraRPG.getPlugin().getDataFolder(), "/WorldEvents/"), name + ".yml"));
	}

	public WorldEventFile(File file) {
		this.file = file;
		this.config = YamlConfiguration.loadConfiguration(file);

		reload();
	}

	public void create() {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			}
			catch (IOException e) {
				ProjectKorraRPG.getLog().info("File creation for " + file.getName() + " failed!");
				e.printStackTrace();
			}
		}
	}

	public void reload() {
		create();
		try {
			config.load(file);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			config.options().copyDefaults(true);
			config.save(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getAliases() {
		return config.getStringList("aliases");
	}

	public List<String> getAttributes() {
		return config.getStringList("attributes");
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public String getDescription() {
		return config.getString("description");
	}

	public String getElement() {
		return config.getString("element");
	}

	public String getEndMessage() {
		return config.getString("endmessage");
	}

	public File getFile() {
		return file;
	}

	public int getFrequency() {
		return config.getInt("frequency");
	}

	public double getModifier() {
		return config.getDouble("modifier");
	}

	public String getName() {
		return config.getString("name");
	}

	public String getStartMessage() {
		return config.getString("startmessage");
	}

	public String getTime() {
		return config.getString("time");
	}

	public boolean getDarkenSky() {
		return config.getBoolean("darkensky");
	}

	public boolean getCreateFog() {
		return config.getBoolean("createfog");
	}

	public void setWorldEvent(WorldEvent event) {
		this.event = event;
	}

	public WorldEvent getWorldEvent() {
		return event == null ? null : event;
	}
}
