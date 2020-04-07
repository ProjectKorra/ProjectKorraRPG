package com.projectkorra.rpg.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.projectkorra.rpg.player.RPGPlayer;

public class RPGPlayerLevelUpEvent extends Event {
	
	public static final HandlerList HANDLERS = new HandlerList();

	private RPGPlayer player;
	private int level;
	
	public RPGPlayerLevelUpEvent(RPGPlayer player, int level) {
		this.player = player;
		this.level = level;
	}
	
	public RPGPlayer getPlayer() {
		return player;
	}
	
	public int getNewLevel() {
		return level;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
