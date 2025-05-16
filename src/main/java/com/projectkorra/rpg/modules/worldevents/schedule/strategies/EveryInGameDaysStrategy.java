package com.projectkorra.rpg.modules.worldevents.schedule.strategies;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduleStrategy;
import com.projectkorra.rpg.modules.worldevents.schedule.storage.ScheduleStorage;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

public class EveryInGameDaysStrategy implements WorldEventScheduleStrategy {
	private final LocalTime timeOfDay;
	private final Duration repeatDuration;
	private final Duration offsetDuration;
	private final double chance;
	private final Duration cooldownDuration;
	private final ScheduleStorage storage;

	private BukkitTask task;
	private Instant lastTriggerTime = null;

	public EveryInGameDaysStrategy(LocalTime timeOfDay, Duration repeatDuration, Duration offsetDuration, double chance, Duration cooldownDuration, ScheduleStorage storage) {
		this.timeOfDay = timeOfDay;
		this.repeatDuration = repeatDuration;
		this.offsetDuration = offsetDuration;
		this.chance = chance;
		this.cooldownDuration = cooldownDuration;
		this.storage = storage;
	}

	@Override
	public void scheduleNext(WorldEvent event, Plugin plugin) {
		//TODO: Implement scheduling
	}

	@Override
	public void cancelSchedule() {
		if (task != null && !task.isCancelled()) {
			task.cancel();
			task = null;
		}
	}

	public LocalTime getTimeOfDay() {
		return timeOfDay;
	}

	public Duration getRepeatDuration() {
		return repeatDuration;
	}

	public Duration getOffsetDuration() {
		return offsetDuration;
	}

	public double getChance() {
		return chance;
	}

	public Duration getCooldownDuration() {
		return cooldownDuration;
	}

	public ScheduleStorage getStorage() {
		return storage;
	}

	public BukkitTask getTask() {
		return task;
	}

	public Instant getLastTriggerTime() {
		return lastTriggerTime;
	}
}
