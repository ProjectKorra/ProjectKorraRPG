package com.projectkorra.rpg.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.rpg.player.RPGPlayer;

public class LevelBarCommand extends RPGCommand {

	public LevelBarCommand() {
		super("levelbar", "/b rpg levelbar <toggle / color [color]>", "Manipulate the bossbar showing level and xp", new String[] {"levelbar", "lb"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!isPlayer(sender)) {
			return;
		}
		
		RPGPlayer player = RPGPlayer.get((Player) sender);
		
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "Player has no data!");
			return;
		}
		
		if (args.get(0).equalsIgnoreCase("toggle")) {
			if (!correctLength(sender, args.size(), 1, 1)) {
				return;
			}
			
			player.getLevelBar().toggle();
			String toggle = "";
			if (player.getLevelBar().isVisible()) {
				toggle = ChatColor.GREEN + "ON";
			} else {
				toggle = ChatColor.RED + "OFF";
			}
			
			sender.sendMessage("Level bar toggled " + toggle);
		} else if (args.get(0).equalsIgnoreCase("color")) {
			if (!correctLength(sender, args.size(), 2, 2)) {
				return;
			}
			
			BarColor color = BarColor.valueOf(args.get(1).toUpperCase());
			
			if (color == null) {
				sender.sendMessage(ChatColor.RED + "Unknown color given, try red, blue, green, yellow, pink, purple, or white.");
				return;
			}
			
			player.getLevelBar().setColor(color);
			sender.sendMessage(ChatColor.GREEN + "Color set to " + color.toString() + "!");
		}
	}
	
	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			return Arrays.asList("toggle", "color");
		} else if (args.size() == 1 && args.get(0).equalsIgnoreCase("color")) {
			return Arrays.asList("red", "blue", "green", "yellow", "pink", "purple", "white");
		} else {
			return new ArrayList<>();
		}
	}
}
