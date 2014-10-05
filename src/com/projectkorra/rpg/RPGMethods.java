package com.projectkorra.rpg;

import org.bukkit.World;

import com.projectkorra.ProjectKorra.Methods;

public class RPGMethods {

	static ProjectKorraRPG plugin;

	public RPGMethods(ProjectKorraRPG plugin) {
		RPGMethods.plugin = plugin;
	}
	
	public static boolean isSozinsComet(World world) {
		WorldEvents comet = WorldEvents.SozinsComet;
		if (!getEnabled(WorldEvents.SozinsComet)) return false;
		int freq = getFrequency(comet);
		
		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}
	
	public static boolean isLunarEclipse(World world) {
		WorldEvents eclipse = WorldEvents.LunarEclipse;
		if (!getEnabled(eclipse)) return false;
		int freq = getFrequency(eclipse);
		
		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}
	
	public static boolean isSolarEclipse(World world) {
		WorldEvents eclipse = WorldEvents.SolarEclipse;
		if (!getEnabled(eclipse)) return false;
		int freq = getFrequency(eclipse);
		
		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}
	
	public static boolean getEnabled(WorldEvents we) {
		return ProjectKorraRPG.plugin.getConfig().getBoolean("WorldEvents." + we.toString() + ".Enabled");
	}
	
	public static int getFrequency(WorldEvents we) {
		return ProjectKorraRPG.plugin.getConfig().getInt("WorldEvents." + we.toString() + ".Frequency");
	}
	
	public static double getFactor(WorldEvents we) {
		return ProjectKorraRPG.plugin.getConfig().getDouble("WorldEvents." + we.toString() + ".Factor");
	}
	
}
