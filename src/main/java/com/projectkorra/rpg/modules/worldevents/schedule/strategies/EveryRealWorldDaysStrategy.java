package com.projectkorra.rpg.modules.worldevents.schedule.strategies;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduleStrategy;
import com.projectkorra.rpg.modules.worldevents.schedule.storage.ScheduleStorage;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.time.*;
import java.util.Optional;
import java.util.Random;

public class EveryRealWorldDaysStrategy implements WorldEventScheduleStrategy {
    private final LocalTime targetTime;
    private final Duration repeatInterval;
    private final Duration maxOffset;
    private final double chance;
    private final Duration cooldown;
    
    private final Random random = new Random();
    private BukkitTask task;
    private final ScheduleStorage storage;
    
    /**
     * Creates a new real-time world event scheduling strategy.
     *
     * @param targetTime   The time of day when the event should trigger (e.g., 7:00 AM)
     * @param repeatInterval    How often to check for triggering the event (e.g., every 7 days)
     * @param maxOffset Maximum random time offset to apply (e.g., up to 3 days and 12 hours)
     * @param chance     Probability of the event triggering when conditions are met (0.0-1.0)
     * @param cooldown  Minimum time between event triggers (e.g., 60 days)
     */
    public EveryRealWorldDaysStrategy(LocalTime targetTime, Duration repeatInterval, Duration maxOffset, double chance, Duration cooldown, ScheduleStorage storage) {
        this.targetTime = targetTime;
        this.repeatInterval = repeatInterval;
        this.maxOffset = maxOffset;
        this.chance = chance;
        this.cooldown = cooldown;
        this.storage = storage;
    }
    
    @Override
    public void scheduleNext(WorldEvent event, Plugin plugin) {
        cancelSchedule();

        String eventKey = event.getKey();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextTime;

        try {
            // Get last trigger time
            Optional<Instant> lastTriggerTime = storage.getLastTriggeredTime(eventKey);

            // Calculate next trigger time
            nextTime = calculateNextTriggeerTime(now, lastTriggerTime);

            // Apply random offset
            if (!maxOffset.isZero() || !maxOffset.isNegative()) {
                long offsetMillis = (long) (random.nextDouble() * maxOffset.toMillis());
                nextTime = nextTime.plus(Duration.ofMillis(offsetMillis));
                plugin.getLogger().info("Applied random offset of " + formatDuration(Duration.ofMillis(offsetMillis)) + " to event " + eventKey);
            }

            // Ensure no re-schedule in past
            if (nextTime.isBefore(now)) {
                nextTime = now.plusSeconds(30);
                plugin.getLogger().info("Adjusted schedule time for " + eventKey + " to be in the future");
            }

            // Actually schedule the event
            long delayTicks = Math.max(100, Duration.between(now, nextTime).toMillis() / 50);
            plugin.getLogger().info("Scheduling " + eventKey + " for " + nextTime + " (in " + formatDuration(Duration.ofMillis(delayTicks * 50)) + ")");

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    tryTriggerEvent(event, plugin);
                }
            }.runTaskLater(plugin, delayTicks);

        } catch (Exception e) {
            plugin.getLogger().severe("Error scheduling " + eventKey + "\n" + e.getMessage());

            // Schedule for retry in 5 minutes
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    scheduleNext(event, plugin);
                }
            }.runTaskLater(plugin, 6000);
        }
    }

    private void tryTriggerEvent(WorldEvent event, Plugin plugin) {
        String eventKey = event.getKey();
        boolean onCooldown = false;

        try {
            // Check cooldown
            Optional<Instant> lastTriggerTime = storage.getLastTriggeredTime(eventKey);

            if (lastTriggerTime.isPresent()) {
                Duration sinceLastTrigger = Duration.between(lastTriggerTime.get(), Instant.now());
                if (sinceLastTrigger.compareTo(cooldown) < 0) {
                    onCooldown = true;
                    plugin.getLogger().info("Event " + eventKey + " is on cooldown for " + formatDuration(sinceLastTrigger));
                }
            }

            // Check chance and cooldown
            if (!onCooldown && random.nextDouble() <= chance) {
                // start event
                plugin.getLogger().info("Starting " + eventKey + " (passed " + (chance * 100) + "% chance check)");

                try {
                    storage.updateLastTriggeredTime(eventKey, Instant.now());
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to update last trigger time for event: " + eventKey + "\n" + e.getMessage());
                }

                event.startEvent();
            } else if (!onCooldown) {
                plugin.getLogger().info("Event " + eventKey + " failed " + (chance * 100) + "% chance check");
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Error checking/triggering " + eventKey + "\n" + e.getMessage());
        }

        // Always schedule the next occurrence
        scheduleNext(event, plugin);
    }

    private LocalDateTime calculateNextTriggeerTime(LocalDateTime now, Optional<Instant> lastTriggerTime) {
        // Start with today at target time
        LocalDateTime candidate = LocalDateTime.of(now.toLocalDate(), targetTime);

        // If that time has passed today, move to tomorrow
        if (candidate.isBefore(now)) {
            candidate = candidate.plusDays(1);
        }

        // If last trigger and cooldown, respect it
        if (lastTriggerTime.isPresent()) {
            LocalDateTime lastTriggerDateTime = LocalDateTime.ofInstant(lastTriggerTime.get(), ZoneId.systemDefault());
            LocalDateTime earliestAllowed = lastTriggerDateTime.plus(cooldown);

            while (candidate.isBefore(earliestAllowed)) {
                candidate = candidate.plus(repeatInterval);
            }
        }

        return candidate;
    }

    @Override
    public void cancelSchedule() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
    }

    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        duration = duration.minusDays(days);

        long hours = duration.toHours();
        duration = duration.minusHours(hours);

        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);

        long seconds = duration.getSeconds();

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || (days == 0 && hours == 0 && minutes == 0)) sb.append(seconds).append("s");

        return sb.toString().trim();
    }


    public LocalTime getTargetTime() {
        return targetTime;
    }

    public Duration getRepeatInterval() {
        return repeatInterval;
    }

    public Duration getMaxOffset() {
        return maxOffset;
    }

    public double getChance() {
        return chance;
    }

    public Duration getCooldown() {
        return cooldown;
    }

    public Random getRandom() {
        return random;
    }

    public BukkitTask getTask() {
        return task;
    }

    public ScheduleStorage getStorage() {
        return storage;
    }
}
