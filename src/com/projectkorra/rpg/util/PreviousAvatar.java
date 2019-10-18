package com.projectkorra.rpg.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.storage.Storable;

public class PreviousAvatar implements Storable{

	public UUID uuid;
	public String name;
	public List<Element> elements;
	public List<SubElement> subs;
	
	public PreviousAvatar(UUID uuid, String name, List<Element> elements, List<SubElement> subs) {
		this.uuid = uuid;
		this.name = name;
		this.elements = elements;
		this.subs = subs;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Element> getElements() {
		return elements;
	}
	
	public List<SubElement> getSubs() {
		return subs;
	}

	@Override
	public String toStorage() {
		return uuid.toString();
	}

	@Override
	public void save() {
		Map<String, Object> storage = new HashMap<>();
		storage.put("uuid", uuid.toString());
		storage.put("name", name);
		
		List<String> names = new ArrayList<>();
		for (Element e : elements) {
			names.add(e.getName());
		}
		storage.put("elements", names);
		
		List<String> names2 = new ArrayList<>();
		for (SubElement se : subs) {
			names2.add(se.getName());
		}
		storage.put("subs", names2);
		
		ProjectKorraRPG.getStorage().save(this, storage);
	}

	public static PreviousAvatar load(String fileName) {
		FileConfiguration config = ProjectKorraRPG.getStorage().load(fileName);
		if (config == null) {
			return null;
		}
		
		UUID uuid = UUID.fromString(config.getString("uuid"));
		String name = config.getString("name");
		List<Element> elements = new ArrayList<>();
		for (String element : config.getStringList("elements")) {
			Element e = Element.fromString(element);
			if (e != null) {
				elements.add(e);
			}
		}
		
		List<SubElement> subs = new ArrayList<>();
		for (String sub : config.getStringList("subs")) {
			Element e = Element.fromString(sub);
			if (e != null && e instanceof SubElement) {
				subs.add((SubElement) e);
			}
		}
		
		return new PreviousAvatar(uuid, name, elements, subs);
	}
}
