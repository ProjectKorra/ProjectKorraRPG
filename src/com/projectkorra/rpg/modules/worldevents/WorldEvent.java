package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.util.display.IWorldEventDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.bossbar.BossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.bossbar.WorldEventBossBar;
import com.projectkorra.rpg.modules.worldevents.util.WorldEventScheduler;
import com.projectkorra.rpg.modules.worldevents.util.display.chat.ChatDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.none.NoDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.scoreboard.ScoreboardDisplay;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.naming.Name;
import java.io.File;
import java.util.*;

public class WorldEvent {
	private static HashMap<String, WorldEvent> ALL_EVENTS = new HashMap<>();
	private static HashSet<WorldEvent> ACTIVE_EVENTS = new HashSet<>();
	private static HashSet<Player> AFFECTED_PLAYERS = new HashSet<>();

	private List<IWorldEventDisplay> displayMethods;

	private final String key;
	private String title;
	private long duration;

	private World world;

	private List<World> disabledWorlds;

	private WorldEventBossBar worldEventBossBar;
	private final FileConfiguration config;

	List<String> affectedElements;

	private final NamespacedKey worldEventNamespacedKey;

	public WorldEvent(String key, String title, long duration, List<World> disabledWorlds, List<IWorldEventDisplay> displayMethods, FileConfiguration config, List<String> affectedElements) {
		this.key = key;
		this.title = title;
		this.duration = duration;
		this.disabledWorlds = disabledWorlds;
		this.displayMethods = (displayMethods == null || displayMethods.isEmpty())
				? Collections.singletonList(new NoDisplay())
				: new ArrayList<>(displayMethods);
		this.config = config;
		this.affectedElements = affectedElements;

		this.worldEventNamespacedKey = new NamespacedKey(ProjectKorraRPG.getPlugin(), key);
	}

	public void startEvent() {
		Bukkit.getPluginManager().callEvent(new WorldEventStartEvent(this));

		ACTIVE_EVENTS.add(this);

		Set<String> disabledWorlds = new HashSet<>();
		if (this.disabledWorlds != null) {
			for (World w : this.disabledWorlds) {
				if (w != null) {
					disabledWorlds.add(w.getName());
				}
			}
		}

		// Add all online players whose worlds are not disabled
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!disabledWorlds.contains(player.getWorld().getName())) {
				AFFECTED_PLAYERS.add(player);
				if (config.getBoolean("PlayEventStartSound")) {
					Sound eventStartSound = Sound.valueOf(config.getString("EventStart.Sound"));
					float volume = Float.parseFloat(config.getString("EventStart.Volume"));
					float pitch = Float.parseFloat(config.getString("EventStart.Pitch"));

					player.getWorld().playSound(player.getLocation(), eventStartSound, volume, pitch);
				}
			}
		}

		// Start the display for the event
		for (IWorldEventDisplay display : this.displayMethods) {
			display.startDisplay(this);
		}

		WorldEventScheduler.startWorldEventSchedule(this);
	}

	public void updateDisplay(double progress) {
		for (IWorldEventDisplay display : this.displayMethods) {
			display.updateDisplay(this, progress);
		}
	}

	public void stopEvent() {
		if (ACTIVE_EVENTS.contains(this)) {
			Bukkit.getPluginManager().callEvent(new WorldEventStopEvent(this));

			ACTIVE_EVENTS.remove(this);

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!disabledWorlds.contains(player.getWorld())) {
					if (config.getBoolean("PlayEventStopSound")) {
						Sound eventStopSound = Sound.valueOf(config.getString("EventStop.Sound"));
						float volume = Float.parseFloat(config.getString("EventStop.Volume"));
						float pitch = Float.parseFloat(config.getString("EventStop.Pitch"));

						player.getWorld().playSound(player.getLocation(), eventStopSound, volume, pitch);
					}
				}
			}

			// Stop the display for the event
			for (IWorldEventDisplay display : this.displayMethods) {
				display.stopDisplay(this);
			}
		} else {
			ProjectKorraRPG.getLog().info("WorldEvent isn't active therefore can't be stopped.");
		}
	}

	public static void initAllWorldEvents() {
		File worldEventsFolder = new File(ProjectKorraRPG.getPlugin().getDataFolder(), "WorldEvents");
		if (!worldEventsFolder.exists() || !worldEventsFolder.isDirectory()) {
			ProjectKorraRPG.getPlugin().getLogger().warning("WorldEvents folder was not found!");
		}

		File[] worldEventsFiles = worldEventsFolder.listFiles(((dir, name) -> name.endsWith(".yml")));
		if (worldEventsFiles == null ||worldEventsFiles.length == 0) {
			ProjectKorraRPG.getPlugin().getLogger().info("No WorldEvents were found.");
			return;
		}

		// Iterate through all WorldEvent configurations
		Arrays.stream(worldEventsFiles).parallel().forEach(file -> {
			String eventKey = file.getName().toLowerCase().replace(".yml", "");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			String eventTitle = config.getString("Title", "&cConfig Title not defined!");
			long duration = config.getLong("Duration", 1000);

			List<IWorldEventDisplay> displayMethods = new ArrayList<>();

			// BossBar-Display
			if (config.getBoolean("DisplayMethods.BossBar.Enabled", false)) {
				BarColor bossBarColor = RPGMethods.convertStringToColor(config.getString("DisplayMethods.BossBar.Color", "RED"));
				BarStyle bossBarStyle = RPGMethods.convertStringToStyle(config.getString("DisplayMethods.BossBar.Style", "SOLID"));
				boolean smoothBossBar = config.getBoolean("DisplayMethods.BossBar.Smooth", true);

				displayMethods.add(new BossBarDisplay(eventTitle, bossBarColor, bossBarStyle, smoothBossBar));
			}

			// Chat-Display
			if (config.getBoolean("DisplayMethods.Chat.Enabled", false)) {
				String eventStartMessage = config.getString("DisplayMethods.Chat.EventStartMessage", "&cEventStartMessage not defined!");
				String eventStopMessage = config.getString("DisplayMethods.Chat.EventStopMessage", "&cEventStopMessage not defined!");

				displayMethods.add(new ChatDisplay(eventStartMessage, eventStopMessage));
			}

			// Scoreboard - Display
			if (config.getBoolean("DisplayMethods.Scoreboard.Enabled", false)) {
				displayMethods.add(new ScoreboardDisplay());
			}

			// Parse Disabled Worlds
			List<String> disabledWorldsStringList = config.getStringList("DisabledWorlds");
			List<World> disabledWorlds = new ArrayList<>();
			if (!disabledWorldsStringList.isEmpty()) {
				for (String worldName : disabledWorldsStringList) {
					World w = Bukkit.getWorld(worldName);
					if (w != null) {
						disabledWorlds.add(w);
					}
				}
			}

			List<String> affectedElements = config.getStringList("AffectedElements");

			if (affectedElements.isEmpty()) {
				affectedElements = new ArrayList<>();
			}

			WorldEvent worldEvent = new WorldEvent(
					eventKey,
					eventTitle,
					duration,
					disabledWorlds,
					displayMethods,
					config,
					affectedElements
			);

			ALL_EVENTS.put(eventKey, worldEvent);
		});
	}

	public static HashMap<String, WorldEvent> getAllEvents() {
		return ALL_EVENTS;
	}

	public static HashSet<WorldEvent> getActiveEvents() {
		return ACTIVE_EVENTS;
	}

	public static HashSet<Player> getAffectedPlayers() {
		return AFFECTED_PLAYERS;
	}

	public List<IWorldEventDisplay> getDisplayMethods() {
		return displayMethods;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return this.title;
	}

	public long getDuration() {
		return this.duration;
	}

	public World getWorld() {
		return world;
	}

	public List<World> getDisabledWorlds() {
		return disabledWorlds;
	}

	public WorldEventBossBar getWorldEventBossBar() {
		return worldEventBossBar;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public List<String> getAffectedElements() {
		return affectedElements;
	}

	public NamespacedKey getWorldEventNamespacedKey() {
		return worldEventNamespacedKey;
	}

	public static void setAllEvents(HashMap<String, WorldEvent> allEvents) {
		ALL_EVENTS = allEvents;
	}

	public static void setActiveEvents(HashSet<WorldEvent> activeEvents) {
		ACTIVE_EVENTS = activeEvents;
	}

	public static void setAffectedPlayers(HashSet<Player> affectedPlayers) {
		AFFECTED_PLAYERS = affectedPlayers;
	}

	public void setDisplayMethods(List<IWorldEventDisplay> displayMethods) {
		this.displayMethods = displayMethods;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setDisabledWorlds(List<World> disabledWorlds) {
		this.disabledWorlds = disabledWorlds;
	}

	public void setWorldEventBossBar(WorldEventBossBar worldEventBossBar) {
		this.worldEventBossBar = worldEventBossBar;
	}

	public void setAffectedElements(List<String> affectedElements) {
		this.affectedElements = affectedElements;
	}
}
