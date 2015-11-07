package com.projectkorra.rpg.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.event.WorldEvent;

public class EventCommand extends PKCommand{

	public EventCommand() {
		super("worldevent", "/bending worldevent [help|start|current] [worldevent]", "This command gives information about or starts a world event.", new String[] {"worldevent", "worlde", "event", "we"});
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
				if (EventManager.getEvents(player.getWorld()).isEmpty()) {
					sender.sendMessage(ChatColor.RED + "There are no current world events happening in this world.");
				} else {
					sender.sendMessage(ChatColor.YELLOW + "Current events:");
					for (WorldEvent events : EventManager.getEvents(player.getWorld())) {
						String event = events.toString();
						sender.sendMessage(ChatColor.GOLD + event);
					}
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
				WorldEvent event = WorldEvent.getWorldEvent(name);
				if (event.equals(WorldEvent.FullMoon)) {
					if (!WaterMethods.isNight(player.getWorld())) {
						player.sendMessage(ChatColor.DARK_AQUA + "There cannot be a full moon during the day!");
						return;
					} else if (RPGMethods.isLunarEclipse(player.getWorld())) {
						player.sendMessage(ChatColor.DARK_AQUA + "The lunar eclipse is blocking your command!");
						return;
					} else if (RPGMethods.isFullMoon(player.getWorld())) {
						player.sendMessage(ChatColor.DARK_AQUA + "There is already a full moon out!");
						return;
					} else if (!RPGMethods.isFullMoon(player.getWorld())) {
						WorldEvent.start(player.getWorld(), event);
					}
				}
				if (event.equals(WorldEvent.LunarEclipse)) {
					if (!WaterMethods.isNight(player.getWorld())) {
						player.sendMessage(WaterMethods.getWaterColor() + "It is not night time, a lunar eclipse cannot happen during the day!");
						return;
					} else if (RPGMethods.isLunarEclipse(player.getWorld())) {
						player.sendMessage(WaterMethods.getWaterColor() + "There is already a lunar eclipse in progress!");
						return;
					} else if (!RPGMethods.isLunarEclipse(player.getWorld())) {
						WorldEvent.start(player.getWorld(), event);
					}
				} else if (event.equals(WorldEvent.SolarEclipse)) {
					if (WaterMethods.isNight(player.getWorld())) {
						player.sendMessage(FireMethods.getFireColor() + "It is not day time, a solar eclipse cannot happen during the day!");
						return;
					} else if (RPGMethods.isSozinsComet(player.getWorld())) {
						player.sendMessage(FireMethods.getFireColor() + "Starting a solar eclipse will have no effect with Sozin's comet in range!");
						return;
					} else if (RPGMethods.isSolarEclipse(player.getWorld())) {
						player.sendMessage(FireMethods.getFireColor() + "There is already a solar eclipse in progress!");
						return;
					} else if (!RPGMethods.isSolarEclipse(player.getWorld())) {
						WorldEvent.start(player.getWorld(), event);
					}
				} else if (event.equals(WorldEvent.SozinsComet)) {
					if (!RPGMethods.isSozinsComet(player.getWorld())) {
						WorldEvent.start(player.getWorld(), event);
					} else if (RPGMethods.isSozinsComet(player.getWorld())) {
						player.sendMessage(ChatColor.DARK_RED + "You cannot have two comets in the sky at the same time!");
					}
				}
			}
		}
	}

}
