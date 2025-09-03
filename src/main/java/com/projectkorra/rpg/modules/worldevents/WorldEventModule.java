package com.projectkorra.rpg.modules.worldevents;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.factory.WorldEventLoader;
import com.projectkorra.rpg.modules.worldevents.listener.HandleWorldEventDisplayListener;
import com.projectkorra.rpg.modules.worldevents.listener.WorldEventModificationListener;
import com.projectkorra.rpg.modules.worldevents.service.WorldEventModificationService;
import com.projectkorra.rpg.modules.worldevents.service.WorldEventService;
import com.projectkorra.rpg.modules.worldevents.storage.ActiveWorldEventIndex;
import com.projectkorra.rpg.modules.worldevents.storage.WorldEventRegistry;
import com.projectkorra.rpg.modules.worldevents.util.BossBarCleanup;
import org.bukkit.event.HandlerList;

public final class WorldEventModule extends Module {
    private WorldEventService worldEventService;
    private WorldEventRegistry worldEventRegistry;
    private ActiveWorldEventIndex activeWorldEventIndex;
	private WorldEventModificationListener modificationListener;
    private HandleWorldEventDisplayListener handleWorldEventDisplayListener;

	public WorldEventModule(ProjectKorraRPG plugin) {
		super(plugin, "WorldEvents");
	}

	@Override
	public void enable() {
		this.getPlugin().getLogger().info("Enabling WorldEvent module...");

        // Store all stale WorldEvents (Not active ones)
        this.worldEventRegistry = new WorldEventRegistry();

        // Register / Store all valid WorldEvents from configurations
        this.worldEventRegistry.registerAll(new WorldEventLoader(getPlugin()).loadEventsFromFolder().values());

        // Handles Active World Event instances
        this.activeWorldEventIndex = new ActiveWorldEventIndex();

        // Handle business logic of active world events and ticks them
        this.worldEventService = new WorldEventService(this.getPlugin(), this.activeWorldEventIndex);

		// Create Listeners
		this.modificationListener = new WorldEventModificationListener(new WorldEventModificationService(this.activeWorldEventIndex));
        this.handleWorldEventDisplayListener = new HandleWorldEventDisplayListener(this.worldEventService);

        // Register Listeners
		registerListeners(
				this.modificationListener,
                this.handleWorldEventDisplayListener
		);

        // Register Commands
        new WorldEventCommand(this.worldEventService, this.worldEventRegistry);

		this.getPlugin().getLogger().info("WorldEvent module enabled successfully!");
	}

	@Override
	public void disable() {
		this.getPlugin().getLogger().info("Disabling WorldEvent module...");

        // Shutdown Service
        if (this.worldEventService != null) {
            this.worldEventService.shutdown();
            this.worldEventService = null;
        }

        // Nullify Registry
        if (this.worldEventRegistry != null) {
            this.worldEventRegistry = null;
        }

        // Cleanup Active Events Index
        if (this.activeWorldEventIndex != null) {
            this.activeWorldEventIndex.clearAll();
            this.activeWorldEventIndex = null;
        }

		// Unregister ModificationListener
		if (this.modificationListener != null) {
			HandlerList.unregisterAll(this.modificationListener);
			this.modificationListener = null;
		}

        // Unregister EventDisplayListener
        if (this.handleWorldEventDisplayListener != null) {
            HandlerList.unregisterAll(this.handleWorldEventDisplayListener);
            this.handleWorldEventDisplayListener = null;
        }

        // Remove Stale / Dead BossBars
        BossBarCleanup.removeAllFor(this.getPlugin());

		this.getPlugin().getLogger().info(getName() + " module disabled successfully!");
	}

    public WorldEventService getWorldEventService() {
        return worldEventService;
    }

    public WorldEventRegistry getWorldEventRegistry() {
        return worldEventRegistry;
    }

    public ActiveWorldEventIndex getActiveWorldEventIndex() {
        return activeWorldEventIndex;
    }

    public WorldEventModificationListener getModificationListener() {
		return modificationListener;
	}

    public HandleWorldEventDisplayListener getPlayerSwitchWorldListener() {
        return handleWorldEventDisplayListener;
    }
}
