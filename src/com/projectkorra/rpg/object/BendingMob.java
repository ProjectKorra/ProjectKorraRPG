package com.projectkorra.rpg.object;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.configuration.ConfigManager;

public class BendingMob {
	
	public Creature creature;
	public Element element;
	protected List<String> abilities;

	public BendingMob(Creature creature, Element element) {
		this.creature = creature;
		this.element = element;
		abilities = loadSkillSet();
	}
	
	protected List<String> loadSkillSet() {
		FileConfiguration fc = ConfigManager.mobsConfigs.get(creature.getType()).get();
		return fc.getStringList("Elements." + element.getName() + ".Skills");
	}
}
