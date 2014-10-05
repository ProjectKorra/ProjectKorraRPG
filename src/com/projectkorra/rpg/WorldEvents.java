package com.projectkorra.rpg;

public enum WorldEvents {

	SolarEclipse, LunarEclipse, SozinsComet;
	
	public static boolean getEnabled(WorldEvents we) {
		return ProjectKorraRPG.plugin.getConfig().getBoolean("WorldEvents." + we.toString() + ".Enabled");
	}
	
	public static int getFrequency(WorldEvents we) {
		return ProjectKorraRPG.plugin.getConfig().getInt("WorldEvents." + we.toString() + ".Frequency");
	}
	
	public static int getFactor(WorldEvents we) {
		return ProjectKorraRPG.plugin.getConfig().getInt("WorldEvents." + we.toString() + ".Factor");
	}
}
