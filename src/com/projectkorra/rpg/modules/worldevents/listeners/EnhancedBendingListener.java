package com.projectkorra.rpg.modules.worldevents.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityRecalculateAttributeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EnhancedBendingListener implements Listener {

	@EventHandler
	public void onBend(AbilityRecalculateAttributeEvent event) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getAbility().getPlayer());
		// WorldEvent activeEvent = WorldEvent.getActiveEvents().get(0);
		CoreAbility abilityToEnhance = event.getAbility();
		// List<String> affectedElements = activeEvent.getAffectedElements();
		// List<String> affectedAttributes = activeEvent.getAffectedAttributes();

	}
}
