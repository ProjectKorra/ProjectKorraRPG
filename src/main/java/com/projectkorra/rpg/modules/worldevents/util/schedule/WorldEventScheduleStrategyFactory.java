package com.projectkorra.rpg.modules.worldevents.util.schedule;

import com.projectkorra.rpg.modules.worldevents.util.schedule.WorldEventScheduleStrategy;
import com.projectkorra.rpg.modules.worldevents.util.schedule.strategies.AtTimeStrategy;
import com.projectkorra.rpg.modules.worldevents.util.schedule.strategies.EveryInGameDaysStrategy;
import com.projectkorra.rpg.modules.worldevents.util.schedule.strategies.EveryRealWorldDaysStrategy;
import com.projectkorra.rpg.modules.worldevents.util.schedule.strategies.RandomChanceStrategy;
import org.bukkit.configuration.file.FileConfiguration;

public class WorldEventScheduleStrategyFactory {
	public static WorldEventScheduleStrategy get(FileConfiguration config) {
		boolean atTime 		 = config.getBoolean("Schedule.AtTime.Enabled");
		boolean realDays	 = config.getBoolean("Schedule.RealDays.Enabled");
		boolean gameDays 	 = config.getBoolean("Schedule.GameDays.Enabled");
		boolean randomChance = config.getBoolean("Schedule.RandomChance.Enabled");

		if (atTime) {
			return new AtTimeStrategy(
					config.getString("Schedule.AtTime.TimeOfDay"),
					config.getDouble("Schedule.AtTime.ChancePerCheck")
			);
		}

		if (realDays) {
			return new EveryRealWorldDaysStrategy(
					config.getInt("Schedule.RealDays.IntervalDays"),
					config.getDouble("Schedule.RealDays.ChancePerCheck")
			);
		}

		if (gameDays) {
			return new EveryInGameDaysStrategy(
					config.getInt("Schedule.GameDays.IntervalDays"),
					config.getDouble("Schedule.GameDays.ChancePerCheck")
			);
		}

		if (randomChance) {
			return new RandomChanceStrategy(
					config.getDouble("Schedule.RandomChance.ChancePerCheck"),
					config.getLong("Schedule.RandomChance.CooldownSeconds")
			);
		}

		return null;
	}
}
