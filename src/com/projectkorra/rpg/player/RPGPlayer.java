package com.projectkorra.rpg.player;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.ability.AbilityTiers.AbilityTier;
import com.projectkorra.rpg.events.RPGPlayerGainXPEvent;
import com.projectkorra.rpg.events.RPGPlayerLevelUpEvent;
import com.projectkorra.rpg.player.ChakraStats.Chakra;
import com.projectkorra.rpg.util.XPControl;

public class RPGPlayer {
	
	private static final Map<UUID, RPGPlayer> PLAYERS = new HashMap<>();

	private int id;
	private Player player;
	private BendingPlayer bPlayer;
	private ChakraStats stats;
	private AbilityTier tier;
	private LevelBar bar;
	private Set<String> unlocked;

	private int level;
	private int xp;
	
	private RPGPlayer(int id, BendingPlayer bPlayer) {
		this(id, bPlayer, new ChakraStats(), 0, ProjectKorraRPG.getAbilityTiers().getAbilityNamesFromTiers(AbilityTier.DEFAULT));
	}
	
	private RPGPlayer(int id, BendingPlayer bPlayer, ChakraStats stats, int xp, Collection<String> unlocked) {
		this.id = id;
		this.player = bPlayer.getPlayer();
		this.bPlayer = bPlayer;
		this.stats = stats;
		this.xp = xp;
		this.level = XPControl.calculateLevel(xp);
		this.tier = AbilityTier.fromLevel(level);
		this.bar = new LevelBar(this);
		this.unlocked = new HashSet<>(unlocked);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public BendingPlayer getBendingPlayer() {
		return bPlayer;
	}
	
	public ChakraStats getStats() {
		return stats;
	}
	
	public AbilityTier getCurrentTier() {
		return tier;
	}

	public int getLevel() {
		return level;
	}
	
	public int getXP() {
		return xp;
	}
	
	public LevelBar getLevelBar() {
		return bar;
	}
	
	public boolean addXP(int xp) {
		if (level < 0 || level >= XPControl.getMaxLevel()) {
			return false;
		}
		
		RPGPlayerGainXPEvent xpEvent = new RPGPlayerGainXPEvent(this, xp);
		Bukkit.getServer().getPluginManager().callEvent(xpEvent);
		
		if (xpEvent.isCancelled()) {
			return false;
		}
		
		int fxp = this.xp + xp;
		int rxp = XPControl.getXPRequired(level);
		
		// this loop is making sure to call the LevelUp event for each level they hit, rather than skipping levels
		while (fxp >= rxp) {
			this.level = XPControl.calculateLevel(rxp);
			this.tier = AbilityTier.fromLevel(level);
			
			RPGPlayerLevelUpEvent event = new RPGPlayerLevelUpEvent(this, level);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			try {
				rxp = XPControl.getXPRequired(level);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
		}
		
		this.xp = fxp;
		this.bar.update();
		return true;
	}
	
	public void setXP(int xp) {
		this.xp = xp;
		this.level = XPControl.calculateLevel(xp);
		this.tier = AbilityTier.fromLevel(level);
		this.bar.update();
	}
	
	public Set<String> getUnlockedAbilities() {
		return new HashSet<>(unlocked);
	}
	
	public boolean hasUnlocked(CoreAbility ability) {
		if (ability.getElement() == Element.AVATAR) {
			return RPGMethods.isCurrentAvatar(bPlayer.getUUID());
		}
		
		return unlocked.contains(ability.getName().toLowerCase());
	}
	
	public boolean unlock(CoreAbility ability) {
		return unlocked.add(ability.getName().toLowerCase());
	}
	
	public boolean addPoint(Chakra chakra) {
		if (level > stats.getTotalPointsUsed()) {
			return stats.increase(chakra);
		}
		
		return false;
	}
	
	public boolean removePoint(Chakra chakra) {
		return stats.decrease(chakra);
	}
	
	/**
	 * Will put as many points as available into the chakra
	 * in an attempt to max out the chakra.
	 * @param chakra Chakra to max out
	 * @return number of points added to chakra
	 */
	public int maxPoints(Chakra chakra) {
		int points = 0;
		
		while (stats.increase(chakra) && level > stats.getTotalPointsUsed()) {
			points++;
		}
		
		return points;
	}
	
	public static RPGPlayer get(BendingPlayer bPlayer) {
		if (bPlayer == null) {
			return null;
		}
		
		if (!PLAYERS.containsKey(bPlayer.getUUID())) {
			PLAYERS.put(bPlayer.getUUID(), load(bPlayer));
		}
		
		return PLAYERS.get(bPlayer.getUUID());
	}
	
	public static RPGPlayer get(Player player) {
		return get(BendingPlayer.getBendingPlayer(player));
	}
	
	private static RPGPlayer load(BendingPlayer bPlayer) {
		if (bPlayer == null) {
			return null;
		}
		
		UUID uuid = bPlayer.getUUID();
		
		ResultSet r = DBConnection.sql.readQuery("SELECT * FROM rpg_players WHERE uuid='" + uuid.toString() + "';");
		
		try {
			if (!r.next()) {
				DBConnection.sql.modifyQuery("INSERT INTO rpg_players (uuid, xp, air, earth, fire, water, light) VALUES ('" + uuid.toString() + "', 0, 0, 0, 0, 0, 0);", false);
				ResultSet r2 = DBConnection.sql.readQuery("SELECT id FROM rpg_players WHERE uuid = '" + uuid.toString() + "';");
				r2.next();
				return new RPGPlayer(r2.getInt("id"), bPlayer);
			} else {
				int id = r.getInt("id");
				int xp = r.getInt("xp");
				
				Map<Chakra, Integer> points = new HashMap<>();
				for (Chakra chakra : Chakra.values()) {
					points.put(chakra, r.getInt(chakra.toString().toLowerCase()));
				}
				ChakraStats stats = new ChakraStats(points);
				
				ResultSet r2 = DBConnection.sql.readQuery("SELECT rpg_ability_ids.name FROM rpg_player_abilities INNER JOIN rpg_ability_ids ON rpg_player_abilities.ability_id = rpg_ability_ids.id WHERE rpg_player_abilities.player_id = " + id);
				Set<String> abilities = new HashSet<>(); 
				
				while (r2.next()) {
					abilities.add(r2.getString("name").toLowerCase());
				}
				
				abilities.addAll(ProjectKorraRPG.getAbilityTiers().getAbilityNamesFromTiers(AbilityTier.DEFAULT));
				
				return new RPGPlayer(id, bPlayer, stats, xp, abilities);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void reset() {
		this.setXP(0);
		this.stats.clearAll();
		this.unlocked.clear();
		this.unlocked.addAll(ProjectKorraRPG.getAbilityTiers().getAbilityNamesFromTiers(AbilityTier.DEFAULT));
		this.bar.update();
	}
	
	public void save(boolean remove) {
		DBConnection.sql.modifyQuery("UPDATE rpg_players SET xp = " + xp + " WHERE id = " + this.id);
		for (Chakra chakra : Chakra.values()) {
			DBConnection.sql.modifyQuery("UPDATE rpg_players SET " + chakra.toString().toLowerCase() + " = " + stats.getPoints(chakra) + " WHERE id = " + this.id);
		}
		
		DBConnection.sql.modifyQuery("DELETE FROM rpg_player_abilities WHERE player_id = " + this.id + ";", false);
		for (String ability : unlocked) {
			int abilID = RPGMethods.getAbilityID(ability);
			if (abilID != -1) {
				DBConnection.sql.modifyQuery("INSERT INTO rpg_player_abilities (player_id, ability_id) VALUES (" + id + ", " + abilID + ");", false);
			}
		}
		
		if (remove) {
			PLAYERS.remove(bPlayer.getUUID());
		}
	}
	
	public static void saveAll() {
		for (RPGPlayer player : PLAYERS.values()) {
			player.save(false);
		}
		
		PLAYERS.clear();
	}
}
