package com.projectkorra.rpg;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.OfflineBendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.avatar.AvatarState;
import com.projectkorra.projectkorra.event.BendingPlayerLoadEvent;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.event.EventManager;
import com.projectkorra.rpg.event.FullMoonEvent;
import com.projectkorra.rpg.event.LunarEclipseEvent;
import com.projectkorra.rpg.event.SolarEclipseEvent;
import com.projectkorra.rpg.event.SozinsCometEvent;
import com.projectkorra.rpg.event.WorldSunRiseEvent;
import com.projectkorra.rpg.event.WorldSunSetEvent;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class RPGListener implements Listener{
    
	private boolean finalState = false;

	public RPGListener(){
		finalState = ConfigManager.rpgConfig.get().getBoolean("Abilities.AvatarStateOnFinalBlow");
	}

	@EventHandler
	public void onAvatarDamaged(EntityDamageEvent event) {
		if(event.isCancelled()) return;

		if(finalState) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				BendingPlayer bP = BendingPlayer.getBendingPlayer(player.getName());
				if (bP != null && (RPGMethods.isCurrentAvatar(player.getUniqueId()))) {
					if (event.getCause() == DamageCause.FALL &&
							bP.hasElement(Element.AIR))
					{
						return;
					}
					else if (event.getCause() == DamageCause.FALL &&
							bP.hasElement(Element.EARTH) &&
							EarthAbility.isEarthbendable(player, player.getLocation().getBlock().getRelative(BlockFace.DOWN)))
					{
						return;
					}
					else
					{
						if (player.getHealth() - event.getDamage() <= 0)
						{
                            if (bP.canBendIgnoreBindsCooldowns(CoreAbility.getAbility("AvatarState")))
                            {
                                    if (!bP.isOnCooldown("AvatarState"))
                                    {
                                            player.setHealth(2);
                                            event.setCancelled(true);
                                            new AvatarState(player);
                                            return;
                                    } 
                            }
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onBendingPlayerDeath(PlayerDeathEvent event) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getEntity());
		if (bPlayer != null) {
			if (RPGMethods.isCurrentAvatar(bPlayer.getUUID())) {
				ProjectKorraRPG.log.log(java.util.logging.Level.INFO, "The Avatar has died by " + event.getEntity().getLastDamageCause().getCause());
				RPGMethods.cycleAvatar(bPlayer);
			} else {
				ProjectKorraRPG.plugin.getAssignmentManager().assignRandomGroup(bPlayer, true);
			}
		}
	}


	@EventHandler
	public void onPlayerJoin(BendingPlayerLoadEvent event) {
		if (event.getBendingPlayer().isOnline()) {
			BendingPlayer bPlayer = (BendingPlayer) event.getBendingPlayer();
			if (bPlayer.getElements().isEmpty() && !bPlayer.isPermaRemoved()) {
				ProjectKorraRPG.plugin.getAssignmentManager().assignRandomGroup(bPlayer, false);
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
		
		if (EventManager.marker.get(world).equals("LunarEclipse")) {
			event.setCancelled(true);
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
		
		if (EventManager.marker.get(world).equals("SozinsComet")) {
			event.setCancelled(true);
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

		if (RPGMethods.isSozinsComet(event.getWorld()) && !EventManager.marker.get(event.getWorld()).equals("SozinsComet")) {
			ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new SozinsCometEvent(event.getWorld()));
		} else if (RPGMethods.isSolarEclipse(event.getWorld()) && !EventManager.marker.get(event.getWorld()).equals("SolarEclipse")) {
			ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new SolarEclipseEvent(event.getWorld()));
		}
	}

	@EventHandler
	public void onSunSet(WorldSunSetEvent event) {
		if (RPGMethods.isHappening(event.getWorld(), "SolarEclipse") || RPGMethods.isHappening(event.getWorld(), "SozinsComet")) {
			EventManager.marker.put(event.getWorld(), "");
		}

		if (RPGMethods.isLunarEclipse(event.getWorld()) && !EventManager.marker.get(event.getWorld()).equals("LunarEclipse")) {
			ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new LunarEclipseEvent(event.getWorld()));
		} else if (RPGMethods.isFullMoon(event.getWorld()) && !EventManager.marker.get(event.getWorld()).equals("FullMoon")) {
			ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new FullMoonEvent(event.getWorld()));
		}
	}
}
