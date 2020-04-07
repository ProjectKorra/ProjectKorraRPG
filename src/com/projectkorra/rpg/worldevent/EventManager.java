package com.projectkorra.rpg.worldevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.events.SunRiseEvent;
import com.projectkorra.rpg.events.SunSetEvent;
import com.projectkorra.rpg.worldevent.util.Time;

public class EventManager implements Runnable {

	private ConcurrentHashMap<World, Map<WorldEvent, WorldEventInstance>> marker = new ConcurrentHashMap<>();
	private ConcurrentHashMap<World, List<WorldEvent>> skipper = new ConcurrentHashMap<>();

	private Time time = Time.BOTH;

	@Override
	public void run() {
		for (World world : Bukkit.getServer().getWorlds()) {
			if (world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.THE_END) {
				continue;
			}

			if (ConfigManager.defaultConfig.get().getStringList("Properties.DisabledWorlds").contains(world.getName())) {
				continue;
			}

			if (Commands.isToggledForAll) {
				continue;
			}

			if (!marker.containsKey(world)) {
				marker.put(world, new HashMap<>());
			}

			if (!skipper.containsKey(world)) {
				skipper.put(world, new ArrayList<>());
			}

			for (WorldEventInstance instance : marker.get(world).values()) {
				ProjectKorraRPG.getDisplayManager().update(instance);
			}

			if (world.getTime() >= 23500 && world.getTime() <= 24000) {
				if (time != null && time == Time.DAY) {
					continue;
				}
				
				time = Time.DAY;
				ProjectKorraRPG.getPlugin().getServer().getPluginManager().callEvent(new SunRiseEvent(world));
			} else if (world.getTime() >= 11500 && world.getTime() <= 12000) {
				if (time != null && time == Time.NIGHT) {
					continue;
				}
				
				time = Time.NIGHT;
				ProjectKorraRPG.getPlugin().getServer().getPluginManager().callEvent(new SunSetEvent(world));
			}
		}
	}
	
	public void startEvent(World world, WorldEvent event) {
		startEvent(world, event, true);
	}

	public void startEvent(World world, WorldEvent event, boolean natural) {
		if (marker.get(world).containsKey(event)) {
			return;
		} else if (ConfigManager.defaultConfig.get().getStringList("Properties.DisabledWorlds").contains(world.getName())) {
			return;
		}
		
		List<WorldEvent> removal = new ArrayList<>();
		for (WorldEvent we : marker.get(world).keySet()) {
			if (we.getBlacklistedEvents().contains(event.getName())) {
				return;
			}
			
			if (event.getBlacklistedEvents().contains(we.getName())) {
				removal.add(we);
			}
		}
		
		for (WorldEvent we : removal) {
			endEvent(world, we, true);
		}

		if (!natural) {
			double daysLeft = event.getFrequency() - (Math.ceil((world.getFullTime() / 24000)) % event.getFrequency());
			
			if (daysLeft > 0) {
				world.setFullTime(world.getFullTime() + (long) daysLeft * 24000);
			}
	
			if (event.getTime() != Time.BOTH && event.getTime() != time) {
				long difference = 0;
				if (event.getTime() == Time.DAY) {
					difference = 24000 - world.getTime();
				} else {
					difference = 12000 - world.getTime();
				}
				world.setFullTime(world.getFullTime() + difference);
				time = event.getTime();
			}
		}

		BukkitRunnable run = new BukkitRunnable() {

			@Override
			public void run() {
				EventManager.this.endEvent(world, event, false, true);
			}
			
		};
		
		WorldEventInstance instance = new WorldEventInstance(event, world, run);
		
		marker.get(world).put(event, instance);
		run.runTaskLater(ProjectKorraRPG.getPlugin(), event.getDuration());
		BossBar bar = ProjectKorraRPG.getDisplayManager().createBossBar(instance);

		for (Player player : world.getPlayers()) {
			player.sendMessage(event.getTextColor() + event.getStartMessage());
			bar.addPlayer(player);
		}
	}
	
	public void endEvent(World world, WorldEvent event) {
		endEvent(world, event, false);
	}
	
	public void endEvent(World world, WorldEvent event, boolean blacklisted) {
		endEvent(world, event, blacklisted, false);
	}

	private void endEvent(World world, WorldEvent event, boolean blacklisted, boolean natural) {
		if (!marker.get(world).containsKey(event)) {
			return;
		}
		
		WorldEventInstance instance = marker.get(world).get(event);
		
		if (!natural) {
			instance.remove();
		}
	
		ProjectKorraRPG.getDisplayManager().removeBossBar(instance);
		marker.get(world).remove(event);

		for (Player player : world.getPlayers()) {
			if (blacklisted) {
				player.sendMessage(event.getTextColor() + event.getName() + " was overpowered by another event!");
			} else {
				player.sendMessage(event.getTextColor() + event.getEndMessage());
			}
		}
	}

	public boolean isSkipping(World world, WorldEvent event) {
		return skipper.get(world).contains(event);
	}

	public boolean setSkipping(World world, WorldEvent event, boolean skip) {
		if (world == null) {
			return false;
		}
		
		if (skip) {
			if (!isSkipping(world, event)) {
				skipper.get(world).add(event);
				return true;
			}
		} else {
			if (isSkipping(world, event)) {
				skipper.get(world).remove(event);
				return true;
			}
		}
		return false;
	}

	public boolean isHappening(World world, WorldEvent event) {
		if (world == null) {
			return false;
		}
		
		if (!marker.containsKey(world)) {
			marker.put(world, new HashMap<>());
			return false;
		}
		return marker.get(world).containsKey(event);
	}

	public List<WorldEvent> getEventsHappening(World world) {
		if (world == null) {
			return new ArrayList<>();
		}
		
		if (!marker.containsKey(world)) {
			return new ArrayList<>();
		}
		
		return new ArrayList<>(marker.get(world).keySet());
	}
	
	public List<WorldEventInstance> getEventInstances(World world) {
		if (world == null) {
			return new ArrayList<>();
		}
		
		if (!marker.containsKey(world)) {
			return new ArrayList<>();
		}
		
		return new ArrayList<>(marker.get(world).values());
	}

	public Time getCurrentTime() {
		return time;
	}
}
