package com.projectkorra.rpg.commands;

import net.md_5.bungee.api.ChatColor;

import com.projectkorra.projectkorra.command.PKCommand;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class RPGCommandBase extends PKCommand {

	public RPGCommandBase() {
		super("RPG", "/bending RPG", "Base command for the RPG side plugin", new String[] {"rpg"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			sender.sendMessage(ChatColor.RED + "/bending rpg help");
			return;
		}
		for (RPGCommand command : RPGCommand.instances.values()) {
			if (Arrays.asList(command.getAliases()).contains(args.get(0).toLowerCase())) {
				command.execute(sender, args.subList(1, args.size()));
			}
		}
	}
}
