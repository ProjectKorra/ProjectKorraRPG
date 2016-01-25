package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.event.FullMoonEvent;
import com.projectkorra.rpg.event.LunarEclipseEvent;
import com.projectkorra.rpg.event.SolarEclipseEvent;
import com.projectkorra.rpg.event.SozinsCometEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
				if (GeneralMethods.getBendingPlayer(player.getName()) != null && (RPGMethods.isCurrentAvatar(player.getUniqueId()) || RPGMethods.hasBeenAvatar(player.getUniqueId()))) {
					BendingPlayer bP = GeneralMethods.getBendingPlayer(player.getName());
					if (event.getCause() == DamageCause.FALL && bP.hasElement(Element.Air)) return;
					if (event.getCause() == DamageCause.FALL && bP.hasElement(Element.Earth) && EarthMethods.isEarthbendable(player, player.getLocation().getBlock().getRelative(BlockFace.DOWN)))
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onFullMoon(FullMoonEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		if (world.getEnvironment().equals(World.Environment.NETHER) || world.getEnvironment().equals(World.Environment.THE_END)) {
			return;
		}
		EventManager.marker.put(world, "FullMoon");
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().equals(world)) {
				if (GeneralMethods.isBender(player.getName(), Element.Water)) {
					if (player.hasPermission("bending.message.nightmessage")) {
						player.sendMessage(ChatColor.DARK_AQUA + event.getMessage());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLunarEclipse(LunarEclipseEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		if (world.getEnvironment().equals(World.Environment.NETHER) || world.getEnvironment().equals(World.Environment.THE_END)) {
			return;
		}
		EventManager.marker.put(world, "LunarEclipse");
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().equals(world)) {
				if (GeneralMethods.isBender(player.getName(), Element.Water)) {
					if (player.hasPermission("bending.message.nightmessage")) {
						player.sendMessage(ChatColor.AQUA + event.getMessage());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSolarEclipse(SolarEclipseEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		if (world.getEnvironment().equals(World.Environment.NETHER) || world.getEnvironment().equals(World.Environment.THE_END)) {
			return;
		}
		EventManager.marker.put(world, "SolarEclipse");
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().equals(world)) {
				if (GeneralMethods.isBender(player.getName(), Element.Fire)) {
					if (player.hasPermission("bending.message.daymessage")) {
						player.sendMessage(ChatColor.RED + event.getMessage());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSozinsComet(SozinsCometEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		EventManager.marker.put(world, "SozinsComet");
		if (world.getEnvironment().equals(World.Environment.NETHER) || world.getEnvironment().equals(World.Environment.THE_END)) {
			return;
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().equals(world)) {
				if (GeneralMethods.isBender(player.getName(), Element.Fire)) {
					if (player.hasPermission("bending.message.daymessage")) {
						player.sendMessage(ChatColor.DARK_RED + event.getMessage());
					}
				}
			}
		}
	}
}
