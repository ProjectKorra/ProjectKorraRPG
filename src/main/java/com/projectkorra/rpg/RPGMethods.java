package com.projectkorra.rpg;

import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import java.time.Duration;

import static com.projectkorra.rpg.ProjectKorraRPG.luckPermsAPI;

public class RPGMethods {
	private static final ProjectKorraRPG plugin = ProjectKorraRPG.getPlugin();

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

	/**
	 * @param period String to convert to duration
	 * @return Duration in the period string
	 * @author CrashCringle
	 * @Description This method converts a period string like 3d4h to a duration object
	 */
	public static Duration periodStringToDuration(String period) {
		// Can be in the formats like: 1s, 1m, 1h, 1d, 2d1h10s etc etc.
		Duration duration = Duration.ZERO;
		if (period == null || period.isEmpty()) {
			plugin.getLogger().info("Invalid period string: " + period);
			return duration;
		}
		String[] parts = period.split("(?<=\\D)(?=\\d)");
		for (String part : parts) {
			String unit = part.replaceAll("\\d", "");
			double value = Double.parseDouble(part.replaceAll("\\D", ""));
			duration = switch (unit) {
				case "w" -> duration.plusHours((long) (value * 168));
				case "d" -> duration.plusHours((long) (value * 24));
				case "h" -> duration.plusHours((long) value);
				case "m" -> duration.plusMinutes((long) value);
				case "s" -> duration.plusSeconds((long) value);
				default -> duration;
			};
		}
		return duration;
	}
}
