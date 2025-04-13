package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.command.PKCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RPGCommandBase extends PKCommand {

	public RPGCommandBase() {
		super("rpg", "/bending rpg", "Basisbefehl für das RPG-Plugin", new String[]{"rpg"});

		new WorldEventCommand();
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (args.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "/bending rpg <subcommand> <args>");
			sender.sendMessage(ChatColor.RED + "Beispiele:");
			sender.sendMessage(ChatColor.RED + " /bending rpg avatar <player> " + ChatColor.YELLOW + "Erstellt einen Avatar.");
			sender.sendMessage(ChatColor.RED + " /bending rpg event start <Event> " + ChatColor.YELLOW + "Startet ein WorldEvent.");
			return;
		}

		String sub = args.getFirst().toLowerCase();

		for (RPGCommand command : RPGCommand.instances.values()) {
			if (Arrays.asList(command.getAliases()).contains(sub)) {
				command.execute(sender, args.subList(1, args.size()));
				return;
			}
		}
		sender.sendMessage(ChatColor.RED + "Unbekanntes Subcommand. Nutze '/bending rpg help' für Hilfe.");
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
		} else {
			String sub = args.getFirst().toLowerCase();
			for (RPGCommand cmd : RPGCommand.instances.values()) {
				if (Arrays.asList(cmd.getAliases()).contains(sub) && sender.hasPermission("bending.command.rpg." + cmd.getName())) {
					List<String> newArgs = new ArrayList<>();
					for (int i = 1; i < args.size(); i++) {
						if (!args.get(i).trim().isEmpty()) {
							newArgs.add(args.get(i));
						}
					}
					return cmd.getTabCompletion(sender, newArgs);
				}
			}
		}
		return new ArrayList<>();
	}
}
