package com.projectkorra.rpg.modules.randomelements;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;

public class RandomElements extends Module {
	public RandomElements() {
		super("RandomElements");
	}

	@Override
	public void enable() {
		ProjectKorraRPG.plugin.setAssignmentManager(new AssignmentManager());
	}

	@Override
	public void disable() {

	}
}
