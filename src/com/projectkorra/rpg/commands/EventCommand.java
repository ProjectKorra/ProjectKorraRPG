package com.projectkorra.rpg.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.worldevent.WorldEvent;

public class EventCommand extends RPGCommand {

	private String[] current = { "current", "curr", "c" };
	private String[] end = { "end", "e", "cancel", "remove" };
	private String[] skip = { "skip", "sk" };
	private String[] noskip = {"noskip", "nsk", "-skip"};
	private String[] start = { "start", "st", "strt", "begin" };

	public EventCommand() {
		super("worldevent", "/bending rpg worldevent <current/end/skip/noskip/start> [worldevent]", "Main command for anything dealing with RPG world events.", new String[] { "worldevent", "worlde", "event", "we" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 1, 2)) {
			return;
		} else if (!hasPermission(sender)) {
			return;
		} else if (!isPlayer(sender)) {
			return;
		}

		Player player = (Player) sender;
		World world = player.getWorld();

		if (Arrays.asList(current).contains(args.get(0).toLowerCase())) {
			if (!hasPermission(sender, "current")) {
				return;
			}
			
			if (ProjectKorraRPG.getEventManager().getEventsHappening(world).isEmpty()) {
				sender.sendMessage(ChatColor.RED + "There are no current WorldEvents happening in this world.");
			} else if (args.size() == 1) {
				sender.sendMessage(ChatColor.YELLOW + "Current WorldEvents: ");
				
				for (WorldEvent event : ProjectKorraRPG.getEventManager().getEventsHappening(world)) {
					sender.sendMessage(event.getElement().getColor() + "- " + event.getName());
				}
			} else if (args.size() == 2) {
				String name = args.get(1);
				WorldEvent event = WorldEvent.get(name);
				
				if (event == null) {
					sender.sendMessage(ChatColor.RED + "That is not a known WorldEvent!");
				} else if (!ProjectKorraRPG.getEventManager().isHappening(world, event)) {
					sender.sendMessage(ChatColor.RED + "That event is currently not happening in this world!");
				} else {
					sender.sendMessage(ChatColor.GOLD + "That event is currently happening in this world!");
				}
			}
		} else if (Arrays.asList(end).contains(args.get(0).toLowerCase())) {
			if (!hasPermission(sender, "end")) {
				return;
			}
			
			if (ProjectKorraRPG.getEventManager().getEventsHappening(world).isEmpty()) {
				sender.sendMessage(ChatColor.RED + "There is no WorldEvent to end at the moment.");
			} else if (args.size() == 1) {
				for (WorldEvent event : WorldEvent.getEvents()) {
					ProjectKorraRPG.getEventManager().endEvent(world, event);
				}
				
				sender.sendMessage(ChatColor.GOLD + "All WorldEvents have been ended!");
			} else if (args.size() == 2) {
				String name = args.get(1);
				WorldEvent event = WorldEvent.get(name);
				
				if (event == null) {
					sender.sendMessage(ChatColor.RED + "That is not a known WorldEvent!");
				} else if (!ProjectKorraRPG.getEventManager().isHappening(world, event)) {
					sender.sendMessage(ChatColor.RED + "That event is currently not happening in this world!");
				} else {
					ProjectKorraRPG.getEventManager().endEvent(world, event);
					sender.sendMessage(ChatColor.GOLD + "You have ended the " + event.getName() + " event!");
				}
			}
		} else if (Arrays.asList(skip).contains(args.get(0).toLowerCase())) {
			if (!hasPermission(sender, "skip")) {
				return;
			}
			
			if (args.size() == 1) {
				for (WorldEvent event : WorldEvent.getEvents()) {
					ProjectKorraRPG.getEventManager().setSkipping(world, event, true);
				}
				
				sender.sendMessage(ChatColor.GOLD + "All WorldEvents have been set to be skipped, if they weren't already!");
			} else if (args.size() == 2) {
				String name = args.get(1);
				WorldEvent event = WorldEvent.get(name);
				
				if (event == null) {
					sender.sendMessage(ChatColor.RED + "That is not a known WorldEvent!");
				} else {
					if (ProjectKorraRPG.getEventManager().setSkipping(world, event, true)) {
						sender.sendMessage(ChatColor.GOLD + "You have set to skip the next " + event.getName() + " event!");
					} else {
						sender.sendMessage(ChatColor.RED + "Someone has already set to skip the next " + event.getName() + " event!");
					}
				}
			}
		} else if (Arrays.asList(noskip).contains(args.get(0).toLowerCase())) {
			if (!hasPermission(sender, "noskip")) {
				return;
			}
			
			if (args.size() == 1) {
				for (WorldEvent event : WorldEvent.getEvents()) {
					ProjectKorraRPG.getEventManager().setSkipping(world, event, false);
				}
				
				sender.sendMessage(ChatColor.GOLD + "All WorldEvents have been set to not be skipped, if they already were!");
			} else if (args.size() == 2) {
				String name = args.get(1);
				WorldEvent event = WorldEvent.get(name);
				
				if (event == null) {
					sender.sendMessage(ChatColor.RED + "That is not a known WorldEvent!");
				} else {
					if (ProjectKorraRPG.getEventManager().setSkipping(world, event, false)) {
						sender.sendMessage(ChatColor.GOLD + "You have set to not skip the next " + event.getName() + " event!");
					} else {
						sender.sendMessage(ChatColor.RED + "The " + event.getName() + " event was not set to skip!");
					}
				}
			}
		} else if (Arrays.asList(start).contains(args.get(0).toLowerCase())) {
			if (!hasPermission(sender, "start")) {
				return;
			}
			
			if (world.getEnvironment().equals(World.Environment.NETHER) || world.getEnvironment().equals(World.Environment.THE_END)) {
				player.sendMessage(ChatColor.RED + "Cannot start a WorldEvent in this world type!");
				return;
			}
			
			if (args.size() == 1) {
				sender.sendMessage(ChatColor.RED + "You must specify an event to start!");
				return;
			}
			
			String name = args.get(1);
			WorldEvent event = WorldEvent.get(name);
			
			if (event == null) {
				sender.sendMessage(ChatColor.RED + "That is not a known WorldEvent!");
			} else if (ProjectKorraRPG.getEventManager().isHappening(world, event)) {
				sender.sendMessage(ChatColor.RED + "That WorldEvent is already happening!");
			} else {
				ProjectKorraRPG.getEventManager().startEvent(world, event);
				sender.sendMessage(ChatColor.GOLD + "Successfully started the " + event.getName() + " event!");
			}
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() >= 2)
			return new ArrayList<>();
		List<String> l = new ArrayList<>();
		if (args.size() == 0) {
			l = Arrays.asList("current", "end", "help", "skip", "start" );
		} else {
			if (Arrays.asList(start).contains(args.get(0).toLowerCase())) {
				l = new ArrayList<>(WorldEvent.getEventNames());
			}
		}
		return l;
	}

}
