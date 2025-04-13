package com.projectkorra.rpg.modules.events.util;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.events.WorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldEventScheduler {

	public static void startWorldEventSchedule(WorldEvent worldEvent) {
		final long duration = worldEvent.getDuration();
		final long startTime = System.currentTimeMillis();

		new BukkitRunnable() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				double elapsed = now - startTime;
				double progress = 1.0 - (elapsed / (double) duration);

				if (progress <= 0.0) {
					worldEvent.getWorldEventBossBar().getBossBar().setProgress(0.0);
					worldEvent.stopEvent();
					this.cancel();
					return;
				}
				worldEvent.getWorldEventBossBar().getBossBar().setProgress(progress);
			}
		}.runTaskTimer(ProjectKorraRPG.getPlugin(), 0, 20);
	}

	public static void stopWorldEventSchedule(WorldEvent worldEvent) {}
}
