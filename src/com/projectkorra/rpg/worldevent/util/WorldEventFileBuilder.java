package com.projectkorra.rpg.worldevent.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.Element;

public class WorldEventFileBuilder {

	private String name;
	private String description;
	private List<String> aliases;
	private List<String> attributes;
	private List<String> elements;
	private int frequency;
	private double modifier;
	private long duration;
	private String time;
	private String startMessage;
	private String endMessage;
	private boolean darkenSky;
	private boolean createFog;
	private List<String> eventBlacklist;
	private String textColor;
	private String barColor;

	public WorldEventFileBuilder() {
		this.name = "GenericName";
		this.description = "GenericDescription";
		this.aliases = new ArrayList<>();
		this.attributes = new ArrayList<>();
		this.frequency = 3;
		this.modifier = 2.0;
		this.duration = 0;
		this.time = "BOTH";
		this.startMessage = "GenericStartMessage";
		this.endMessage = "GenericEndMessage";
		this.eventBlacklist = new ArrayList<>();
		this.elements = new ArrayList<>();
		this.textColor = "WHITE";
		this.barColor = "WHITE";
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
	
	public WorldEventFileBuilder addBlacklistedEvent(String event) {
		this.eventBlacklist.add(event);
		return this;
	}
	
	public WorldEventFileBuilder addBlacklistedEvents(String... events) {
		for (String event : events) {
			this.eventBlacklist.add(event);
		}
		return this;
	}
	
	public WorldEventFileBuilder setBlacklistedEvents(List<String> events) {
		this.eventBlacklist = events;
		return this;
	}

	public WorldEventFileBuilder addElement(Element element) {
		this.elements.add(element.getName());
		return this;
	}
	
	public WorldEventFileBuilder addElements(Element... elements) {
		for (Element element : elements) {
			this.elements.add(element.getName());
		}
		return this;
	}
	
	public WorldEventFileBuilder setElements(List<Element> elements) {
		this.elements = Arrays.asList(elements.stream().map(Element::getName).toArray(String[]::new));
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
	
	public WorldEventFileBuilder duration(long duration) {
		this.duration = duration;
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
	
	public WorldEventFileBuilder barColor(BarColor color) {
		this.barColor = color.toString();
		return this;
	}
	
	public WorldEventFileBuilder textColor(ChatColor color) {
		this.textColor = color.name();
		return this;
	}

	public WorldEventFile build() {
		WorldEventFile wFile = new WorldEventFile(name);
		FileConfiguration config = wFile.getConfig();

		config.addDefault("name", name);
		config.addDefault("description", description);
		config.addDefault("aliases", aliases);
		config.addDefault("elements", elements);
		config.addDefault("time", time);
		config.addDefault("modifier", modifier);
		config.addDefault("duration", duration);
		config.addDefault("attributes", attributes);
		config.addDefault("frequency", frequency);
		config.addDefault("start-message", startMessage);
		config.addDefault("end-message", endMessage);
		config.addDefault("darken-sky", darkenSky);
		config.addDefault("create-fog", createFog);
		config.addDefault("event-blacklist", eventBlacklist);
		config.addDefault("text-color", textColor);
		config.addDefault("bar-color", barColor);

		wFile.save();
		return wFile;
	}
}
