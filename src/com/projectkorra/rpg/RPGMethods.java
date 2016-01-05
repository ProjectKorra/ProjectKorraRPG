package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.storage.DBConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RPGMethods {

	static ProjectKorraRPG plugin;

	public RPGMethods(ProjectKorraRPG plugin) {
		RPGMethods.plugin = plugin;
	}
	
	/**
	 * Returns false if the world event isn't enabled
	 * @param world World being checked. 
	 * @return if FullMoon frequency lines up
	 */
	public static boolean isFullMoon(World world) {
		if (!getEnabled("FullMoon")) return false;
		long days = world.getFullTime() / 24000;
		long phase = days % 8;
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
		if (EventManager.marker.get(world) == null) return false;
		if (EventManager.marker.get(world) == "") return false;
		if (EventManager.marker.get(world) == worldevent) return true;
		return false;
	}
	
	/**
	 * Returns false if the world event isn't enabled
	 * @param world World being checked. 
	 * @return if SozinsComet frequency lines up
	 */
	public static boolean isSozinsComet(World world) {
		String comet = "SozinsComet";
		if (!getEnabled(comet)) return false;
		int freq = getFrequency(comet);

		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}

	/**
	 * Returns false if the world event isn't enabled
	 * @param world World being checked. 
	 * @return if LunarEclipse frequency lines up
	 */
	public static boolean isLunarEclipse(World world) {
		String eclipse = "LunarEclipse";
		if (!getEnabled(eclipse)) return false;
		int freq = getFrequency(eclipse);

		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}
	
	/**
	 * Returns false if the world event isn't enabled
	 * @param world World being checked. 
	 * @return if SolarEclipse frequency lines up
	 */
	public static boolean isSolarEclipse(World world) {
		String eclipse = "SolarEclipse";
		if (!getEnabled(eclipse)) return false;
		int freq = getFrequency(eclipse);

		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}

	/**
	 * 
	 * @param we World Event. Choices are LunarEclipse, SolarEclipse, SozinsComet, and FullMoon
	 * @return boolean of if param we is enabled
	 */
	public static boolean getEnabled(String we) {
		return ProjectKorraRPG.plugin.getConfig().getBoolean("WorldEvents." + we + ".Enabled");
	}

	/**
	 * 
	 * @param we World Event. Choices are LunarEclipse, SolarEclipse, SozinsComet, and FullMoon
	 * @return int of frequency for param we
	 */
	public static int getFrequency(String we) {
		if (we == "FullMoon")
			return 8;
		return ProjectKorraRPG.plugin.getConfig().getInt("WorldEvents." + we + ".Frequency");
	}

	/**
	 * 
	 * @param we World event. Choices are LunarEclipse, SolarEclipse, SozinsComet, and FullMoon
	 * @return double of factor for param we
	 */
	public static double getFactor(String we) {
		if (we == "SolarEclipse" || we == "LunarEclipse")
			return 0;
		return ProjectKorraRPG.plugin.getConfig().getDouble("WorldEvents." + we + ".Factor");
	}
	
	/**
	 * Randomly assigns an element to the param player if enabled in the config
	 * @param player BendingPlayer being assigned an element to
	 */
	public static void randomAssign(BendingPlayer player) {
		double rand = Math.random();
		double earthchance = ProjectKorraRPG.plugin.getConfig().getDouble("ElementAssign.Percentages.Earth");
		double firechance = ProjectKorraRPG.plugin.getConfig().getDouble("ElementAssign.Percentages.Fire");
		double airchance = ProjectKorraRPG.plugin.getConfig().getDouble("ElementAssign.Percentages.Air");
		double waterchance = ProjectKorraRPG.plugin.getConfig().getDouble("ElementAssign.Percentages.Water");
		double chichance = ProjectKorraRPG.plugin.getConfig().getDouble("ElementAssign.Percentages.Chi");

		if(ProjectKorraRPG.plugin.getConfig().getBoolean("ElementAssign.Enabled")) {
			if (rand < earthchance ) {
				assignElement(player, Element.Earth, false);
				return;
			}

			else if (rand < waterchance + earthchance && rand > earthchance) {
				assignElement(player, Element.Water, false);
				return;
			}

			else if (rand < airchance + waterchance + earthchance && rand > waterchance + earthchance) {
				assignElement(player, Element.Air, false);
				return;
			}

			else if (rand < firechance + airchance + waterchance + earthchance && rand > airchance + waterchance + earthchance) {
				assignElement(player, Element.Fire, false);
				return;
			}

			else if (rand < chichance + firechance + airchance + waterchance + earthchance && rand > firechance + airchance + waterchance + earthchance) {
				assignElement(player, Element.Chi, true);
				return;
			}
		} else {
			String defaultElement = ProjectKorraRPG.plugin.getConfig().getString("ElementAssign.Default");
			Element e = Element.Earth;
			
			if(defaultElement.equalsIgnoreCase("None")) {
				return;
			}

			if(defaultElement.equalsIgnoreCase("Chi")) {
				assignElement(player, Element.Chi, true);
				return;
			}

			if(defaultElement.equalsIgnoreCase("Water")) e = Element.Water;
			if(defaultElement.equalsIgnoreCase("Earth")) e = Element.Earth;
			if(defaultElement.equalsIgnoreCase("Fire")) e = Element.Fire;
			if(defaultElement.equalsIgnoreCase("Air")) e = Element.Air;

			assignElement(player, e, false);
			return;
		}
	}

	/**
	 * Sets the player's element as param e, sending a message on what they became.
	 * @param player BendingPlayer which the element is being added to
	 * @param e Element being added to the player
	 * @param chiblocker if the player is becoming a chiblocker 
	 */
	private static void assignElement(BendingPlayer player, Element e, Boolean chiblocker) {
		player.setElement(e);
		GeneralMethods.saveElements(player);
		if(!chiblocker) {
			if(e.toString().equalsIgnoreCase("Earth")) Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been born as an " + EarthMethods.getEarthColor() + e.toString() + "bender!");
			if(e.toString().equalsIgnoreCase("Fire")) Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been born as a " + FireMethods.getFireColor() + e.toString() + "bender!");
			if(e.toString().equalsIgnoreCase("Water")) Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been born as a " + WaterMethods.getWaterColor() + e.toString() + "bender!");
			if(e.toString().equalsIgnoreCase("Air")) Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been born as an " + AirMethods.getAirColor() + e.toString() + "bender!");
		}else{
			Bukkit.getPlayer(player.getUUID()).sendMessage(ChatColor.WHITE + "You have been raised as a " + ChiMethods.getChiColor() + "Chiblocker!");
		}
	}
	
	/**
	 * Sets a player to the avatar giving them all the elements (excluding chiblocking) and the extra perks such as almost death avatarstate.  
	 * @param uuid UUID of player being set as the avatar
	 */
	public static void setAvatar(UUID uuid) {
		plugin.getConfig().set("Avatar.Current", uuid.toString());
		plugin.saveConfig();
		Player player = Bukkit.getPlayer(uuid);
		BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
		String element = "none";
		if (bPlayer.getElements().contains(Element.Air)) element = "air";
		if (bPlayer.getElements().contains(Element.Water)) element = "water";
		if (bPlayer.getElements().contains(Element.Earth)) element = "earth";
		if (bPlayer.getElements().contains(Element.Fire)) element = "fire";
		if (bPlayer.getElements().contains(Element.Chi)) element = "chi";

		/*
		 * Gives them the elements
		 */
		if (!bPlayer.getElements().contains(Element.Air)) bPlayer.addElement(Element.Air);
		if (!bPlayer.getElements().contains(Element.Water)) bPlayer.addElement(Element.Water);
		if (!bPlayer.getElements().contains(Element.Earth)) bPlayer.addElement(Element.Earth);
		if (!bPlayer.getElements().contains(Element.Fire)) bPlayer.addElement(Element.Fire);

		DBConnection.sql.modifyQuery("INSERT INTO pk_avatars (uuid, player, element) VALUES ('" + uuid.toString() + "', '" + player.getName() + "', '" + element + "')");
	}

	/**
	 * Checks if player with uuid is they current avatar. Returns null if there is no current avatar
	 * @param uuid UUID of player being checked
	 * @return if player with uuid is or is not the current avatar
	 */
	public static boolean isCurrentAvatar(UUID uuid) {
		String currAvatar = plugin.getConfig().getString("Avatar.Current");
		if (currAvatar == null) {
			return false;
		}
		UUID uuid2 = UUID.fromString(currAvatar);
		if (uuid.toString().equalsIgnoreCase(uuid2.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the player with uuid has been the avatar. Returns true if player is current avatar
	 * @param uuid UUID of player being checked
	 * @return if player with uuid has been the avatar
	 */
	public static boolean hasBeenAvatar(UUID uuid) {
		if (isCurrentAvatar(uuid)) return true;
		ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM pk_avatars WHERE uuid = '" + uuid.toString() + "'");
		try {
			if (rs2.next()) {
				return true;
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
