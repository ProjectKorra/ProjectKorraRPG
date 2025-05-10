package com.projectkorra.rpg.modules.worldevents.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorldEventCommand extends RPGCommand {
	private List<String> worldEvents;

	public WorldEventCommand() {
		super("event", "/bending rpg event start <Event>", "Starts a world event", new String[]{"event", "e", "ev"});
		fillWorldEvents();
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("Console may not execute this type of command!");
			return;
		}

		if (args.size() < 2) {
			help(sender, true);
			return;
		}

		if (args.get(0).equalsIgnoreCase("start")) {
			WorldEvent we = WorldEvent.getAllEvents().get(args.get(1).toLowerCase());

			if (we == null) {
				sender.sendMessage("WorldEvent '" + args.get(1) + "' not found.");
			} else {
				we.startEvent(player.getWorld());
			}

		} else if (args.get(0).equalsIgnoreCase("stop")) {
			if (args.get(1) != null) {
				WorldEvent we = WorldEvent.getAllEvents().get(args.get(1).toLowerCase());

				if (we == null) {
					sender.sendMessage("WorldEvent '" + args.get(1) + "' not found.");
				} else {
					we.stopEvent();
				}

			} else {
				for (WorldEvent worldEvent : WorldEvent.getActiveEvents()) {
					if (worldEvent.getWorld() == player.getWorld()) {
						worldEvent.stopEvent();
					}
				}
			}
		} else {
			help(sender, true);
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.isEmpty()) {
			return Arrays.asList("start", "stop");
		}
		if (args.size() == 1 && args.getFirst().equalsIgnoreCase("start")) {
			return worldEvents;
		}
		return Collections.emptyList();
	}

	private void fillWorldEvents() {
		worldEvents = new ArrayList<>(WorldEvent.getAllEvents().keySet());
		Collections.sort(worldEvents);
	}
}
