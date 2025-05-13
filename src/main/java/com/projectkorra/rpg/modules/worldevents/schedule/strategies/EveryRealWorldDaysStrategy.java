package com.projectkorra.rpg.modules.worldevents.schedule.strategies;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduleStrategy;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EveryRealWorldDaysStrategy implements WorldEventScheduleStrategy {
	private final LocalTime timeOfDay;
	private final Duration repeatDuration;
	private final Duration offsetDuration;
	private final double chance;
	private final Duration cooldownDuration;

	private BukkitTask task;
	private Instant lastTriggerTime = null;

	public EveryRealWorldDaysStrategy(LocalTime timeOfDay, Duration repeatDuration, Duration offsetDuration, double chance, Duration cooldownDuration) {
		this.timeOfDay = timeOfDay;
		this.repeatDuration = repeatDuration;
		this.offsetDuration = offsetDuration;
		this.chance = chance;
		this.cooldownDuration = cooldownDuration;
	}

	//TODO: Still very early WIP with AI quirks (probably, haven't tested yet)
	@Override
	public void scheduleNext(WorldEvent event, Plugin plugin) {
		cancelSchedule();

		// Calculate the next scheduled time
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime targetDateTime = calculateNextOccurrence(now);

		// Convert to milliseconds until that time
		long delayMillis = calculateDelay(now, targetDateTime);

		ProjectKorraRPG.getPlugin().getLogger().info("Scheduling event " + event.getKey() + " for " + targetDateTime);

		// Schedule the task
		task = new BukkitRunnable() {
			@Override
			public void run() {
				// Check if we're still on cooldown
				if (lastTriggerTime != null && Duration.between(lastTriggerTime, Instant.now()).compareTo(cooldownDuration) < 0) {
					ProjectKorraRPG.getPlugin().getLogger().info("Event " + event.getKey() + " still on cooldown, skipping trigger");
					return;
				}

				// Roll for chance
				if (Math.random() <= chance) {
					ProjectKorraRPG.getPlugin().getLogger().info("Starting scheduled event " + event.getKey());
					event.startEvent();
					lastTriggerTime = Instant.now();
				} else {
					ProjectKorraRPG.getPlugin().getLogger().info("Event " + event.getKey() + " failed chance roll, waiting for next trigger");
				}

				// Schedule the next occurrence
				rescheduleNext(event, plugin);
			}
		}.runTaskLater(plugin, delayMillis / 50); // Convert to ticks (50ms per tick)
	}

	private void rescheduleNext(WorldEvent event, Plugin plugin) {
		task = null;
		scheduleNext(event, plugin);
	}

	private LocalDateTime calculateNextOccurrence(LocalDateTime from) {
		LocalDateTime candidate = LocalDateTime.of(from.toLocalDate(), timeOfDay);

		// If that time has already passed today, move tomorrow
		if (candidate.isBefore(from)) {
			candidate = candidate.plusDays(1);
		}

		// Apply offset
		candidate = candidate.plus(offsetDuration);

		// Apply a repeat interval
		if (!repeatDuration.isZero()) {
			// Find the next occurrence based on the repeat interval
			while (candidate.isBefore(from)) {
				candidate = candidate.plus(repeatDuration);
			}
		}

		return candidate;
	}

	private long calculateDelay(LocalDateTime from, LocalDateTime to) {
		return Duration.between(from, to).toMillis();
	}

	@Override
	public void cancelSchedule() {
		if (task != null && !task.isCancelled()) {
			task.cancel();
			task = null;
		}
	}
}
