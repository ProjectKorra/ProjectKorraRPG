package com.projectkorra.rpg.modules.worldevents.models;

import com.projectkorra.rpg.modules.worldevents.display.IBossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.display.IChatDisplay;
import com.projectkorra.rpg.modules.worldevents.display.ISoundDisplay;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public final class WorldEvent implements Keyed {
    private final NamespacedKey key;
    private final String title;
    private final long duration;

    private final List<World> scheduledWorlds;
    private final List<World> disabledWorlds;

    private final @Nullable IChatDisplay chatDisplay;
    private final @Nullable IBossBarDisplay bossBarDisplay;
    private final @Nullable ISoundDisplay soundDisplay;

    private final @Nullable ScheduleSpecifications scheduleSpecifications;
    private final AttributeRules attributeRules;

    public WorldEvent(
            NamespacedKey key,
            String title,
            long duration,
            List<World> scheduledWorlds,
            List<World> disabledWorlds,
            @Nullable IChatDisplay chatDisplay,
            @Nullable IBossBarDisplay bossBarDisplay,
            @Nullable ISoundDisplay soundDisplay,
            @Nullable ScheduleSpecifications scheduleSpecifications,
            AttributeRules attributeRules)
    {
        this.key = key;
        this.title = title;
        this.duration = duration;
        this.scheduledWorlds = scheduledWorlds == null ? List.of() : java.util.List.copyOf(scheduledWorlds);
        this.disabledWorlds = disabledWorlds == null ? List.of() : java.util.List.copyOf(disabledWorlds);
        this.chatDisplay = chatDisplay;
        this.bossBarDisplay = bossBarDisplay;
        this.soundDisplay = soundDisplay;
        this.scheduleSpecifications = scheduleSpecifications;
        this.attributeRules = attributeRules;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public List<World> getScheduledWorlds() {
        return scheduledWorlds;
    }

    public List<World> getDisabledWorlds() {
        return disabledWorlds;
    }

    public boolean isWorldDisabled(World world) {
        return world != null && disabledWorlds.contains(world);
    }

    public @Nullable IChatDisplay getChatDisplay() {
        return chatDisplay;
    }

    public @Nullable IBossBarDisplay getBossBarDisplay() {
        return bossBarDisplay;
    }

    public @Nullable ISoundDisplay getSoundDisplay() {
        return soundDisplay;
    }

    public @Nullable ScheduleSpecifications getScheduleSpecifications() {
        return scheduleSpecifications;
    }

    public AttributeRules getAttributeRules() {
        return attributeRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldEvent other)) return false;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        String scheduled = scheduledWorlds.isEmpty()
                ? "[]"
                : scheduledWorlds.stream()
                .map(World::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        String disabled = disabledWorlds.isEmpty()
                ? "[]"
                : disabledWorlds.stream()
                .map(World::getName)
                .collect(Collectors.joining(", ", "[", "]"));

        return "WorldEvent{hashcode=" + hashCode() +
                ", key=" + key +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", scheduledWorlds=" + scheduled +
                ", disabledWorlds=" + disabled +
                "}";
    }
}
