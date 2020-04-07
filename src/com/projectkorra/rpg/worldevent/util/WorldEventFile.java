package com.projectkorra.rpg.worldevent.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.projectkorra.projectkorra.Element;
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
	
	public long getDuration() {
		return config.getLong("duration");
	}

	public Element[] getElements() {
		return config.getStringList("elements").stream().map(Element::getElement).filter((a) -> a != null).toArray(Element[]::new);
	}

	public String getEndMessage() {
		return config.getString("end-message");
	}
	
	public List<String> getEventBlacklist() {
		return config.getStringList("event-blacklist");
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
		return config.getString("start-message");
	}

	public Time getTime() {
		Time time = Time.valueOf(config.getString("time").toUpperCase());
		
		if (time == null) {
			time = Time.BOTH;
		}
		
		return time;
	}

	public boolean getDarkenSky() {
		return config.getBoolean("darken-sky");
	}

	public boolean getCreateFog() {
		return config.getBoolean("create-fog");
	}
	
	public ChatColor getTextColor() {
		ChatColor text = ChatColor.valueOf(config.getString("text-color").toUpperCase());
		
		if (text == null) {
			text = ChatColor.WHITE;
		}
		
		return text;
	}
	
	public BarColor getBarColor() {
		BarColor bar = BarColor.valueOf(config.getString("bar-color").toUpperCase());
		
		if (bar == null) {
			bar = BarColor.WHITE;
		}
		
		return bar;
	}

	public void setWorldEvent(WorldEvent event) {
		this.event = event;
	}

	public WorldEvent getWorldEvent() {
		return event == null ? null : event;
	}
}
