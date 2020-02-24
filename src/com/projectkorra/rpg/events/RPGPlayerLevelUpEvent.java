package com.projectkorra.rpg.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.projectkorra.rpg.player.RPGPlayer;

public class RPGPlayerLevelUpEvent extends Event implements Cancellable {
	
	public static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled;
	private RPGPlayer player;
	private int level;
	
	public RPGPlayerLevelUpEvent(RPGPlayer player, int level) {
		this.player = player;
		this.level = level;
		this.cancelled = false;
	}
	
	public RPGPlayer getPlayer() {
		return player;
	}
	
	public int getNewLevel() {
		return level;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
