package com.projectkorra.rpg.modules.worldevents.listeners;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityRecalculateAttributeEvent;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStartEvent;
import com.projectkorra.rpg.modules.worldevents.event.WorldEventStopEvent;
import com.projectkorra.rpg.modules.worldevents.methods.EnhancedBendingMethods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EnhancedBendingListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onAttributeRecalc(AbilityRecalculateAttributeEvent event) {
		EnhancedBendingMethods.applyWorldEventMods(event);
	}

	@EventHandler
	public void onWorldEventStart(WorldEventStartEvent event) {}

	@EventHandler
	public void onWorldEventStop(WorldEventStopEvent event) {
		for (CoreAbility ability : CoreAbility.getAbilitiesByInstances()) {
			ability.recalculateAttributes();
		}
	}
}
