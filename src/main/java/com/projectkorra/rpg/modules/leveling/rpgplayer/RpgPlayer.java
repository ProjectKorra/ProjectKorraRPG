package com.projectkorra.rpg.modules.leveling.rpgplayer;

import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public record RpgPlayer(UUID uuid, int level, double xp) {

    private Player asPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public BendingPlayer asBendingPlayer() {
        return BendingPlayer.getBendingPlayer(asPlayer());
    }
}
