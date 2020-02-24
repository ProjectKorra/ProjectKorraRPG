package com.projectkorra.rpg.ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.player.RPGPlayer;

public class AbilityTiers {
	
	public static FileConfiguration config = ProjectKorraRPG.getStorage().load("ability_tiers");

	public static enum AbilityTier {
		DEFAULT(0), NOVICE(10), INTERMEDIATE(20), ADVANCED(30), MASTER(40);
		
		private int level;
		
		private AbilityTier(int level) {
			this.level = level;
		}
		
		public int getLevel() {
			return level;
		}
		
		public boolean aboveOrSame(AbilityTier other) {
			return this.level >= other.level;
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
			} else if (level == 40) {
				return MASTER;
			}
			
			return DEFAULT;
		}
	}
	
	public Map<Element, Map<AbilityTier, Set<CoreAbility>>> tiers;
	public Map<CoreAbility, AbilityTier> abilities;
	
	public AbilityTiers() {
		this.tiers = new HashMap<>();
		
		for (Element e : Element.getAllElements()) {
			Map<AbilityTier, Set<CoreAbility>> elements = new HashMap<>();
			
			for (AbilityTier tier : AbilityTier.values()) {
				Set<CoreAbility> abilities = new HashSet<>();
				
				for (String s : config.getStringList(e.getName() + "." + tier.toString())) {
					CoreAbility ability = CoreAbility.getAbility(s);
					
					if  (ability != null) {
						this.abilities.put(ability, tier);
						abilities.add(ability);
					}
				}
				
				elements.put(tier, abilities);
			}
			
			tiers.put(e, elements);
		}
	}
	
	public List<CoreAbility> getAbilitiesFromTiers(AbilityTier...tiers) {
		List<CoreAbility> abilities = new ArrayList<>();
		for (Element e : Element.getAllElements()) {
			for (AbilityTier tier : tiers) {
				abilities.addAll(this.tiers.get(e).get(tier));
			}
		}
		
		return abilities;
	}
	
	public AbilityTier getAbilityTier(CoreAbility ability) {
		if (ability.getPlayer() != null) {
			ability = CoreAbility.getAbility(ability.getClass());
		}
		
		if (abilities.containsKey(ability)) {
			return abilities.get(ability);
		}
		
		return AbilityTier.DEFAULT;
	}
	
	public boolean canLearnAbility(RPGPlayer player, CoreAbility ability) {
		return player.getLevel(ability.getElement()).getCurrentTier().aboveOrSame(getAbilityTier(ability));
	}
}
