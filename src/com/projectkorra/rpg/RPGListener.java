package com.projectkorra.rpg;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.AttributeModifier;
import com.projectkorra.projectkorra.avatar.AvatarState;
import com.projectkorra.projectkorra.event.AbilityDamageEntityEvent;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import com.projectkorra.projectkorra.event.BendingPlayerCreationEvent;
import com.projectkorra.projectkorra.event.EntityBendingDeathEvent;
import com.projectkorra.projectkorra.event.PlayerBindChangeEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent.Result;
import com.projectkorra.projectkorra.util.ActionBar;
import com.projectkorra.rpg.ability.AbilityScroll;
import com.projectkorra.rpg.ability.AbilityTiers.AbilityTier;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.events.RPGPlayerGainXPEvent;
import com.projectkorra.rpg.events.RPGPlayerLevelUpEvent;
import com.projectkorra.rpg.events.SunRiseEvent;
import com.projectkorra.rpg.events.SunSetEvent;
import com.projectkorra.rpg.events.WorldEventEndEvent;
import com.projectkorra.rpg.events.WorldEventStartEvent;
import com.projectkorra.rpg.player.ChakraStats.Chakra;
import com.projectkorra.rpg.player.RPGPlayer;
import com.projectkorra.rpg.worldevent.WorldEvent;
import com.projectkorra.rpg.worldevent.WorldEventInstance;
import com.projectkorra.rpg.worldevent.util.Time;

public class RPGListener implements Listener {

	private boolean finalState = false;
	private Set<AnvilInventory> anvils;

	public RPGListener() {
		finalState = ConfigManager.getConfig().getBoolean("Avatar.AvatarStateOnFinalBlow");
		anvils = new HashSet<>();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAvatarDamaged(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();

		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player.getName());

		if (bPlayer != null && RPGMethods.isCurrentAvatar(player.getUniqueId())) {
			if (event.getCause() == DamageCause.FALL && bPlayer.hasElement(Element.AIR)) {
				return;
			} else if (event.getCause() == DamageCause.FALL && bPlayer.hasElement(Element.EARTH) && EarthAbility.isEarthbendable(player, player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
				return;
			}

			if (player.getHealth() - event.getDamage() <= 0) {
				if (finalState && bPlayer.canBendIgnoreBindsCooldowns(CoreAbility.getAbility("AvatarState"))) {
					if (!bPlayer.isOnCooldown("AvatarState")) {
						player.setHealth(2);
						event.setCancelled(true);
						new AvatarState(player);
						return;
					}
				}
				
				if (ConfigManager.getConfig().getBoolean("Avatar.AutoCycle.Enabled") && !event.isCancelled()) {
					RPGMethods.cycleAvatar(player.getUniqueId());
				}
			}
		}
	}
	
	@EventHandler
	public void onAbilityDamage(AbilityDamageEntityEvent event) {
		Entity e = event.getEntity();
		if (!(e instanceof Player)) {
			return;
		}
		
		RPGPlayer player = RPGPlayer.get((Player) e);
		
		if (player != null) {
			event.setDamage(player.getStats().getPercent(Chakra.EARTH) * event.getDamage());
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
		
		RPGPlayer player = RPGPlayer.get(ability.getPlayer());
		
		if (player != null) {
			if (!player.hasUnlocked(ability) && !((ability instanceof PassiveAbility) || ability.isHiddenAbility())) {
				event.setCancelled(true);
				return;
			}
			
			for (String attr : RPGMethods.getAttributes(ability)) {	
				if (attr.equals(Attribute.DAMAGE)) {
					ability.addAttributeModifier(Attribute.DAMAGE, player.getStats().getPercent(Chakra.FIRE), AttributeModifier.MULTIPLICATION);
				} else if (attr.equals(Attribute.COOLDOWN)) {
					ability.addAttributeModifier(Attribute.COOLDOWN, player.getStats().getPercent(Chakra.WATER), AttributeModifier.MULTIPLICATION);
				} else if (attr.equals(Attribute.RANGE)) {
					ability.addAttributeModifier(Attribute.RANGE, player.getStats().getPercent(Chakra.AIR), AttributeModifier.MULTIPLICATION);
				} else if (RPGMethods.isLightChakraAttribute(attr)) {
					ability.addAttributeModifier(attr, player.getStats().getPercent(Chakra.LIGHT), RPGMethods.getLightChakraAttributeModifier(attr));
				}
			}
		}

		for (WorldEvent we : ProjectKorraRPG.getEventManager().getEventsHappening(world)) {
			Element e = ability.getElement();
			
			if (e instanceof SubElement && !we.getElements().contains(e)) {
				e = ((SubElement) e).getParentElement();
			}
			
			if (we.getElements().contains(e)) {
				if (we.getModifier() <= 0) {
					event.setCancelled(true);
				} else {
					loop: for (String attribute : we.getAttributes()) {
						String[] split = attribute.split("::");
						
						for (Field f : ability.getClass().getDeclaredFields()) {
							if (f.isAnnotationPresent(Attribute.class)) {
								if (f.getAnnotation(Attribute.class).value().equals(split[0])) {
									ability.addAttributeModifier(split[0], we.getModifier(), AttributeModifier.valueOf(split[1].toUpperCase()));
									continue loop;
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onAnvilPrepare(PrepareAnvilEvent event) {
		AnvilInventory anvil = event.getInventory();
		
		if (com.projectkorra.projectkorra.configuration.ConfigManager.getConfig().getStringList("Properties.DisabledWorlds").contains(event.getViewers().get(0).getWorld().getName())) {
			return;
		}
		
		CoreAbility ability = CoreAbility.getAbility(anvil.getRenameText());
		
		if (ability == null) {
			return;
		}
		
		RPGPlayer player = RPGPlayer.get((Player) event.getViewers().get(0));
		
		if (anvil.getItem(0) != null && anvil.getItem(0).getType() != Material.PAPER) {
			event.setResult(null);
			return;
		} else if (anvil.getItem(1) != null && anvil.getItem(1).getType() != Material.INK_SAC) {
			event.setResult(null);
			return;
		} else if (player == null) {
			event.setResult(null);
			return;
		} else if (!player.hasUnlocked(ability)) {
			event.setResult(null);
			return;
		} else if (player.getCurrentTier() != AbilityTier.MASTER) {
			event.setResult(null);
			return;
		} else if (ProjectKorraRPG.getAbilityTiers().getAbilityTier(ability) == AbilityTier.DEFAULT) {
			event.setResult(null);
			return;
		}
		
		anvil.setRepairCost(0);
		event.setResult(new AbilityScroll(ability));
		anvils.add(anvil);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inv = event.getInventory();
		
		if (!(inv instanceof AnvilInventory)) {
			return;
		}
		
		AnvilInventory anvil = (AnvilInventory) inv;
		
		if (!anvils.contains(anvil)) {
			return;
		}
		
		if (event.getViewers().size() < 1) {
			return;
		} else if (!(event.getViewers().get(0) instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getViewers().get(0);
		player.getInventory().addItem(anvil.getItem(2));
		anvil.setItem(0, null);
		anvil.setItem(1, null);
		anvil.setItem(2, null);
		player.updateInventory();
		
		anvils.remove(anvil);
	}
	
	@EventHandler
	public void onItemClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}
		
		RPGPlayer player = RPGPlayer.get(event.getPlayer());
		if (player == null) {
			return;
		}
		
		ItemStack is = event.getItem();
		
		if (!AbilityScroll.isScroll(is)) {
			return;
		}
		
		CoreAbility ability = CoreAbility.getAbility(ChatColor.stripColor(is.getItemMeta().getDisplayName().split(" ")[0]));
		
		if (ability == null) {
			return;
		}
		
		if ((ability.getElement() == Element.AVATAR && !RPGMethods.isCurrentAvatar(event.getPlayer().getUniqueId())) || !player.getBendingPlayer().hasElement(ability.getElement())) {
			ActionBar.sendActionBar(ChatColor.RED + "!> You don't have that ability's element <!", event.getPlayer());
			return;
		}
		
		if (!ProjectKorraRPG.getAbilityTiers().canLearnAbility(player, ability)) {
			ActionBar.sendActionBar(ChatColor.RED + "!> You are not a high enough level to learn that <!", event.getPlayer());
			return;
		}
		
		if (!RPGMethods.hasEnoughScrolls(event.getPlayer(), ability)) {
			ActionBar.sendActionBar(ChatColor.RED + "!> Not enough scrolls <!", event.getPlayer());
			return;
		}
		
		if (!RPGMethods.useScrolls(event.getPlayer(), ability)) {
			ActionBar.sendActionBar(ChatColor.RED + "!> Ability already unlocked <!", event.getPlayer());
		} else {
			ActionBar.sendActionBar(ChatColor.GREEN + "!> " + ability.getName() + " unlocked <!", event.getPlayer());
		}
	}
	
	@EventHandler
	public void onLevelUp(RPGPlayerLevelUpEvent event) {
		Player player = event.getPlayer().getPlayer();
		player.playSound(player.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 0.4f, 0.7f);
		player.sendMessage(ChatColor.GOLD + (ChatColor.BOLD + "You are now level " + ChatColor.DARK_AQUA + (ChatColor.BOLD + "" + event.getNewLevel())));
	}
	
	@EventHandler
	public void onXPGain(RPGPlayerGainXPEvent event) {
		Player player = event.getPlayer().getPlayer();
		ActionBar.sendActionBar(ChatColor.GOLD + (ChatColor.BOLD + (event.getXPGained() >= 0 ? "+" : "") + event.getXPGained() + " XP"), player);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		event.getDrops().addAll(RPGMethods.getMobDropScrolls(event.getEntityType()));
	}
	
	@EventHandler
	public void onEntityBendingDeath(EntityBendingDeathEvent event) {
		if (event.getAttacker() == null) {
			return;
		}
		
		RPGPlayer killer = RPGPlayer.get(event.getAttacker());
		
		if (killer != null) {
			killer.addXP(RPGMethods.getMobDropXP(event.getEntity().getType()));
		}
	}

	@EventHandler
	public void onElementChange(PlayerChangeElementEvent event) {
		if (event.getResult() == Result.REMOVE) {
			if (RPGMethods.isCurrentAvatar(event.getTarget().getUniqueId())) {
				RPGMethods.revokeAvatar(event.getTarget());
			}
		}
		
		if (!ConfigManager.getConfig().getBoolean("ResetLevelOnElementChange")) {
			return;
		}
		
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getTarget());
		RPGPlayer rpgPlayer = RPGPlayer.get(event.getTarget());
		
		if (bPlayer == null || rpgPlayer == null) {
			return;
		}
		
		if (event.getResult() == Result.CHOOSE || (event.getResult() == Result.REMOVE && bPlayer.getElements().isEmpty())) {
			rpgPlayer.reset();
		}
	}
	
	@EventHandler
	public void onPlayerBind(PlayerBindChangeEvent event) {
		CoreAbility ability = CoreAbility.getAbility(event.getAbility());
		
		if (ability == null) {
			return;
		}
		
		RPGPlayer player = RPGPlayer.get(event.getPlayer());
		if (player == null) {
			return;
		}
		
		if (event.isBinding() && !player.hasUnlocked(ability)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBendingPlayerCreationEvent(BendingPlayerCreationEvent event) {
		if (!ConfigManager.getConfig().getBoolean("ElementAssign.Enabled"))
			return;

		if (event.getBendingPlayer() != null) {
			BendingPlayer bPlayer = event.getBendingPlayer();
			
			RPGPlayer.get(bPlayer);

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
				if (Math.ceil((event.getWorld().getFullTime() / 24000)) % wEvent.getFrequency() == 0) {
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
				if (Math.ceil((event.getWorld().getFullTime() / 24000)) % wEvent.getFrequency() == 0) {
					WorldEventStartEvent startEvent = new WorldEventStartEvent(event.getWorld(), wEvent);
					Bukkit.getServer().getPluginManager().callEvent(startEvent);

					if (startEvent.isCancelled()) {
						continue;
					}

					ProjectKorraRPG.getEventManager().startEvent(event.getWorld(), wEvent);
				}
			} else if (wEvent.getTime() == Time.DAY) {
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
	public void onPlayerQuit(PlayerQuitEvent event) {
		RPGPlayer player = RPGPlayer.get(event.getPlayer());
		ProjectKorraRPG.getLog().info("Saved " + event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId().toString() + ")");
		
		if (player != null) {
			player.save(true);
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		World from = event.getFrom().getWorld();
		World to = event.getTo().getWorld();
		if (from.equals(to)) {
			return;
		}

		for (WorldEventInstance instance : ProjectKorraRPG.getEventManager().getEventInstances(from)) {
			ProjectKorraRPG.getDisplayManager().getBossBar(instance).removePlayer(event.getPlayer());
		}
		
		for (WorldEventInstance instance : ProjectKorraRPG.getEventManager().getEventInstances(to)) {
			ProjectKorraRPG.getDisplayManager().getBossBar(instance).addPlayer(event.getPlayer());
		}
	}
}
