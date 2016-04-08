package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.event.FullMoonEvent;
import com.projectkorra.rpg.event.LunarEclipseEvent;
import com.projectkorra.rpg.event.SolarEclipseEvent;
import com.projectkorra.rpg.event.SozinsCometEvent;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventCommand extends RPGCommand {
	
	private String[] help = {"help", "h", "?"};
	private String[] current = {"current", "curr", "c"};
	private String[] end = {"end", "e", "cancel", "remove"};
	private String[] skip = {"skip", "sk"};
	private String[] start = {"start", "st", "strt", "begin"};

	public EventCommand() {
		super("worldevent", "/bending rpg worldevent <Current/End/Help/Skip/Start> [Event]", "Main command for anything dealing with RPG world events.", new String[] { "worldevent", "worlde", "event", "we" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 1, 2)) {
			return;
		} else if (args.size() == 1) {
			if (!hasPermission(sender)) {
				return;
			} else if (Arrays.asList(help).contains(args.get(0).toLowerCase())) {
				sender.sendMessage(ChatColor.YELLOW + getDescription());
				sender.sendMessage(ChatColor.YELLOW + "WorldEvents:");
				sender.sendMessage(Element.ICE.getColor() + "FullMoon - Makes Waterbending super enhanced");
				sender.sendMessage(Element.WATER.getColor() + "LunarEclipse - Makes Waterbending useless");
				sender.sendMessage(Element.FIRE.getColor() + "SolarEclipse - Makes Firebending useless");
				sender.sendMessage(Element.COMBUSTION.getColor() + "SozinsComet - Makes Firebending super enhanced");
			} else if (Arrays.asList(current).contains(args.get(0).toLowerCase())) {
				if (!isPlayer(sender))
					return;
				Player player = (Player) sender;
				if (!EventManager.marker.containsKey(player.getWorld()) || EventManager.marker.get(player.getWorld()) == "") {
					sender.sendMessage(ChatColor.RED + "There are no current world events happening in this world.");
				} else {
					sender.sendMessage(ChatColor.YELLOW + "Current event: " + EventManager.marker.get(player.getWorld()));
				}
			} else if (Arrays.asList(end).contains(args.get(0).toLowerCase())) {
				if (!isPlayer(sender))
					return;
				Player player = (Player) sender;
				if (!EventManager.marker.containsKey(player.getWorld()) || EventManager.marker.get(player.getWorld()) == "") {
					sender.sendMessage(ChatColor.RED + "There is no event to end at the moment.");
				} else {
					EventManager.endEvent(player.getWorld());
					sender.sendMessage(ChatColor.GREEN + "The event occupying this world has been ended.");
				}
			} else if (Arrays.asList(skip).contains(args.get(0).toLowerCase())) {
				if (!isPlayer(sender))
					return;
				Player player = (Player) sender;
				EventManager.skipper.put(player.getWorld(), true);
				sender.sendMessage(ChatColor.GREEN + "The next worldevent will be skipped.");
			}
		} else if (args.size() == 2) {
			if (!hasPermission(sender)) {
				return;
			} else if (Arrays.asList(start).contains(args.get(0).toLowerCase())) {
				if (!isPlayer(sender)) {
					return;
				}
				Player player = (Player) sender;
				if (player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
					player.sendMessage(ChatColor.RED + "Cannot start an Event in this world type!");
					return;
				}
				String name = args.get(1);
				if (name.equalsIgnoreCase("FullMoon")) {
					if (!WaterAbility.isNight(player.getWorld())) {
						player.sendMessage(Element.ICE.getColor() + "There cannot be a full moon during the day!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "LunarEclipse")) {
						player.sendMessage(Element.ICE.getColor() + "The lunar eclipse is blocking your command!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "FullMoon")) {
						player.sendMessage(Element.ICE.getColor() + "There is already a full moon out!");
						return;
					} else {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new FullMoonEvent(player.getWorld()));
					}
				} else if (name.equalsIgnoreCase("LunarEclipse")) {
					if (!WaterAbility.isNight(player.getWorld())) {
						player.sendMessage(Element.WATER.getColor() + "It is not night time, a lunar eclipse cannot happen during the day!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "LunarEclipse")) {
						player.sendMessage(Element.WATER.getColor() + "There is already a lunar eclipse in progress!");
						return;
					} else {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new LunarEclipseEvent(player.getWorld()));
					}
				} else if (name.equalsIgnoreCase("SolarEclipse")) {
					if (WaterAbility.isNight(player.getWorld())) {
						player.sendMessage(Element.FIRE.getColor() + "It is not day time, a solar eclipse cannot happen during the day!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "SozinsComet")) {
						player.sendMessage(Element.FIRE.getColor() + "Starting a solar eclipse will have no effect with Sozin's comet in range!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "SolarEclipse")) {
						player.sendMessage(Element.FIRE.getColor() + "There is already a solar eclipse in progress!");
						return;
					} else {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new SolarEclipseEvent(player.getWorld()));
					}
				} else if (name.equalsIgnoreCase("SozinsComet")) {
					if (!RPGMethods.isHappening(player.getWorld(), "SozinsComet")) {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new SozinsCometEvent(player.getWorld()));
					} else {
						player.sendMessage(Element.COMBUSTION.getColor() + "You cannot have two comets in the sky at the same time!");
					}
				}
			}
		}
	}
	
	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() >= 2) return new ArrayList<String>();
		List<String> l = new ArrayList<String>();
		if (args.size() == 0) {
			l = Arrays.asList(new String[] {"current", "end", "help", "skip", "start"});
		} else {
			if (Arrays.asList(start).contains(args.get(0).toLowerCase())) {
				l = Arrays.asList(new String[] {"FullMoon", "LunarEclipse", "SolarEclipse", "SozinsComet"});
			}
		}
		return l;
	}

}
