package com.projectkorra.rpg.util;

public class XPControl {

	private static final int MAX_LEVEL = 40;
	private static final int[] XP_LEVEL = loadXPPerLevel(); // the required xp at the level matching the index to level up
	
	/**
	 * Calculates the level from the given amount of <b>total</b> XP
	 * @param xp amount of xp
	 * @return level from xp
	 */
	public static int calculateLevel(long xp) {
		int level = 0;
		
		while (level < MAX_LEVEL && XP_LEVEL[level] <= xp) {
			level++;
		}
		
		return level;
	}
	
	public static int getMaxLevel() {
		return MAX_LEVEL;
	}
	
	/**
	 * Returns the required amount of <b>total</b> XP to level up at the given level
	 * @param level current level, must be in the range [0, 40)
	 * @return xp required to level up
	 */
	public static int getXPRequired(int level) throws IndexOutOfBoundsException {
		if (level < 0 || level >= MAX_LEVEL) {
			throw new IndexOutOfBoundsException(level + " is not an acceptable level!");
		}
		
		return XP_LEVEL[level];
	}
	
	private static int[] loadXPPerLevel() {
		int[] xp_level = new int[MAX_LEVEL];
		xp_level[0] = 0;
		
		for (int i = 1; i < MAX_LEVEL; i++) {
			xp_level[i] = ((int) Math.pow(Math.E, 0.125 * i)) * 100 + xp_level[i - 1];
		}
		
		return xp_level;
	}
}
