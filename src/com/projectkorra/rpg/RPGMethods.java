package com.projectkorra.rpg;

import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import net.luckperms.api.node.Node;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.projectkorra.rpg.ProjectKorraRPG.luckPermsAPI;

public class RPGMethods {
	private static Set<String> cachedDisabledWorldNames = new HashSet<>();

	public static Set<String> getDisabledWorldNames(WorldEvent event) {
		Set<String> names = new HashSet<>();
		List<World> disabledWorlds = event.getDisabledWorlds();
		if (disabledWorlds != null) {
			for (World world : disabledWorlds) {
				if (world != null) {
					names.add(world.getName());
				}
			}
		}
		return names;
	}

	public static void updateBlacklistedWorldNames(List<World> blacklistedWorlds) {
		Set<String> names = new HashSet<>();
		if (blacklistedWorlds != null) {
			for (World world : blacklistedWorlds) {
				if (world != null) {
					names.add(world.getName());
				}
			}
		}
		cachedDisabledWorldNames = names;
	}

	public static BarColor convertStringToColor(String colorStr) {
		if (colorStr == null) {
			return BarColor.RED;
		}
		return switch (colorStr.toUpperCase()) {
			case "GREEN" -> BarColor.GREEN;
			case "BLUE" -> BarColor.BLUE;
			case "YELLOW" -> BarColor.YELLOW;
			case "PURPLE" -> BarColor.PURPLE;
			case "WHITE" -> BarColor.WHITE;
			case "PINK" -> BarColor.PINK;
			default -> BarColor.RED;
		};
	}

	public static BarStyle convertStringToStyle(String styleStr) {
		if (styleStr == null) {
			return BarStyle.SOLID;
		}
		return switch (styleStr.toUpperCase()) {
			case "SEGMENTED_6" -> BarStyle.SEGMENTED_6;
			case "SEGMENTED_10" -> BarStyle.SEGMENTED_10;
			case "SEGMENTED_12" -> BarStyle.SEGMENTED_12;
			case "SEGMENTED_20" -> BarStyle.SEGMENTED_20;
			default -> BarStyle.SOLID;
		};
	}

	/**
	 * @param player     Player who will lose permission
	 * @param permission Permission to remove from the player as a string
	 * @author CrashCringle
	 * @Description This method is a simplified way of removing
	 * Permissions to players via LuckPerms
	 */
	public static void removePermission(Player player, String permission) {
		if (luckPermsAPI == null)
			return;

		luckPermsAPI.getUserManager().getUser(player.getUniqueId()).data()
				.remove(Node.builder(permission).build());
		luckPermsAPI.getUserManager().saveUser(luckPermsAPI.getUserManager().getUser(player.getUniqueId()));

	}

	/**
	 * @param player     Player who will receive permission
	 * @param permission Permission to give to the player as a string
	 * @author CrashCringle
	 * @Description This method is a simplified way of adding
	 * Permissions to players via LuckPerms
	 */
	public static void addPermission(Player player, String permission) {
		if (luckPermsAPI == null)
			return;
		luckPermsAPI.getUserManager().getUser(player.getUniqueId()).data()
				.add(Node.builder(permission).build());
		luckPermsAPI.getUserManager().saveUser(luckPermsAPI.getUserManager().getUser(player.getUniqueId()));
	}

	public static Set<String> getCachedDisabledWorldNames() {
		return cachedDisabledWorldNames;
	}
}
