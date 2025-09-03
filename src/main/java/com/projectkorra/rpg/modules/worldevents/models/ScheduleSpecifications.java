package com.projectkorra.rpg.modules.worldevents.models;

import java.time.Duration;
import java.time.LocalTime;

public final class ScheduleSpecifications {
    public enum Calendar {
        REAL_DAYS,
        IN_GAME_DAYS
    }

    private final Calendar calendar;
    private final LocalTime localTime; // tim of day trigger
    private final Duration repeat; // how often to check / trigger
    private final Duration offset; // random offset windows
    private final Duration cooldown; // min time between triggers
    private final double triggerChance;

    public ScheduleSpecifications(Calendar calendar, LocalTime localTime, Duration repeat, Duration offset, Duration cooldown, double triggerChance) {
        this.calendar = calendar;
        this.localTime = localTime;
        this.repeat = repeat;
        this.offset = offset;
        this.cooldown = cooldown;
        this.triggerChance = triggerChance;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public Duration getRepeat() {
        return repeat;
    }

    public Duration getOffset() {
        return offset;
    }

    public Duration getCooldown() {
        return cooldown;
    }

    public double getTriggerChance() {
        return triggerChance;
    }
}
