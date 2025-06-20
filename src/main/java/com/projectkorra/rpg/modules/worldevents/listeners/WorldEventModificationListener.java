package com.projectkorra.rpg.modules.worldevents.listeners;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityRecalculateAttributeEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.methods.WorldEventModificationService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class WorldEventModificationListener implements Listener {
	private final WorldEventModificationService modificationService;

	public WorldEventModificationListener(WorldEventModificationService modificationService) {
		this.modificationService = modificationService;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAttributeRecalc(final AbilityRecalculateAttributeEvent event) {
		this.modificationService.applyWorldEventMods(event);
	}

	@EventHandler
	public void onWorldEventStop(final WorldEventStopEvent event) {
		for (CoreAbility ability : CoreAbility.getAbilitiesByInstances()) {
			ability.recalculateAttributes();
		}
	}
}
