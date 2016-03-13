package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.storage.DBConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RPGMethods {

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

	/**
	 * Randomly assigns an element to the param player if enabled in the config
	 * 
	 * @param player BendingPlayer being assigned an element to
	 */
	public static void randomAssign(BendingPlayer player) {
		double rand = Math.random();
		double earthchance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Earth");
		double firechance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Fire");
		double airchance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Air");
		double waterchance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Water");
		double chichance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Chi");

		if (ConfigManager.rpgConfig.get().getBoolean("ElementAssign.Enabled")) {
			if (rand < earthchance) {
				assignElement(player, Element.EARTH, false);
				return;
			}

			else if (rand < waterchance + earthchance && rand > earthchance) {
				assignElement(player, Element.WATER, false);
				return;
			}

			else if (rand < airchance + waterchance + earthchance && rand > waterchance + earthchance) {
				assignElement(player, Element.AIR, false);
				return;
			}

			else if (rand < firechance + airchance + waterchance + earthchance && rand > airchance + waterchance + earthchance) {
				assignElement(player, Element.FIRE, false);
				return;
			}

			else if (rand < chichance + firechance + airchance + waterchance + earthchance && rand > firechance + airchance + waterchance + earthchance) {
				assignElement(player, Element.CHI, true);
				return;
			}
		} else {
			String defaultElement = ConfigManager.rpgConfig.get().getString("ElementAssign.Default");
			Element e = Element.EARTH;

			if (defaultElement.equalsIgnoreCase("None")) {
				return;
			}

			if (defaultElement.equalsIgnoreCase("Chi")) {
				assignElement(player, Element.CHI, true);
				return;
			}

			if (defaultElement.equalsIgnoreCase("Water"))
				e = Element.WATER;
			if (defaultElement.equalsIgnoreCase("Earth"))
				e = Element.EARTH;
			if (defaultElement.equalsIgnoreCase("Fire"))
				e = Element.FIRE;
			if (defaultElement.equalsIgnoreCase("Air"))
				e = Element.AIR;

			assignElement(player, e, false);
			return;
		}
	}

	/**
	 * Sets the player's element as param e, sending a message on what they
	 * became.
	 * 
	 * @param player BendingPlayer which the element is being added to
	 * @param e Element being added to the player
	 * @param chiblocker if the player is becoming a chiblocker
	 */
	private static void assignElement(BendingPlayer player, Element e, Boolean chiblocker) {
		player.setElement(e);
		GeneralMethods.saveElements(player);
		if (!chiblocker) {
			if (e.toString().equalsIgnoreCase("Earth"))
				Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been born as an " + ChatColor.GREEN + e.toString() + "bender!");
			if (e.toString().equalsIgnoreCase("Fire"))
				Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been born as a " + ChatColor.RED + e.toString() + "bender!");
			if (e.toString().equalsIgnoreCase("Water"))
				Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been born as a " + ChatColor.AQUA + e.toString() + "bender!");
			if (e.toString().equalsIgnoreCase("Air"))
				Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been born as an " + ChatColor.GRAY + e.toString() + "bender!");
		} else {
			Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been raised as a " + ChatColor.GOLD + "Chiblocker!");
		}
	}

	/**
	 * Sets a player to the avatar giving them all the elements (excluding
	 * chiblocking) and the extra perks such as almost death avatarstate.
	 * 
	 * @param uuid UUID of player being set as the avatar
	 */
	public static void setAvatar(UUID uuid) {
		if (ConfigManager.avatarConfig.get().contains("Avatar.Current")) {
			UUID curr = UUID.fromString(ConfigManager.avatarConfig.get().getString("Avatar.Current"));
			revokeAvatar(curr);
		}
		ConfigManager.avatarConfig.get().set("Avatar.Current", uuid.toString());
		Player player = Bukkit.getPlayer(uuid);
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player.getName());
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Element e : bPlayer.getElements()) {
			if (e instanceof SubElement)
				continue;

			if (bPlayer.getElements().size() - 1 == i) {
				sb.append(e.toString());
			} else {
				sb.append(e.toString() + ":");
			}
			i += 1;
		}
		DBConnection.sql.modifyQuery("INSERT INTO pk_avatars (uuid, player, elements) VALUES ('" + uuid.toString() + "', '" + player.getName() + "', '" + sb.toString() + "')");
		/*
		 * Gives them the elements
		 */
		bPlayer.getElements().clear();
		bPlayer.getElements().addAll(Arrays.asList(Element.getAllElements()));
		GeneralMethods.saveElements(bPlayer);
		ConfigManager.avatarConfig.save();
	}

	/**
	 * Checks if player with uuid is they current avatar. Returns null if there
	 * is no current avatar
	 * 
	 * @param uuid UUID of player being checked
	 * @return if player with uuid is the current avatar
	 */
	public static boolean isCurrentAvatar(UUID uuid) {
		String currAvatar = ConfigManager.avatarConfig.get().getString("Avatar.Current");
		if (currAvatar == null) {
			return false;
		}
		if (uuid.toString().equalsIgnoreCase(currAvatar)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the player with uuid has been the avatar. Returns true if
	 * player is current avatar
	 * 
	 * @param uuid UUID of player being checked
	 * @return if player with uuid has been the avatar
	 */
	public static boolean hasBeenAvatar(UUID uuid) {
		if (isCurrentAvatar(uuid))
			return true;
		ResultSet rs = DBConnection.sql.readQuery("SELECT uuid FROM pk_avatars WHERE uuid = '" + uuid.toString() + "'");
		boolean valid = false;
		try {
			valid = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return valid;
	}

	/**
	 * Removes the rpg avatar permissions for the player with the matching uuid,
	 * if they are the current avatar
	 * 
	 * @param uuid UUID of player being checked
	 */
	public static void revokeAvatar(UUID uuid) {
		if (!isCurrentAvatar(uuid))
			return;
		List<Element> elements = new ArrayList<>();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(Bukkit.getPlayer(uuid));
		if (bPlayer == null)
			return;
		String elements2 = "";
		ResultSet rs = DBConnection.sql.readQuery("SELECT elements FROM pk_avatars WHERE uuid = '" + uuid.toString() + "'");
		try {
			rs.next();
			elements2 = rs.getString(4);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		for (String s : elements2.split(":")) {
			elements.add(Element.fromString(s));
		}
		bPlayer.getElements().clear();
		bPlayer.getElements().addAll(elements);
		GeneralMethods.saveElements(bPlayer);
	}
}
