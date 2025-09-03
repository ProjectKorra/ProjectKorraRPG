package com.projectkorra.rpg.modules.worldevents.service;

import com.projectkorra.projectkorra.attribute.AttributeModification;
import com.projectkorra.projectkorra.attribute.AttributeModifier;
import com.projectkorra.projectkorra.attribute.AttributeUtil;
import com.projectkorra.projectkorra.event.AbilityRecalculateAttributeEvent;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.models.AttributeRules;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.storage.ActiveWorldEventIndex;
import commonslang3.projectkorra.lang3.tuple.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

/**
 * A service class responsible for applying world-event-based attribute modifications to abilities.
 * It processes active world events and applies relevant attribute modifications configured within
 * the world event's configuration file to the abilities based on their context (element, ability name, attribute name)
 * Has to listen to the {@link AbilityRecalculateAttributeEvent} to function.
 */
public final class WorldEventModificationService {
    private final ActiveWorldEventIndex activeEventsIndex;

    public WorldEventModificationService(final ActiveWorldEventIndex activeEventsIndex) {
        this.activeEventsIndex = activeEventsIndex;
    }

	/**
	 * Applies modifications from active WorldEvents to the abilities / elements configured in the corresponding config
	 */
	public void applyWorldEventMods(AbilityRecalculateAttributeEvent event, World world) {
        final String element = event.getAbility().getElement().getName();
        final String ability = event.getAbility().getName();
        final String attribute = event.getAttribute();

        for (WorldEvent worldEvent : activeEventsIndex.getActiveIn(world)) {
            AttributeRules rules = worldEvent.getAttributeRules();
            if (rules == null) continue;

            Object raw = rules.find(element, ability, attribute);
            if (raw == null) continue;

            AttributeModification mod = buildModification(raw, worldEvent.getKey());
            if (mod != null) event.addModification(mod);
        }
	}

	private AttributeModification buildModification(Object raw, NamespacedKey key) {
		if (raw instanceof Boolean b) {
			return AttributeModification.setter(b, AttributeModification.PRIORITY_NORMAL, key);
		}

		if (raw instanceof Number n) {
			return AttributeModification.of(AttributeModifier.SET, n, AttributeModification.PRIORITY_NORMAL, key);
		}

		String rawStr = raw.toString().replace(" ", "");
		Pair<AttributeModifier, Number> parsed = AttributeUtil.getModification(rawStr);

		if (parsed != null) {
			return AttributeModification.of(parsed.getLeft(), parsed.getRight(), AttributeModification.PRIORITY_NORMAL, key);
		}

		ProjectKorraRPG.getPlugin().getLogger().warning("WorldEvent parse failed for key:" + key.getKey() + " raw:" + rawStr);
		return null;
	}

    public ActiveWorldEventIndex getActiveEventsIndex() {
        return activeEventsIndex;
    }
}
