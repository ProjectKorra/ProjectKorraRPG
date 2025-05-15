package com.projectkorra.rpg.modules.worldevents.schedule.strategies.util;

public enum ScheduleType {
	REAL_DAYS,
	IN_GAME_DAYS;

	public static ScheduleType fromString(String name) {
		if (name == null) {
			return null;
		}

		String lowercaseName = name.toLowerCase();

		// Regex for REAL_DAYS to cover all possible variations
		if (lowercaseName.matches("^real[-_\\s]?(?:world[-_\\s]?)?(?:days?|time|hours?|minutes?)$")) {
			return REAL_DAYS;
		}

		// Regex for IN_GAME_DAYS to cover all possible variations
		if (lowercaseName.matches("^(?:in[-_\\s]?)?(?:game|mc|minecraft)[-_\\s]?(?:days?|time|hours?|minutes?)$")) {
			return IN_GAME_DAYS;
		}

		return null;
	}
}
