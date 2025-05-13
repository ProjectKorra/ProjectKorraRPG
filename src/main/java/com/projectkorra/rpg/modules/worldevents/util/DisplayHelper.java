package com.projectkorra.rpg.modules.worldevents.util;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class DisplayHelper {
	public static BarColor convertStringToBarColor(String colorStr) {
		if (colorStr == null) {
			return BarColor.RED;
		}
		return switch (colorStr.toUpperCase()) {
			case "GREEN" -> BarColor.GREEN;
			case "BLUE" -> BarColor.BLUE;
			case "YELLOW" -> BarColor.YELLOW;
			case "PURPLE" -> BarColor.PURPLE;
			case "WHITE" -> BarColor.WHITE;
			case "PINK" -> BarColor.PINK;
			default -> BarColor.RED;
		};
	}

	public static BarStyle convertStringToBarStyle(String styleStr) {
		if (styleStr == null) {
			return BarStyle.SOLID;
		}
		return switch (styleStr.toUpperCase()) {
			case "SEGMENTED_6" -> BarStyle.SEGMENTED_6;
			case "SEGMENTED_10" -> BarStyle.SEGMENTED_10;
			case "SEGMENTED_12" -> BarStyle.SEGMENTED_12;
			case "SEGMENTED_20" -> BarStyle.SEGMENTED_20;
			default -> BarStyle.SOLID;
		};
	}
}
