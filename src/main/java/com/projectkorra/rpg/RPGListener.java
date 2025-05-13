package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;
import com.projectkorra.rpg.modules.randomavatar.commands.AvatarCommand;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.commands.WorldEventCommand;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RPGListener implements Listener {
	private final AvatarManager avatarManager;
	private final AssignmentManager assignmentManager;

	public RPGListener() {
		this.avatarManager = ProjectKorraRPG.getPlugin().getModuleManager().getRandomAvatarModule().getAvatarManager();
		this.assignmentManager = ProjectKorraRPG.getPlugin().getModuleManager().getElementAssignmentsModule().getAssignmentManager();
	}

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
			if (avatarManager.isEnabled()) {
				if (avatarManager.isCurrentRPGAvatar(bPlayer.getUUID())) {
					valid = false;
					if (avatarManager.handleAvatarDeath(bPlayer)) {
						ChatUtil.sendBrandingMessage(bPlayer.getPlayer(), Element.AVATAR.getColor() + "You have lost Avatar");
						ProjectKorraRPG.getPlugin().getLogger().info(bPlayer.getName() + " is no longer the Avatar.");
					}
				}
			}

			if (assignmentManager.isEnabled()) {
				if (valid) {
					assignmentManager.assignRandomGroup(bPlayer, true);
				}
			}
		}
	}
}
