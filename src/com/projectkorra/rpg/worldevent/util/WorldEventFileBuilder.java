package com.projectkorra.rpg.worldevent.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.Element;

public class WorldEventFileBuilder {

	private String name;
	private String description;
	private List<String> aliases;
	private List<String> attributes;
	private String element;
	private int frequency;
	private double modifier;
	private String time;
	private String startMessage;
	private String endMessage;
	private boolean darkenSky;
	private boolean createFog;

	public WorldEventFileBuilder() {
		this.name = "GenericName";
		this.description = "GenericDescription";
		this.aliases = new ArrayList<>();
		this.attributes = new ArrayList<>();
		this.frequency = 3;
		this.modifier = 2.0;
		this.time = "BOTH";
		this.startMessage = "GenericStartMessage";
		this.endMessage = "GenericEndMessage";
	}

	public WorldEventFileBuilder name(String name) {
		this.name = name;
		return this;
	}

	public WorldEventFileBuilder description(String description) {
		this.description = description;
		return this;
	}

	public WorldEventFileBuilder addAlias(String alias) {
		this.aliases.add(alias.toLowerCase());
		return this;
	}

	public WorldEventFileBuilder addAliases(String... aliases) {
		for (String alias : aliases) {
			this.aliases.add(alias.toLowerCase());
		}
		return this;
	}

	public WorldEventFileBuilder setAliases(List<String> aliases) {
		for (int i = aliases.size() - 1; i >= 0; i--) {
			aliases.set(i, aliases.get(i).toLowerCase());
		}
		this.aliases = aliases;
		return this;
	}

	public WorldEventFileBuilder addAttribute(String attribute) {
		this.attributes.add(attribute);
		return this;
	}

	public WorldEventFileBuilder addAttributes(String... attributes) {
		for (String attribute : attributes) {
			this.attributes.add(attribute);
		}
		return this;
	}

	public WorldEventFileBuilder setAttributes(List<String> attributes) {
		this.attributes = attributes;
		return this;
	}

	public WorldEventFileBuilder element(Element element) {
		this.element = element.getName();
		return this;
	}

	public WorldEventFileBuilder frequency(int frequency) {
		this.frequency = frequency;
		return this;
	}

	public WorldEventFileBuilder modifier(double modifier) {
		this.modifier = modifier;
		return this;
	}

	public WorldEventFileBuilder time(Time time) {
		this.time = time.toString().toUpperCase();
		return this;
	}

	public WorldEventFileBuilder startMessage(String message) {
		this.startMessage = message;
		return this;
	}

	public WorldEventFileBuilder endMessage(String message) {
		this.endMessage = message;
		return this;
	}

	public WorldEventFileBuilder darkenSky(boolean darkenSky) {
		this.darkenSky = darkenSky;
		return this;
	}

	public WorldEventFileBuilder createFog(boolean createFog) {
		this.createFog = createFog;
		return this;
	}

	public WorldEventFile build() {
		WorldEventFile wFile = new WorldEventFile(name);
		FileConfiguration config = wFile.getConfig();

		config.addDefault("name", name);
		config.addDefault("description", description);
		config.addDefault("aliases", aliases);
		config.addDefault("element", element);
		config.addDefault("time", time);
		config.addDefault("modifier", modifier);
		config.addDefault("attributes", attributes);
		config.addDefault("frequency", frequency);
		config.addDefault("startmessage", startMessage);
		config.addDefault("endmessage", endMessage);
		config.addDefault("darkensky", darkenSky);
		config.addDefault("createfog", createFog);

		wFile.save();
		return wFile;
	}
}
