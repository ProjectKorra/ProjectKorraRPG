package com.projectkorra.rpg.player;

import java.util.Arrays;

import com.projectkorra.rpg.util.XPControl;

import org.bukkit.ChatColor;

public class ChakraStats {
	
	public static final int MAX_POINTS = (XPControl.MAX_LEVEL + 25 - (XPControl.MAX_LEVEL % 5)) / 5;

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
	
	private int[] points;
	
	public ChakraStats() {
		this.points = new int[Chakra.values().length];
	}
	
	public ChakraStats(int[] copy) {
		this.points = Arrays.copyOf(copy, copy.length);
	}
	
	public int getTotalPointsUsed() {
		int sum = 0;
		for (int i : this.points) {
			sum += i;
		}
		
		return sum;
	}
	
	public int getPoints(Chakra stat) {
		return points[stat.ordinal()];
	}
	
	public double getPercent(Chakra stat) {
		return (1 + stat.getPercent() * getPoints(stat));
	}
	
	public boolean increase(Chakra stat) {
		if (getPoints(stat) < MAX_POINTS) {
			++points[stat.ordinal()];
			return true;
		}
		
		return false;
	}
	
	public boolean decrease(Chakra stat) {
		if (getPoints(stat) > 0) {
			--points[stat.ordinal()];
			return true;
		}
		
		return false;
	}
	
	public void clear(Chakra stat) {
		this.points[stat.ordinal()] = 0;
	}
	
	public void clearAll() {
		Arrays.fill(this.points, 0);
	}
}
