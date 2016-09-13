package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.avatar.AvatarState;
import com.projectkorra.projectkorra.event.BendingPlayerCreationEvent;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.event.FullMoonEvent;
import com.projectkorra.rpg.event.LunarEclipseEvent;
import com.projectkorra.rpg.event.SolarEclipseEvent;
import com.projectkorra.rpg.event.SozinsCometEvent;
import com.projectkorra.rpg.event.WorldSunRiseEvent;
import com.projectkorra.rpg.event.WorldSunSetEvent;
import org.bukkit.Bukkit;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class RPGListener implements Listener{

	@EventHandler
	public void onAvatarDamaged(EntityDamageEvent event) {
		if(event.isCancelled()) return;

		if(ConfigManager.rpgConfig.get().getBoolean("Abilities.AvatarStateOnFinalBlow")) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				BendingPlayer bP = BendingPlayer.getBendingPlayer(player.getName());
				if (bP != null && (RPGMethods.isCurrentAvatar(player.getUniqueId()) || RPGMethods.hasBeenAvatar(player.getUniqueId()))) {
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
	
	/*@EventHandler
	public void onAvatarDeath(PlayerDeathEvent event) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getEntity());
		if (RPGMethods.isCurrentAvatar(bPlayer.getUUID())) {
			RPGMethods.cycleAvatar(bPlayer);
		}
	}*/
	
	@EventHandler
	public void onBendingPlayerCreationEvent(BendingPlayerCreationEvent event) {
		if (!ConfigManager.rpgConfig.get().getBoolean("ElementAssign.Enabled")) return;

		if (event.getBendingPlayer() != null) {
			BendingPlayer bPlayer = event.getBendingPlayer();

			if ((bPlayer.getElements().isEmpty()) && (!bPlayer.isPermaRemoved())) {
				RPGMethods.randomAssign(bPlayer);
				if (ConfigManager.rpgConfig.get().getBoolean("SubElementAssign.Enabled")) {
					RPGMethods.randomAssignSubElements(bPlayer);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onFullMoon(FullMoonEvent event) {
		if (event.isCancelled())
			return;
		World world = event.getWorld();
		if (EventManager.skipper.get(world)) {
			EventManager.skipper.put(world, false);
			return;
		}
		EventManager.marker.put(world, "FullMoon");
		for (Player player : world.getPlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null) {
				if (player.hasPermission("bending.message.nightmessage")) {
					player.sendMessage(Element.ICE.getColor() + event.getMessage());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLunarEclipse(LunarEclipseEvent event) {
		if (event.isCancelled())
			return;
		World world = event.getWorld();
		if (EventManager.skipper.get(world)) {
			EventManager.skipper.put(world, false);
			return;
		}
		EventManager.marker.put(world, "LunarEclipse");
		for (Player player : world.getPlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null) {
				if (player.hasPermission("bending.message.nightmessage")) {
					player.sendMessage(Element.WATER.getColor() + event.getMessage());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSolarEclipse(SolarEclipseEvent event) {
		if (event.isCancelled())
			return;
		World world = event.getWorld();
		if (EventManager.skipper.get(world)) {
			EventManager.skipper.put(world, false);
			return;
		}
		EventManager.marker.put(world, "SolarEclipse");
		for (Player player : world.getPlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null) {
				if (player.hasPermission("bending.message.daymessage")) {
					player.sendMessage(Element.FIRE.getColor() + event.getMessage());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSozinsComet(SozinsCometEvent event) {
		if (event.isCancelled())
			return;
		World world = event.getWorld();
		if (EventManager.skipper.get(world)) {
			EventManager.skipper.put(world, false);
			return;
		}
		EventManager.marker.put(world, "SozinsComet");
		for (Player player : world.getPlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer != null) {
				if (player.hasPermission("bending.message.daymessage")) {
					player.sendMessage(Element.COMBUSTION.getColor() + event.getMessage());
				}
			}
		}
	}

	@EventHandler
	public void onSunRise(WorldSunRiseEvent event) {
		if (RPGMethods.isHappening(event.getWorld(), "FullMoon") || RPGMethods.isHappening(event.getWorld(), "LunarEclipse")) {
			EventManager.marker.put(event.getWorld(), "");
		}

		if (RPGMethods.isSozinsComet(event.getWorld())) {
			ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new SozinsCometEvent(event.getWorld()));
		} else if (RPGMethods.isSolarEclipse(event.getWorld())) {
			ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new SolarEclipseEvent(event.getWorld()));
		}
	}

	@EventHandler
	public void onSunSet(WorldSunSetEvent event) {
		if (RPGMethods.isHappening(event.getWorld(), "SolarEclipse") || RPGMethods.isHappening(event.getWorld(), "SozinsComet")) {
			EventManager.marker.put(event.getWorld(), "");
		}

		if (RPGMethods.isLunarEclipse(event.getWorld())) {
			ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new LunarEclipseEvent(event.getWorld()));
		} else if (RPGMethods.isFullMoon(event.getWorld())) {
			ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new FullMoonEvent(event.getWorld()));
		}
	}
}
