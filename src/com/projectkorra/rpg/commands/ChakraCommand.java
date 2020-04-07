package com.projectkorra.rpg.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.rpg.player.ChakraStats.Chakra;
import com.projectkorra.rpg.player.RPGPlayer;

public class ChakraCommand extends RPGCommand {

	public ChakraCommand() {
		super("chakra", "/b rpg chakra <chakra> <add/remove/clear/max> [user]", "Command for manipulating chakra points.", new String[] {"chakra", "chk", "ck"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 1, 3)) {
			return;
		}
		
		Player target = null;

		Chakra chakra;
		
		try {
			chakra = Chakra.valueOf(args.get(0).toUpperCase());
		} catch (IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + "Unknown chakra given!");
			sender.sendMessage(Chakra.list());
			return;
		}
		
		if (args.size() == 1) {
			sender.sendMessage("The " + chakra.getDisplay() + ChatColor.WHITE + " chakra " + chakra.getHelp());
			return;
		} else if (args.size() == 2) {
			if (!isPlayer(sender) || !hasPermission(sender)) {
				return;
			}
			
			target = (Player) sender;
		} else if (args.size() == 3) {
			if (!hasPermission(sender, "others")) {
				return;
			}
			
			target = Bukkit.getPlayer(args.get(2));
		}
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Unknown player!");
			return;
		}
		
		RPGPlayer player = RPGPlayer.get(target);
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "Player has no data!");
			return;
		}
		
		String cmd = args.get(1);
		boolean pass = false;
		String version;
		
		if (cmd.equalsIgnoreCase("add")) {
			pass = player.addPoint(chakra);
			version = "added a point to";
		} else if (cmd.equalsIgnoreCase("remove")) {
			pass = player.removePoint(chakra);
			version = "removed a point from";
		} else if (cmd.equalsIgnoreCase("clear")) {
			pass = true;
			version = "cleared";
			player.getStats().clear(chakra);
		} else if (cmd.equalsIgnoreCase("max")) {
			pass = true;
			version = "added " + player.maxPoints(chakra) + " points to";
		} else {
			help(sender, false);
			return;
		}
		
		String success = "";
		if (pass) {
			success = ChatColor.GREEN + "Successfully ";
		} else {
			success = ChatColor.RED + "Unsuccessfully ";
		}
		
		if (sender.equals(target)) {
			sender.sendMessage(success + ChatColor.WHITE + version + " the " + chakra.getDisplay() + ChatColor.WHITE + " chakra!");
		} else {
			sender.sendMessage(success + ChatColor.WHITE + version + " the " + chakra.getDisplay() + ChatColor.WHITE + " chakra for " + target.getName() + "!");
			
			if (pass) {
				String senderName = "CONSOLE";
				if (isPlayer(sender)) {
					senderName = ((Player) sender).getName();
				}
				target.sendMessage(senderName + " has successfully " + version + " your " + chakra.getDisplay() + ChatColor.WHITE + " chakra!");
			}
		}
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			return Arrays.asList("air", "earth", "fire", "water", "light");
		} else if (args.size() == 1) {
			return Arrays.asList("add", "remove", "clear", "max");
		} else if (args.size() == 2 && hasPermission(sender, "others")) {
			return Arrays.asList(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));
		} else {
			return new ArrayList<>();
		}
	}
}
