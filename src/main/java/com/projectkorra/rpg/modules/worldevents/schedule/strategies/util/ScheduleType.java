package com.projectkorra.rpg.modules.worldevents.schedule.strategies.util;

public enum ScheduleType {
	REAL_DAYS,
	IN_GAME_DAYS;

	public static ScheduleType fromString(String name) {
		if (name == null) {
			return null;
		}

		String lowercaseName = name.toLowerCase();

		// Regex for REAL_DAYS to prevent user input failures
		if (lowercaseName.matches("^real(-?days|-?time)$")) {
			return REAL_DAYS;
		}

		// Regex for IN_GAME_DAYS to prevent user input failures
		if (lowercaseName.matches("^in-?game(-?days|-?time)$")) {
			return IN_GAME_DAYS;
		}

		return null;
	}
}
