package com.projectkorra.rpg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.event.WorldEvent;
import com.projectkorra.rpg.storage.DBConnection;

public class RPGMethods {

	static ProjectKorraRPG plugin;

	public RPGMethods(ProjectKorraRPG plugin) {
		RPGMethods.plugin = plugin;
	}
	
	public static boolean isFullMoon(World world) {
		if (getEnabled(WorldEvent.FullMoon) == false) return false;
		if (!EventManager.getEvents().isEmpty() && EventManager.getEvents().containsKey(WorldEvent.FullMoon) && EventManager.getEvents().get(WorldEvent.FullMoon).equals(world)) return true;
		long days = world.getFullTime() / 24000;
		long phase = days % 8;
		if (phase == 0) {
			return true;
		}
		return false;
	}

	public static boolean isSozinsComet(World world) {
		WorldEvent comet = WorldEvent.SozinsComet;
		if (getEnabled(comet) == false) return false;
		if (!EventManager.getEvents().isEmpty() && EventManager.getEvents().containsKey(WorldEvent.SozinsComet) && EventManager.getEvents().get(comet).equals(world)) return true;
		int freq = getFrequency(comet);

		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}

	public static boolean isLunarEclipse(World world) {
		WorldEvent eclipse = WorldEvent.LunarEclipse;
		if (getEnabled(eclipse) == false) return false;
		if (!EventManager.getEvents().isEmpty() && EventManager.getEvents().containsKey(WorldEvent.LunarEclipse) && EventManager.getEvents().get(eclipse).equals(world)) return true;
		int freq = getFrequency(eclipse);

		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}

	public static boolean isSolarEclipse(World world) {
		WorldEvent eclipse = WorldEvent.SolarEclipse;
		if (getEnabled(eclipse) == false) return false;
		if (!EventManager.getEvents().isEmpty() && EventManager.getEvents().containsKey(WorldEvent.SolarEclipse) && EventManager.getEvents().get(eclipse).equals(world)) return true;
		int freq = getFrequency(eclipse);

		long days = world.getFullTime() / 24000;
		if (days%freq == 0) return true;
		return false;
	}

	public static boolean getEnabled(WorldEvent we) {
		return ProjectKorraRPG.plugin.getConfig().getBoolean("WorldEvents." + we.toString() + ".Enabled");
	}

	public static int getFrequency(WorldEvent we) {
		if (we == WorldEvent.FullMoon)
			return 8;
		return ProjectKorraRPG.plugin.getConfig().getInt("WorldEvents." + we.toString() + ".Frequency");
	}

	public static double getFactor(WorldEvent we) {
		if (we == WorldEvent.SolarEclipse || we == WorldEvent.LunarEclipse)
			return 0;
		return ProjectKorraRPG.plugin.getConfig().getDouble("WorldEvents." + we.toString() + ".Factor");
	}

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
