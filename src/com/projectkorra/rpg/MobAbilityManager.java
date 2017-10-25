package com.projectkorra.rpg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.projectkorra.rpg.object.MobAbility;

public class MobAbilityManager {

	protected Set<MobAbility> instances;
	protected Map<Class<? extends MobAbility>, Map<Integer, MobAbility>> classMap;
	
	public MobAbilityManager() {
		instances = new HashSet<>();
		classMap = new HashMap<>();
	}
	
	public MobAbility getAbility(Class<? extends MobAbility> clazz, int id) {
		return classMap.containsKey(clazz) ? classMap.get(clazz).get(id) : null;
	}
	
	public Set<MobAbility> getInstances() {
		return instances;
	}
	
	public void removeAbility(MobAbility ability) {
		instances.remove(ability);
		classMap.get(ability.getClass()).remove(ability.getID());
	}
	
	public void startAbility(MobAbility ability) {
		instances.add(ability);
		if (!classMap.containsKey(ability.getClass())) {
			classMap.put(ability.getClass(), new HashMap<>());
		}
		classMap.get(ability.getClass()).put(ability.getID(), ability);
	}
	
	public static class AbilityManager implements Runnable {
		
		public MobAbilityManager manager;
		
		public AbilityManager(MobAbilityManager manager) {
			this.manager = manager;
		}

		@Override
		public void run() {
			for (MobAbility abil : manager.getInstances()) {
				if (abil.getCreature().isDead()) {
					abil.remove();
					continue;
				}
				abil.progress();
			}
		}
		
	}
}
