package com.projectkorra.rpg.modules.worldevents.listener;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.event.AbilityRecalculateAttributeEvent;
import com.projectkorra.rpg.modules.worldevents.service.WorldEventModificationService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class WorldEventModificationListener implements Listener {
	private final WorldEventModificationService modificationService;

	public WorldEventModificationListener(final WorldEventModificationService modificationService) {
		this.modificationService = modificationService;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAttributeRecalc(final AbilityRecalculateAttributeEvent event) {
        BendingPlayer bendingPlayer = event.getAbility().getBendingPlayer();
        if (bendingPlayer == null) return;
        Player player = bendingPlayer.getPlayer();
        if (player == null || !player.isOnline()) return;

        if (!modificationService.getActiveEventsIndex().hasActiveIn(player.getWorld())) return;

        modificationService.applyWorldEventMods(event, player.getWorld());
	}
}
