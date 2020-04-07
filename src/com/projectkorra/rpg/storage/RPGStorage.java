package com.projectkorra.rpg.storage;

import java.io.File;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.Config;

public class RPGStorage {

	private File folder;

	public RPGStorage() {
		this.folder = new File(ProjectKorraRPG.getPlugin().getDataFolder(), "/global_data/");

		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public void save(Storable src, Map<String, Object> map) {
		File file = new File(src.toStorage() + ".yml");
		Config config = new Config(folder, file);
		FileConfiguration c = config.get();

		for (String path : map.keySet()) {
			c.set(path, map.get(path));
		}
		config.save();
	}

	public FileConfiguration load(String fileName) {
		if (!fileName.endsWith(".yml")) {
			fileName = fileName + ".yml";
		}

		File file = null;

		if (hasFile(fileName)) {
			file = new File(fileName);
		}

		if (file == null) {
			return null;
		}

		return new Config(folder, file).get();
	}

	public boolean hasFile(String fileName) {
		if (!fileName.contains(".yml")) {
			fileName = fileName + ".yml";
		}

		for (File file : folder.listFiles()) {
			if (file.getName().equals(fileName)) {
				return true;
			}
		}
		return false;
	}

	public File getFolder() {
		return folder;
	}
}
