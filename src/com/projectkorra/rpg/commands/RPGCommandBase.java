package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.command.PKCommand;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;

public class RPGCommandBase extends PKCommand {

	public RPGCommandBase() {
		super("rpg", "/bending rpg", "Base command for the RPG side plugin", new String[] { "rpg" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (args.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "/bending rpg avatar <player> " + ChatColor.YELLOW + "Create an Avatar.");
			sender.sendMessage(ChatColor.RED + "/bending rpg help <command/worldevent> " + ChatColor.YELLOW + "Display help.");
			sender.sendMessage(ChatColor.RED + "/bending rpg worldevent <argument> [worldevent] " + ChatColor.YELLOW + "Manipulate events.");
			return;
		}
		for (RPGCommand command : RPGCommand.instances.values()) {
//			if (Arrays.asList(command.getAliases()).contains(args.getFirst().toLowerCase())) {
//				command.execute(sender, args.subList(1, args.size()));
//			}
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.isEmpty()) {
			List<String> l = new ArrayList<>();
			for (RPGCommand cmd : RPGCommand.instances.values()) {
				l.add(cmd.getName());
			}
			Collections.sort(l);
			return l;
		} else
			for (RPGCommand cmd : RPGCommand.instances.values()) {
				if (Arrays.asList(cmd.getAliases()).contains(sender.hasPermission("bending.command.rpg." + cmd.getName()))) {
					List<String> newargs = new ArrayList<>();
					for (int i = 1; i < args.size(); i++) {
						if (!(args.get(i).isEmpty() || args.get(i).equals(" ")))
							newargs.add(args.get(i));
					}
					return cmd.getTabCompletion(sender, newargs);
				}
			}
		return new ArrayList<String>();
	}
}
