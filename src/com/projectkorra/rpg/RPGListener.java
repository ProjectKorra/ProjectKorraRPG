package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.avatar.AvatarState;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.event.FullMoonEvent;
import com.projectkorra.rpg.event.LunarEclipseEvent;
import com.projectkorra.rpg.event.SolarEclipseEvent;
import com.projectkorra.rpg.event.SozinsCometEvent;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;

public class RPGListener implements Listener{

	@EventHandler
	public void onAvatarDamaged(EntityDamageEvent event) {
		if(event.isCancelled()) return;

		if(ProjectKorraRPG.plugin.getConfig().getBoolean("Abilities.AvatarStateOnFinalBlow")) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				if (BendingPlayer.getBendingPlayer(player.getName()) != null && (RPGMethods.isCurrentAvatar(player.getUniqueId()) || RPGMethods.hasBeenAvatar(player.getUniqueId()))) {
					BendingPlayer bP = BendingPlayer.getBendingPlayer(player.getName());
					if (event.getCause() == DamageCause.FALL && bP.hasElement(Element.AIR)) return;
					else if (event.getCause() == DamageCause.FALL && bP.hasElement(Element.EARTH) && EarthAbility.isEarthbendable(player, player.getLocation().getBlock().getRelative(BlockFace.DOWN))) return;
					else if (event.getCause() == DamageCause.FALL) {
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
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onFullMoon(FullMoonEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		EventManager.marker.put(world, "FullMoon");
		for (Player player : world.getPlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null && bPlayer.hasElement(Element.WATER) && bPlayer.isElementToggled(Element.WATER)) {
				if (player.hasPermission("bending.message.nightmessage")) {
					player.sendMessage(Element.ICE.getColor() + event.getMessage());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLunarEclipse(LunarEclipseEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		EventManager.marker.put(world, "LunarEclipse");
		for (Player player : world.getPlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null && bPlayer.hasElement(Element.WATER) && bPlayer.isElementToggled(Element.WATER)) {
				if (player.hasPermission("bending.message.nightmessage")) {
					player.sendMessage(Element.WATER.getColor() + event.getMessage());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSolarEclipse(SolarEclipseEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		EventManager.marker.put(world, "SolarEclipse");
		for (Player player : world.getPlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null && bPlayer.hasElement(Element.FIRE) && bPlayer.isElementToggled(Element.FIRE)) {
				if (player.hasPermission("bending.message.daymessage")) {
					player.sendMessage(Element.FIRE.getColor() + event.getMessage());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSozinsComet(SozinsCometEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		EventManager.marker.put(world, "SozinsComet");
		for (Player player : world.getPlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null && bPlayer.hasElement(Element.FIRE) && bPlayer.isElementToggled(Element.FIRE)) {
				if (player.hasPermission("bending.message.daymessage")) {
					player.sendMessage(Element.COMBUSTION.getColor() + event.getMessage());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void randomElementAssign(PlayerJoinEvent event) {
		if(!ProjectKorraRPG.plugin.getConfig().getBoolean("ElementAssign.Enabled")) return;

		if(BendingPlayer.getBendingPlayer(event.getPlayer().getName()) != null) {
			BendingPlayer bp = BendingPlayer.getBendingPlayer(event.getPlayer().getName());

			if((bp.getElements().isEmpty()) && (!bp.isPermaRemoved())) {
				RPGMethods.randomAssign(bp);
			}
		}
	}
}
