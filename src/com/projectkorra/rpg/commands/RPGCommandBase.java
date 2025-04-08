package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.command.PKCommand;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bukkit.ChatColor;

public class RPGCommandBase extends PKCommand {

	public RPGCommandBase() {
		super("rpg", "/bending rpg", "Base command for the RPG side plugin", new String[] {"rpg"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			sender.sendMessage(ChatColor.RED + "/bending rpg avatar <Player> " + ChatColor.YELLOW + "Create an Avatar.");
			sender.sendMessage(ChatColor.RED + "/bending rpg help <Command/Event> " + ChatColor.YELLOW + "Display help.");
			sender.sendMessage(ChatColor.RED + "/bending rpg worldevent <Argument> [Event] " + ChatColor.YELLOW + "Manipulate events.");
			return;
		}
		for (RPGCommand command : RPGCommand.instances.values()) {
			if (Arrays.asList(command.getAliases()).contains(args.get(0).toLowerCase())) {
				command.execute(sender, args.subList(1, args.size()));
			}
		}
	}
	
	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			List<String> l = new ArrayList<String>();
			for (RPGCommand cmd : RPGCommand.instances.values()) {
				l.add(cmd.getName());
			}
			Collections.sort(l, Comparator.nullsLast(Comparator.naturalOrder()));
			return l;
		}
		else
		for (RPGCommand cmd : RPGCommand.instances.values()) {
			if (Arrays.asList(cmd.getAliases()).contains(args.get(0).toLowerCase()) && sender.hasPermission("bending.command.rpg." + cmd.getName())) {
				List<String> newargs = new ArrayList<String>();
				for (int i = 1; i < args.size(); i++) {
					if (!(args.get(i).equals("") || args.get(i).equals(" ")) && args.size() >= 1)
					newargs.add(args.get(i));
				}
				return cmd.getTabCompletion(sender, newargs);
			}
		}
		return new ArrayList<String>();
	}
}
