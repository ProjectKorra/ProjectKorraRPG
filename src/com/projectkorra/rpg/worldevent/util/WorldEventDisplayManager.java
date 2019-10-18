package com.projectkorra.rpg.worldevent.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.worldevent.WorldEvent;

public class WorldEventDisplayManager {
	
	private Map<Element, BarColor> colors;
	private Map<World, Set<BossBar>> active;
	private ProjectKorraRPG plugin;

	public WorldEventDisplayManager(ProjectKorraRPG plugin) {
		colors = new HashMap<>();
		colors.put(Element.AIR, BarColor.WHITE);
		colors.put(Element.AVATAR, BarColor.PURPLE);
		colors.put(Element.CHI, BarColor.YELLOW);
		colors.put(Element.EARTH, BarColor.GREEN);
		colors.put(Element.FIRE, BarColor.RED);
		colors.put(Element.WATER, BarColor.BLUE);
		
		active = new HashMap<>();
		
		this.plugin = plugin;
	}
	
	public void addColor(Element e, BarColor color) {
		if (!colors.containsKey(e)) {
			colors.put(e, color);
		}
	}
	
	public BarColor getBarColor(Element e) {
		return colors.containsKey(e) ? colors.get(e) : BarColor.PINK;
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
			BossBar bar = plugin.getServer().createBossBar(event.getElement().getColor() + event.getName(), getBarColor(event.getElement()), BarStyle.SOLID, flags.toArray(new BarFlag[size]));
			if (!active.containsKey(world)) {
				active.put(world, new HashSet<>());
			}
			active.get(world).add(bar);
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
		for (BossBar bar : active.get(world)) {
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
			for (BossBar bar : active.get(world)) {
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
		for (BossBar bar : active.get(world)) {
			for (Player player : world.getPlayers()) {
				if (!bar.getPlayers().contains(player)) {
					bar.addPlayer(player);
				}
			}
			
			long currTime = world.getTime() % 12000;
			if (currTime >= 12000) {
				bar.removeAll();
				remove.add(bar);
			}
			double progress = 1 - (currTime / 12000);
			bar.setProgress(progress);
		}
		
		for (BossBar bar : remove) {
			active.get(world).remove(bar);
		}
	}
}
