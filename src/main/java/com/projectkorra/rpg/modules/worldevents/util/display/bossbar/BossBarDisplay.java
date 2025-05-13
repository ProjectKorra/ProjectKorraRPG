package com.projectkorra.rpg.modules.worldevents.util.display.bossbar;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.display.IWorldEventDisplay;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class BossBarDisplay implements IWorldEventDisplay {

	private final WorldEventBossBar worldEventBossBar;
	private final BarColor barColor;
	private final BarStyle barStyle;
	private final boolean smooth;

	/**
	 *
	 * @param barColor Color of BossBar
	 * @param barStyle Style of BossBar
	 * @param smooth   <code>true</code> Refresh every tick <code>else</code> every second
	 */
	public BossBarDisplay(String title, BarColor barColor, BarStyle barStyle, boolean smooth) {
		this.barColor = barColor;
		this.barStyle = barStyle;
		this.smooth = smooth;

		worldEventBossBar = new WorldEventBossBar(ChatUtil.color(title), barColor, barStyle, smooth);
	}

	@Override
	public void startDisplay(WorldEvent event) {
		event.setWorldEventBossBar(getWorldEventBossBar());

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (WorldEvent.getAffectedPlayers().contains(player)) {
				event.getWorldEventBossBar().getBossBar().addPlayer(player);
			}
		}
	}

	@Override
	public void updateDisplay(WorldEvent event, double progress) {
		if (event.getWorldEventBossBar() != null && event.getWorldEventBossBar().getBossBar() != null) {
			event.getWorldEventBossBar().getBossBar().setProgress(progress);
		}
	}

	@Override
	public void stopDisplay(WorldEvent event) {
		if (event.getWorldEventBossBar() != null && event.getWorldEventBossBar().getBossBar() != null) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (WorldEvent.getAffectedPlayers().contains(player)) {
					event.getWorldEventBossBar().getBossBar().removePlayer(player);
				}
			}
		}
	}

	public WorldEventBossBar getWorldEventBossBar() {
		return worldEventBossBar;
	}

	public BarColor getBarColor() {
		return barColor;
	}

	public BarStyle getBarStyle() {
		return barStyle;
	}

	public boolean isSmooth() {
		return smooth;
	}
}
