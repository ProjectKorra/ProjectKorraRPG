package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.util.attribute.AttributeParser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
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

			List<String> displayMethodStringList = config.getStringList("DisplayMethod");

			List<WorldEvent.DISPLAY_METHOD> displayMethods = new ArrayList<>();

			for (String methodStr : displayMethodStringList) {
				try {
					displayMethods.add(WorldEvent.DISPLAY_METHOD.valueOf(methodStr.toUpperCase()));
				} catch (IllegalArgumentException ignored) {}
			}

			BarColor barColor = null;
			BarStyle barStyle = null;
			boolean smoothBossBar = true;

			if (displayMethods.contains(WorldEvent.DISPLAY_METHOD.BOSSBAR)) {
				barColor = convertStringToColor(config.getString("BossBarColor", "RED"));
				barStyle = convertStringToStyle(config.getString("BossBarStyle", "SOLID"));

				smoothBossBar = config.getBoolean("SmoothBossBar");
			}

			String startMessage = null;
			String stopMessage = null;

			if (displayMethods.contains(WorldEvent.DISPLAY_METHOD.CHAT)) {
				startMessage = config.getString("EventStartMessage");
				stopMessage = config.getString("EventStopMessage");
			}

			List<String> blackListedWorldsStringList = config.getStringList("BlackListedWorlds");
			List<World> blackListedWorlds = new ArrayList<>();

			if (!blackListedWorldsStringList.isEmpty()) {
				for (String worldName : blackListedWorldsStringList) {
					blackListedWorlds.add(Bukkit.getWorld(worldName));
				}
			}

			List<String> affectedElementsStringList = config.getStringList("AffectedElements");
			List<Element> affectedElements = new ArrayList<>();

			if (!affectedElementsStringList.isEmpty()) {
				for (String elementString : affectedElementsStringList) {
					affectedElements.add(Element.fromString(elementString.toUpperCase()));
				}
			}

			List<String> affectedAttributesStringList = config.getStringList("AffectedAttributes");
			List<Attribute> affectedAttributes = new ArrayList<>();

			if (!affectedAttributesStringList.isEmpty()) {
				for (String attributeStr : affectedAttributesStringList) {
					Attribute parsedAttribute = AttributeParser.parseAttribute(attributeStr);
					if (parsedAttribute != null) {
						affectedAttributes.add(parsedAttribute);
					} else {
						ProjectKorraRPG.getPlugin().getLogger().warning("Unknown attribute in config: " + attributeStr);
					}
				}
			}

			List<Ability> affectedAbilities = new ArrayList<>();

			WorldEvent worldEvent = new WorldEvent(title, duration, barColor, barStyle, startMessage, stopMessage, blackListedWorlds, affectedElements, affectedAttributes, affectedAbilities, displayMethods);

			if (displayMethods.contains(WorldEvent.DISPLAY_METHOD.BOSSBAR)) {
				worldEvent.setSmoothBossBar(smoothBossBar);
			}

			worldEvent.startEvent();

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
	private BarColor convertStringToColor(String colorStr) {
		if (colorStr == null) {
			return BarColor.RED;
		}
		return switch (colorStr.toUpperCase()) {
			case "GREEN" -> BarColor.GREEN;
			case "BLUE" -> BarColor.BLUE;
			case "YELLOW" -> BarColor.YELLOW;
			case "PURPLE" -> BarColor.PURPLE;
			case "WHITE" -> BarColor.WHITE;
			case "PINK" -> BarColor.PINK;
			default -> BarColor.RED;
		};
	}

	private BarStyle convertStringToStyle(String colorStr) {
		if (colorStr == null) {
			return BarStyle.SOLID;
		}

		return switch (colorStr.toUpperCase()) {
			case "SEGMENTED_6" -> BarStyle.SEGMENTED_6;
			case "SEGMENTED_10" -> BarStyle.SEGMENTED_10;
			case "SEGMENTED_12" -> BarStyle.SEGMENTED_12;
			case "SEGMENTED_20" -> BarStyle.SEGMENTED_20;
			default -> BarStyle.SOLID;
		};
	}
}
