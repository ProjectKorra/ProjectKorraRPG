package com.projectkorra.rpg.modules.leveling.rpgplayer;

import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RpgPlayer {
    private final UUID uuid;
    private final int level;
    private final double xp;

    public RpgPlayer(UUID uuid, int level, double xp) {
        this.uuid = uuid;
        this.level = level;
        this.xp = xp;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getLevel() {
        return this.level;
    }

    public double getXp() {
        return this.xp;
    }

    private Player asPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public BendingPlayer asBendingPlayer() {
        return BendingPlayer.getBendingPlayer(asPlayer());
    }
}
