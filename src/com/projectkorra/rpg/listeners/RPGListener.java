package com.projectkorra.rpg.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.configuration.Config;
import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class RPGListener implements Listener {
	@EventHandler
	public void onBendingConfigReload(BendingReloadEvent event) {
		for (Config config : ConfigManager.getAllConfigs()) {
			config.reload();
		}

		initCommands();

		if (ProjectKorraRPG.getPlugin().getModuleManager().getWorldEventsModule().isEnabled()) {
			ProjectKorraRPG.getPlugin().getModuleManager().getWorldEventsModule().enable();
		}
	}

	void initCommands() {
		new RPGCommandBase();
		new AvatarCommand();
		new HelpCommand();
	}

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
