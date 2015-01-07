package com.projectkorra.rpg;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.ProjectKorra.BendingPlayer;
import com.projectkorra.ProjectKorra.Methods;
import com.projectkorra.ProjectKorra.ProjectKorra;
import com.projectkorra.ProjectKorra.Ability.AvatarState;

public class RPGListener implements Listener{
	
	public static ConcurrentHashMap<String, IronGolem> riding = new ConcurrentHashMap<String, IronGolem>();
	public static ConcurrentHashMap<IronGolem, Location> unmounted = new ConcurrentHashMap<IronGolem, Location>();
	
	@EventHandler
	public void onAvatarDamaged(EntityDamageEvent event) {
		if(event.isCancelled()) return;
		
		if(ProjectKorraRPG.plugin.getConfig().getBoolean("Abilities.AvatarStateOnFinalBlow")) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				if (Methods.getBendingPlayer(player.getName()) != null && (RPGMethods.isCurrentAvatar(player.getUniqueId()) || RPGMethods.hasBeenAvatar(player.getUniqueId()))) {
					BendingPlayer bP = Methods.getBendingPlayer(player.getName());
					
					if (player.getHealth() - event.getDamage() <= 0) {
						if (AvatarState.cooldowns.containsKey(player.getName())) {
							if (AvatarState.cooldowns.get(player.getName()) + ProjectKorra.plugin.getConfig().getLong("Abilities.AvatarState.Cooldown") >= System.currentTimeMillis()) {
								return;
							} else {
								AvatarState.cooldowns.remove(player.getName());
							}
						} 
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
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block block = event.getClickedBlock();
		
		ItemStack is = p.getItemInHand();
		if (is != null && is.hasItemMeta() && block != null) {
			if (is.getItemMeta().getDisplayName().equals(Methods.getChiColor() + "Mecha Suit")) {
				LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(block.getLocation().add(0, 1, 0), EntityType.IRON_GOLEM);
				entity.setCustomName(Methods.getChiColor() + "Mecha Suit");
				entity.setHealth(entity.getHealth() / 2);
				unmounted.put((IronGolem)entity, entity.getLocation());
				p.getInventory().remove(is);
				p.updateInventory();
			}
		}
	}
	
	@EventHandler
	public void onPlayerGetInSuit(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		
		if(event.getRightClicked() instanceof IronGolem) {
			IronGolem suit = (IronGolem) event.getRightClicked();
			
			if(suit.getCustomName().equalsIgnoreCase(Methods.getChiColor() + "Mecha Suit")) {
				
				if(riding.contains(suit)) {
					for(String key : riding.keySet()) {
						if(riding.get(key) == suit) {
							riding.remove(key);
							unmounted.put(suit, suit.getLocation());
							return;
						}
					}
				}
				
				player.teleport(suit.getLocation());
				riding.put(player.getUniqueId().toString(), suit);
				unmounted.remove(suit);
			}
		}
	}
	
	@EventHandler
	public void riderDamaged(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			for(String key : riding.keySet()) {
				if(key.equalsIgnoreCase(player.getUniqueId().toString())) {
					riding.get(key).damage(event.getDamage());
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void suitDie(EntityDeathEvent event) {
		if(event.getEntity() instanceof IronGolem) {
			IronGolem suit = (IronGolem) event.getEntity();
			
			if(riding.contains(suit)) {
				for(String key : riding.keySet()) {
					if(riding.get(key) == suit) {
						riding.remove(key);
					}
				}
				event.getDrops().clear();
			}
			if(unmounted.containsKey(suit)) {
				unmounted.remove(suit);
				event.getDrops().clear();
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
