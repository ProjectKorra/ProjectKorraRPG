package com.projectkorra.rpg.ability;

import java.util.Arrays;

import com.projectkorra.projectkorra.ability.CoreAbility;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AbilityScroll extends ItemStack {
	
	private CoreAbility ability;

	public AbilityScroll(CoreAbility ability) {
		super(Material.PAPER);
		this.ability = ability;
		
		ItemMeta im = this.getItemMeta();
		
		im.setDisplayName(ability.getElement().getColor() + ability.getName() + " Scroll");
		
		AbilityTier tier = AbilityTiers.getAbilityTier(ability);
		
		String first = tier.getDisplay() + " " + ChatColor.WHITE + ability.getElement().getName() + " ability";
		String second = ChatColor.WHITE + "This ability requires " + tier.getColor() + tier.getRequiredScrolls() + ChatColor.WHITE + " scrolls to learn!";
		
		im.setLore(Arrays.asList(first, second));
		this.setItemMeta(im);
	}
	
	public CoreAbility getAbility() {
		return ability;
	}
	
	public static boolean isScroll(ItemStack is) {
		if (is.getType() != Material.PAPER) {
			return false;
		} else if (!is.hasItemMeta()) {
			return false;
		} else if (!is.getItemMeta().getDisplayName().endsWith("Scroll")) {
			return false;
		} else if (is.getItemMeta().getLore().size() != 2) {
			return false;
		}
		
		return true;
	}
}
