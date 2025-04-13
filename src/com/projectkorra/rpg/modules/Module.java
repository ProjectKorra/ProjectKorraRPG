package com.projectkorra.rpg.modules;

import com.projectkorra.rpg.configuration.ConfigManager;

public abstract class Module {
	private final String name;

	public Module(String name) {
		this.name = name;
	}

	public abstract void enable();

	public abstract void disable();

	public String getName() {
		return this.name;
	}

	public boolean isEnabled() {
		return ConfigManager.getFileConfig().getBoolean("Modules." + getName() + ".Enabled");
	}
}
