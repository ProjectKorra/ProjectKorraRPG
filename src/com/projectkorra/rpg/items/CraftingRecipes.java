package com.projectkorra.rpg.items;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.api.MechaAPI;

public class CraftingRecipes {
	
	static ProjectKorraRPG plugin;
	
	public CraftingRecipes(ProjectKorraRPG plugin) {
		CraftingRecipes.plugin = plugin;
		registerRecipes();
	}
	
	static FileConfiguration config = ProjectKorraRPG.plugin.getConfig();
	
	public static void registerRecipes() {
		if (config.getBoolean("CustomItems.MechaSuitArmor.Enabled")) {
			plugin.getServer().addRecipe(mechaHelmet);
			plugin.getServer().addRecipe(mechaChestplate);
			plugin.getServer().addRecipe(mechaLeggings);
			plugin.getServer().addRecipe(mechaBoots);
		}
		
		if (config.getBoolean("CustomItems.MechaSuit.Enabled")) {
			ShapelessRecipe sr = new ShapelessRecipe(MechaAPI.createMechaSuit());
			sr.addIngredient(1, Material.IRON_HELMET);
			sr.addIngredient(1, Material.IRON_CHESTPLATE);
			sr.addIngredient(1, Material.IRON_LEGGINGS);
			sr.addIngredient(1, Material.IRON_BOOTS);
			plugin.getServer().addRecipe(sr);
		}
	}
	
	static ShapedRecipe mechaHelmet = new ShapedRecipe(MechaAPI.createHelmetPiece())
		.shape("***", "*a*", "***")
		.setIngredient('*', Material.REDSTONE)
		.setIngredient('a', Material.IRON_HELMET);
	
	static ShapedRecipe mechaChestplate = new ShapedRecipe(MechaAPI.createChestPiece())
		.shape("***", "*a*", "***")
		.setIngredient('*', Material.REDSTONE)
		.setIngredient('a', Material.IRON_CHESTPLATE);
	
	static ShapedRecipe mechaLeggings = new ShapedRecipe(MechaAPI.createLeggingPiece())
	.shape("***", "*a*", "***")
	.setIngredient('*', Material.REDSTONE)
	.setIngredient('a', Material.IRON_LEGGINGS);
	
	static ShapedRecipe mechaBoots = new ShapedRecipe(MechaAPI.createBootPiece())
	.shape("***", "*a*", "***")
	.setIngredient('*', Material.REDSTONE)
	.setIngredient('a', Material.IRON_BOOTS);

}
