package com.projectkorra.rpg.modules.worldevents.util.schedule.strategies;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.schedule.WorldEventScheduleStrategy;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RandomChanceStrategy implements WorldEventScheduleStrategy {
	private static final long CHECK_INTERVAL_TICKS = 20L * 60L; // 60 Seconds

	private final double chancePerCheck;
	private final long cooldownMillis;

	private long lastTriggerTime = 0L;

	/**
	 * @param chancePerCheck 0.05 means a 5% chance each check
	 * @param cooldownSeconds seconds to wait after a successful trigger
	 */
	public RandomChanceStrategy(double chancePerCheck, long cooldownSeconds) {
		this.chancePerCheck = chancePerCheck;
		this.cooldownMillis = cooldownSeconds * 1000L;
	}

	@Override
	public void scheduleNext(WorldEvent event, Plugin plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();

				// still on cooldown?
				if (now - lastTriggerTime < cooldownMillis) {
					return;
				}

				// roll the dice
				if (Math.random() <= chancePerCheck) {
					event.startEvent();
					lastTriggerTime = now;
					cancel(); // stop repeat
				}
			}
		}.runTaskTimer(plugin, 0L, CHECK_INTERVAL_TICKS);
	}
}
