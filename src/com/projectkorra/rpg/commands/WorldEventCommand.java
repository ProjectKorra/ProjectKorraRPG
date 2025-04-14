package com.projectkorra.rpg.commands;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldEventCommand extends RPGCommand {
	private final List<String> worldEvents;

	public WorldEventCommand() {
		super("event", "/bending rpg event start <Event>", "Erstellt einen WorldEvent", new String[]{"event", "e", "ev"});
		worldEvents = new ArrayList<>();
		fillWorldEvents();
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (args.size() < 2) {
			help(sender, true);
			return;
		}

		if (args.get(0).equalsIgnoreCase("start")) {
			String eventKey = args.get(1);

			File eventFile = new File(ProjectKorraRPG.getPlugin().getDataFolder(), "WorldEvents" + File.separator + eventKey + ".yml");
			if (!eventFile.exists()) {
				sender.sendMessage("WorldEvent file '" + eventKey + ".yml' does not exist.");
				return;
			}

			FileConfiguration config = YamlConfiguration.loadConfiguration(eventFile);

			String title = config.getString("Title");
			long duration = config.getLong("Duration", 5000L);
			String colorStr = config.getString("Color", "RED");

			Color color = convertStringToColor(colorStr);

			new WorldEvent(title, duration, color).startEvent();
		} else {
			help(sender, true);
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		List<String> completions = new ArrayList<>();

		if (args.isEmpty()) {
			completions.add("start");
			completions.add("stop");
		} else if (args.size() == 1 && args.getFirst().equalsIgnoreCase("start")) {
			completions.addAll(this.worldEvents);
		}
		return completions;
	}

	/**
	 * Fills the worldEvents list using file names from the WorldEvents folder
	 */
	private void fillWorldEvents() {
		worldEvents.clear();
		File eventsFolder = new File(ProjectKorraRPG.getPlugin().getDataFolder(), "WorldEvents");
		if (eventsFolder.exists() && eventsFolder.isDirectory()) {
			File[] files = eventsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
			if (files != null) {
				for (File file : files) {
					String fileName = file.getName();
					if (fileName.endsWith(".yml")) {
						fileName = fileName.substring(0, fileName.length() - 4);
					}
					worldEvents.add(fileName);
				}
			}
		}
	}

	/**
	 * Converts a string to a Bukkit Color. Supported colors are RED, GREEN, BLUE, YELLOW,
	 * PINK, PURPLE, and WHITE. Defaults to RED for unrecognized names.
	 */
	private Color convertStringToColor(String colorStr) {
		if (colorStr == null) {
			return Color.RED;
		}
		return switch (colorStr.toUpperCase()) {
			case "GREEN" -> Color.GREEN;
			case "BLUE" -> Color.BLUE;
			case "YELLOW" -> Color.YELLOW;
			case "PURPLE" -> Color.PURPLE;
			case "WHITE" -> Color.WHITE;
			default -> Color.RED;
		};
	}
}
