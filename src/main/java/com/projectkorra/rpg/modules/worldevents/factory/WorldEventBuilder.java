package com.projectkorra.rpg.modules.worldevents.factory;

import com.projectkorra.rpg.modules.worldevents.display.IBossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.display.IChatDisplay;
import com.projectkorra.rpg.modules.worldevents.display.ISoundDisplay;
import com.projectkorra.rpg.modules.worldevents.models.AttributeRules;
import com.projectkorra.rpg.modules.worldevents.models.ScheduleSpecifications;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class WorldEventBuilder {
    private NamespacedKey key;
    private String title;
    private Long duration;

    private final List<World> scheduledWorlds = new ArrayList<>();
    private final List<World> disabledWorlds = new ArrayList<>();

    private @Nullable IChatDisplay chatDisplay;
    private @Nullable IBossBarDisplay bossBarDisplay;
    private @Nullable ISoundDisplay soundDisplay;

    private @Nullable ScheduleSpecifications schedule;
    private AttributeRules attributeRules;

    private WorldEventBuilder() {}

    public static WorldEventBuilder create() {
        return new WorldEventBuilder();
    }

    public WorldEventBuilder key(final NamespacedKey key) {
        this.key = key;
        return this;
    }

    public WorldEventBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public WorldEventBuilder duration(final long durationMillis) {
        this.duration = durationMillis;
        return this;
    }

    @Deprecated
    public WorldEventBuilder world(final World world) {
        if (world != null) this.scheduledWorlds.add(world);
        return this;
    }

    public WorldEventBuilder scheduledWorlds(final Collection<World> worlds) {
        if (worlds != null) {
            for (World world : worlds) {
                if (world != null) this.scheduledWorlds.add(world);
            }
        }
        return this;
    }

    public WorldEventBuilder chatDisplay(final @Nullable IChatDisplay chatDisplay) {
        if (chatDisplay != null) {
            this.chatDisplay = chatDisplay;
        }
        return this;
    }

    public WorldEventBuilder bossBarDisplay(final @Nullable IBossBarDisplay bossBarDisplay) {
        if (bossBarDisplay != null) {
            this.bossBarDisplay = bossBarDisplay;
        }
        return this;
    }

    public WorldEventBuilder soundDisplay(final @Nullable ISoundDisplay soundDisplay) {
        if (soundDisplay != null) {
            this.soundDisplay = soundDisplay;
        }
        return this;
    }

    public WorldEventBuilder disabledWorlds(final Collection<World> worlds) {
        if (worlds != null) {
            for (World world : worlds) {
                if (world != null) this.disabledWorlds.add(world);
            }
        }
        return this;
    }

    public WorldEventBuilder schedule(final @Nullable ScheduleSpecifications schedule) {
        if (schedule != null) {
            this.schedule = schedule;
        }
        return this;
    }

    public WorldEventBuilder attributes(final AttributeRules attributeRules) {
        this.attributeRules = attributeRules;
        return this;
    }

    public Optional<WorldEvent> tryBuild(Consumer<String> onError) {
        List<String> errors = new ArrayList<>();

        if (key == null || key.getKey().isBlank()) errors.add("key is missing");
        if (title == null || title.isBlank()) errors.add("Title is missing / blank");
        if (duration == null || duration <= 0) errors.add("duration is missing / <= 0");

        // Failed
        if (!errors.isEmpty()) {
            if (onError != null) {
                for (String error : errors) {
                    onError.accept(error);
                }
            }
            return Optional.empty();
        }

        // Success
        return Optional.of(new WorldEvent(
                        key,
                        title,
                        duration,
                        List.copyOf(scheduledWorlds),
                        List.copyOf(disabledWorlds),
                        chatDisplay,
                        bossBarDisplay,
                        soundDisplay,
                        schedule,
                        attributeRules
        ));
    }
}
