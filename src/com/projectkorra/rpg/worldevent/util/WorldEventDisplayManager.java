package com.projectkorra.rpg.worldevent.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.worldevent.WorldEventInstance;

public class WorldEventDisplayManager {

	private Map<WorldEventInstance, BossBar> active;
	private ProjectKorraRPG plugin;

	public WorldEventDisplayManager(ProjectKorraRPG plugin) {
		active = new HashMap<>();

		this.plugin = plugin;
	}

	public BossBar createBossBar(WorldEventInstance instance) {
		if (ProjectKorraRPG.getEventManager().isHappening(instance.getWorld(), instance.getEvent())) {
			List<BarFlag> flags = new ArrayList<>();
			int size = 0;
			if (instance.getEvent().getDarkenSky()) {
				size++;
				flags.add(BarFlag.DARKEN_SKY);
			}
			if (instance.getEvent().getCreateFog()) {
				size++;
				flags.add(BarFlag.CREATE_FOG);
			}
			BossBar bar = plugin.getServer().createBossBar(ChatColor.BOLD + (instance.getEvent().getTextColor() + instance.getEvent().getName()), instance.getEvent().getBarColor(), BarStyle.SOLID, flags.toArray(new BarFlag[size]));
			active.put(instance, bar);
			bar.setVisible(true);
			return bar;
		}
		return null;
	}

	public void removeBossBar(WorldEventInstance instance) {
		if (!active.containsKey(instance)) {
			return;
		}
		
		active.get(instance).removeAll();
		active.remove(instance);
	}

	public BossBar getBossBar(WorldEventInstance instance) {
		if (!active.containsKey(instance)) {
			return null;
		}

		return active.get(instance);
	}

	public void removeAll() {
		for (BossBar bar : active.values()) {
			bar.removeAll();
		}
	}

	public void update(WorldEventInstance instance) {
		if (!active.containsKey(instance)) {
			return;
		}
		
		BossBar bar = active.get(instance);
		
		for (Player player : instance.getWorld().getPlayers()) {
			if (!bar.getPlayers().contains(player)) {
				bar.addPlayer(player);
			}
		}
		
		double elapsed = instance.getElapsedTime() <= 0 ? 0 : instance.getElapsedTime();
		
		if (elapsed >= instance.getDuration()) {
			bar.removeAll();
			active.remove(instance);
		} else {
			double progress = 1.0 - (elapsed / (double) instance.getDuration());
			bar.setProgress(progress);
		}
	}
}
