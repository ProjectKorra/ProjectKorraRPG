package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.modules.elementassignments.ElementAssignments;
import com.projectkorra.rpg.modules.leveling.RPGLeveling;
import com.projectkorra.rpg.modules.randomavatar.RandomAvatar;
import com.projectkorra.rpg.modules.worldevents.WorldEvents;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new WorldEvents());
        modules.add(new RPGLeveling());
        modules.add(new RandomAvatar());
        modules.add(new ElementAssignments());
    }

    public void enableModules() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.enable();
            }
        }
    }

    public void disableModules() {
        for (Module module : modules) {
            module.disable();
        }
    }

    public List<Module> getModules() {
        return modules;
    }
}
