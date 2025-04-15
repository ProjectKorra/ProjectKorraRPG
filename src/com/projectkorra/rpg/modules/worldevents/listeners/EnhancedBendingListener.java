package com.projectkorra.rpg.modules.worldevents.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.event.AbilityRecalculateAttributeEvent;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class EnhancedBendingListener implements Listener {

	@EventHandler
	public void onBend(AbilityRecalculateAttributeEvent event) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getAbility().getPlayer());
		WorldEvent activeEvent = WorldEvent.getActiveEvents().get(0);
		CoreAbility abilityToEnhance = event.getAbility();
		List<Element> affectedElements = activeEvent.getAffectedElements();
		List<Attribute> affectedAttributes = activeEvent.getAffectedAttributes();
	}
}
