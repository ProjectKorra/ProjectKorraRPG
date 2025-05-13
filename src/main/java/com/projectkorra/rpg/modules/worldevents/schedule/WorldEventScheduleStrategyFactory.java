package com.projectkorra.rpg.modules.worldevents.schedule;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.schedule.strategies.EveryInGameDaysStrategy;
import com.projectkorra.rpg.modules.worldevents.schedule.strategies.EveryRealWorldDaysStrategy;
import com.projectkorra.rpg.modules.worldevents.schedule.strategies.util.ScheduleType;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.Duration;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldEventScheduleStrategyFactory {
	public static WorldEventScheduleStrategy get(FileConfiguration config) {
		String rawType = config.getString("Schedule.Calendar", "INGAME");
		ScheduleType scheduleType = ScheduleType.fromString(rawType);

		LocalTime timeOfDay 	= parseTimeOfDay(config.getString("Schedule.At", "7am"));
		Duration repeatDuration = parseDuration(config.getString("Schedule.Repeat", "7d"));
		Duration offsetDuration = parseDuration(config.getString("Schedule.OffsetDays", "0d"));
		Duration cooldown 		= parseDuration(config.getString("Schedule.Cooldown", "1d"));
		double chance 			= config.getDouble("Schedule.TriggerChance", 0.5);

		return switch (scheduleType) {
			case REAL_DAYS -> new EveryRealWorldDaysStrategy(
					timeOfDay,
					repeatDuration,
					offsetDuration,
					chance,
					cooldown
			);
			case IN_GAME_DAYS -> new EveryInGameDaysStrategy(
				timeOfDay,
				repeatDuration,
				offsetDuration,
				chance,
				cooldown
			);
		};
	}

	/**
	 * Parses a human-readable time of day string like "7am", "3:30pm" into a LocalTime
	 */
	private static LocalTime parseTimeOfDay(String timeStr) {
		timeStr = timeStr.toLowerCase().trim();

		// Match patterns like "7am", "7:30am", "15:45", etc.
		Matcher matcher = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?(am|pm)?").matcher(timeStr);

		if (matcher.matches()) {
			int hour = Integer.parseInt(matcher.group(1));
			int minute = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
			String period = matcher.group(3);

			// Handle 12-hour clock format
			if (period != null) {
				if (period.equals("pm") && hour < 12) {
					hour += 12;
				} else if (period.equals("am") && hour == 12) {
					hour = 0;
				}
			}

			return LocalTime.of(hour, minute);
		}

		// Default to 7am if unparseable
		ProjectKorraRPG.getPlugin().getLogger().warning("Could not parse time of day: " + timeStr + ", defaulting to 7am");
		return LocalTime.of(7, 0);
	}

	/**
	 * Parses a duration string like "7d", "3d12h", "30m" into a Duration
	 */
	private static Duration parseDuration(String durationStr) {
		durationStr = durationStr.toLowerCase().trim();

		long totalSeconds = 0;

		// Match patterns like "7d", "12h", "30m", "45s", or combinations like "3d12h30m"
		Matcher matcher = Pattern.compile("(\\d+)([dhms])").matcher(durationStr);

		while (matcher.find()) {
			int value = Integer.parseInt(matcher.group(1));
			String unit = matcher.group(2);

			switch (unit) {
				case "d" -> totalSeconds += value * 86400L; // days to seconds
				case "h" -> totalSeconds += value * 3600L;  // hours to seconds
				case "m" -> totalSeconds += value * 60L;    // minutes to seconds
				case "s" -> totalSeconds += value;          // seconds
			}
		}

		// Default to 1 day if unparseable
		if (totalSeconds == 0) {
			ProjectKorraRPG.getPlugin().getLogger().warning("Could not parse duration: " + durationStr + ", defaulting to 1d");
			totalSeconds = 86400L;
		}

		return Duration.ofSeconds(totalSeconds);
	}
}
