package com.projectkorra.rpg.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.player.ChakraStats.Chakra;
import com.projectkorra.rpg.player.RPGPlayer;
import com.projectkorra.rpg.util.XPControl;

public class WhoCommand extends RPGCommand {

	public WhoCommand() {
		super("who", "/bending rpg who [user]", "List information about the target. Target refers to command sender or [user] if given.", new String[] {"who", "whois"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender)) {
			return;
		} else if (!correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		
		Player target = null;
		
		if (args.size() == 0) {
			if (!isPlayer(sender)) {
				return;
			}
			
			target = (Player) sender;
		} else {
			target = Bukkit.getPlayer(args.get(0));
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
		
		String tier = player.getCurrentTier().getDisplay();
		String level = "Level " + ChatColor.DARK_AQUA + player.getLevel();
		String xp = ChatColor.DARK_AQUA + "" + player.getXP() + ChatColor.WHITE + " XP";
		String next = player.getLevel() < 40 ? (" (" + ChatColor.DARK_AQUA + "" + (XPControl.getXPRequired(player.getLevel()) - player.getXP()) + " xp till level " + (player.getLevel() + 1) + ChatColor.WHITE + ")") : "";
		
		sender.sendMessage(ChatColor.BOLD + target.getName() + ChatColor.WHITE + " (" + tier + ChatColor.WHITE + ")");
		sender.sendMessage(level + ChatColor.WHITE + ", " + xp + next);
		sender.sendMessage("Chakra Points:");
		for (Chakra chakra : Chakra.values()) {
			sender.sendMessage("- " + player.getStats().getPoints(chakra) + " " + chakra.getDisplay());
		}
		
		if (RPGMethods.isCurrentAvatar(target.getUniqueId())) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Current Avatar");
		} else if (RPGMethods.hasBeenAvatar(target.getUniqueId())) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Past Life Avatar");
		}
	}
	
	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			return Arrays.asList(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));
		} else {
			return new ArrayList<>();
		}
	}
}
