package com.projectkorra.rpg.modules.elementassignments;

import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.elementassignments.listeners.AssignmentListener;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;

public class ElementAssignments extends Module {
	private AssignmentManager assignmentManager;

	public ElementAssignments() {
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
