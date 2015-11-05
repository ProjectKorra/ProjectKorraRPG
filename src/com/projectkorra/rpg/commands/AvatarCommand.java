package com.projectkorra.rpg.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.rpg.RPGMethods;

public class AvatarCommand extends PKCommand{

	public AvatarCommand() {
		super("avatar", "/bending avatar [player]", "This command defines a player as the avatar and gives them all the elements and other perks.", new String[] {"avatar", "av", "a", "avy"});
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
			sender.sendMessage(ChatColor.RED + "Player has already been avatar!");
			return;
		} else {
			RPGMethods.setAvatar(target.getUniqueId());
			Bukkit.broadcastMessage(ChatColor.WHITE + target.getName() + GeneralMethods.getAvatarColor() + " has been declared avatar!");
		}
	}

}
