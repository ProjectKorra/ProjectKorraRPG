package com.projectkorra.rpg.ability;

import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.util.XPControl;

import net.md_5.bungee.api.ChatColor;

public enum AbilityTier {
    DEFAULT(0, "Default"), 
    NOVICE(1, "Novice"), 
    INTERMEDIATE(XPControl.getMaxLevel() / 4, "Intermediate"), 
    ADVANCED(XPControl.getMaxLevel() / 2, "Advanced"), 
    MASTER(3 * XPControl.getMaxLevel() / 4, "Master");
    
    private int level;
    private String display;
    
    private AbilityTier(int level, String display) {
        this.level = level;
        this.display = display;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDisplay() {
        return getColor() + display;
    }
    
    public boolean aboveOrSame(AbilityTier other) {
        return this.level >= other.level;
    }
    
    public int getRequiredScrolls() {
        return level / 10;
    }
    
    public ChatColor getColor() {
        ChatColor color = ChatColor.of(ConfigManager.getConfig().getString("ChatColors." + this.toString()));
        
        if (color == null) {
            color = ChatColor.WHITE;
        }
        
        return color;
    }
    
    public static AbilityTier fromLevel(int level) {
        if (level < 1) {
            return DEFAULT;
        } else if (level < XPControl.getMaxLevel() / 4) {
            return NOVICE;
        } else if (level < XPControl.getMaxLevel() / 2) {
            return INTERMEDIATE;
        } else if (level < 3 * XPControl.getMaxLevel() / 4) {
            return ADVANCED;
        } else {
            return MASTER;
        }
    }
}
