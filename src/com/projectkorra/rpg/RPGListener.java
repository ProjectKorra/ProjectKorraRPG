package com.projectkorra.rpg;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.projectkorra.ProjectKorra.BendingPlayer;
import com.projectkorra.ProjectKorra.Methods;
import com.projectkorra.ProjectKorra.Ability.AvatarState;

public class RPGListener implements Listener{
	
	@EventHandler
	public void onAvatarDamaged(EntityDamageEvent event) {
		if(event.isCancelled()) return;
		
		if(!ProjectKorraRPG.plugin.getConfig().getBoolean("Abilities.AvatarStateOnFinalBlow")) return;
		
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(Methods.getBendingPlayer(player.getName()) != null) {
				BendingPlayer bp = Methods.getBendingPlayer(player.getName());
				
				if(bp.isOnCooldown("AvatarState")) return;
				
				if(Methods.hasPermission(player, "AvatarState")) {
					if(player.getHealth() - event.getDamage() <= 0) {
						event.setCancelled(true);
						new AvatarState(player);
					}
				}
			}
		}
	}

}
