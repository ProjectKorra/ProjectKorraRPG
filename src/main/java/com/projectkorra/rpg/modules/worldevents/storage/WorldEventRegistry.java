package com.projectkorra.rpg.modules.worldevents.storage;

import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.NamespacedKey;

import java.util.*;

public final class WorldEventRegistry {
    private final Map<NamespacedKey, WorldEvent> loadedWorldEvents = new HashMap<>();
    private final Map<String, NamespacedKey> byPath = new HashMap<>();

    public void register(WorldEvent worldEvent) {
        if (worldEvent == null) return;
        loadedWorldEvents.put(worldEvent.getKey(), worldEvent);
        byPath.put(worldEvent.getKey().getKey().toLowerCase(Locale.ROOT), worldEvent.getKey());
    }

    public void registerAll(Collection<WorldEvent> worldEvents) {
        if (worldEvents == null) return;
        for (WorldEvent worldEvent : worldEvents) {
            register(worldEvent);
        }
    }

    public void clear() {
        loadedWorldEvents.clear();
        byPath.clear();
    }

    public Optional<WorldEvent> findByKey(NamespacedKey key) {
        if (key == null) return Optional.empty();
        return Optional.ofNullable(loadedWorldEvents.get(key));
    }

    public Optional<WorldEvent> findByPath(String path) {
        if (path == null) return Optional.empty();
        NamespacedKey key = byPath.get(path.toLowerCase(Locale.ROOT));
        return key == null ? Optional.empty() : Optional.ofNullable(loadedWorldEvents.get(key));
    }

    public Map<NamespacedKey, WorldEvent> getAll() {
        return Collections.unmodifiableMap(loadedWorldEvents);
    }
}
