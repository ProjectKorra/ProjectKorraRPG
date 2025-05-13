package com.projectkorra.rpg.modules.worldevents.util.display.chat;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.display.IWorldEventDisplay;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatDisplay implements IWorldEventDisplay {
	private final String startMessage;
	private final String stopMessage;

	public ChatDisplay(String startMessage, String stopMessage) {
		this.startMessage = startMessage;
		this.stopMessage = stopMessage;
	}

	@Override
	public void startDisplay(WorldEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (WorldEvent.getAffectedPlayers().contains(player)) {
				ChatUtil.sendBrandingMessage(player, startMessage);
			}
		}
	}

	@Override
	public void updateDisplay(WorldEvent event, double progress) {}

	@Override
	public void stopDisplay(WorldEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (WorldEvent.getAffectedPlayers().contains(player)) {
				ChatUtil.sendBrandingMessage(player, stopMessage);
			}
		}
	}
}
