package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.projectkorra.storage.DBConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
	
	public static void cycleAvatar(BendingPlayer bPlayer) {
		if (Bukkit.getOnlinePlayers().size() <= 1) return; //Don't bother with 1 person or less on...
		revokeAvatar(bPlayer.getUUID());
		Player avatar = Bukkit.getPlayer(bPlayer.getUUID());
		Random rand = new Random();
		int i = rand.nextInt(Bukkit.getOnlinePlayers().size());
		Player p = (Player) Bukkit.getOnlinePlayers().toArray()[i];
		while (p == avatar) {	
			i = rand.nextInt(Bukkit.getOnlinePlayers().size());
			p = (Player) Bukkit.getOnlinePlayers().toArray()[i];
		}
		setAvatar(p.getUniqueId());
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
		Element newElement = null;

		if (ConfigManager.rpgConfig.get().getBoolean("ElementAssign.Enabled")) {
			double rand = Math.random();
			double earthchance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Earth");
			double firechance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Fire");
			double airchance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Air");
			double waterchance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Water");
			double chichance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Percentages.Chi");

			if (rand < earthchance) {
				newElement = Element.EARTH;
			}
			else if (rand < waterchance + earthchance) {
				assignElement(player, Element.WATER);
			}
			else if (rand < airchance + waterchance + earthchance) {
				assignElement(player, Element.AIR);
			}
			else if (rand < firechance + airchance + waterchance + earthchance) {
				assignElement(player, Element.FIRE);
			}
			else if (rand < chichance + firechance + airchance + waterchance + earthchance) {
				assignElement(player, Element.CHI);
			}
		} else {
			String defaultElement = ConfigManager.rpgConfig.get().getString("ElementAssign.Default");

			if (defaultElement.equalsIgnoreCase("chi")) {
				newElement = Element.CHI;
			}
			else if (defaultElement.equalsIgnoreCase("water")) {
				newElement = Element.WATER;
			}
			else if (defaultElement.equalsIgnoreCase("earth")) {
				newElement = Element.EARTH;
			}
			else if (defaultElement.equalsIgnoreCase("fire")) {
				newElement = Element.FIRE;
			}
			else if (defaultElement.equalsIgnoreCase("air")) {
				newElement = Element.AIR;
			}
		}
		
		if (newElement != null) {
			assignElement(player, newElement);
		}
	}
	
	public static void randomAssignSubElements(BendingPlayer bPlayer) {
		if (bPlayer.hasElement(Element.CHI)) return;
                
		double chance = 0;
		// String[] subs = {"Blood", "Combustion", "Flight", "Healing", "Ice", "Lava", "Lightning", "Metal", "Plant", "Sand", "SpiritualProjection"};
		SubElement[] subs = SubElement.getAllSubElements();
		StringBuilder sb = new StringBuilder(ChatColor.YELLOW + "You have an affinity for ");
		ArrayList<SubElement> sublist = new ArrayList<>();
		boolean checkConflicts = ConfigManager.rpgConfig.get().getBoolean("SubElementAssign.CheckConflicts", true);
		int maxSubElements = ConfigManager.rpgConfig.get().getInt("SubElementAssign.MaxSubElements", -1);
		
		for (SubElement sub : subs) {
			if (maxSubElements == 0) {
				break;
			}
			double rand = Math.random();
			String name = sub.getName();
			Element e = sub.getParentElement();
			chance = ConfigManager.rpgConfig.get().getDouble("SubElementAssign.Percentages." + name);

			if (!bPlayer.hasElement(e) || chance <= 0) {
				continue;
			}

			if (checkConflicts) {
				if (sub == SubElement.METAL && sublist.contains(SubElement.LAVA)) continue;
				if (sub == SubElement.LAVA && sublist.contains(SubElement.METAL)) continue;
				if (sub== SubElement.LIGHTNING && sublist.contains(SubElement.COMBUSTION)) continue;
				if (sub== SubElement.COMBUSTION && sublist.contains(SubElement.LIGHTNING)) continue;
			}

			if (rand < chance) {
				sublist.add(sub);
				bPlayer.addSubElement(sub);
				GeneralMethods.saveSubElements(bPlayer);
				maxSubElements--;
			}
		}
		int size = sublist.size();
		if (size >= 1) {
			for (SubElement sub : sublist) {
				String name = sub.getName();
				sb.append(Element.getElement(name).getColor()).append(name);
				size--;
				if (size == 0) {
					sb.append(ChatColor.YELLOW).append(".");
				}
				else if (size == 1) {
					sb.append(ChatColor.YELLOW).append(" and ");
				}
				else {
					sb.append(ChatColor.YELLOW).append(", ");
				}
			}
		} else {
			sb = new StringBuilder(ChatColor.RED + "You sadly don't have any extra affinity for your element.");
		}
		
		Player p = Bukkit.getPlayer(bPlayer.getUUID());
		if (p != null) {
			p.sendMessage(sb.toString());
		}
	}

	/**
	 * Sets the player's element as param e, sending a message on what they
	 * became.
	 * 
	 * @param bPlayer BendingPlayer which the element is being added to
	 * @param e Element being added to the player
	 */
	private static void assignElement(BendingPlayer bPlayer, Element e) {
		bPlayer.setElement(e);
		GeneralMethods.saveElements(bPlayer);
		Bukkit.getPlayer(bPlayer.getUUID()).sendMessage(ChatColor.YELLOW + "You have been born as an " + e.getColor() + e.getName() + e.getType().getBender() + ChatColor.YELLOW +  "!");
	}
        
        /**
	 * Returns if there is an avatar, if he/she has already been choosen or not.
	 */
	public static boolean isAvatarChoosen() {
		if (ConfigManager.avatarConfig.get().contains("Avatar.Current")) {
			if (!"".equals(ConfigManager.avatarConfig.get().getString("Avatar.Current")) || (ConfigManager.avatarConfig.get().getString("Avatar.Current")) != null)
                                return true;
		}
                return false;
	}

	/**
	 * Sets a player to the avatar giving them all the elements (excluding
	 * chiblocking) and the extra perks such as almost death avatarstate.
	 * 
	 * @param uuid UUID of player being set as the avatar
	 */
	public static void setAvatar(UUID uuid) {
		if (!isAvatarChoosen()) {
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
				sb.append(e.getName());
			} else {
				sb.append(e.getName() + ":");
			}
			i += 1;
		}
                DBConnection.sql.modifyQuery("DELETE FROM pk_avatars WHERE uuid = '" + uuid.toString() + "'");
		DBConnection.sql.modifyQuery("INSERT INTO pk_avatars (uuid, player, elements) VALUES ('" + uuid.toString() + "', '" + player.getName() + "', '" + sb.toString() + "')");
		/*
		 * Gives them the elements
		 */
		bPlayer.getElements().clear();
                Set<Element> shouldBeAdded = new HashSet<>(Arrays.asList(Element.getAllElements()));
                if (shouldBeAdded.contains(Element.CHI)){
                        shouldBeAdded.remove(Element.CHI);
                }
		bPlayer.getElements().addAll(shouldBeAdded);
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
		boolean valid;
		try {
			valid = rs.next();
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			valid = false;
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
                if (uuid == null)
                        return;
		if (!isCurrentAvatar(uuid))
			return;
		List<Element> elements = new ArrayList<>();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(Bukkit.getPlayer(uuid));
		if (bPlayer == null)
			return;
		String elements2 = "";
		if (DBConnection.sql == null)
			return;
		if (DBConnection.sql.getConnection() == null)
			return;
		ResultSet rs = DBConnection.sql.readQuery("SELECT elements FROM pk_avatars WHERE uuid = '" + uuid.toString() + "'");
		try {
			if (rs.next()) {
				elements2 = rs.getString("elements");
			}
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();
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
		ConfigManager.avatarConfig.get().set("Avatar.Current", "");
                ConfigManager.avatarConfig.save();
	}
}
