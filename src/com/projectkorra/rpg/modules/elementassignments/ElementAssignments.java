package com.projectkorra.rpg.modules.elementassignments;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;

public class ElementAssignments extends Module {
	public ElementAssignments() {
		super("ElementAssignments");
	}

	@Override
	public void enable() {
		ProjectKorraRPG.plugin.setAssignmentManager(new AssignmentManager());
	}

	@Override
	public void disable() {

	}
}
