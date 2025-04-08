package com.projectkorra.rpg.modules.events;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;

import java.util.List;

public class WorldEvent {
	private final String name;
	private final long duration;
	private final ChatColor color;

	private String eventStartMessage;
	List<World> blacklistedWorlds;

	public WorldEvent(String name, long duration, ChatColor color) {
		this.name = name;
		this.duration = duration;
		this.color = color;
	}

	public String getName() {
		return this.name;
	}

	public long getDuration() {
		return this.duration;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public String getEventStartMessage() {
		return this.eventStartMessage;
	}

	public void setEventStartMessage(String eventStartMessage) {
		this.eventStartMessage = eventStartMessage;
	}

	public List<World> getBlacklistedWorlds() {
		return blacklistedWorlds;
	}

	public void setBlacklistedWorlds(List<World> blacklistedWorlds) {
		this.blacklistedWorlds = blacklistedWorlds;
	}
}
