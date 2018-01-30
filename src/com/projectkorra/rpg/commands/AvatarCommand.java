package com.projectkorra.rpg.commands;

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
		if (!hasPermission(sender))
			return;
		if (args.get(0).equalsIgnoreCase("list")) {
			List<String> avatars = new ArrayList<>();
			ResultSet rs = DBConnection.sql.readQuery("SELECT player FROM pk_avatars");
			try {
				while (rs.next()) {
					if (avatars.contains(rs.getString(1))) continue;
					avatars.add(rs.getString(1));
				}
				Statement stmt = rs.getStatement();
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			sender.sendMessage("Avatar Past Lives:");
			for (String s : avatars) {
				sender.sendMessage(s);
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
		if (args.size() >= 1) return new ArrayList<String>();
		List<String> players = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			players.add(p.getName());
		}
		return players;
	}
}
