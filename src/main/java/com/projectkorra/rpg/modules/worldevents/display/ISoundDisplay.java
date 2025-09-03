package com.projectkorra.rpg.modules.worldevents.display;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface ISoundDisplay {
    void playStartSound(Player player);
    void playStopSound(Player player);

    void playStartSound(Collection<Player> players);
    void playStopSound(Collection<Player> players);
}
