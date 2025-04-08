package com.projectkorra.rpg.modules.randomelements.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;

public class RandomElementMethods {
	private static final FileConfiguration config = ConfigManager.getFileConfig();

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
			Element chosen;

			if (roll < waterChance) {
				chosen = Element.WATER;
			} else if ((roll -= waterChance) < earthChance) {
				chosen = Element.EARTH;
			} else if (roll - earthChance < fireChance) {
				chosen = Element.FIRE;
			} else {
				chosen = Element.AIR;
			}
			elements.add(chosen);

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
		Random random = new Random(System.nanoTime());
		List<Element.SubElement> subElements = new ArrayList<>();

		if (element == Element.WATER) {
			double bloodChance = config.getDouble("Configuration.Modules.RandomElements.Element.Water.Blood.Chance", 1);
			double iceChance = config.getDouble("Configuration.Modules.RandomElements.Element.Water.Ice.Chance", 75);
			double healingChance = config.getDouble("Configuration.Modules.RandomElements.Element.Water.Healing.Chance", 50);
			double plantChance = config.getDouble("Configuration.Modules.RandomElements.Element.Water.Plant.Chance", 50);

			if (random.nextDouble() * 100 < bloodChance) {
				subElements.add(Element.SubElement.BLOOD);
			}
			if (random.nextDouble() * 100 < iceChance) {
				subElements.add(Element.SubElement.ICE);
			}
			if (random.nextDouble() * 100 < healingChance) {
				subElements.add(Element.SubElement.HEALING);
			}
			if (random.nextDouble() * 100 < plantChance) {
				subElements.add(Element.SubElement.PLANT);
			}
		} else if (element == Element.EARTH) {
			double lavaChance = config.getDouble("Configuration.Modules.RandomElements.Element.Earth.Lava.Chance", 5);
			double metalChance = config.getDouble("Configuration.Modules.RandomElements.Element.Earth.Metal.Chance", 35);
			double sandChance = config.getDouble("Configuration.Modules.RandomElements.Element.Earth.Sand.Chance", 85);

			if (random.nextDouble() * 100 < lavaChance) {
				subElements.add(Element.SubElement.LAVA);
			}
			if (random.nextDouble() * 100 < metalChance) {
				subElements.add(Element.SubElement.METAL);
			}
			if (random.nextDouble() * 100 < sandChance) {
				subElements.add(Element.SubElement.SAND);
			}
		} else if (element == Element.FIRE) {
			double lightningChance = config.getDouble("Configuration.Modules.RandomElements.Element.Fire.Lightning.Chance", 25);
			double combustionChance = config.getDouble("Configuration.Modules.RandomElements.Element.Fire.Combustion.Chance", 5);
			double blueFireChance = config.getDouble("Configuration.Modules.RandomElements.Element.Fire.BlueFire.Chance", 5);

			if (random.nextDouble() * 100 < lightningChance) {
				subElements.add(Element.SubElement.LIGHTNING);
			}
			if (random.nextDouble() * 100 < combustionChance) {
				subElements.add(Element.SubElement.COMBUSTION);
			}
			if (random.nextDouble() * 100 < blueFireChance) {
				subElements.add(Element.SubElement.BLUE_FIRE);
			}
		} else {
			double flightChance = config.getDouble("Configuration.Modules.RandomElements.Element.Air.Flight.Chance", 1);
			double spiritChance = config.getDouble("Configuration.Modules.RandomElements.Element.Air.Spiritual.Chance", 50);

			if (random.nextDouble() * 100 < flightChance) {
				subElements.add(Element.SubElement.FLIGHT);
			}
			if (random.nextDouble() * 100 < spiritChance) {
				subElements.add(Element.SubElement.SPIRITUAL);
			}
		}
		return subElements;
	}
}
