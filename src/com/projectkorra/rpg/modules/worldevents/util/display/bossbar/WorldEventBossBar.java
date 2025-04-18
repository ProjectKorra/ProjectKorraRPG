package com.projectkorra.rpg.modules.worldevents.util.display.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class WorldEventBossBar {
    private String title;
    private BarColor barColor;
    private BarStyle barStyle;
    private BossBar bossBar;

    public WorldEventBossBar(String title, BarColor barColor, BarStyle barStyle) {
        this.title = title;
        this.barColor = barColor;
        this.barStyle = barStyle;

        this.bossBar = Bukkit.createBossBar(this.title, this.barColor, this.barStyle);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BarColor getBarColor() {
        return barColor;
    }

    public void setBarColor(BarColor barColor) {
        this.barColor = barColor;
    }

    public BarStyle getBarStyle() {
        return barStyle;
    }

    public void setBarStyle(BarStyle barStyle) {
        this.barStyle = barStyle;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void setBossBar(BossBar bossBar) {
        this.bossBar = bossBar;
    }
}
