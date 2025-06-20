package com.projectkorra.rpg.modules.leveling;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.leveling.commands.LevelCommand;

public class LevelingModule extends Module {
    public LevelingModule() {
        super("Leveling");
    }

    @Override
    public void enable() {
        new LevelCommand();
    }

    @Override
    public void disable() {

    }
}
