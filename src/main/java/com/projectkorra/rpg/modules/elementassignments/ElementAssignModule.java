package com.projectkorra.rpg.modules.elementassignments;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.elementassignments.listeners.AssignmentListener;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;
import org.bukkit.event.HandlerList;

public class ElementAssignModule extends Module {
	private AssignmentManager assignmentManager;
	private AssignmentListener assignmentListener;

	public ElementAssignModule(ProjectKorraRPG plugin) {
		super(plugin, "ElementAssignments");
    }

	@Override
	public void enable() {
		this.assignmentManager = new AssignmentManager();
		this.assignmentListener = new AssignmentListener(getPlugin());

		registerListeners(
				this.assignmentListener
		);
	}

    @Override
    public void disable() {
		this.assignmentManager = null;

		if (this.assignmentListener != null) {
			HandlerList.unregisterAll(this.assignmentListener);
			this.assignmentListener = null;
		}
	}

	public AssignmentManager getAssignmentManager() {
		return assignmentManager;
	}
}
