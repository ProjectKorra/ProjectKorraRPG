package com.projectkorra.rpg.modules.worldevents.display;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface IBossBarDisplay {
    void start(WorldEvent event);
    void stop(WorldEvent event);

    long tickPeriod();
    void updateTick(WorldEvent event, double progress);

    void addViewer(Player viewer);
    void removeViewer(Player viewer);

    void addViewers(Collection<Player> viewers);
    void removeViewers(Collection<Player> viewers);
}
