package com.projectkorra.rpg.player;

import org.bukkit.Bukkit;

import com.projectkorra.rpg.ability.AbilityTiers.AbilityTier;
import com.projectkorra.rpg.events.RPGPlayerGainXPEvent;
import com.projectkorra.rpg.events.RPGPlayerLevelUpEvent;

public class BendingLevel {
	
	private static final int MAX_LEVEL = 40;
	private static final double[] XP_LEVEL = loadXPPerLevel();

	private RPGPlayer player;
	private int level;
	private double xp;
	private AbilityTier tier;
	
	public BendingLevel(RPGPlayer player, int level, double xp) {
		this.player = player;
		this.level = level;
		this.xp = xp;
		this.tier = AbilityTier.fromLevel(level);
	}
	
	public int getLevel() {
		return level;
	}
	
	public double getXP() {
		return xp;
	}
	
	public AbilityTier getCurrentTier() {
		return tier;
	}
	
	public BendingLevel addXP(double xp) {
		if (level < 0 || level >= MAX_LEVEL) {
			return this;
		}
		
		RPGPlayerGainXPEvent xpEvent = new RPGPlayerGainXPEvent(player, xp);
		Bukkit.getServer().getPluginManager().callEvent(xpEvent);
		
		if (xpEvent.isCancelled()) {
			return this;
		}
		
		double fxp = this.xp + xp;
		double rxp = XP_LEVEL[level];
		
		if (fxp >= rxp) {
			RPGPlayerLevelUpEvent event = new RPGPlayerLevelUpEvent(player, level + 1);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			if (event.isCancelled()) {
				return this;
			}
			
			level++;
			tier = AbilityTier.fromLevel(level);
			fxp -= rxp;
		}
		
		this.xp = fxp;
		return this;
	}
	
	public static double[] loadXPPerLevel() {
		double[] xp_level = new double[MAX_LEVEL];
		
		for (int i = 0; i < MAX_LEVEL; i++) {
			xp_level[i] = Math.E * 10 * (i + 1);
		}
		
		return xp_level;
	}
}
