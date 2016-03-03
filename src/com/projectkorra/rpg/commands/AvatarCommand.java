package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.rpg.RPGMethods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AvatarCommand extends PKCommand{

	public AvatarCommand() {
		super("avatar", "/bending avatar [player]", "This command defines a player as the avatar and gives them all the elements and other perks.", new String[] {"avatar", "av", "avy"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 1, 1))
			return;
		if (!hasPermission(sender))
			return;
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
}
