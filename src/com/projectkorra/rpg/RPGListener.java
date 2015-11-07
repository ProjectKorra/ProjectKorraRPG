package com.projectkorra.rpg;

import java.util.concurrent.ConcurrentHashMap;

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

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.rpg.event.WorldEvent;
import com.projectkorra.rpg.event.WorldEvent.FullMoonEvent;
import com.projectkorra.rpg.event.WorldEvent.LunarEclipseEvent;
import com.projectkorra.rpg.event.WorldEvent.SolarEclipseEvent;
import com.projectkorra.rpg.event.WorldEvent.SozinsCometEvent;

public class RPGListener implements Listener{
	
	private ConcurrentHashMap<World, WorldEvent> events = new ConcurrentHashMap<World, WorldEvent>();
	
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
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFullMoon(FullMoonEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		if (!events.isEmpty() && (events.get(world).equals(WorldEvent.LunarEclipse) || events.get(world).equals(WorldEvent.FullMoon))) {
			event.setCancelled(true);
			return;
		}
		for (Player player : world.getPlayers()) {
			player.sendMessage(ChatColor.DARK_AQUA + event.getMessage());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLunarEclipse(LunarEclipseEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		if (!events.isEmpty() && events.get(world).equals(WorldEvent.LunarEclipse)) {
			event.setCancelled(true);
			return;
		}
		events.put(world, event.getWorldEvent());
		for (Player player : world.getPlayers()) {
			player.sendMessage(WaterMethods.getWaterColor() + event.getMessage());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSolarEclipse(SolarEclipseEvent event) {
		if (event.isCancelled()) return;
		World world = event.getWorld();
		if (!events.isEmpty() && (events.get(world).equals(WorldEvent.SozinsComet) || events.get(world).equals(WorldEvent.SolarEclipse))) {
			event.setCancelled(true);
			return;
		}
		for (Player player : world.getPlayers()) {
			player.sendMessage(FireMethods.getFireColor() + event.getMessage());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSozinsComet(SozinsCometEvent event) {
		World world = event.getWorld();
		WorldEvent we = event.getWorldEvent();
		if (!events.isEmpty() && events.get(world).equals(WorldEvent.SozinsComet)) {
			event.setCancelled(true);
			return;
		}
		events.put(world, we);
		for (Player player : world.getPlayers()) {
			player.sendMessage(ChatColor.DARK_RED + event.getMessage());
		}
	}
}
