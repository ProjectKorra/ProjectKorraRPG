package com.projectkorra.rpg;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AvatarState;

public class RPGListener implements Listener{
	
	@EventHandler
	public void onAvatarDamaged(EntityDamageEvent event) {
		if(event.isCancelled()) return;
		
		if(ProjectKorraRPG.plugin.getConfig().getBoolean("Abilities.AvatarStateOnFinalBlow")) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				if (GeneralMethods.getBendingPlayer(player.getName()) != null && (RPGMethods.isCurrentAvatar(player.getUniqueId()) || RPGMethods.hasBeenAvatar(player.getUniqueId()))) {
					BendingPlayer bP = GeneralMethods.getBendingPlayer(player.getName());
					
					if (player.getHealth() - event.getDamage() <= 0) {
						if (!bP.isOnCooldown("AvatarState")) {
							event.setCancelled(true);
							new AvatarState(player);
						} 
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void randomElementAssign(PlayerJoinEvent event) {

		if(!ProjectKorraRPG.plugin.getConfig().getBoolean("ElementAssign.Enabled")) return;
		
		if(GeneralMethods.getBendingPlayer(event.getPlayer().getName()) != null) {
			BendingPlayer bp = GeneralMethods.getBendingPlayer(event.getPlayer().getName());
			
			if((bp.getElements().isEmpty()) && (!bp.isPermaRemoved())) {
				RPGMethods.randomAssign(bp);
			}
		}
	}

}
