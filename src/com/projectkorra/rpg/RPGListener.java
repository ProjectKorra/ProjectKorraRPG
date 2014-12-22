package com.projectkorra.rpg;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.projectkorra.ProjectKorra.BendingPlayer;
import com.projectkorra.ProjectKorra.Methods;
import com.projectkorra.ProjectKorra.ProjectKorra;
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

}
