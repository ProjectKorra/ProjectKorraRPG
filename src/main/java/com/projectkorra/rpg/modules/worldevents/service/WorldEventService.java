package com.projectkorra.rpg.modules.worldevents.service;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.models.ActiveWorldEvent;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.storage.ActiveWorldEventIndex;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WorldEventService {
    private final ProjectKorraRPG plugin;
    private final ActiveWorldEventIndex activeEventsIndex;

    // For non ticking events (No BossBar usage)
    private final Map<WorldEvent, BukkitTask> nonTickingStops = new HashMap<>();

    // Global ticker so the BossBar for each ActiveWorldEvent receives a tick update
    private BukkitTask ticker;
    private long tickNo;

    public WorldEventService(ProjectKorraRPG plugin, ActiveWorldEventIndex activeEventsIndex) {
        this.plugin = plugin;
        this.activeEventsIndex = activeEventsIndex;
    }

    public boolean start(WorldEvent worldEvent, World runtimeWorld) {
        if (worldEvent == null || runtimeWorld == null) return false;

        if (activeEventsIndex.contains(worldEvent)) {
            plugin.getLogger().warning("WorldEvent already running: " + worldEvent.getKey());
            return false;
        }
        if (worldEvent.isWorldDisabled(runtimeWorld)) {
            plugin.getLogger().info("Cannot start WorldEvent " + worldEvent.getKey() + " world is null or disabled");
            return false;
        }

        ActiveWorldEvent active = new ActiveWorldEvent(worldEvent, runtimeWorld);

        // Publish to index
        activeEventsIndex.add(worldEvent, active);

        // Start displays / Set start time for ticker
        active.start();

        // Recalc ability attributes
        recalcAllAbilities();

        if (active.requiresTicking()) {
            ensureTicker(); // TaskTimer because of BossBar
        } else {
            scheduleNonTickingStop(worldEvent); // Scheduler to prevent useless ticking without given BossBar
        }

        Bukkit.getPluginManager().callEvent(new WorldEventStartEvent(worldEvent));
        return true;
    }

    /**
     * Temp method, will lead to issues with coming Scheduler
     */
    public boolean start(WorldEvent worldEvent) {
        World runtimeWorld = null;

        if (worldEvent != null && !worldEvent.getScheduledWorlds().isEmpty()) {
            runtimeWorld = worldEvent.getScheduledWorlds().getFirst();
        }
        if (runtimeWorld == null && !plugin.getServer().getWorlds().isEmpty()) {
            runtimeWorld = plugin.getServer().getWorlds().getFirst();
        }

        return start(worldEvent, runtimeWorld);
    }

    public boolean stop(WorldEvent worldEvent) {
        BukkitTask delayed = nonTickingStops.remove(worldEvent);
        if (delayed != null) delayed.cancel();

        ActiveWorldEvent active = activeEventsIndex.remove(worldEvent);
        if (active == null) {
            plugin.getLogger().warning("Tried to stop non running WorldEvent: " + worldEvent.getKey());
            return false;
        }

        // Stop displays / clear viewers
        active.stop();

        // Stop ticker
        tryStopTicker();

        // Recalc ability attributes
        recalcAllAbilities();

        Bukkit.getPluginManager().callEvent(new WorldEventStopEvent(worldEvent));
        return true;
    }

    public void stopAll() {
        for (BukkitTask task : nonTickingStops.values()) {
            task.cancel();
        }
        nonTickingStops.clear();

        for (WorldEvent worldEvent : new ArrayList<>(activeEventsIndex.activeWorldEvents())) {
            stop(worldEvent);
        }
    }

    public void shutdown() {
        stopAll();
        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }
    }

    private void recalcAllAbilities() {
        for (CoreAbility ability : CoreAbility.getAbilitiesByInstances()) {
            ability.recalculateAttributes();
        }
    }

    public void addViewer(WorldEvent worldEvent, Player viewer) {
        if (worldEvent == null) return;
        ActiveWorldEvent active = activeEventsIndex.getActive(worldEvent);

        if (active != null) {
            active.addViewer(viewer);
        }
    }

    public void removeViewer(WorldEvent worldEvent, Player viewer) {
        if (worldEvent == null) return;
        ActiveWorldEvent active = activeEventsIndex.getActive(worldEvent);

        if (active != null) {
            active.removeViewer(viewer);
        }
    }

    public void sendWorldEventRunningMessage(WorldEvent worldEvent, Player player) {
        if (worldEvent == null) return;
        ActiveWorldEvent active = activeEventsIndex.getActive(worldEvent);

        if (active != null) {
            active.sendWorldEventRunningMessage(player);
        }
    }

    private void scheduleNonTickingStop(WorldEvent worldEvent) {
        long delayTicks = Math.max(1L, worldEvent.getDuration() / 50L); // Convert milliseconds to ticks
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                stop(worldEvent);
            }
        }.runTaskLater(plugin, delayTicks);

        nonTickingStops.put(worldEvent, task);
    }

    private void ensureTicker() {
        if (ticker != null || !activeEventsIndex.hasTicking()) return;

        tickNo = 0;
        ticker = new BukkitRunnable() {
            @Override
            public void run() {
                tickNo++;
                long now = System.currentTimeMillis();

                // Use snapshot to iterate to defend against mutation in some cases
                ActiveWorldEvent[] snapshot = activeEventsIndex.snapshot();
                if (snapshot.length == 0) {
                    tryStopTicker();
                    return;
                }

                List<WorldEvent> expired = null;
                for (ActiveWorldEvent active : snapshot) {
                    if (active.tick(tickNo, now)) {
                        if (expired == null) expired = new ArrayList<>();
                        expired.add(active.getWorldEvent());
                    }
                }
                if (expired != null) {
                    for (WorldEvent worldEvent : expired) {
                        stop(worldEvent);
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private void tryStopTicker() {
        if (activeEventsIndex.isEmpty() && ticker != null) {
            ticker.cancel();
            ticker = null;
        }
    }

    public ActiveWorldEventIndex getActiveEventsIndex() {
        return activeEventsIndex;
    }
}
