package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.projectkorra.storage.DBConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AvatarCommand extends RPGCommand{

	public AvatarCommand() {
		super("avatar", "/bending rpg avatar <Player>", "This command defines a player as the avatar and gives them all the elements and other perks.", new String[] {"avatar", "av", "avy"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 1, 1))
			return;
		if (ProjectKorraRPG.plugin.getAvatarManager() == null || !ProjectKorraRPG.plugin.getAvatarManager().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "Avatar system is not enabled!");
			return;
		}
		if (sender instanceof Player) {
			if (!hasPermission(sender))
				return;
		}

		if (args.get(0).equalsIgnoreCase("list")) {
			List<String> avatars =  ProjectKorraRPG.plugin.getAvatarManager().getPastLives();
			sender.sendMessage(ChatColor.BOLD + "Avatar Past Lives:");
			for (String s : avatars) {
				sender.sendMessage(s);
			}
			return;
		}
		Player target = Bukkit.getPlayer(args.get(0));
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Player not found!");
			return;
		} else {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(target);
			boolean succesful = ProjectKorraRPG.plugin.getAvatarManager().makeAvatar(target.getUniqueId());
			if (!succesful) {
				sender.sendMessage(ChatColor.RED + "Failed to declare avatar!");
				return;
			} else {
				sender.sendMessage(ChatColor.DARK_PURPLE + target.getName() + " has been declared the Avatar!");
			}
		} 
	}
	
	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() >= 1) return new ArrayList<String>();
		List<String> players = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			players.add(p.getName());
		}
		return players;
	}
}
