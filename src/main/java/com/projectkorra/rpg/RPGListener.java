package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RPGListener implements Listener {
	@EventHandler
	public void onBendingConfigReload(BendingReloadEvent event) {
		ConfigManager.reloadConfigs();

		new BukkitRunnable() {
			@Override
			public void run() {
				new RPGCommandBase();
				new AvatarCommand();
				new HelpCommand();
				new WorldEventCommand();
			}
		}.runTaskLater(ProjectKorraRPG.getPlugin(), 20);

		for (WorldEvent worldEvent : WorldEvent.getActiveEvents()) {
			worldEvent.stopEvent();
		}

		ProjectKorraRPG.getPlugin().getModuleManager().enableModules();
	}

	@EventHandler
	public void onBendingPlayerDeath(final PlayerDeathEvent event) {
		boolean valid = true;
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getEntity());
		if (bPlayer != null) {
			if (ProjectKorraRPG.getPlugin().getModuleManager().getRandomAvatarModule().getAvatarManager().isEnabled()) {
				if(ProjectKorraRPG.getPlugin().getModuleManager().getRandomAvatarModule().getAvatarManager().isCurrentRPGAvatar(bPlayer.getUUID())) {
					valid = false;
					if (ProjectKorraRPG.getPlugin().getModuleManager().getRandomAvatarModule().getAvatarManager().handleAvatarDeath(bPlayer)) {
						bPlayer.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You have been lost Avatar");
						ProjectKorraRPG.getPlugin().getLogger().info(bPlayer.getName() + " is no longer the Avatar.");
					}
				}
			}
			if (ProjectKorraRPG.getPlugin().getModuleManager().getElementAssignmentsModule().getAssignmentManager().isEnabled()) {
				if (valid) {
					ProjectKorraRPG.getPlugin().getModuleManager().getElementAssignmentsModule().getAssignmentManager().assignRandomGroup(bPlayer, true);
				}
			}
		}
	}
}
