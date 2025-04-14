package com.projectkorra.rpg.rpgplayer;

import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.entity.Player;

public class RpgPlayer extends BendingPlayer {
	private int level;
	private double xp;

	public RpgPlayer(Player player, int level, double xp) {
		super(player);
		this.level = level;
		this.xp = xp;
	}

	public int getLevel() {
		return this.level;
	}

	public double getXp() {
		return this.xp;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setXp(double xp) {
		this.xp = xp;
	}
}
