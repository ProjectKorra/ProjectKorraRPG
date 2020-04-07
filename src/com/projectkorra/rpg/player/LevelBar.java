package com.projectkorra.rpg.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import com.projectkorra.rpg.util.XPControl;

public class LevelBar {

	private RPGPlayer player;
	private BossBar bar;
	private boolean toggled = false;
	
	public LevelBar(RPGPlayer player) {
		this.player = player;
		this.bar = Bukkit.createBossBar("Loading", BarColor.PURPLE, BarStyle.SOLID);
		this.bar.addPlayer(player.getPlayer());
		this.update();
	}
	
	public void update() {
		double progress = 1;
		if (player.getLevel() < 40) {
			double currentLevelXP = player.getXP() - XPControl.getXPRequired(player.getLevel() - 1);
			double requiredXP = XPControl.getXPRequired(player.getLevel()) - XPControl.getXPRequired(player.getLevel() - 1);
			progress = currentLevelXP / requiredXP;
		}
		
		this.bar.setTitle(ChatColor.BOLD + "Level " + player.getCurrentTier().getColor() + (ChatColor.BOLD + "" + player.getLevel()));
		this.bar.setProgress(progress);
	}
	
	public void toggle() {
		this.bar.setVisible(toggled);
		this.toggled = !toggled;
	}
	
	public void setColor(BarColor color) {
		this.bar.setColor(color);
	}
	
	public boolean isVisible() {
		return !toggled;
	}
	
	public void destroy() {
		this.bar.removeAll();
	}
}
