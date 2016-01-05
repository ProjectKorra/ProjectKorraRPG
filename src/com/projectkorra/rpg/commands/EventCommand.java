package com.projectkorra.rpg.commands;

import com.projectkorra.rpg.event.LunarEclipseEvent;
import com.projectkorra.rpg.event.SolarEclipseEvent;
import com.projectkorra.rpg.event.SozinsCometEvent;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.event.FullMoonEvent;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EventCommand extends PKCommand{

	public EventCommand() {
		super("worldevent", "/bending worldevent help|start|current [worldevent]", "Main command for anything dealing with RPG world events.", new String[] {"worldevent", "worlde", "event", "we"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 1, 2)) {
			return;
		} else if (args.size() == 1) {
			if (!hasPermission(sender)) {
				return;
			} else if (args.get(0).equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.YELLOW + getDescription());
				sender.sendMessage(ChatColor.YELLOW + "WorldEvents:");
				sender.sendMessage(ChatColor.DARK_AQUA + "FullMoon");
				sender.sendMessage(ChatColor.AQUA + "LunarEclipse");
				sender.sendMessage(ChatColor.RED + "SolarEclipse");
				sender.sendMessage(ChatColor.DARK_RED + "SozinsComet");
			} else if (args.get(0).equalsIgnoreCase("current")) {
				if (!isPlayer(sender)) return;
				Player player = (Player)sender;
				if (EventManager.marker.get(player.getWorld()) == "") {
					sender.sendMessage(ChatColor.RED + "There are no current world events happening in this world.");
				} else {
					sender.sendMessage(ChatColor.YELLOW + "Current event:" + EventManager.marker.get(player.getWorld()));
				}
			}
		} else if (args.size() == 2) {
			if (!hasPermission(sender)) {
				return;
			} else if (args.get(0).equalsIgnoreCase("start")) {
				if (!isPlayer(sender)) {
					return;
				}
				Player player = (Player)sender;
				String name = args.get(1);
				if (name.equalsIgnoreCase("FullMoon")) {
					if (!WaterMethods.isNight(player.getWorld())) {
						player.sendMessage(ChatColor.DARK_AQUA + "There cannot be a full moon during the day!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "LunarEclipse")) {
						player.sendMessage(ChatColor.DARK_AQUA + "The lunar eclipse is blocking your command!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "FullMoon")) {
						player.sendMessage(ChatColor.DARK_AQUA + "There is already a full moon out!");
						return;
					} else {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new FullMoonEvent(player.getWorld()));
					}
				} else if (name.equalsIgnoreCase("LunarEclipse")) {
					if (!WaterMethods.isNight(player.getWorld())) {
						player.sendMessage(WaterMethods.getWaterColor() + "It is not night time, a lunar eclipse cannot happen during the day!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "LunarEclipse")) {
						player.sendMessage(WaterMethods.getWaterColor() + "There is already a lunar eclipse in progress!");
						return;
					} else {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new LunarEclipseEvent(player.getWorld()));
					}
				} else if (name.equalsIgnoreCase("SolarEclipse")) {
					if (WaterMethods.isNight(player.getWorld())) {
						player.sendMessage(FireMethods.getFireColor() + "It is not day time, a solar eclipse cannot happen during the day!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "SozinsComet")) {
						player.sendMessage(FireMethods.getFireColor() + "Starting a solar eclipse will have no effect with Sozin's comet in range!");
						return;
					} else if (RPGMethods.isHappening(player.getWorld(), "SolarEclipse")) {
						player.sendMessage(FireMethods.getFireColor() + "There is already a solar eclipse in progress!");
						return;
					} else {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new SolarEclipseEvent(player.getWorld()));
					}
				} else if (name.equalsIgnoreCase("SozinsComet")) {
					if (!RPGMethods.isHappening(player.getWorld(), "SozinsComet")) {
						ProjectKorra.plugin.getServer().getPluginManager().callEvent(new SozinsCometEvent(player.getWorld()));
					} else {
						player.sendMessage(ChatColor.DARK_RED + "You cannot have two comets in the sky at the same time!");
					}
				}
			}
		}
	}

}
