package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.Element;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HelpCommand extends RPGCommand{
	
	private String[] fullmoon = {"fullmoon", "fm", "fmoon", "fullm"}; 
	private String[] lunar = {"lunareclipse", "le", "leclipse", "lunare"}; 
	private String[] solar = {"solareclipse", "se", "seclipse", "solare"}; 
	private String[] sozins = {"sozinscomet", "sc", "sozins", "sozinsc", "scomet", "comet"};
	private String modifiers = ChatColor.GOLD + "Commands: <required> [optional]";

	public HelpCommand() {
		super("help", "/bending rpg help <Command/Event>", "Shows all helpful information for rpg", new String[] {"help", "h", "?"});
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
			if (Arrays.asList(fullmoon).contains(args.get(0).toLowerCase())) {
				if (!hasPermission(sender, fullmoon[1]))
					return;
				sender.sendMessage(Element.ICE.getColor() + "Full Moon - This is a world event in which the moon is full, enhancing the power of waterbending by a large amount.");
			} else if (Arrays.asList(lunar).contains(args.get(0).toLowerCase())) {
				if (!hasPermission(sender, lunar[1]))
					return;
				sender.sendMessage(Element.WATER.getColor() + "Lunar Eclipse - This is a world event in which the moon is unable to be seen in the night sky, causing waterbending to lose all it's power.");				
			} else if (Arrays.asList(solar).contains(args.get(0).toLowerCase())) {
				if (!hasPermission(sender, solar[1]))
					return;
				sender.sendMessage(Element.FIRE.getColor() + "Solar Eclipse - This is a world event in which the sun is unable to be seen in the day sky, causing firebending to lose all it's power.");				
			} else if (Arrays.asList(sozins).contains(args.get(0).toLowerCase())) {
				if (!hasPermission(sender, sozins[1]))
					return;
				sender.sendMessage(Element.COMBUSTION.getColor() + "Sozins Comet - This is a world event in which a comet passes by the earth, enhancing firebending by a large amount.");				
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
		Collections.sort(l, Comparator.nullsLast(Comparator.naturalOrder()));
		l.addAll(Arrays.asList(new String[] {"FullMoon", "LunarEclipse", "SolarEclipse", "SozinsComet"}));
		return l;
	}
}
