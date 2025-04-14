package com.projectkorra.rpg.modules.worldevents.util.display.chat;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.display.IWorldEventDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatDisplay implements IWorldEventDisplay {

	@Override
	public void startDisplay(WorldEvent event) {
		Set<String> blacklistedNames = getBlacklistedWorldNames(event);

		for (Player player : Bukkit.getOnlinePlayers()) {
			String playerWorld = player.getWorld().getName();
			if (!blacklistedNames.contains(playerWorld)) {
				ChatUtil.sendBrandingMessage(player, event.getEventStartMessage());
			}
		}
	}

	@Override
	public void updateDisplay(WorldEvent event, double progress) {}

	@Override
	public void stopDisplay(WorldEvent event) {
		Set<String> blacklistedNames = getBlacklistedWorldNames(event);

		for (Player player : Bukkit.getOnlinePlayers()) {
			String playerWorld = player.getWorld().getName();
			if (!blacklistedNames.contains(playerWorld)) {
				ChatUtil.sendBrandingMessage(player, event.getEventStopMessage());
			}
		}
	}

	/**
	 * Helper method to extract a set of valid blacklisted world names from the event.
	 */
	private Set<String> getBlacklistedWorldNames(WorldEvent event) {
		Set<String> names = new HashSet<>();
		List<org.bukkit.World> blWorlds = event.getBlacklistedWorlds();
		if (blWorlds != null) {
			for (org.bukkit.World world : blWorlds) {
				if (world != null ) {
					names.add(world.getName());
				}
			}
		}
		return names;
	}
}
