package com.projectkorra.rpg.modules.worldevents.display;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BossBarDisplay implements IBossBarDisplay {
    private final NamespacedKey key;
    private final String title;
    private final BarColor barColor;
    private final BarStyle barStyle;
    private final boolean smooth;

    private KeyedBossBar bossBar;

    private double lastProgress = -1.0;
    private static final double PROGRESS_EPSILON = 0.01;

	/**
	 * @param barColor Color of BossBar
	 * @param barStyle Style of BossBar
	 * @param smooth <code>true</code> Refresh every tick <code>else</code> every second
	 */
	public BossBarDisplay(NamespacedKey key, String title, BarColor barColor, BarStyle barStyle, boolean smooth) {
        this.key = key;
        this.title = title;
        this.barColor = barColor;
        this.barStyle = barStyle;
        this.smooth = smooth;
	}

	@Override
	public void start(WorldEvent event) {
        KeyedBossBar existing = Bukkit.getBossBar(key);
        this.bossBar = (existing != null) ? existing : Bukkit.createBossBar(key, ChatUtil.color(title), barColor, barStyle);

        bossBar.setTitle(ChatUtil.color(title));
        bossBar.setColor(barColor);
        bossBar.setStyle(barStyle);
        bossBar.setProgress(1.0);
        bossBar.setVisible(true);
        bossBar.removeAll(); // WorldEventService handles viewers

        lastProgress = 1.0;
	}

    @Override
    public void stop(WorldEvent event) {
        if (bossBar == null) return;

        bossBar.removeAll();
        Bukkit.removeBossBar(key);
        bossBar = null;
        lastProgress = -1.0;
    }

    @Override
    public long tickPeriod() {
        return smooth ? 1L : 20L;
    }

    @Override
    public void updateTick(WorldEvent event, double progress) {
        if (bossBar == null) return;

        double clamped = (progress < 0.0) ? 0.0 : (Math.min(progress, 1.0));
        if (Math.abs(clamped - lastProgress) >= PROGRESS_EPSILON) {
            bossBar.setProgress(progress);
            lastProgress = clamped;
        }
    }

    @Override
    public void addViewer(Player viewer) {
        if (viewer == null || !viewer.isOnline() || bossBar == null) return;
        bossBar.addPlayer(viewer);
    }

    @Override
    public void removeViewer(Player viewer) {
        if (viewer == null || !viewer.isOnline() || bossBar == null) return;
        bossBar.removePlayer(viewer);
    }

    @Override
    public void addViewers(Collection<Player> viewers) {
        if (viewers == null || viewers.isEmpty() || bossBar == null) return;
        viewers.forEach(viewer -> bossBar.addPlayer(viewer));
    }

    @Override
    public void removeViewers(Collection<Player> viewers) {
        if (viewers == null || viewers.isEmpty() || bossBar == null) return;
        viewers.forEach(viewer -> bossBar.removePlayer(viewer));
    }
}
