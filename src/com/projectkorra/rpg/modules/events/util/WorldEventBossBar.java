package com.projectkorra.rpg.modules.events.util;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class WorldEventBossBar {
	private BossBar bossBar;
	private BarColor barColor;
	private String title;

	public WorldEventBossBar(String title, BarColor barColor) {
		this.title = title;
		this.barColor = barColor;

		this.bossBar = Bukkit.createBossBar(this.title, this.barColor, BarStyle.SOLID);
	}

	public BossBar getBossBar() {
		return bossBar;
	}

	public BarColor getBarColor() {
		return barColor;
	}

	public String getTitle() {
		return title;
	}

	public void setBossBar(BossBar bossBar) {
		this.bossBar = bossBar;
	}

	public void setBarColor(BarColor barColor) {
		this.barColor = barColor;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
