package com.projectkorra.rpg;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.ProjectKorra.BendingPlayer;
import com.projectkorra.ProjectKorra.Methods;
import com.projectkorra.ProjectKorra.Ability.AvatarState;

public class RPGListener implements Listener{
	
	@EventHandler
	public void onAvatarDamaged(EntityDamageEvent event) {
		if(event.isCancelled()) return;
		
		if(ProjectKorraRPG.plugin.getConfig().getBoolean("Abilities.AvatarStateOnFinalBlow")) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				if (Methods.getBendingPlayer(player.getName()) != null && (RPGMethods.isCurrentAvatar(player.getUniqueId()) || RPGMethods.hasBeenAvatar(player.getUniqueId()))) {
					BendingPlayer bP = Methods.getBendingPlayer(player.getName());
					
					if (player.getHealth() - event.getDamage() <= 0) {
						if (bP.isOnCooldown("AvatarState")) return;
						event.setCancelled(true);
						new AvatarState(player);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void randomElementAssign(PlayerJoinEvent event) {

		if(!ProjectKorraRPG.plugin.getConfig().getBoolean("ElementAssign.Enabled")) return;
		
		if(Methods.getBendingPlayer(event.getPlayer().getName()) != null) {
			BendingPlayer bp = Methods.getBendingPlayer(event.getPlayer().getName());
			
			if((bp.getElements().isEmpty()) && (!bp.isPermaRemoved())) {
				RPGMethods.randomAssign(bp);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block block = event.getClickedBlock();
		
		ItemStack is = p.getItemInHand();
		if (is != null && is.hasItemMeta() && block != null) {
			if (is.getItemMeta().getDisplayName().equals(Methods.getChiColor() + "Mecha Suit")) {
				LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(block.getLocation().add(0, 1, 0), EntityType.IRON_GOLEM);
				entity.setCustomName(Methods.getChiColor() + "Mecha Suit");
				p.getInventory().remove(is);
				p.updateInventory();
			}
		}
	}
	@EventHandler
	public void mechaCrafting(PrepareItemCraftEvent event) {
		CraftingInventory ci = event.getInventory();
		if (ci.getResult().hasItemMeta() && ci.getResult().getItemMeta().getDisplayName().equals(Methods.getChiColor() + "Mecha Suit")) {
			boolean found1 = false, found2 = false, found3 = false, found4 = false;
			for (ItemStack item: ci.getMatrix()) {
				if (item != null && item.hasItemMeta()) {
					if (item.getItemMeta().getDisplayName().equals(Methods.getChiColor() + "Mecha Helmet")) found1 = true;
					else if (item.getItemMeta().getDisplayName().equals(Methods.getChiColor() + "Mecha Chestplate")) found2 = true;
					else if (item.getItemMeta().getDisplayName().equals(Methods.getChiColor() + "Mecha Leggings")) found3 = true;
					else if (item.getItemMeta().getDisplayName().equals(Methods.getChiColor() + "Mecha Boots")) found4 = true;
				}
			}
			
			if (!found1 || !found2 || !found3 || !found4) {
				ci.setResult(null);
			}
		}
	}

}
