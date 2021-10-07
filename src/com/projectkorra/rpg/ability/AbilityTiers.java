package com.projectkorra.rpg.ability;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.Config;
import com.projectkorra.rpg.player.RPGPlayer;

import org.bukkit.configuration.file.FileConfiguration;

public class AbilityTiers {
	
	private static Config _config;
	private static FileConfiguration config;
	
	static {
		loadConfig();
	}
	
	private static final Map<AbilityTier, Set<String>> TIERS = new HashMap<>();
	private static final Map<String, AbilityTier> ABILITIES = new HashMap<>();
	
	public static void init() {
		TIERS.put(null, new HashSet<>());
		
		for (AbilityTier tier : AbilityTier.values()) {
			Set<String> abils = new HashSet<>();
			
			for (String s : config.getStringList(tier.toString())) {
					ABILITIES.put(s.toLowerCase(), tier);
					abils.add(s.toLowerCase());
			}
			
			TIERS.put(tier, abils);
		}
		
		for (CoreAbility ability : CoreAbility.getAbilities()) {
			getAbilityTier(ability);
		}
	}
	
	public static Set<CoreAbility> getAbilitiesFromTiers(AbilityTier...tiers) {
		Set<CoreAbility> abils = new HashSet<>();
		
		for (AbilityTier tier : tiers) {
			if (tier != null) {
				for (String s : TIERS.get(tier)) {
					CoreAbility ability = CoreAbility.getAbility(s);
					
					if (ability != null) {
						abils.add(ability);
					}
				}
			}
		}
		
		return abils;
	}
	
	public static Set<String> getAbilityNamesFromTiers(AbilityTier...tiers) {
		Set<String> abils = new HashSet<>();
		
		for (AbilityTier tier : tiers) {
			if (tier != null) {
				abils.addAll(TIERS.get(tier));
			}
		}
		
		return abils;
	}
	
	public static AbilityTier getAbilityTier(CoreAbility ability) {
		if (ability == null) {
			return null;
		}
		
		String name = ability.getName().toLowerCase();
		
		if (!ABILITIES.containsKey(name)) {
			AbilityTier tier = AbilityTier.MASTER;
			if (ability instanceof PassiveAbility || ability.isHiddenAbility()) {
				tier = AbilityTier.DEFAULT;
			}
			
			ABILITIES.put(name, tier);
			TIERS.get(tier).add(name);
			return tier;
		}
		
		return ABILITIES.get(name);
	}
	
	public static boolean canLearnAbility(RPGPlayer player, CoreAbility ability) {
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
