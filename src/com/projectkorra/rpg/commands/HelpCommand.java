package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.worldevent.WorldEvent;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends RPGCommand{
	
	private String modifiers = ChatColor.GOLD + "Commands: <required> [optional]";

	public HelpCommand() {
		super("help", "/bending rpg help <command/worldevent>", "Shows all helpful information for rpg", new String[] {"help", "h", "?"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		
		StringBuilder help = new StringBuilder(modifiers);
		for (RPGCommand command : RPGCommand.instances.values()) {
			help.append(ChatColor.YELLOW + "\n" + command.getProperUse());
		}
		
		if (args.size() == 0) {
			sender.sendMessage(help.toString());
		} else if (args.size() == 1) {
			for (RPGCommand command : RPGCommand.instances.values()) {
				if (command instanceof HelpCommand) continue;
				if (Arrays.asList(command.getAliases()).contains(args.get(0).toLowerCase())) {
					sender.sendMessage(ChatColor.GOLD + "Proper use: " + ChatColor.DARK_AQUA + command.getProperUse());
					sender.sendMessage(ChatColor.YELLOW + command.getDescription());
					if (command.getName().equalsIgnoreCase("avatar")) {
						if (!hasPermission(sender, "avatar"))
							return;
						sender.sendMessage(Element.AVATAR.getColor() + "Avatar - The avatar is the only bender who can wield all four elements. He or she is the bridge between the spirit and physical world, keeping balance in the physical world. The avatar is the most powerful bender, and the cycle for which element is to be the next avatar goes Fire, Air, Water, Earth, starting with the first firebending avatar, Wan");				
					}
					return;
				}
				continue;
			}
			
			for (WorldEvent event : WorldEvent.getEvents()) {
				if (args.get(0).equalsIgnoreCase(event.getName()) || event.getAliases().contains(args.get(0).toLowerCase())) {
					sender.sendMessage(ChatColor.BOLD + event.getName());
					sender.sendMessage(event.getElement().getColor() + event.getDescription());
					return;
				}
			}
		}
	}
	
	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() >= 1) return new ArrayList<String>();
		List<String> l = new ArrayList<String>();
		for (RPGCommand cmd : RPGCommand.instances.values()) {
			if (!cmd.getName().equals("help")) {
				l.add(cmd.getName());
			}
		}
		Collections.sort(l);
		l.addAll(WorldEvent.getEventNames());
		return l;
	}
}
