package com.projectkorra.rpg.modules.leveling;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.leveling.storage.Storage;

public class LevelingModule extends Module {
    public LevelingModule() {
        super("Leveling");
    }

    @Override
    public void enable() {
        Storage.init();
    }

    @Override
    public void disable() {

    }
}
