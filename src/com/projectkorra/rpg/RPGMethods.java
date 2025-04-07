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
	
	public static void cycleAvatar(BendingPlayer oldBPlayer) {
		if (Bukkit.getOnlinePlayers().size() <= 1) return; //Don't bother with 1 person or less on...
		revokeAvatar(oldBPlayer.getUUID());
		attemptCycle();
	}

	public static void attemptCycle() {
		// Choose a random player of the onlineplayers
		if (Bukkit.getOnlinePlayers().size() < 1) return; //Don't bother with 1 person or less on...
		// Shuffle the list of online players and attempt to cycle the avatar
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		Collections.shuffle(players);
		for (Player p : players) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(p);
			if (bPlayer == null) continue;
			if (isCurrentAvatar(bPlayer.getUUID()) && !hasBeenAvatar(bPlayer.getUUID())) {
				setAvatar(p.getUniqueId());
				return;
			}
		}
		// If we get there then set AvatarChosen to false.
		ConfigManager.avatarConfig.get().set("Avatar.Current", "");
		ConfigManager.avatarConfig.get().set("Avatar.AvatarChosen", false);
		ConfigManager.avatarConfig.save();
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
			if (ConfigManager.avatarConfig.get().getString("Avatar.Current") == null) {
				ConfigManager.avatarConfig.get().set("Avatar.Current", uuid.toString());
			}
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

		List<String> avatars = new ArrayList<>();
		ResultSet rs = DBConnection.sql.readQuery("SELECT player FROM pk_avatars");
		try {
			while (rs.next()) {
				if (avatars.contains(rs.getString(1))) continue;
				avatars.add(rs.getString(1));
			}
			Statement stmt = rs.getStatement();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		player.sendMessage("Avatar Past Lives:");
		for (String s : avatars) {
			player.sendMessage(s);
		}
		addPermission(bPlayer.getPlayer(), "bending.avatar");
		addPermission(bPlayer.getPlayer(), "bending.ability.avatarstate");
		addPermission(bPlayer.getPlayer(), "bending.ability.elementsphere");
		addPermission(bPlayer.getPlayer(), "bending.ability.spiritbeam");
		addPermission(bPlayer.getPlayer(), "bending.ability.flight");
		addPermission(bPlayer.getPlayer(), "bending.air.flight");
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
		bPlayer.getPlayer().sendMessage("You feel the power of the Avatar leaving you.");
		bPlayer.getElements().clear();
		bPlayer.getElements().addAll(elements);
		GeneralMethods.saveElements(bPlayer);
		ConfigManager.avatarConfig.get().set("Avatar.Current", "");
        ConfigManager.avatarConfig.save();
		removePermission(bPlayer.getPlayer(), "bending.avatar");
		removePermission(bPlayer.getPlayer(), "bending.ability.avatarstate");
		removePermission(bPlayer.getPlayer(), "bending.ability.elementsphere");
		removePermission(bPlayer.getPlayer(), "bending.ability.spiritbeam");
		removePermission(bPlayer.getPlayer(), "bending.ability.flight");
		removePermission(bPlayer.getPlayer(), "bending.air.flight");
	}



}
