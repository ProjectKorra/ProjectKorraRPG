package com.projectkorra.rpg.modules.worldevents.display;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface IChatDisplay {
    void sendStartMessage(Player player);
    void sendStopMessage(Player player);
    void sendEventCurrentlyRunning(Player player);

    void sendStartMessage(Collection<Player> players);
    void sendStopMessage(Collection<Player> players);
    void sendEventCurrentlyRunning(Collection<Player> players);
}
