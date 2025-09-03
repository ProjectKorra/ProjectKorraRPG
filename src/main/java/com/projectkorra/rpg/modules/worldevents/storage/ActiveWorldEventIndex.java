package com.projectkorra.rpg.modules.worldevents.storage;

import com.projectkorra.rpg.modules.worldevents.models.ActiveWorldEvent;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.World;

import java.util.*;

public final class ActiveWorldEventIndex {
    private final Map<WorldEvent, ActiveWorldEvent> activeWorldEventMap = new HashMap<>();
    private final Map<UUID, Set<WorldEvent>> activeWorldEventsByWorld = new HashMap<>();

    // Volatile to make ticker [WorldEventService] always see latest published array without locking
    private volatile ActiveWorldEvent[] snapshot = new ActiveWorldEvent[0];

    public boolean contains(WorldEvent worldEvent) {
        return activeWorldEventMap.containsKey(worldEvent);
    }

    public void add(WorldEvent worldEvent, ActiveWorldEvent active) {
        activeWorldEventMap.put(worldEvent, active);
        Set<WorldEvent> set = activeWorldEventsByWorld.computeIfAbsent(active.getRuntimeWorld().getUID(), k -> new HashSet<>());
        set.add(worldEvent);

        rebuildSnapshot();
    }

    public ActiveWorldEvent remove(WorldEvent worldEvent) {
        ActiveWorldEvent removed = activeWorldEventMap.remove(worldEvent);
        if (removed != null) {
            UUID worldId = removed.getRuntimeWorld().getUID();
            Set<WorldEvent> set = activeWorldEventsByWorld.get(worldId);
            if (set != null) {
                set.remove(worldEvent);
                if (set.isEmpty()) activeWorldEventsByWorld.remove(worldId);
            }
            rebuildSnapshot();
        }
        return removed;
    }

    public boolean hasTicking() {
        for (ActiveWorldEvent active : activeWorldEventMap.values()) {
            if (active.requiresTicking()) return true;
        }
        return false;
    }

    public ActiveWorldEvent[] tickingSnapshot() {
        List<ActiveWorldEvent> list = new ArrayList<>(activeWorldEventMap.size());
        for (ActiveWorldEvent active : activeWorldEventMap.values()) {
            if (active.requiresTicking()) {
                list.add(active);
            }
        }
        return list.toArray(ActiveWorldEvent[]::new);
    }

    public void clearAll() {
        activeWorldEventMap.clear();
        activeWorldEventsByWorld.clear();
        snapshot = new ActiveWorldEvent[0];
    }

    public boolean isEmpty() {
        return activeWorldEventMap.isEmpty();
    }

    public Set<WorldEvent> activeWorldEvents() {
        return Collections.unmodifiableSet(activeWorldEventMap.keySet());
    }

    public Set<WorldEvent> getActiveIn(World world) {
        if (world == null) return Collections.emptySet();
        Set<WorldEvent> set = activeWorldEventsByWorld.get(world.getUID());
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    public boolean hasActiveIn(World world) {
        Set<WorldEvent> worldEventsInWorld = activeWorldEventsByWorld.get(world.getUID());
        return worldEventsInWorld != null && !worldEventsInWorld.isEmpty();
    }

    public ActiveWorldEvent getActive(WorldEvent spec) {
        return activeWorldEventMap.get(spec);
    }

    public ActiveWorldEvent[] snapshot() {
        return snapshot;
    }

    private void rebuildSnapshot() {
        Collection<ActiveWorldEvent> values = activeWorldEventMap.values();
        ActiveWorldEvent[] array = new ActiveWorldEvent[values.size()];
        snapshot = values.toArray(array);
    }
}
