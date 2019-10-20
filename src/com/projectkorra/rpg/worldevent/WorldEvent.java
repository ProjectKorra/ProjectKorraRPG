package com.projectkorra.rpg.worldevent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.worldevent.util.Time;
import com.projectkorra.rpg.worldevent.util.WorldEventFile;

public class WorldEvent implements IWorldEvent {

	protected static Map<String, WorldEvent> events = new HashMap<>();
	protected static Map<Element, Set<WorldEvent>> eventsByElement = new HashMap<>();

	static {
		for (Element e : Element.getAllElements()) {
			eventsByElement.put(e, new HashSet<>());
		}
	}

	private String name;
	private String description;
	private List<String> aliases;
	private List<String> attributes;
	private Element element;
	private Time time;
	private int frequency;
	private double modifier;
	private String startMessage;
	private String endMessage;
	private boolean darkenSky;
	private boolean createFog;

	public WorldEvent(WorldEventFile wFile) {
		this(wFile.getName(), wFile.getDescription(), wFile.getAliases(), wFile.getAttributes(), Element.getElement(wFile.getElement()), Time.valueOf(wFile.getTime().toUpperCase()), wFile.getFrequency(), wFile.getModifier(), wFile.getStartMessage(), wFile.getEndMessage(), wFile.getDarkenSky(), wFile.getCreateFog());
	}

	public WorldEvent(String name, String description, List<String> aliases, List<String> attributes, Element element, Time time, int frequency, double modifier, String startMessage, String endMessage, boolean darkenSky, boolean createFog) {
		this.name = name;
		this.description = description;
		this.aliases = aliases;
		this.attributes = attributes;
		this.element = element;
		this.time = time;
		this.frequency = frequency;
		this.modifier = modifier;
		this.startMessage = startMessage;
		this.endMessage = endMessage;
		this.darkenSky = darkenSky;
		this.createFog = createFog;
		events.put(name.toLowerCase(), this);
		if (!eventsByElement.containsKey(element)) {
			eventsByElement.put(element, new HashSet<>());
		}
		eventsByElement.get(element).add(this);
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
	public Element getElement() {
		return element;
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

	public static WorldEvent get(String name) {
		return events.containsKey(name.toLowerCase()) ? events.get(name.toLowerCase()) : (ProjectKorraRPG.getFileManager().loadFile(name.toLowerCase()) == null ? null : get(name));
	}

	public static Set<WorldEvent> getEventsByElement(Element e) {
		if (!eventsByElement.containsKey(e)) {
			eventsByElement.put(e, new HashSet<>());
		}
		return eventsByElement.get(e);
	}

	public static Set<String> getEventNames() {
		return events.keySet();
	}

	public static Collection<WorldEvent> getEvents() {
		return events.values();
	}
}
