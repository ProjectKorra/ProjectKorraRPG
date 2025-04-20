package com.projectkorra.rpg;

import net.luckperms.api.node.Node;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import static com.projectkorra.rpg.ProjectKorraRPG.luckPermsAPI;

public class RPGMethods {
	public static BarColor convertStringToBarColor(String colorStr) {
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

	public static BarStyle convertStringToBarStyle(String styleStr) {
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
}
