package com.projectkorra.rpg.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.projectkorra.ProjectKorra.Methods;

public class MechaAPI {

	public static ItemStack createHelmetPiece() {
		ItemStack is = new ItemStack(Material.IRON_HELMET);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Methods.getChiColor() + "Mecha Helmet");
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack createChestPiece() {
		ItemStack is = new ItemStack(Material.IRON_CHESTPLATE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Methods.getChiColor() + "Mecha Chestplate");
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack createLeggingPiece() {
		ItemStack is = new ItemStack(Material.IRON_LEGGINGS);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Methods.getChiColor() + "Mecha Leggings");
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack createBootPiece() {
		ItemStack is = new ItemStack(Material.IRON_BOOTS);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Methods.getChiColor() + "Mecha Boots");
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack createMechaSuit() {
		ItemStack is = new ItemStack(Material.MONSTER_EGG, 1, (short) 99);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(Methods.getChiColor() + "Mecha Suit");
		is.setItemMeta(im);
		return is;
	}
}
