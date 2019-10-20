package com.projectkorra.rpg.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;

public class AvatarCommand extends RPGCommand {

	public AvatarCommand() {
		super("avatar", "/bending rpg avatar <player | --list | --clear>", "This command defines a player as the avatar and gives them all the elements and other perks. \n--list: list all past avatars\n--clear: clear all past avatars", new String[] { "avatar", "av", "avy" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 1, 1))
			return;
		if (!hasPermission(sender))
			return;
		if (args.get(0).equalsIgnoreCase("--list")) {
			List<String> avatars = new ArrayList<>();
			for (File file : ProjectKorraRPG.getStorage().getFolder().listFiles()) {
				if (!file.getName().equals("AvatarCycle.yml")) {
					UUID uuid = UUID.fromString(file.getName().substring(0, 36));
					if (uuid != null) {
						OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
						avatars.add(player.getName());
					}
				}
			}
			sender.sendMessage(ChatColor.DARK_PURPLE + "Avatar Past Lives:");
			for (String s : avatars) {
				sender.sendMessage("- " + s);
			}
			return;
		} else if (args.get(0).equalsIgnoreCase("--clear")) {
			if (RPGMethods.clearPastAvatars()) {
				sender.sendMessage(ChatColor.GREEN + "Successfully cleared all past avatars!");
			} else {
				sender.sendMessage(ChatColor.RED + "Unknown error! Check console or try again later.");
			}
			return;
		}

		Player target = Bukkit.getPlayer(args.get(0));
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Player not found!");
			return;
		} else if (RPGMethods.hasBeenAvatar(target.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "Player has already been the avatar!");
			return;
		}
		RPGMethods.setAvatar(target.getUniqueId());
		Bukkit.broadcastMessage(ChatColor.WHITE + target.getName() + ChatColor.DARK_PURPLE + " has been declared avatar!");
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() >= 1)
			return new ArrayList<String>();
		List<String> players = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			players.add(p.getName());
		}
		return players;
	}
}
