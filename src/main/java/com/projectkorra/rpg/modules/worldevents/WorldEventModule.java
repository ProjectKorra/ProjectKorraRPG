package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.listeners.WorldEventModificationListener;
import com.projectkorra.rpg.modules.worldevents.listeners.WorldEventScheduleListener;
import com.projectkorra.rpg.modules.worldevents.methods.WorldEventModificationService;
import com.projectkorra.rpg.modules.worldevents.schedule.WorldEventScheduler;
import com.projectkorra.rpg.modules.worldevents.schedule.storage.ScheduleStorage;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class WorldEventModule extends Module {
	private WorldEventModificationListener modificationListener;
	private WorldEventModificationService modificationService;

	private WorldEventScheduleListener scheduleListener;
	private WorldEventScheduler worldEventScheduler;
	private ScheduleStorage scheduleStorage;

	public WorldEventModule() {
		super("WorldEvents");
	}

	@Override
	public void enable() {
		ProjectKorraRPG.getPlugin().getLogger().info("Enabling WorldEvent module...");

		// Create DB Table
		this.scheduleStorage = new ScheduleStorage();

		// Initialize all valid WorldEvents found in each config file in the WorldEvents directory
		WorldEvent.initAllWorldEvents();

		// Create ModificationService for Listener
		this.modificationService = new WorldEventModificationService();

		// Scheduler to make events start based on config
		this.worldEventScheduler = new WorldEventScheduler(scheduleListener);

		// Create and Register Modification Listener
		this.modificationListener = new WorldEventModificationListener(this.modificationService);
		this.scheduleListener = new WorldEventScheduleListener(this.worldEventScheduler);

		// Register Commands
		new WorldEventCommand();

		registerListeners(
				this.modificationListener,
				this.scheduleListener
		);

		ProjectKorraRPG.getPlugin().getLogger().info("WorldEvent module enabled successfully!");
	}

	@Override
	public void disable() {
		ProjectKorraRPG.getPlugin().getLogger().info("Disabling WorldEvent module...");

		// Cleanup Scheduler
		if (this.worldEventScheduler != null) {
			this.worldEventScheduler.cleanup();
			this.worldEventScheduler = null;
		}

		// Stop all active events
		try {
			new ArrayList<>(WorldEvent.getActiveEvents()).forEach( WorldEvent::stopEvent);
		} catch (Exception e) {
			ProjectKorraRPG.getPlugin().getLogger().severe("Failed to stop all active events!" + e.getMessage());
		}

		// Unregister ModificationListener
		if (this.modificationListener != null) {
			HandlerList.unregisterAll(this.modificationListener);
			this.modificationListener = null;
		}

		// Clear Worldevent maps
		WorldEvent.getActiveEvents().clear();
		WorldEvent.getAllEvents().clear();
		WorldEvent.getAffectedPlayers().clear();

		ProjectKorraRPG.getPlugin().getLogger().info("WorldEvent module disabled successfully!");
	}

	public WorldEventModificationListener getModificationListener() {
		return modificationListener;
	}

	public WorldEventModificationService getModificationService() {
		return modificationService;
	}

	public WorldEventScheduler getWorldEventScheduler() {
		return this.worldEventScheduler;
	}

	public ScheduleStorage getScheduleStorage() {
		return scheduleStorage;
	}
}
