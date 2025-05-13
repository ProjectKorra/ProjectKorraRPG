package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.listeners.WorldEventModificationListener;
import com.projectkorra.rpg.modules.worldevents.methods.WorldEventModificationService;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduler;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class WorldEventModule extends Module {
	private WorldEventModificationListener modificationListener;
	private WorldEventModificationService modificationService;
	private WorldEventScheduler worldEventScheduler;

	public WorldEventModule() {
		super("WorldEvents");
	}

	@Override
	public void enable() {
		ProjectKorraRPG.getPlugin().getLogger().info("Enabling WorldEvent module...");

		// Initialize all valid WorldEvents found in each config file in the WorldEvents directory
		WorldEvent.initAllWorldEvents();

		// Create ModificationService for Listener
		this.modificationService = new WorldEventModificationService();

		// Create and Register Modification Listener
		this.modificationListener = new WorldEventModificationListener(this.modificationService);
		registerListeners(this.modificationListener);

		// Register Commands
		new WorldEventCommand();

		// Scheduler to make events start based on config
		this.worldEventScheduler = new WorldEventScheduler();
		ProjectKorraRPG.getPlugin().getLogger().info("WorldEvent module enabled successfully");
	}

	@Override
	public void disable() {
		ProjectKorraRPG.getPlugin().getLogger().info("Disabling WorldEvent module...");

		if (this.worldEventScheduler != null) {
			this.worldEventScheduler.cleanup();
			this.worldEventScheduler = null;
			ProjectKorraRPG.getPlugin().getLogger().info("WorldEvent scheduler cleaned up");
		}

		// Stop all active events
		try {
			int activeCount = WorldEvent.getActiveEvents().size();
			ProjectKorraRPG.getPlugin().getLogger().info("Stopping " + activeCount + " active WorldEvents...");
			new ArrayList<>(WorldEvent.getActiveEvents()).forEach( WorldEvent::stopEvent);
		} catch (Exception e) {
			ProjectKorraRPG.getPlugin().getLogger().severe("Failed to stop all active events!" + e.getMessage());
		}

		if (this.modificationListener != null) {
			HandlerList.unregisterAll(this.modificationListener);
			this.modificationListener = null;
			ProjectKorraRPG.getPlugin().getLogger().info("WorldEvent modification listener unregistered");
		}

		// Clear Worldevent maps
		WorldEvent.getActiveEvents().clear();
		WorldEvent.getAllEvents().clear();
		WorldEvent.getAffectedPlayers().clear();

		ProjectKorraRPG.getPlugin().getLogger().info("WorldEvent module disabled successfully");
	}

	public WorldEventScheduler getWorldEventScheduler() {
		return this.worldEventScheduler;
	}

	public WorldEventModificationService getModificationService() {
		return modificationService;
	}
}
