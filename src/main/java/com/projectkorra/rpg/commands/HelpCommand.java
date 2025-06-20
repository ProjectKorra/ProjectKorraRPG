package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.Element;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class HelpCommand extends RPGCommand {
	private final String modifiers = ChatColor.GOLD + "Commands: <required> [optional]";

	public HelpCommand() {
		super("help", "/bending rpg help <Command/Event>", "Shows all helpful information for rpg", new String[]{"help", "h", "?"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		
		StringBuilder help = new StringBuilder(modifiers);
		for (RPGCommand command : RPGCommand.instances.values()) {
			help.append(ChatColor.YELLOW).append("\n").append(command.getProperUse());
		}
		
		if (args.isEmpty()) {
			sender.sendMessage(help.toString());
		} else if (args.size() == 1) {
			for (RPGCommand command : RPGCommand.instances.values()) {
				if (command instanceof HelpCommand) continue;
				if (Arrays.asList(command.getAliases()).contains(args.getFirst().toLowerCase())) {
					sender.sendMessage(ChatColor.GOLD + "Proper use: " + ChatColor.DARK_AQUA + command.getProperUse());
					sender.sendMessage(ChatColor.YELLOW + command.getDescription());
					if (command.getName().equalsIgnoreCase("avatar")) {
						if (!hasPermission(sender, "avatar"))
							return;
						sender.sendMessage(Element.AVATAR.getColor() + "Avatar - The avatar is the only bender who can wield all four elements. He or she is the bridge between the spirit and physical world, keeping balance in the physical world. The avatar is the most powerful bender, and the cycle for which element is to be the next avatar goes Fire, Air, Water, Earth, starting with the first firebending avatar, Wan");				
					}
					return;
				}
			}
		}
	}
	
	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (!args.isEmpty()) return new ArrayList<>();
		List<String> l = new ArrayList<>();
		for (RPGCommand cmd : RPGCommand.instances.values()) {
			if (!cmd.getName().equals("help")) {
				l.add(cmd.getName());
			}
		}
		l.sort(Comparator.nullsLast(Comparator.naturalOrder()));
		return l;
	}
}
