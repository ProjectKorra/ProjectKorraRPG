package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.util.DisplayHelper;
import com.projectkorra.rpg.modules.worldevents.util.display.IWorldEventDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.bossbar.BossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.bossbar.WorldEventBossBar;
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
import org.bukkit.scheduler.BukkitRunnable;

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
	private List<World> disabledWorlds;

	private final FileConfiguration config;

	private WorldEventBossBar worldEventBossBar;

	public WorldEvent(String key, String title, long duration, List<World> disabledWorlds, FileConfiguration config, World world, List<IWorldEventDisplay> displayMethods) {
		this.key = key;
		this.title = title;
		this.duration = duration;
		this.disabledWorlds = disabledWorlds;
		this.config = config;
		this.world = world;
		this.displayMethods = (displayMethods == null || displayMethods.isEmpty()) ? Collections.singletonList(new NoDisplay()) : new ArrayList<>(displayMethods);

		this.worldEventNamespacedKey = new NamespacedKey(ProjectKorraRPG.getPlugin(), key);
	}

	public void startEvent() {
		if (getDisabledWorlds().contains(world)) {
			ProjectKorraRPG.getPlugin().getLogger().info("Couldn't start worldevent because world is a disabled world!");
			return;
		}

		getActiveEvents().add(this);

		Bukkit.getPluginManager().callEvent(new WorldEventStartEvent(this));

		// Add all online players in the world to the Set
		// And play Sound if user configured
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld() == this.world) {
				getAffectedPlayers().add(player);
				if (getConfig().getBoolean("PlayEventStartSound")) {
					String soundName = getConfig().getString("EventStart.Sound", "ENTITY_EXPERIENCE_ORB_PICKUP");

					Sound eventStartSound = Sound.valueOf(soundName);
					float volume = (float) getConfig().getDouble("EventStart.Volume");
					float pitch = (float) getConfig().getDouble("EventStart.Pitch");

					player.getWorld().playSound(player.getLocation(), eventStartSound, volume, pitch);
				}
			}
		}

		// Start the display for the event
		for (IWorldEventDisplay display : getDisplayMethods()) {
			display.startDisplay(this);
		}

		startWorldEventTimer();
	}

	/**
	 * Stop active WorldEvent
	 */
	public void stopEvent() {
		Bukkit.getPluginManager().callEvent(new WorldEventStopEvent(this));
		getActiveEvents().remove(this);

		// Play EventStop sound for each player in an active WorldEvent world
		for (Player player : getWorld().getPlayers()) {
			if (getAffectedPlayers().contains(player)) {
				if (getConfig().getBoolean("PlayEventStopSound")) {
					String soundName = getConfig().getString("EventStop.Sound", "ENTITY_EXPERIENCE_ORB_PICKUP");

					Sound eventStopSound = Sound.valueOf(soundName.toUpperCase());
					float volume = (float) getConfig().getDouble("EventStop.Volume", 1.0);
					float pitch = (float) getConfig().getDouble("EventStop.Pitch", 1.0);

					player.getWorld().playSound(player.getLocation(), eventStopSound, volume, pitch);
				}
			}
		}

		// Stop the display for the event
		for (IWorldEventDisplay display : getDisplayMethods()) {
			display.stopDisplay(this);
		}
	}

	// Updated WorldEvent display
	public void updateDisplay(double progress) {
		for (IWorldEventDisplay display : getDisplayMethods()) {
			display.updateDisplay(this, progress);
		}
	}

	/**
	 * Puts all WorldEvents from WorldEvents directory into the {@link WorldEvent#getAllEvents()} map
	 */
	public static void initAllWorldEvents() {
		File worldEventsFolder = new File(ProjectKorraRPG.getPlugin().getDataFolder(), "WorldEvents");
		if (!worldEventsFolder.exists() || !worldEventsFolder.isDirectory()) {
			ProjectKorraRPG.getPlugin().getLogger().warning("WorldEvents folder was not found!");
			return;
		}

		File[] worldEventsFiles = worldEventsFolder.listFiles(((dir, name) -> name.endsWith(".yml")));
		if (worldEventsFiles == null || worldEventsFiles.length == 0) {
			ProjectKorraRPG.getPlugin().getLogger().info("No WorldEvents were found.");
			return;
		}

		// Iterate through all WorldEvent configurations
		Arrays.stream(worldEventsFiles).parallel().forEach(file -> {
			String eventKey = file.getName().toLowerCase().replace(".yml", ""); // Event key is file name without yml extension
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			String eventTitle = config.getString("Title", "&cConfig Title not defined!");
			long duration = config.getLong("Duration", 1000);

			String configWorldName = config.getString("World", null);
			World world = (configWorldName == null ? null : Bukkit.getWorld(configWorldName));

			List<IWorldEventDisplay> displayMethods = new ArrayList<>();

			// BossBar-Display
			if (config.getBoolean("DisplayMethods.BossBar.Enabled", false)) {
				BarColor bossBarColor = DisplayHelper.convertStringToBarColor(config.getString("DisplayMethods.BossBar.Color", "RED"));
				BarStyle bossBarStyle = DisplayHelper.convertStringToBarStyle(config.getString("DisplayMethods.BossBar.Style", "SOLID"));
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

			getAllEvents().put(eventKey, new WorldEvent(eventKey, eventTitle, duration, disabledWorlds, config, world, displayMethods));
		});
	}

	private void startWorldEventTimer() {
		final long duration = getDuration();
		final long startTime = System.currentTimeMillis();

		new BukkitRunnable() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				double elapsed = now - startTime;
				double progress = 1.0 - (elapsed / (double) duration);

				if (progress <= 0.0) {
					updateDisplay(0.0);
					stopEvent();
					this.cancel();
					return;
				}

				updateDisplay(progress);
			}
		}.runTaskTimer(ProjectKorraRPG.getPlugin(), 0, getWorldEventBossBar().isSmooth() ? 1 : 20);
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

	public void setDisabledWorlds(List<World> disabledWorlds) {
		this.disabledWorlds = disabledWorlds;
	}

	public void setWorldEventBossBar(WorldEventBossBar worldEventBossBar) {
		this.worldEventBossBar = worldEventBossBar;
	}
}
