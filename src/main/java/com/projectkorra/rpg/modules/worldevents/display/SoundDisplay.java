package com.projectkorra.rpg.modules.worldevents.display;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class SoundDisplay implements ISoundDisplay {
    private final @Nullable Sound start;
    private final float startVolume;
    private final float startPitch;

    private final @Nullable Sound stop;
    private final float stopVolume;
    private final float stopPitch;

    public SoundDisplay(@Nullable Sound start, float startVolume, float startPitch,
                        @Nullable Sound stop, float stopVolume, float stopPitch) {
        this.start = start;
        this.startVolume = startVolume;
        this.startPitch = startPitch;

        this.stop = stop;
        this.stopVolume = stopVolume;
        this.stopPitch = stopPitch;
    }

    @Override
    public void playStartSound(Player player) {
        if (player == null || !player.isOnline() || start == null) return;
        player.playSound(player.getLocation(), start, SoundCategory.AMBIENT, startVolume, startPitch);

    }

    @Override
    public void playStopSound(Player player) {
        if (player == null || !player.isOnline() || stop == null) return;
        player.playSound(player.getLocation(), stop, SoundCategory.AMBIENT, stopVolume, stopPitch);
    }

    @Override
    public void playStartSound(Collection<Player> players) {
        if (players == null || players.isEmpty()) return;
        players.forEach(this::playStartSound);
    }

    @Override
    public void playStopSound(Collection<Player> players) {
        if (players == null || players.isEmpty()) return;
        players.forEach(this::playStopSound);
    }
}
