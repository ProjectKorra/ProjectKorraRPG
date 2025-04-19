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

import java.io.File;
import java.util.*;

public class WorldEvent {
	private static HashMap<String, WorldEvent> ALL_EVENTS = new HashMap<>();
	private static HashSet<WorldEvent> ACTIVE_EVENTS = new HashSet<>();
	private static HashSet<Player> AFFECTED_PLAYERS = new HashSet<>();

	private final NamespacedKey worldEventNamespacedKey;

	private final String key;
	private String title;
	private long duration;
	private World world;

	private List<IWorldEventDisplay> displayMethods;
	private List<String> affectedElements;
	private List<World> disabledWorlds;

	private final FileConfiguration config;

	private WorldEventBossBar worldEventBossBar;

	public WorldEvent(String key, String title, long duration, List<IWorldEventDisplay> displayMethods, List<String> affectedElements, List<World> disabledWorlds, FileConfiguration config) {
		this.key = key;
		this.title = title;
		this.duration = duration;
		this.displayMethods = (displayMethods == null || displayMethods.isEmpty())
				? Collections.singletonList(new NoDisplay())
				: new ArrayList<>(displayMethods);
		this.affectedElements = affectedElements;
		this.disabledWorlds = disabledWorlds;
		this.config = config;

		this.worldEventNamespacedKey = new NamespacedKey(ProjectKorraRPG.getPlugin(), key);
	}

	public void startEvent() {
		Bukkit.getPluginManager().callEvent(new WorldEventStartEvent(this));

		getActiveEvents().add(this);

		Set<String> disabledWorlds = new HashSet<>();
		if (getDisabledWorlds() != null) {
			for (World w : getDisabledWorlds()) {
				if (w != null) {
					disabledWorlds.add(w.getName());
				}
			}
		}

		// Add all online players whose worlds are not disabled
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!disabledWorlds.contains(player.getWorld().getName())) {
				getAffectedPlayers().add(player);
				if (getConfig().getBoolean("PlayEventStartSound")) {
					Sound eventStartSound = Sound.valueOf(getConfig().getString("EventStart.Sound"));
					float volume = Float.parseFloat(getConfig().getString("EventStart.Volume"));
					float pitch = Float.parseFloat(getConfig().getString("EventStart.Pitch"));

					player.getWorld().playSound(player.getLocation(), eventStartSound, volume, pitch);
				}
			}
		}

		// Start the display for the event
		for (IWorldEventDisplay display : getDisplayMethods()) {
			display.startDisplay(this);
		}

		WorldEventScheduler.startWorldEventSchedule(this);
	}

	public void updateDisplay(double progress) {
		for (IWorldEventDisplay display : getDisplayMethods()) {
			display.updateDisplay(this, progress);
		}
	}

	public void stopEvent() {
		if (getActiveEvents().contains(this)) {
			Bukkit.getPluginManager().callEvent(new WorldEventStopEvent(this));

			getActiveEvents().remove(this);

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!getDisabledWorlds().contains(player.getWorld())) {
					if (getConfig().getBoolean("PlayEventStopSound")) {
						Sound eventStopSound = Sound.valueOf(getConfig().getString("EventStop.Sound"));

						float volume = Float.parseFloat(getConfig().getString("EventStop.Volume"));
						float pitch = Float.parseFloat(getConfig().getString("EventStop.Pitch"));

						player.getWorld().playSound(player.getLocation(), eventStopSound, volume, pitch);
					}
				}
			}

			// Stop the display for the event
			for (IWorldEventDisplay display : getDisplayMethods()) {
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
					displayMethods,
					affectedElements,
					disabledWorlds,
					config
			);

			getAllEvents().put(eventKey, worldEvent);
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

	public NamespacedKey getWorldEventNamespacedKey() {
		return worldEventNamespacedKey;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public long getDuration() {
		return duration;
	}

	public World getWorld() {
		return world;
	}

	public List<IWorldEventDisplay> getDisplayMethods() {
		return displayMethods;
	}

	public List<String> getAffectedElements() {
		return affectedElements;
	}

	public List<World> getDisabledWorlds() {
		return disabledWorlds;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public WorldEventBossBar getWorldEventBossBar() {
		return worldEventBossBar;
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

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setDisplayMethods(List<IWorldEventDisplay> displayMethods) {
		this.displayMethods = displayMethods;
	}

	public void setAffectedElements(List<String> affectedElements) {
		this.affectedElements = affectedElements;
	}

	public void setDisabledWorlds(List<World> disabledWorlds) {
		this.disabledWorlds = disabledWorlds;
	}

	public void setWorldEventBossBar(WorldEventBossBar worldEventBossBar) {
		this.worldEventBossBar = worldEventBossBar;
	}
}
