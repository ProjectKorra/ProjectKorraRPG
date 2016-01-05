package com.projectkorra.rpg.event;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class EventManager implements Runnable{
	
	private static long duration = ProjectKorraRPG.plugin.getConfig().getLong("WorldEvents.SozinsComet.Duration");
	private long DURATION;
	
	public static ConcurrentHashMap<World, String> marker = new ConcurrentHashMap<>();

	@Override
	public void run() {
		for (World world : Bukkit.getServer().getWorlds()) {
			if (ConfigManager.defaultConfig.get().getStringList("Properties.DisabledWorlds").contains(world.getName())) {
				continue;
			}
			
			if (!marker.containsKey(world)) {
				marker.put(world, "");
			}
			
			if (!marker.get(world).equals("")) {
				continue;
			}
			
			if (RPGMethods.isHappening(world, "SozinsComet")) {
				handleSozinsComet(world);
			}
			
			if (FireMethods.isDay(world)) {
				if (RPGMethods.isSozinsComet(world) && !RPGMethods.isHappening(world, "SozinsComet")) {
					DURATION = System.currentTimeMillis() + duration;
					ProjectKorra.plugin.getServer().getPluginManager().callEvent(new SozinsCometEvent(world));
					continue;
				}
				
				if (RPGMethods.isSolarEclipse(world) && !RPGMethods.isHappening(world, "SolarEclipse")) {
					ProjectKorra.plugin.getServer().getPluginManager().callEvent(new SolarEclipseEvent(world));
					continue;
				} else {
					marker.put(world, "");
					continue;
				}
			} else {
				if (RPGMethods.isLunarEclipse(world) && !RPGMethods.isHappening(world, "LunarEclipse")) {
					ProjectKorra.plugin.getServer().getPluginManager().callEvent(new LunarEclipseEvent(world));
					continue;
				}
				
				if (RPGMethods.isFullMoon(world) && !RPGMethods.isHappening(world, "FullMoon")) {
					ProjectKorra.plugin.getServer().getPluginManager().callEvent(new FullMoonEvent(world));
					continue;
				} else {
					marker.put(world, "");
					continue;
				}
			}
		}
	}
	
	public void handleSozinsComet(World world) {
		if (System.currentTimeMillis() >= DURATION) {
			marker.put(world, "");
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (GeneralMethods.isBender(player.getName(), Element.Fire)) {
					player.sendMessage(ChatColor.DARK_RED + "Sozin's Comet is over!");
				}
			}
		}
	}
}
