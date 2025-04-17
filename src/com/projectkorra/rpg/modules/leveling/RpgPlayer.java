package com.projectkorra.rpg.modules.leveling;

import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.entity.Player;

public class RpgPlayer extends BendingPlayer {
    private final int level;
    private final double xp;

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
}
