package com.projectkorra.rpg.worldevent.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.worldevent.WorldEvent;

public class WorldEventDisplayManager {

	private Map<World, Map<BossBar, WorldEvent>> active;
	private ProjectKorraRPG plugin;

	public WorldEventDisplayManager(ProjectKorraRPG plugin) {
		active = new HashMap<>();

		this.plugin = plugin;
	}

	public BossBar createBossBar(World world, WorldEvent event) {
		if (ProjectKorraRPG.getEventManager().isHappening(world, event)) {
			List<BarFlag> flags = new ArrayList<>();
			int size = 0;
			if (event.getDarkenSky()) {
				size++;
				flags.add(BarFlag.DARKEN_SKY);
			}
			if (event.getCreateFog()) {
				size++;
				flags.add(BarFlag.CREATE_FOG);
			}
			BossBar bar = plugin.getServer().createBossBar(ChatColor.BOLD + (event.getTextColor() + event.getName()), event.getBarColor(), BarStyle.SOLID, flags.toArray(new BarFlag[size]));
			if (!active.containsKey(world)) {
				active.put(world, new HashMap<>());
			}
			active.get(world).put(bar, event);
			bar.setVisible(true);
			return bar;
		}
		return null;
	}

	public void removeBossBar(World world, WorldEvent event) {
		if (!active.containsKey(world)) {
			return;
		}
		BossBar remove = getBossBar(world, event);

		if (remove != null) {
			remove.removeAll();
			active.get(world).remove(remove);
		}
	}

	public BossBar getBossBar(World world, WorldEvent event) {
		if (!active.containsKey(world)) {
			return null;
		}

		BossBar get = null;
		for (BossBar bar : active.get(world).keySet()) {
			if (ChatColor.stripColor(bar.getTitle()).equals(event.getName())) {
				get = bar;
				break;
			}
		}

		if (get != null) {
			return get;
		}
		return null;
	}

	public void removeAll() {
		for (World world : active.keySet()) {
			for (BossBar bar : active.get(world).keySet()) {
				bar.removeAll();
			}
		}
	}

	public void update(World world) {
		if (active.containsKey(world)) {
			if (active.get(world).isEmpty()) {
				active.remove(world);
			}
		}

		if (!active.containsKey(world)) {
			return;
		}

		List<BossBar> remove = new ArrayList<>();
		for (BossBar bar : active.get(world).keySet()) {
			for (Player player : world.getPlayers()) {
				if (!bar.getPlayers().contains(player)) {
					bar.addPlayer(player);
				}
			}
			
			WorldEvent we = active.get(world).get(bar);
			long time = 24000;
			if (we.getTime() == Time.DAY) {
				time = 12000;
			}

			long currTime = world.getTime() % time;
			if (currTime >= time) {
				bar.removeAll();
				remove.add(bar);
			} else {
				double progress = 1 - (currTime / time);
				bar.setProgress(progress);
			}
		}

		for (BossBar bar : remove) {
			active.get(world).remove(bar);
		}
	}
}
