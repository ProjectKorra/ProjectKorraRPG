package com.projectkorra.rpg.modules.leveling.gui;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.projectkorra.rpg.ProjectKorraRPG;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {
    private final ProjectKorraRPG plugin;
    private final List<Gui> allGuis = new ArrayList<>();

    public GuiManager(ProjectKorraRPG plugin) {
        this.plugin = plugin;
    }

    public void init() {

    }

    public ProjectKorraRPG getPlugin() {
        return plugin;
    }

    public List<Gui> getAllGuis() {
        return allGuis;
    }
}
