package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.modules.elementassignments.ElementAssignments;
import com.projectkorra.rpg.modules.leveling.RPGLeveling;
import com.projectkorra.rpg.modules.randomavatar.RandomAvatar;
import com.projectkorra.rpg.modules.worldevents.WorldEvents;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

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

	public WorldEvents getWorldEventsModule() {
		if (worldEventsModule.isEnabled()) {
			return worldEventsModule;
		}
		throw new NullPointerException("WorldEvents Module is disabled! Enable it in config.yml");
	}

	public RPGLeveling getRpgLevelingModule() {
		if (rpgLevelingModule.isEnabled()) {
			return rpgLevelingModule;
		}
		throw new NullPointerException("Level Module is disabled! Enable it in config.yml");
	}

	public RandomAvatar getRandomAvatarModule() {
		if (randomAvatarModule.isEnabled()) {
			return randomAvatarModule;
		}
		throw new NullPointerException("AvatarCycle Module is disabled! Enable it in config.yml");
	}

	public ElementAssignments getElementAssignmentsModule() {
		if (elementAssignmentsModule.isEnabled()) {
			return elementAssignmentsModule;
		}
		throw new NullPointerException("ElementAssign Module is disabled! Enable it in config.yml");
	}
}
