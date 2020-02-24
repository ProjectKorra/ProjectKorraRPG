package com.projectkorra.rpg.player;

import java.util.HashMap;
import java.util.Map;

public class BendingStats {
	
	private static final int MAX_POINTS = 10;

	public static enum BendingStat {
		DAMAGE, RANGE
	}
	
	private Map<BendingStat, Integer> points;
	
	public BendingStats() {
		this.points = new HashMap<>();
		for (BendingStat stat : BendingStat.values()) {
			points.put(stat, 0);
		}
	}
	
	public BendingStats(Map<BendingStat, Integer> points) {
		this.points = points;
	}
	
	public int getPoints(BendingStat stat) {
		return points.containsKey(stat) ? points.get(stat) : 0;
	}
	
	public boolean increase(BendingStat stat) {
		if (canIncrease(stat)) {
			points.put(stat, points.get(stat) + 1);
			return true;
		}
		
		return false;
	}
	
	public boolean canIncrease(BendingStat stat) {
		return points.containsKey(stat) ? points.get(stat) < MAX_POINTS : false;
	}
}
