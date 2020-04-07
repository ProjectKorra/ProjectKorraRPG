package com.projectkorra.rpg.worldevent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.worldevent.util.Time;
import com.projectkorra.rpg.worldevent.util.WorldEventFile;

public class WorldEvent implements RPGWorldEvent {

	protected static Map<String, WorldEvent> events = new HashMap<>();

	private String name;
	private String description;
	private List<String> aliases;
	private List<String> attributes;
	private Set<Element> elements;
	private Time time;
	private int frequency;
	private double modifier;
	private long duration;
	private String startMessage;
	private String endMessage;
	private boolean darkenSky;
	private boolean createFog;
	private List<String> eventBlacklist;
	private ChatColor text;
	private BarColor bar;

	public WorldEvent(WorldEventFile wFile) {
		this(wFile.getName(), wFile.getDescription(), wFile.getAliases(), wFile.getAttributes(), wFile.getElements(), wFile.getTime(), wFile.getFrequency(), wFile.getModifier(), wFile.getDuration(), wFile.getStartMessage(), wFile.getEndMessage(), wFile.getDarkenSky(), wFile.getCreateFog(), wFile.getEventBlacklist(), wFile.getTextColor(), wFile.getBarColor());
	}

	public WorldEvent(String name, String description, List<String> aliases, List<String> attributes, Element[] elements, Time time, int frequency, double modifier, long duration, String startMessage, String endMessage, boolean darkenSky, boolean createFog, List<String> eventBlacklist, ChatColor text, BarColor bar) {
		this.name = name;
		this.description = description;
		this.aliases = aliases;
		this.attributes = attributes;
		this.elements = new HashSet<>(Arrays.asList(elements));
		this.time = time;
		this.frequency = frequency;
		this.modifier = modifier;
		this.duration = duration;
		this.startMessage = startMessage;
		this.endMessage = endMessage;
		this.darkenSky = darkenSky;
		this.createFog = createFog;
		this.eventBlacklist = eventBlacklist;
		this.text = text;
		this.bar = bar;
		events.put(name.toLowerCase(), this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public List<String> getAttributes() {
		return attributes;
	}
	
	@Override
	public List<String> getBlacklistedEvents() {
		return eventBlacklist;
	}

	@Override
	public Set<Element> getElements() {
		return elements;
	}

	@Override
	public Time getTime() {
		return time;
	}

	@Override
	public int getFrequency() {
		return frequency;
	}

	@Override
	public double getModifier() {
		return modifier;
	}
	
	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public String getStartMessage() {
		return startMessage;
	}

	@Override
	public String getEndMessage() {
		return endMessage;
	}

	@Override
	public boolean getDarkenSky() {
		return darkenSky;
	}

	@Override
	public boolean getCreateFog() {
		return createFog;
	}
	
	@Override
	public ChatColor getTextColor() {
		return text;
	}
	
	@Override
	public BarColor getBarColor() {
		return bar;
	}

	public static WorldEvent get(String name) {
		return events.containsKey(name.toLowerCase()) ? events.get(name.toLowerCase()) : (ProjectKorraRPG.getFileManager().loadFile(name.toLowerCase()) == null ? null : get(name));
	}

	public static Set<String> getEventNames() {
		return new HashSet<>(events.keySet());
	}

	public static Set<WorldEvent> getEvents() {
		return new HashSet<>(events.values());
	}
}
