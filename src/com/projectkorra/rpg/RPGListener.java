package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class RPGListener implements Listener{

	public RPGListener() {}

	/*
	TODO: Find a better spot to place this / handle this
	 */
	@EventHandler
	public void onBendingPlayerDeath(final PlayerDeathEvent event) {
		boolean valid = true;
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getEntity());
		if (bPlayer != null) {
			if (ProjectKorraRPG.plugin.getAvatarManager().isEnabled()) {
				if(ProjectKorraRPG.plugin.getAvatarManager().isCurrentRPGAvatar(bPlayer.getUUID())) {
					valid = false;
					if (ProjectKorraRPG.plugin.getAvatarManager().handleAvatarDeath(bPlayer)) {
						bPlayer.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You have been lost Avatar");
						ProjectKorraRPG.log.info(bPlayer.getName() + " is no longer the Avatar.");
					}
				}
			}
			if (ProjectKorraRPG.plugin.getAssignmentManager().isEnabled()) {
				if (valid) {
					ProjectKorraRPG.plugin.getAssignmentManager().assignRandomGroup(bPlayer, true);
				}
			}
		}
	}
}
