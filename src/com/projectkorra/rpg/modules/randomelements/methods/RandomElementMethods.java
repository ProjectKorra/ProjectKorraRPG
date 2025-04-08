package com.projectkorra.rpg.modules.randomelements.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;

public class RandomElementMethods {
	private static final FileConfiguration config = ConfigManager.getConfig();

	/**
	 * Randomly selects element(s) based on configuration.
	 * <p>
	 * In hybrid mode, exactly one main bending element is chosen,
	 * and then an independent roll determines if Chi is added.
	 * <p>
	 * In non-hybrid mode, a single element is chosen from all possibilities using selection.
	 *
	 * @param hybrid If true, the possibility exists to receive both a main element and Chi.
	 * @return A list of Elements. In hybrid mode, the list always contains one main element and optionally Chi
	 */
	public static List<Element> getRandomizedElements(boolean hybrid) {
		List<Element> elements = new ArrayList<>();
		Random random = new Random(System.nanoTime());

		double waterChance = config.getDouble("Configuration.Modules.RandomElements.Element.Water.Chance", 25);
		double earthChance = config.getDouble("Configuration.Modules.RandomElements.Element.Earth.Chance", 25);
		double fireChance = config.getDouble("Configuration.Modules.RandomElements.Element.Fire.Chance", 25);
		double airChance = config.getDouble("Configuration.Modules.RandomElements.Element.Air.Chance", 25);
		double chiChance = config.getDouble("Configuration.Modules.RandomElements.Element.Chi.Chance", 0);

		if (hybrid) {
			double totalMainChance = waterChance + earthChance + fireChance + airChance;
			double roll = random.nextDouble() * totalMainChance;
			Element mainElement;

			if (roll < waterChance) {
				mainElement = Element.WATER;
			} else if ((roll -= waterChance) < earthChance) {
				mainElement = Element.EARTH;
			} else if (roll - earthChance < fireChance) {
				mainElement = Element.FIRE;
			} else {
				mainElement = Element.AIR;
			}
			elements.add(mainElement);

			if (random.nextDouble() * 100 < chiChance) {
				elements.add(Element.CHI);
			}
		} else {
			double totalChance = chiChance + waterChance + earthChance + fireChance + airChance;

			double roll = random.nextDouble() * totalChance;
			Element chosen;
			if (roll < chiChance) {
				chosen = Element.CHI;
			} else {
				roll -= chiChance;
				if (roll < waterChance) {
					chosen = Element.WATER;
				} else {
					roll -= waterChance;
					if (roll < earthChance) {
						chosen = Element.EARTH;
					} else {
						roll -= earthChance;
						if (roll < fireChance) {
							chosen = Element.FIRE;
						} else {
							chosen = Element.AIR;
						}
					}
				}
			}
			elements.add(chosen);
		}
		return elements;
	}

	/**
	 * Randomly determines sub-elements for a given Element based on config percentages.
	 *
	 * @param element the base element for which to determine sub-elements.
	 * @return a list of sub-elements that have passed the random chance check.
	 */
	public static List<Element.SubElement> getRandomizedSubElements(Element element) {
		return null;
	}
}
