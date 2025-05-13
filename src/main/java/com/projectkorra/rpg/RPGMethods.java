package com.projectkorra.rpg;

import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import static com.projectkorra.rpg.ProjectKorraRPG.luckPermsAPI;

public class RPGMethods {
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
