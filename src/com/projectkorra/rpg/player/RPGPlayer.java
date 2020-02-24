package com.projectkorra.rpg.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;

public class RPGPlayer {

	private Player player;
	private BendingPlayer bPlayer;
	private BendingStats stats;
	private Map<Element, BendingLevel> levels;
	
	public RPGPlayer(Player player) {
		this(player, new BendingStats(), new HashMap<>());
	}
	
	public RPGPlayer(Player player, BendingStats stats, Map<Element, BendingLevel> levels) {
		this.player = player;
		this.bPlayer = BendingPlayer.getBendingPlayer(player);
		this.stats = stats;
		
		if (levels.isEmpty()) {
			for (Element e : Element.getAllElements()) {
				levels.put(e, new BendingLevel(this, 0, 0));
			}
		}
		this.levels = levels;
	}
	
	public BendingLevel getLevel(Element e) {
		if (!levels.containsKey(e)) {
			levels.put(e, new BendingLevel(this, 0, 0));
		}
		
		return levels.get(e);
	}
	
	public BendingStats getStats() {
		return stats;
	}
	
	public Player getPlayer() {
		return player;
	}
}
