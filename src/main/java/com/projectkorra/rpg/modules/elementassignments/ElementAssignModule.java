package com.projectkorra.rpg.modules.elementassignments;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.elementassignments.listeners.AssignmentListener;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;

public class ElementAssignModule extends Module {
	private AssignmentManager assignmentManager;

	public ElementAssignModule() {
        super("ElementAssignments");
    }

	@Override
	public void enable() {
		this.assignmentManager = new AssignmentManager();

		registerListeners(
				new AssignmentListener()
		);
	}

    @Override
    public void disable() {}

	public AssignmentManager getAssignmentManager() {
		return assignmentManager;
	}

	public void setAssignmentManager(AssignmentManager assignmentManager) {
		this.assignmentManager = assignmentManager;
	}
}
