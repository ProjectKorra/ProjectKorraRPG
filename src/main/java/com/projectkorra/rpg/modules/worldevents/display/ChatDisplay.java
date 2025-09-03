package com.projectkorra.rpg.modules.worldevents.display;

import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Chat Display method for an {@link com.projectkorra.rpg.modules.worldevents.models.WorldEvent} <br>
 * Three types of Messages included: <br> <br>
 * - EventStart - Event start message <br>
 * - EventStop - Event stop message <br>
 * - EventRunning - Event currently active message <br>
 * <br>
 * EventRunning is for players joining / re-joining the server <br>
 * so they will know that an event is currently running (given {@link BossBarDisplay} not active)
 */
public class ChatDisplay implements IChatDisplay {
	private final String startMessage;
	private final String stopMessage;
    private final String currentlyActiveMessage;

	public ChatDisplay(String startMessage, String stopMessage, String currentlyActiveMessage) {
		this.startMessage = startMessage;
		this.stopMessage = stopMessage;
        this.currentlyActiveMessage = currentlyActiveMessage;
	}

    @Override
    public void sendStartMessage(Player player) {
        if (startMessage == null || startMessage.isBlank() || player == null || !player.isOnline()) return;
        ChatUtil.sendBrandingMessage(player, startMessage);
    }

    @Override
    public void sendStopMessage(Player player) {
        if (stopMessage == null || stopMessage.isBlank() || player == null || !player.isOnline()) return;
        ChatUtil.sendBrandingMessage(player, stopMessage);
    }

    @Override
    public void sendEventCurrentlyRunning(Player player) {
        if (currentlyActiveMessage == null || currentlyActiveMessage.isBlank() || player == null || !player.isOnline()) return;
        ChatUtil.sendBrandingMessage(player, currentlyActiveMessage);
    }

    @Override
    public void sendStartMessage(Collection<Player> players) {
        if (players == null || players.isEmpty()) return;
        players.forEach(this::sendStartMessage);
    }

    @Override
    public void sendStopMessage(Collection<Player> players) {
        if (players == null || players.isEmpty()) return;
        players.forEach(this::sendStopMessage);
    }

    @Override
    public void sendEventCurrentlyRunning(Collection<Player> players) {
        if (players == null || players.isEmpty()) return;
        players.forEach(this::sendEventCurrentlyRunning);
    }
}
