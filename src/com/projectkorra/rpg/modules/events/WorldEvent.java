package com.projectkorra.rpg.modules.events;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.events.util.WorldEventBossBar;
import com.projectkorra.rpg.modules.events.util.WorldEventScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldEvent {
	private static HashMap<Integer, WorldEvent> ACTIVE_EVENTS = new HashMap<>();

	private String name;
	private long duration;
	private Color color;

	private String eventStartMessage;
	private String eventStopMessage;
	private List<World> blacklistedWorlds;

	private List<Attribute> affectedAttributes;
	private List<Element> affectedElements;

	private WorldEventBossBar worldEventBossBar;

	public WorldEvent(String name, long duration, Color color) {
		this.name = name;
		this.duration = duration;
		this.color = color;

		this.blacklistedWorlds = new ArrayList<>();
		this.affectedAttributes = new ArrayList<>();
		this.affectedElements = new ArrayList<>();

		this.worldEventBossBar = new WorldEventBossBar(ChatUtil.color(getName()), parseColor(getColor()));
	}

	public void startEvent() {
		WorldEventScheduler.startWorldEventSchedule(this);

		if (!getBlacklistedWorlds().isEmpty()) {
			for (Player allPlayers : Bukkit.getOnlinePlayers()) {
				for (World world : getBlacklistedWorlds()) {
					if (!allPlayers.getWorld().getName().equals(world.getName())) {
						ChatUtil.sendBrandingMessage(allPlayers, getEventStartMessage());
						getWorldEventBossBar().getBossBar().addPlayer(allPlayers);
					}
				}
			}
		} else {
			for (Player allPlayers : Bukkit.getOnlinePlayers()) {
				ChatUtil.sendBrandingMessage(allPlayers, getEventStartMessage());
				getWorldEventBossBar().getBossBar().addPlayer(allPlayers);
			}
		}
	}

	public void stopEvent() {
		WorldEventScheduler.stopWorldEventSchedule(this);

		if (!getBlacklistedWorlds().isEmpty()) {
			for (Player allPlayers : Bukkit.getOnlinePlayers()) {
				for (World world : getBlacklistedWorlds()) {
					if (!allPlayers.getWorld().getName().equals(world.getName())) {
						ChatUtil.sendBrandingMessage(allPlayers, getEventStopMessage());
						getWorldEventBossBar().getBossBar().removeAll();
					}
				}
			}
		} else {
			for (Player allPlayers : Bukkit.getOnlinePlayers()) {
				ChatUtil.sendBrandingMessage(allPlayers, getEventStopMessage());
				getWorldEventBossBar().getBossBar().removeAll();
			}
		}
	}

	private BarColor parseColor(Color color) {
		Map<BarColor, Color> targetColors = new HashMap<>();
		targetColors.put(BarColor.RED, Color.fromRGB(255, 0, 0));
		targetColors.put(BarColor.GREEN, Color.fromRGB(0, 255, 0));
		targetColors.put(BarColor.BLUE, Color.fromRGB(0, 0, 255));
		targetColors.put(BarColor.YELLOW, Color.fromRGB(255, 255, 0));
		targetColors.put(BarColor.PURPLE, Color.fromRGB(128, 0, 128));
		targetColors.put(BarColor.PINK, Color.fromRGB(255, 105, 180));
		targetColors.put(BarColor.WHITE, Color.fromRGB(255, 255, 255));

		double minDistance = Double.MAX_VALUE;
		BarColor closest = BarColor.WHITE; // default

		for (Map.Entry<BarColor, Color> entry : targetColors.entrySet()) {
			double distance = colorDistance(color, entry.getValue());
			if (distance < minDistance) {
				minDistance = distance;
				closest = entry.getKey();
			}
		}
		return closest;
	}

	// Helper method to calculate the Euclidean distance between two colors
	private double colorDistance(Color c1, Color c2) {
		int redDiff = c1.getRed() - c2.getRed();
		int greenDiff = c1.getGreen() - c2.getGreen();
		int blueDiff = c1.getBlue() - c2.getBlue();
		return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
	}

	public static HashMap<Integer, WorldEvent> getActiveEvents() {
		return ACTIVE_EVENTS;
	}

	public String getName() {
		return this.name;
	}

	public long getDuration() {
		return this.duration;
	}

	public Color getColor() {
		return this.color;
	}

	public String getEventStartMessage() {
		return this.eventStartMessage;
	}

	public String getEventStopMessage() {
		return eventStopMessage;
	}

	public List<World> getBlacklistedWorlds() {
		return blacklistedWorlds;
	}

	public List<Attribute> getAffectedAttributes() {
		return affectedAttributes;
	}

	public List<Element> getAffectedElements() {
		return affectedElements;
	}

	public WorldEventBossBar getWorldEventBossBar() {
		return worldEventBossBar;
	}

	public static void setActiveEvents(HashMap<Integer, WorldEvent> activeEvents) {
		ACTIVE_EVENTS = activeEvents;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setEventStartMessage(String eventStartMessage) {
		this.eventStartMessage = eventStartMessage;
	}

	public void setEventStopMessage(String eventStopMessage) {
		this.eventStopMessage = eventStopMessage;
	}

	public void setBlacklistedWorlds(List<World> blacklistedWorlds) {
		this.blacklistedWorlds = blacklistedWorlds;
	}

	public void setAffectedAttributes(List<Attribute> affectedAttributes) {
		this.affectedAttributes = affectedAttributes;
	}

	public void setAffectedElements(List<Element> affectedElements) {
		this.affectedElements = affectedElements;
	}

	public void setWorldEventBossBar(WorldEventBossBar worldEventBossBar) {
		this.worldEventBossBar = worldEventBossBar;
	}
}
