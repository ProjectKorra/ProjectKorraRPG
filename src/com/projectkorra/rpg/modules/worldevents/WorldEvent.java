package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.rpg.modules.worldevents.util.display.IWorldEventDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.bossbar.BossBarDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.bossbar.WorldEventBossBar;
import com.projectkorra.rpg.modules.worldevents.util.WorldEventScheduler;
import com.projectkorra.rpg.modules.worldevents.util.display.chat.ChatDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.none.NoDisplay;
import com.projectkorra.rpg.modules.worldevents.util.display.scoreboard.ScoreboardDisplay;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public class WorldEvent {
	private static HashMap<Integer, WorldEvent> ACTIVE_EVENTS = new HashMap<>();
	private static Set<BendingPlayer> AFFECTED_PLAYERS = new HashSet<>();

	private List<IWorldEventDisplay> displayStrategies;

	private String name;
	private long duration;
	private BarColor barColor;
	private BarStyle barStyle;
	private boolean smoothBossBar;

	private String eventStartMessage;
	private String eventStopMessage;
	private List<World> blacklistedWorlds;

	private List<Element> affectedElements;
	private List<Attribute> affectedAttributes;
	private List<Ability> affectedAbilities;

	private WorldEventBossBar worldEventBossBar;

	private DISPLAY_METHOD displayMethods;

	public WorldEvent(
			String name,
			long duration,
			@Nullable BarColor barColor,
			@Nullable BarStyle barStyle,
			@Nullable String eventStartMessage,
			@Nullable String eventStopMessage,
			@Nullable List<World> blacklistedWorlds,
			@Nullable List<Element> affectedElements,
			@Nullable List<Attribute> affectedAttributes,
			@Nullable List<Ability> affectedAbilities,
			List<DISPLAY_METHOD> displayMethods
	)
	{
		this.name = name;
		this.duration = duration;
		this.barColor = barColor;
		this.barStyle = barStyle;
		this.eventStartMessage = eventStartMessage;
		this.eventStopMessage = eventStopMessage;
		this.blacklistedWorlds = blacklistedWorlds;
		this.affectedElements = affectedElements;
		this.affectedAttributes = affectedAttributes;
		this.affectedAbilities = affectedAbilities;

		this.displayStrategies = new ArrayList<>();

		for (DISPLAY_METHOD method : displayMethods) {
			switch (method) {
				case BOSSBAR -> this.displayStrategies.add(new BossBarDisplay());
				case SCOREBOARD -> this.displayStrategies.add(new ScoreboardDisplay());
				case CHAT -> this.displayStrategies.add(new ChatDisplay());
				case NONE -> this.displayStrategies.add(new NoDisplay());
			}
		}
	}

	public void startEvent() {
		ACTIVE_EVENTS.put(0, this);

		Set<String> blacklistedNames = new HashSet<>();
		if (blacklistedWorlds != null) {
			for (World w : blacklistedWorlds) {
				if (w != null) {
					blacklistedNames.add(w.getName());
				}
			}
		}

		// Add all online players whose worlds are not in the blacklist.
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!blacklistedNames.contains(player.getWorld().getName())) {
				AFFECTED_PLAYERS.add(BendingPlayer.getBendingPlayer(player));
			}
		}

		// Start the display for the event
		for (IWorldEventDisplay display : this.displayStrategies) {
			display.startDisplay(this);
		}

		WorldEventScheduler.startWorldEventSchedule(this);
	}

	public void updateDisplay(double progress) {
		Set<String> blacklistedNames = new HashSet<>();
		if (blacklistedWorlds != null) {
			for (World w : blacklistedWorlds) {
				if (w != null) {
					blacklistedNames.add(w.getName());
				}
			}
		}

		// Update display only for players not in a blacklisted world.
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!blacklistedNames.contains(player.getWorld().getName())) {
				for (IWorldEventDisplay display : this.displayStrategies) {
					display.updateDisplay(this, progress);
				}
			}
		}
	}

	public void stopEvent() {
		ACTIVE_EVENTS.remove(0);

		Set<String> blacklistedNames = new HashSet<>();
		if (blacklistedWorlds != null) {
			for (World w : blacklistedWorlds) {
				if (w != null) {
					blacklistedNames.add(w.getName());
				}
			}
		}

		// Stop display for players not in a blacklisted world.
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!blacklistedNames.contains(player.getWorld().getName())) {
				for (IWorldEventDisplay display : this.displayStrategies) {
					display.stopDisplay(this);
				}
			}
		}
	}

	public enum DISPLAY_METHOD {
		BOSSBAR,
		SCOREBOARD,
		CHAT,
		NONE;
	}

	public static HashMap<Integer, WorldEvent> getActiveEvents() {
		return ACTIVE_EVENTS;
	}

	public static Set<BendingPlayer> getAffectedPlayers() {
		return AFFECTED_PLAYERS;
	}

	public List<IWorldEventDisplay> getDisplayStrategies() {
		return displayStrategies;
	}

	public String getName() {
		return this.name;
	}

	public long getDuration() {
		return this.duration;
	}

	public BarColor getBarColor() {
		return this.barColor;
	}

	public BarStyle getBarStyle() {
		return barStyle;
	}

	public boolean isSmoothBossBar() {
		return smoothBossBar;
	}

	public String getEventStartMessage() {
		return this.eventStartMessage;
	}

	public String getEventStopMessage() {
		return eventStopMessage;
	}

	public List<World> getBlacklistedWorlds() {
		return blacklistedWorlds;
	}

	public List<Element> getAffectedElements() {
		return affectedElements;
	}

	public List<Attribute> getAffectedAttributes() {
		return affectedAttributes;
	}

	public List<Ability> getAffectedAbilities() {
		return affectedAbilities;
	}

	public WorldEventBossBar getWorldEventBossBar() {
		return worldEventBossBar;
	}

	public DISPLAY_METHOD getDisplayMethods() {
		return displayMethods;
	}

	public static void setActiveEvents(HashMap<Integer, WorldEvent> activeEvents) {
		ACTIVE_EVENTS = activeEvents;
	}

	public static void setAffectedPlayers(Set<BendingPlayer> affectedPlayers) {
		AFFECTED_PLAYERS = affectedPlayers;
	}

	public void setDisplayStrategies(List<IWorldEventDisplay> displayStrategies) {
		this.displayStrategies = displayStrategies;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setBarColor(BarColor barColor) {
		this.barColor = barColor;
	}

	public void setBarStyle(BarStyle barStyle) {
		this.barStyle = barStyle;
	}

	public void setSmoothBossBar(boolean smoothBossBar) {
		this.smoothBossBar = smoothBossBar;
	}

	public void setEventStartMessage(String eventStartMessage) {
		this.eventStartMessage = eventStartMessage;
	}

	public void setEventStopMessage(String eventStopMessage) {
		this.eventStopMessage = eventStopMessage;
	}

	public void setBlacklistedWorlds(List<World> blacklistedWorlds) {
		this.blacklistedWorlds = blacklistedWorlds;
	}

	public void setAffectedElements(List<Element> affectedElements) {
		this.affectedElements = affectedElements;
	}

	public void setAffectedAttributes(List<Attribute> affectedAttributes) {
		this.affectedAttributes = affectedAttributes;
	}

	public void setAffectedAbilities(List<Ability> affectedAbilities) {
		this.affectedAbilities = affectedAbilities;
	}

	public void setWorldEventBossBar(WorldEventBossBar worldEventBossBar) {
		this.worldEventBossBar = worldEventBossBar;
	}

	public void setDisplayMethods(DISPLAY_METHOD displayMethods) {
		this.displayMethods = displayMethods;
	}
}
