package com.projectkorra.rpg.event;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class EventManager implements Runnable{

	private static String message = ProjectKorraRPG.plugin.getConfig().getString("WorldEvents.SozinsComet.EndMessage");

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
				if (marker.get(world).equalsIgnoreCase("SozinsComet")) {
					handleSozinsComet(world);
				}
				if (FireAbility.isDay(world)) {
					if (marker.get(world).equalsIgnoreCase("LunarEclipse")) {
						marker.replace(world, "");
					}
					if (marker.get(world).equalsIgnoreCase("FullMoon")) {
						marker.replace(world, "");
					}
				} else {
					if (marker.get(world).equalsIgnoreCase("SolarEclipse")) {
						marker.replace(world, "");
					}
				}
			}

			if (!RPGMethods.isEventHappening(world)) {
				if (FireAbility.isDay(world)) {
					if (RPGMethods.isSozinsComet(world) && !RPGMethods.isHappening(world, "SozinsComet")) {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new SozinsCometEvent(world));
						continue;
					} else if (RPGMethods.isSolarEclipse(world) && !RPGMethods.isHappening(world, "SolarEclipse")) {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new SolarEclipseEvent(world));
						continue;
					} 
				} else {
					if (RPGMethods.isLunarEclipse(world) && !RPGMethods.isHappening(world, "LunarEclipse")) {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new LunarEclipseEvent(world));
						continue;
					} else if (RPGMethods.isFullMoon(world) && !RPGMethods.isHappening(world, "FullMoon")) {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new FullMoonEvent(world));
						continue;
					}
				}
			}
		}
	}

	public void handleSozinsComet(World world) {
		if (!FireAbility.isDay(world)) {
			marker.put(world, "");
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (BendingPlayer.getBendingPlayer(player).hasElement(Element.FIRE)) {
					player.sendMessage(ChatColor.DARK_RED + message);
				}
			}
		}
	}
}
