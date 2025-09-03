package com.projectkorra.rpg.modules.worldevents.listener;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.service.WorldEventService;
import com.projectkorra.rpg.modules.worldevents.storage.ActiveWorldEventIndex;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.ArrayList;

/**
 * Handle players switching worlds to remove
 * player BossBar and generally from a {@link WorldEvent}
 */
public class HandleWorldEventDisplayListener implements Listener {
    private final WorldEventService service;
    private final ActiveWorldEventIndex index;

    public HandleWorldEventDisplayListener(WorldEventService service) {
        this.service = service;
        this.index = service.getActiveEventsIndex();
    }

    @EventHandler
    public void onWorldSwitch(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        for (WorldEvent worldEvent : new ArrayList<>(index.getActiveIn(event.getFrom()))) {
            service.removeViewer(worldEvent, player);
        }

        for (WorldEvent worldEvent : new ArrayList<>(index.getActiveIn(player.getWorld()))) {
            service.addViewer(worldEvent, player);
            service.sendWorldEventRunningMessage(worldEvent, player);
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        for (WorldEvent worldEvent : new ArrayList<>(index.getActiveIn(event.getPlayer().getWorld()))) {
            service.addViewer(worldEvent, event.getPlayer());
            service.sendWorldEventRunningMessage(worldEvent, event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        for (WorldEvent worldEvent : new ArrayList<>(index.getActiveIn(event.getPlayer().getWorld()))) {
            service.removeViewer(worldEvent, event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        for (WorldEvent worldEvent : new ArrayList<>(index.getActiveIn(event.getWorld()))) {
            service.stop(worldEvent);
        }
    }

    public WorldEventService getService() {
        return service;
    }
}
