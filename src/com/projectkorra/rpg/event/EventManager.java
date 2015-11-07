package com.projectkorra.rpg.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.event.WorldEvent.FullMoonEvent;
import com.projectkorra.rpg.event.WorldEvent.LunarEclipseEvent;
import com.projectkorra.rpg.event.WorldEvent.SolarEclipseEvent;
import com.projectkorra.rpg.event.WorldEvent.SozinsCometEvent;

public class EventManager implements Runnable{
	
	private static long duration = ProjectKorraRPG.plugin.getConfig().getLong("WorldEvents.SozinsComet.Duration");
	private static long startTime;
	
	private static ConcurrentHashMap<World, List<WorldEvent>> events = new ConcurrentHashMap<World, List<WorldEvent>>();
	private static ConcurrentHashMap<World, Long> times = new ConcurrentHashMap<World, Long>();

	@Override
	public void run() {
		for (World world : Bukkit.getServer().getWorlds()) {
			if (!events.isEmpty() && !events.get(world).isEmpty()) {
				if (RPGMethods.isFullMoon(world))
					handleFullMoon(world);
				if (RPGMethods.isLunarEclipse(world))
					handleLunarEclipse(world);
				if (RPGMethods.isSolarEclipse(world))
					handleSolarEclipse(world);
				if (RPGMethods.isSozinsComet(world))
					handleSozinsComet(world);
			} else {
				List<WorldEvent> wEvents = new ArrayList<WorldEvent>();
				if (FireMethods.isDay(world) && RPGMethods.isSolarEclipse(world)) {
					Bukkit.getServer().getPluginManager().callEvent(new SolarEclipseEvent(world));
					wEvents.add(WorldEvent.SolarEclipse);
				}
				else if (WaterMethods.isNight(world)) {
					if (RPGMethods.isLunarEclipse(world)) {
						Bukkit.getServer().getPluginManager().callEvent(new LunarEclipseEvent(world));
						wEvents.add(WorldEvent.LunarEclipse);
					} else if (RPGMethods.isFullMoon(world)) {
						Bukkit.getServer().getPluginManager().callEvent(new FullMoonEvent(world));
						wEvents.add(WorldEvent.FullMoon);
					}
				}
				else if (RPGMethods.isSozinsComet(world)) {
					Bukkit.getServer().getPluginManager().callEvent(new SozinsCometEvent(world));
					wEvents.add(WorldEvent.SozinsComet);
					times.put(world, world.getTime());
					startTime = world.getTime();
				}
			}
		}
	}
	
	public static void handleFullMoon(World world) {
		if (events.isEmpty() || !events.containsKey(world) || world == null) return;
		if (FireMethods.isDay(world)) {
			events.remove(world);
		}
	}
	
	public static void handleLunarEclipse(World world) {
		if (events.isEmpty() || !events.containsKey(world) || world == null) return;
		if (FireMethods.isDay(world)) {
			events.remove(world);
		}
	}
	
	public static void handleSolarEclipse(World world) {
		if (events.isEmpty() || !events.containsKey(world) || world == null) return;
		if (WaterMethods.isNight(world)) {
			events.remove(world);
		}
	}
	
	public static void handleSozinsComet(World world) {
		if (events.isEmpty() || !events.containsKey(world) || world == null) return;
		times.put(world, times.get(world)+1);
		if (times.get(world) - duration >= startTime) {
			events.remove(WorldEvent.SozinsComet);
			times.remove(world);
		}
	}
	
	public static List<WorldEvent> getEvents(World world) {
		return events.get(world);
	}
	
	public static void setEvent(World world, WorldEvent event) {
		List<WorldEvent> we = getEvents(world);
		if (!we.contains(event)) {
			we.add(event);
		}
		events.put(world, we);
	}
}
