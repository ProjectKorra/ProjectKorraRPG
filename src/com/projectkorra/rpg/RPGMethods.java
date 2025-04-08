package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.projectkorra.storage.DBConnection;

import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.projectkorra.rpg.ProjectKorraRPG.api;

public class RPGMethods {

	/**
	 * @author CrashCringle
	 *
	 * @Description This method is a simplified way of removing
	 * Permissions to players via LuckPerms
	 *
	 * @param player Player who will lose permission
	 * @param permission Permission to remove from the player as a string
	 */
	public static void removePermission(Player player, String permission) {
		api.getUserManager().getUser(player.getUniqueId()).data()
				.remove(Node.builder(permission).build());
		api.getUserManager().saveUser(api.getUserManager().getUser(player.getUniqueId()));

	}
	/**
	 * @author CrashCringle
	 *
	 * @Description This method is a simplified way of adding
	 * Permissions to players via LuckPerms
	 *
	 * @param player Player who will receive permission
	 * @param permission Permission to give to the player as a string
	 */
	public static void addPermission(Player player, String permission) {
		api.getUserManager().getUser(player.getUniqueId()).data()
				.add(Node.builder(permission).build());
		api.getUserManager().saveUser(api.getUserManager().getUser(player.getUniqueId()));

	}
	/**
	 * Checks every event interval for an event
	 * 
	 * @param world World being checked for an event
	 * @return true if event interval is found
	 */
	public static boolean checkEveryInterval(World world) {
		if (isFullMoon(world))
			return true;
		if (isLunarEclipse(world))
			return true;
		if (isSolarEclipse(world))
			return true;
		if (isSozinsComet(world))
			return true;
		return false;
	}

	/**
	 * Checks for if the next event in the world is being skipped
	 * 
	 * @param world World being checked
	 * @return true if being skipped
	 */
	public static boolean isBeingSkipped(World world) {
		if (EventManager.skipper == null)
			return false;
		return EventManager.skipper.get(world);
	}

	/**
	 * Returns false if the world event isn't enabled
	 * 
	 * @param world World being checked.
	 * @return if FullMoon frequency lines up
	 */
	public static boolean isFullMoon(World world) {
		if (!getEnabled("FullMoon"))
			return false;
		long days = world.getFullTime() / 24000;
		long phase = days % getFrequency("FullMoon");
		if (phase == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param world World being checked
	 * @param worldevent World event to check for in param world
	 * @return if param worldevent is happening in param world
	 */
	public static boolean isHappening(World world, String worldevent) {
		if (EventManager.marker.get(world) == null)
			return false;
		if (EventManager.marker.get(world) == "")
			return false;
		if (EventManager.marker.get(world).equalsIgnoreCase(worldevent))
			return true;
		return false;
	}

	/**
	 * Checks if an event is happening in the provided world
	 * 
	 * @param world World being checked
	 * @return whether there is an event happening or not
	 */
	public static boolean isEventHappening(World world) {
		if (EventManager.marker.get(world) == null)
			return false;
		if (EventManager.marker.get(world) == "")
			return false;
		return true;
	}

	/**
	 * Returns false if the world event isn't enabled
	 * 
	 * @param world World being checked.
	 * @return if LunarEclipse frequency lines up
	 */
	public static boolean isLunarEclipse(World world) {
		String eclipse = "LunarEclipse";
		if (!getEnabled(eclipse))
			return false;
		int freq = getFrequency(eclipse);

		long days = (world.getFullTime() + 500) / 24000;
		if (days % freq == 0)
			return true;
		return false;
	}

	/**
	 * Returns false if the world event isn't enabled
	 * 
	 * @param world World being checked.
	 * @return if SolarEclipse frequency lines up
	 */
	public static boolean isSolarEclipse(World world) {
		String eclipse = "SolarEclipse";
		if (!getEnabled(eclipse))
			return false;
		int freq = getFrequency(eclipse);

		long days = (world.getFullTime() + 500) / 24000;
		if (days % freq == 0)
			return true;
		return false;
	}

	/**
	 * Returns false if the world event isn't enabled
	 * 
	 * @param world World being checked.
	 * @return if SozinsComet frequency lines up
	 */
	public static boolean isSozinsComet(World world) {
		String comet = "SozinsComet";
		if (!getEnabled(comet))
			return false;
		int freq = getFrequency(comet);

		long days = (world.getFullTime() + 500) / 24000;
		if (days % freq == 0)
			return true;
		return false;
	}

	/**
	 * 
	 * @param we World Event. Choices are LunarEclipse, SolarEclipse,
	 *            SozinsComet, and FullMoon
	 * @return boolean of if param we is enabled
	 */
	public static boolean getEnabled(String we) {
		return ConfigManager.rpgConfig.get().getBoolean("WorldEvents." + we + ".Enabled");
	}

	/**
	 * 
	 * @param we World Event. Choices are LunarEclipse, SolarEclipse,
	 *            SozinsComet, and FullMoon
	 * @return int of frequency for param we
	 */
	public static int getFrequency(String we) {
		if (we == "FullMoon")
			return 8;
		return ConfigManager.rpgConfig.get().getInt("WorldEvents." + we + ".Frequency");
	}

	/**
	 * 
	 * @param we World event. Choices are LunarEclipse, SolarEclipse,
	 *            SozinsComet, and FullMoon
	 * @return double of factor for param we
	 */
	public static double getFactor(String we) {
		if (we == "SolarEclipse" || we == "LunarEclipse")
			return 0;
		return ConfigManager.rpgConfig.get().getDouble("WorldEvents." + we + ".Factor");
	}


}
