package com.projectkorra.rpg.modules.leveling;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.leveling.storage.Storage;

public class RPGLeveling extends Module {
    public RPGLeveling() {
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
