package com.projectkorra.rpg.worldevent.util;
/**
 * Used to track day and night, used in {@link WorldEvent} to track when the events happen
 * @author simp
 *
 */
public enum Time {
	/**
	 * Allows the event to happen at daytime, will cancel when night comes around
	 */
	DAY,
	
	/**
	 * Allows the event to happen at night-time, will cancel when morning comes
	 */
	NIGHT,
	
	/**
	 * Allows the event to happen all day, will start at day and end at the next day
	 */
	BOTH;
}
