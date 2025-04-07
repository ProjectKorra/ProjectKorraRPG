package com.projectkorra.rpg.modules.manager;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.events.WorldEvents;
import com.projectkorra.rpg.modules.leveling.RPGLeveling;
import com.projectkorra.rpg.modules.randomavatar.RandomAvatar;
import com.projectkorra.rpg.modules.randomelements.RandomElements;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	public ModuleManager() {
		modules.add(new WorldEvents());
		modules.add(new RPGLeveling());
		modules.add(new RandomAvatar());
		modules.add(new RandomElements());
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
