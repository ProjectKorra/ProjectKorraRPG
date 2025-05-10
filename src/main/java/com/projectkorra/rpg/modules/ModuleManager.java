package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.WorldEvents;
import com.projectkorra.rpg.modules.leveling.RPGLeveling;
import com.projectkorra.rpg.modules.randomavatar.RandomAvatar;
import com.projectkorra.rpg.modules.elementassignments.ElementAssignments;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	private final List<com.projectkorra.rpg.modules.Module> modules = new ArrayList<>();

	private final WorldEvents worldEventsModule;
	private final RPGLeveling rpgLevelingModule;
	private final RandomAvatar randomAvatarModule;
	private final ElementAssignments elementAssignmentsModule;

	public ModuleManager() {
		modules.add(worldEventsModule = new WorldEvents());
		modules.add(rpgLevelingModule = new RPGLeveling());
		modules.add(randomAvatarModule = new RandomAvatar());
		modules.add(elementAssignmentsModule = new ElementAssignments());
	}

	public void enableModules() {
		for (com.projectkorra.rpg.modules.Module module : modules) {
			if (module.isEnabled()) {
				module.enable();
			}
		}
	}

	public void disableModules() {
		for (com.projectkorra.rpg.modules.Module module : modules) {
			module.disable();
		}
	}

	public List<Module> getModules() {
		return modules;
	}

	public WorldEvents getWorldEventsModule() {
		return worldEventsModule;
	}

	public RPGLeveling getRpgLevelingModule() {
		return rpgLevelingModule;
	}

	public RandomAvatar getRandomAvatarModule() {
		return randomAvatarModule;
	}

	public ElementAssignments getElementAssignmentsModule() {
		return elementAssignmentsModule;
	}
}
