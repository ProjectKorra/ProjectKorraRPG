package com.projectkorra.rpg.ability;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.Config;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.player.RPGPlayer;

public class AbilityTiers {
	
	private static Config _config;
	private static FileConfiguration config;
	
	static {
		loadConfig();
	}

	public static enum AbilityTier {
		DEFAULT(0, "Bender"), NOVICE(10, "Novice"), INTERMEDIATE(20, "Intermediate"), ADVANCED(30, "Advanced"), MASTER(40, "Master");
		
		private int level;
		private String display;
		
		private AbilityTier(int level, String display) {
			this.level = level;
			this.display = display;
		}
		
		public int getLevel() {
			return level;
		}
		
		public String getDisplay() {
			return getColor() + display;
		}
		
		public boolean aboveOrSame(AbilityTier other) {
			return this.level >= other.level;
		}
		
		public int getRequiredScrolls() {
			return level / 10;
		}
		
		public ChatColor getColor() {
			ChatColor color = ChatColor.valueOf(ConfigManager.getConfig().getString("ChatColors." + this.toString()));
			
			if (color == null) {
				color = ChatColor.WHITE;
			}
			
			return color;
		}
		
		public static AbilityTier fromLevel(int level) {
			if (level < 10) {
				return DEFAULT;
			} else if (level < 20) {
				return NOVICE;
			} else if (level < 30) {
				return INTERMEDIATE;
			} else if (level < 40) {
				return ADVANCED;
			} else {
				return MASTER;
			}
		}
	}
	
	private Map<AbilityTier, Set<String>> tiers;
	private Map<String, AbilityTier> abilities;
	
	public AbilityTiers() {
		this.tiers = new HashMap<>();
		this.abilities = new HashMap<>();
		
		this.tiers.put(null, new HashSet<>());
		
		for (AbilityTier tier : AbilityTier.values()) {
			Set<String> abilities = new HashSet<>();
			
			for (String s : config.getStringList(tier.toString())) {
					this.abilities.put(s.toLowerCase(), tier);
					abilities.add(s.toLowerCase());
			}
			
			tiers.put(tier, abilities);
		}
		
		for (CoreAbility ability : CoreAbility.getAbilities()) {
			getAbilityTier(ability);
		}
	}
	
	public Set<CoreAbility> getAbilitiesFromTiers(AbilityTier...tiers) {
		Set<CoreAbility> abils = new HashSet<>();
		
		for (AbilityTier tier : tiers) {
			if (tier != null) {
				for (String s : this.tiers.get(tier)) {
					CoreAbility ability = CoreAbility.getAbility(s);
					
					if (ability != null) {
						abils.add(ability);
					}
				}
			}
		}
		
		return abils;
	}
	
	public Set<String> getAbilityNamesFromTiers(AbilityTier...tiers) {
		Set<String> abils = new HashSet<>();
		
		for (AbilityTier tier : tiers) {
			if (tier != null) {
				abils.addAll(this.tiers.get(tier));
			}
		}
		
		return abils;
	}
	
	public AbilityTier getAbilityTier(CoreAbility ability) {
		if (ability == null) {
			return null;
		}
		
		String name = ability.getName().toLowerCase();
		
		if (!abilities.containsKey(name)) {
			AbilityTier tier = AbilityTier.MASTER;
			if (ability instanceof PassiveAbility || ability.isHiddenAbility()) {
				tier = AbilityTier.DEFAULT;
			}
			
			abilities.put(name, tier);
			tiers.get(tier).add(name);
			return tier;
		}
		
		return abilities.get(ability.getName().toLowerCase());
	}
	
	public boolean canLearnAbility(RPGPlayer player, CoreAbility ability) {
		return player.getCurrentTier().aboveOrSame(getAbilityTier(ability));
	}
	
	private static void loadConfig() {
		_config = new Config(ProjectKorraRPG.getPlugin().getDataFolder(), new File("ability_tiers.yml"));
		config = _config.get();
		
		List<String> given = Arrays.asList("AirBlast", "AirSwipe", "AirShield", "EarthBlast", "Catapult", "RaiseEarth", "FireBlast", "Illumination", "FireShield", "WaterManipulation", "WaterBubble", "Surge", "Bottlebending", "QuickStrike", "SwiftKick", "HighJump");
		List<String> novice = Arrays.asList("AirSuction", "AirBurst", "Collapse", "EarthSmash", "HeatControl", "FireBurst", "FireKick", "PhaseChange", "Torrent", "HealingWaters", "Smokescreen", "WarriorStance");
		List<String> interm = Arrays.asList("Tornado", "AirScooter", "AirSweep", "EarthTunnel", "EarthArmor", "LavaFlow", "Extraction", "WallOfFire", "Blaze", "FireWheel", "Lightning", "Combustion", "OctopusForm", "IceSpike", "AcrobatStance");
		List<String> advanced = Arrays.asList("Suffocate", "AirStream", "Twister", "Tremorsense", "Shockwave", "EarthGrab", "MetalClips", "FireSpin", "FireJet", "WaterArms", "IceBlast", "Bloodbending", "RapidPunch", "Paralyze", "AvatarState");
		List<String> master = Arrays.asList("AirSpout", "Flight", "EarthPillars", "EarthDome", "JetBlast", "JetBlaze", "FireManipulation", "WaterSpout", "IceBullet", "IceWave", "Immobilize");
		
		config.addDefault("DEFAULT", given);
		config.addDefault("NOVICE", novice);
		config.addDefault("INTERMEDIATE", interm);
		config.addDefault("ADVANCED", advanced);
		config.addDefault("MASTER", master);
		
		_config.save();
	}
}
