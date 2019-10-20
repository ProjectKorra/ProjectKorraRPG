package com.projectkorra.rpg;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.attribute.AttributeModifier;
import com.projectkorra.projectkorra.avatar.AvatarState;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import com.projectkorra.projectkorra.event.BendingPlayerCreationEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent.Result;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.worldevent.WorldEvent;
import com.projectkorra.rpg.worldevent.event.SunRiseEvent;
import com.projectkorra.rpg.worldevent.event.SunSetEvent;
import com.projectkorra.rpg.worldevent.event.WorldEventEndEvent;
import com.projectkorra.rpg.worldevent.event.WorldEventStartEvent;
import com.projectkorra.rpg.worldevent.util.Time;

public class RPGListener implements Listener {

	private boolean finalState = false;

	public RPGListener() {
		finalState = ConfigManager.getConfig().getBoolean("Avatar.AvatarStateOnFinalBlow");
	}

	@EventHandler
	public void onAvatarDamaged(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (finalState) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player.getName());
				
				if (bPlayer != null && RPGMethods.isCurrentAvatar(player.getUniqueId())) {
					if (event.getCause() == DamageCause.FALL && bPlayer.hasElement(Element.AIR)) {
						return;
					} else if (event.getCause() == DamageCause.FALL && bPlayer.hasElement(Element.EARTH) && EarthAbility.isEarthbendable(player, player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
						return;
					}
					
					if (player.getHealth() - event.getDamage() <= 0) {
						if (bPlayer.canBendIgnoreBindsCooldowns(CoreAbility.getAbility("AvatarState"))) {
							if (!bPlayer.isOnCooldown("AvatarState")) {
								player.setHealth(2);
								event.setCancelled(true);
								new AvatarState(player);
								return;
							}
						}
						
						if (ConfigManager.getConfig().getBoolean("Avatar.AutoCycle.Enabled")) {
							RPGMethods.cycleAvatar(player.getUniqueId());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onAbilityStart(AbilityStartEvent event) {
		CoreAbility ability = (CoreAbility) event.getAbility();
		World world = null;
		if (ability.getLocation() == null) {
			world = ability.getPlayer().getWorld();
		} else {
			world = ability.getLocation().getWorld();
		}
		
		if (world == null) {
			return;
		}
		
		for (WorldEvent we : ProjectKorraRPG.getEventManager().getEventsHappening(world)) {
			if (ability.getElement().equals(we.getElement())) {
				if (we.getModifier() <= 0) {
					event.setCancelled(true);
				} else {
					for (String attribute : we.getAttributes()) {
						String[] split = attribute.split("::");
						ability.addAttributeModifier(split[0], we.getModifier(), AttributeModifier.valueOf(split[1].toUpperCase()));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onElementChange(PlayerChangeElementEvent event) {
		if (event.getResult() == Result.REMOVE) {
			if (RPGMethods.isCurrentAvatar(event.getTarget().getUniqueId())) {
				RPGMethods.revokeAvatar(event.getTarget());
			}
		}
	}

	@EventHandler
	public void onBendingPlayerCreationEvent(BendingPlayerCreationEvent event) {
		if (!ConfigManager.getConfig().getBoolean("ElementAssign.Enabled"))
			return;

		if (event.getBendingPlayer() != null) {
			BendingPlayer bPlayer = event.getBendingPlayer();

			if ((bPlayer.getElements().isEmpty()) && (!bPlayer.isPermaRemoved())) {
				RPGMethods.randomAssignElement(bPlayer);
			}
		}
	}

	@EventHandler
	public void onSunRise(SunRiseEvent event) {
		for (WorldEvent wEvent : WorldEvent.getEvents()) {
			if (wEvent.getTime() == Time.BOTH && ProjectKorraRPG.getEventManager().isHappening(event.getWorld(), wEvent)) {
				WorldEventEndEvent endEvent = new WorldEventEndEvent(event.getWorld(), wEvent);
				Bukkit.getServer().getPluginManager().callEvent(endEvent);
				
				if (endEvent.isCancelled()) {
					continue;
				}
				
				ProjectKorraRPG.getEventManager().endEvent(event.getWorld(), wEvent);
			} else if (wEvent.getTime() == Time.DAY || wEvent.getTime() == Time.BOTH) {
				if ((Math.ceil((event.getWorld().getFullTime()/24000)) + 1) % wEvent.getFrequency() == 0) {
					WorldEventStartEvent startEvent = new WorldEventStartEvent(event.getWorld(), wEvent);
					Bukkit.getServer().getPluginManager().callEvent(startEvent);
					
					if (startEvent.isCancelled()) {
						continue;
					}
					
					ProjectKorraRPG.getEventManager().startEvent(event.getWorld(), wEvent);
				}
			} else {
				if (ProjectKorraRPG.getEventManager().isHappening(event.getWorld(), wEvent)) {
					WorldEventEndEvent endEvent = new WorldEventEndEvent(event.getWorld(), wEvent);
					Bukkit.getServer().getPluginManager().callEvent(endEvent);
					
					if (endEvent.isCancelled()) {
						continue;
					}
					
					ProjectKorraRPG.getEventManager().endEvent(event.getWorld(), wEvent);
				}
			}
		}
	}

	@EventHandler
	public void onSunSet(SunSetEvent event) {
		for (WorldEvent wEvent : WorldEvent.getEvents()) {
			if (wEvent.getTime() == Time.NIGHT) {
				if ((Math.ceil((event.getWorld().getFullTime()/24000)) + 1) % wEvent.getFrequency() == 0) {
					WorldEventStartEvent startEvent = new WorldEventStartEvent(event.getWorld(), wEvent);
					Bukkit.getServer().getPluginManager().callEvent(startEvent);
					
					if (startEvent.isCancelled()) {
						continue;
					}
					
					ProjectKorraRPG.getEventManager().startEvent(event.getWorld(), wEvent);
				}
			} else if (wEvent.getTime() == Time.DAY){
				if (ProjectKorraRPG.getEventManager().isHappening(event.getWorld(), wEvent)) {
					WorldEventEndEvent endEvent = new WorldEventEndEvent(event.getWorld(), wEvent);
					Bukkit.getServer().getPluginManager().callEvent(endEvent);
					
					if (endEvent.isCancelled()) {
						continue;
					}
					
					ProjectKorraRPG.getEventManager().endEvent(event.getWorld(), wEvent);
				}
			}
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getTo().getWorld().equals(event.getFrom().getWorld())) {
			return;
		}
		
		for (WorldEvent we : WorldEvent.getEvents()) {
			if (ProjectKorraRPG.getEventManager().isHappening(event.getFrom().getWorld(), we)) {
				BossBar bar = ProjectKorraRPG.getDisplayManager().getBossBar(event.getTo().getWorld(), we);
				bar.removePlayer(event.getPlayer());
			}
			
			if (ProjectKorraRPG.getEventManager().isHappening(event.getTo().getWorld(), we)) {
				BossBar bar = ProjectKorraRPG.getDisplayManager().getBossBar(event.getTo().getWorld(), we);
				bar.addPlayer(event.getPlayer());
			}
		}
	}
}
