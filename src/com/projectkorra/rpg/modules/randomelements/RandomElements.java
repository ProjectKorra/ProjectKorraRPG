package com.projectkorra.rpg.modules.randomelements;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.randomelements.listeners.RandomElementsListener;

public class RandomElements extends Module {
	public RandomElements() {
		super("RandomElements");
	}

	@Override
	public void enable() {
		ProjectKorraRPG.getPlugin().getServer().getPluginManager().registerEvents(new RandomElementsListener(), ProjectKorraRPG.getPlugin());
	}

	@Override
	public void disable() {

	}
}
