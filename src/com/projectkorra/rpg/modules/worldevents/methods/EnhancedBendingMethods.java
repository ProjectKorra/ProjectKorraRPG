package com.projectkorra.rpg.modules.worldevents.methods;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityRecalculateAttributeEvent;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.attribute.AttributeModification;
import com.projectkorra.projectkorra.attribute.AttributeModifier;
import com.projectkorra.projectkorra.attribute.AttributeUtil;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import commonslang3.projectkorra.lang3.tuple.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

public class EnhancedBendingMethods {

	/**
	 * For every active worldevent, look up any matching modifier in its config
	 * and apply it to the event.
	 */
	public static void applyWorldEventMods(AbilityRecalculateAttributeEvent event) {
		CoreAbility ability = event.getAbility();
		String element = ability.getElement().getName();
		if (ability.getElement() instanceof Element.SubElement) {
			element = ((Element.SubElement) ability.getElement())
					.getParentElement()
					.getName();
		}

		String abilityName   = ability.getName();
		String attributeName = event.getAttribute();

		for (WorldEvent worldEvent : WorldEvent.getActiveEvents()) {
			// filter by element & ability
			if (!worldEvent.getAffectedElements().isEmpty()
					&& !worldEvent.getAffectedElements().contains(element)) continue;
			if (!worldEvent.getAffectedAbilities().isEmpty()
					&& !worldEvent.getAffectedAbilities().contains(abilityName)) continue;

			FileConfiguration config = worldEvent.getConfig();

			// three‑tier lookup in Abilities.<Elem>.<Ability>.<Attr>
			String path = String.format("Abilities.%s.%s.%s", element, abilityName, attributeName);
			Object raw = config.get(path);
			if (raw == null) raw = config.get("Abilities." + element + "._All." + attributeName);
			if (raw == null) raw = config.get("Abilities._All." + attributeName);
			if (raw == null) continue;

			// build a unique NamespacedKey for this event/ability/attribute
			String id = String.join("_",
					"worldevent",
					worldEvent.getKey(),
					element.toLowerCase(),
					abilityName.toLowerCase(),
					attributeName.toLowerCase()
			);

			NamespacedKey namespacedKey = new NamespacedKey(ProjectKorraRPG.getPlugin(), id);

			// parse & build the modification
			AttributeModification mod = buildModification(raw, raw.toString().replace(" ", ""), namespacedKey);
			if (mod != null) {
				event.addModification(mod);
			}
		}
	}

	private static AttributeModification buildModification(Object raw, String rawStr, NamespacedKey nsKey) {
		if (raw instanceof Boolean) {
			return AttributeModification.setter((Boolean) raw, AttributeModification.PRIORITY_NORMAL, nsKey);
		}

		if (raw instanceof Number) {
			return AttributeModification.of(AttributeModifier.SET, (Number) raw, AttributeModification.PRIORITY_NORMAL, nsKey);
		}

		Pair<AttributeModifier, Number> parsed = AttributeUtil.getModification(rawStr);

		if (parsed != null) {
			return AttributeModification.of(parsed.getLeft(), parsed.getRight(), AttributeModification.PRIORITY_NORMAL, nsKey);
		}

		ProjectKorraRPG.getPlugin().getLogger().warning("WorldEvent parse failed for key=" + nsKey.getKey() + " raw=" + rawStr);
		return null;
	}
}
