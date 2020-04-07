package com.projectkorra.rpg.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class ChakraStats {
	
	private static final int MAX_POINTS = 10;

	public static enum Chakra {
		AIR (0.01, ChatColor.GRAY, "increases bending range, 1% per point"),
		FIRE (0.03, ChatColor.RED, "increases bending damage, 3% per point"),
		EARTH (-0.03, ChatColor.GREEN, "decreases bending damage taken, 3% per point"),
		WATER (-0.02, ChatColor.AQUA, "gives cooldown reduction, 2% per point"),
		LIGHT (0.02, ChatColor.YELLOW, "enhances bending effects, 2% per point");
		
		private double percent;
		private ChatColor color;
		private String help;
		
		private Chakra(double percent, ChatColor color, String help) {
			this.percent = percent;
			this.color = color;
			this.help = help;
		}
		
		public double getPercent() {
			return percent;
		}
		
		public ChatColor getColor() {
			return color;
		}
		
		public String getHelp() {
			return help;
		}
		
		public String getDisplay() {
			return color + toString();
		}
		
		public static String list() {
			StringBuilder build = new StringBuilder("Chakras: ");
			
			for (Chakra chakra : values()) {
				build.append(chakra.getColor() + chakra.toString());
				
				if (chakra != LIGHT) {
					build.append(ChatColor.WHITE + ", ");
				}
			}
			
			return build.toString();
		}
	}
	
	private Map<Chakra, Integer> points;
	
	public ChakraStats() {
		this.points = new HashMap<>();
		for (Chakra stat : Chakra.values()) {
			points.put(stat, 0);
		}
	}
	
	public ChakraStats(Map<Chakra, Integer> copy) {
		this.points = new HashMap<>();
		for (Chakra stat : Chakra.values()) {
			if (copy.containsKey(stat)) {
				points.put(stat, copy.get(stat));
			} else {
				points.put(stat, 0);
			}
		}
	}
	
	public int getTotalPointsUsed() {
		int sum = 0;
		for (int i : points.values()) {
			sum += i;
		}
		
		return sum;
	}
	
	public int getPoints(Chakra stat) {
		return points.containsKey(stat) ? points.get(stat) : 0;
	}
	
	public double getPercent(Chakra stat) {
		return (1 + stat.getPercent() * getPoints(stat));
	}
	
	public boolean increase(Chakra stat) {
		if (!points.containsKey(stat)) {
			points.put(stat, 0);
		}
		
		if (points.get(stat) < MAX_POINTS) {
			points.put(stat, points.get(stat) + 1);
			return true;
		}
		
		return false;
	}
	
	public boolean decrease(Chakra stat) {
		if (!points.containsKey(stat)) {
			points.put(stat, 0);
		}
		
		if (points.get(stat) > 0) {
			points.put(stat, points.get(stat) - 1);
			return true;
		}
		
		return false;
	}
	
	public void clear(Chakra chakra) {
		this.points.put(chakra, 0);
	}
	
	public void clearAll() {
		this.points = new HashMap<>();
	}
}
