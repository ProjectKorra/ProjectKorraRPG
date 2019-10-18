package com.projectkorra.rpg.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.storage.Storable;

public class AvatarCycle implements Storable{

	public Element first;
	public Element second;
	public Element third;
	public Element fourth;
	
	public AvatarCycle(Element first, Element second, Element third, Element fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}

	@Override
	public String toStorage() {
		return "AvatarCycle";
	}

	@Override
	public void save() {
		Map<String, Object> storage = new HashMap<>();
		storage.put("first", first.getName());
		storage.put("second", second.getName());
		storage.put("third", third.getName());
		storage.put("fourth", fourth.getName());
		
		ProjectKorraRPG.getStorage().save(this, storage);
	}

	public static AvatarCycle load() {
		FileConfiguration config = ProjectKorraRPG.getStorage().load("AvatarCycle");
		if (config == null) {
			return new AvatarCycle(Element.FIRE, Element.AIR, Element.WATER, Element.EARTH);
		}
		
		String[] order = {"first", "second", "third", "fourth"};
		Element[] elements = new Element[4];
		for (int i = 0; i < 4; i++) {
			String element = config.getString(order[i]);
			Element e = Element.getElement(element);
			elements[i] = e;
		}
		
		return new AvatarCycle(elements[0], elements[1], elements[2], elements[3]);
	}
}
