package com.projectkorra.rpg.ability;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.ability.AbilityTiers.AbilityTier;

public class AbilityScroll {

	public static ItemStack get(CoreAbility ability) {
		ItemStack is = new ItemStack(Material.PAPER);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(ability.getName() + " Scroll");
		String element = "Element: " + ability.getElement().getColor() + ability.getElement().getName();
		String tier = "Tier: " + ProjectKorraRPG.getAbilityTiers().getAbilityTier(ability);
		String scrolls = "# needed to learn: " + ProjectKorraRPG.getAbilityTiers().getAbilityTier(ability).getLevel() / 10;
		im.setLore(Arrays.asList(element, tier, scrolls));
		
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack getRandomScroll() {
		List<CoreAbility> abilities = ProjectKorraRPG.getAbilityTiers().getAbilitiesFromTiers(AbilityTier.NOVICE, AbilityTier.INTERMEDIATE, AbilityTier.ADVANCED, AbilityTier.MASTER);
		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			rand.nextInt(abilities.size());
		}
		
		return get(abilities.get(rand.nextInt(abilities.size())));
	}
}
