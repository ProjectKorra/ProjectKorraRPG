package com.projectkorra.rpg.worldevent.util;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.worldevent.WorldEvent;

public class WorldEventFileManager {

	private static String mult = "::MULTIPLICATION", divi = "::DIVISION";

	protected Map<String, WorldEventFile> files;
	protected File folder;

	public WorldEventFileManager() {
		files = new HashMap<>();

		folder = new File(ProjectKorraRPG.getPlugin().getDataFolder(), "/WorldEvents/");
		if (!folder.exists()) {
			createDefaults();
		}

		loadFiles();
	}

	private void createDefaults() {
		new WorldEventFileBuilder()
		.name("FullMoon")
		.setAliases(Arrays.asList("fm", "fullm", "fmoon", "moon"))
		.setAttributes(Arrays.asList(Attribute.DAMAGE + mult, Attribute.HEIGHT + mult, Attribute.RANGE + mult, Attribute.WIDTH + mult, Attribute.KNOCKBACK + mult, Attribute.CHARGE_DURATION + divi, Attribute.COOLDOWN + divi, Attribute.SPEED + mult))
		.addElement(Element.WATER)
		.barColor(BarColor.BLUE)
		.textColor(ChatColor.DARK_AQUA)
		.modifier(3.0)
		.duration(12000)
		.frequency(8)
		.time(Time.NIGHT)
		.description("When the full moon rises overhead waterbending is greatly enhanced due to its lunar affinity. This is almost always to the point where a single waterbender can overpower multiple opponents with relative ease.")
		.startMessage("A full moon is rising, empowering waterbending like never before.")
		.endMessage("The full moon has passed, waterbending is no longer empowered!")
		.build();

		new WorldEventFileBuilder()
		.name("SozinsComet")
		.setAliases(Arrays.asList("sc", "sozinsc", "scomet", "comet"))
		.setAttributes(Arrays.asList(Attribute.DAMAGE + mult, Attribute.RANGE + mult, Attribute.KNOCKBACK + mult, Attribute.CHARGE_DURATION + divi, Attribute.DURATION + mult, Attribute.SPEED + mult, Attribute.WIDTH + mult))
		.addElement(Element.FIRE)
		.barColor(BarColor.RED)
		.textColor(ChatColor.DARK_RED)
		.modifier(5.0)
		.duration(5000)
		.frequency(100)
		.time(Time.BOTH)
		.description("As Sozin's Comet streaks through the upper atmosphere, firebending is drastically enhanced. With an extraterrestrial heat source so much closer to the planet than the Sun, all firebenders become capable of massive destruction.")
		.startMessage("Sozin's Comet is passing overhead! Firebending is now at its most powerful.")
		.endMessage("Sozin's comet has passed, firebending is no longer empowered!")
		.darkenSky(true)
		.addBlacklistedEvent("SolarEclipse")
		.build();

		new WorldEventFileBuilder()
		.name("SolarEclipse")
		.setAliases(Arrays.asList("se", "solare", "seclipse", "solar"))
		.addElement(Element.FIRE)
		.barColor(BarColor.RED)
		.textColor(ChatColor.RED)
		.modifier(0.0)
		.duration(1000)
		.frequency(14)
		.time(Time.DAY)
		.description("As the sky darkens during a solar eclipse, all firebending subsides. Firebenders typically draw their power from the sun and are unable to do so while their connection is temporarily severed.")
		.startMessage("A solar eclipse is out! Firebenders are temporarily powerless.")
		.endMessage("The solar eclipse has ended!")
		.build();

		new WorldEventFileBuilder()
		.name("LunarEclipse")
		.setAliases(Arrays.asList("le", "lunare", "leclipse", "lunar"))
		.addElement(Element.WATER)
		.barColor(BarColor.BLUE)
		.textColor(ChatColor.AQUA)
		.modifier(0.0)
		.duration(1000)
		.frequency(22)
		.time(Time.NIGHT)
		.description("As the moon darkens during a lunar eclipse, all waterbending subsides. Waterbenders typically draw their power from the moon and are unable to do so while their connection is temporarily severed.")
		.startMessage("A lunar eclipse is out! Waterbenders are temporarily powerless.")
		.endMessage("The lunar eclipse has ended!")
		.addBlacklistedEvent("FullMoon")
		.build();
		
		new WorldEventFileBuilder()
		.name("HarmonicConvergence")
		.setAliases(Arrays.asList("hc", "harmonic", "convergence", "hconvergence"))
		.setAttributes(Arrays.asList(Attribute.DAMAGE + mult, Attribute.CHARGE_DURATION + divi, Attribute.COOLDOWN + divi, Attribute.KNOCKBACK + mult, Attribute.DURATION + mult, Attribute.WIDTH + mult, Attribute.SPEED + mult, Attribute.HEIGHT + mult))
		.addElements(Element.AIR, Element.EARTH, Element.FIRE, Element.WATER)
		.barColor(BarColor.PINK)
		.textColor(ChatColor.WHITE)
		.modifier(3.0)
		.duration(7000)
		.frequency(10000)
		.time(Time.BOTH)
		.description("Harmonic Convergence is a supernatural phenomenon that occurs once every ten thousand years. When the planets align, spiritual energy is greatly amplified, causing the spirit portals at the North and South Poles to merge, while an aura of spirit energy envelops the Earth.")
		.startMessage("Harmonic Convergence has flooded the world with spiritual energy, empowering all bending for the day!")
		.endMessage("Harmonic Convergence has ended!")
		.addBlacklistedEvents("SolarEclipse", "LunarEclipse")
		.build();
		
		new WorldEventFileBuilder()
		.name("BloodMoon")
		.setAliases(Arrays.asList("bloodm", "bm", "bmoon"))
		.addElements(Element.FIRE, Element.WATER)
		.barColor(BarColor.RED)
		.textColor(ChatColor.DARK_AQUA)
		.modifier(0.0)
		.duration(3000)
		.frequency(64)
		.time(Time.NIGHT)
		.description("A bloodmoon occurs when someone disgraces the moon spirit, subsequently causing the moon to turn a blood-red and breaking firebenders and waterbenders spiritual connections!")
		.startMessage("Someone disgraced the moon spirit and has caused the moon to turn blood-red and causing firebending and waterbending to subside. Firebenders and waterbenders are powerless!")
		.endMessage("The moon spirit has been restored and the blood moon has gone away!")
		.addBlacklistedEvents("SozinsComet", "LunarEclipse", "FullMoon")
		.build();
	}

	public void loadFiles() {
		for (File file : folder.listFiles()) {
			loadFile(file);
		}
	}

	public WorldEventFile loadFile(String name) {
		File file = new File(folder, name.toLowerCase() + ".yml");
		if (file.exists()) {
			return loadFile(file);
		} else {
			return null;
		}
	}

	public WorldEventFile loadFile(File file) {
		if (!file.exists()) {
			return null;
		}

		WorldEventFile wFile = new WorldEventFile(file);
		try {
			validateParams(wFile);
		}
		catch (WorldEventFileException e) {
			ProjectKorraRPG.getLog().severe(e.getMessage());
			ProjectKorraRPG.getLog().severe("WorldEvent " + file.getName() + " will not be loaded until the problem is fixed!");
			return null;
		}

		new WorldEvent(wFile);
		return wFile;
	}

	private void validateParams(WorldEventFile wFile) throws WorldEventFileException {
		String message = "(f) field of WorldEvent " + wFile.getFile().getName() + " configuration file was null, empty, or an unknown value; correct and retry";
		if (wFile.getName() == null || wFile.getName().isEmpty()) {
			throw new WorldEventFileException(message.replaceAll("(f)", "Name"));
		} else if (wFile.getDescription() == null || wFile.getDescription().isEmpty()) {
			throw new WorldEventFileException(message.replaceAll("(f)", "Description"));
		} else if (wFile.getAliases() == null || wFile.getAliases().isEmpty()) {
			throw new WorldEventFileException(message.replaceAll("(f)", "Aliases"));
		} else if (wFile.getElements().length == 0) {
			throw new WorldEventFileException(message.replaceAll("(f)", "Elements"));
		} else if (wFile.getEndMessage() == null || wFile.getEndMessage().isEmpty()) {
			throw new WorldEventFileException(message.replaceAll("(f)", "End Message"));
		} else if (wFile.getStartMessage() == null || wFile.getStartMessage().isEmpty()) {
			throw new WorldEventFileException(message.replaceAll("(f)", "Start Message"));
		} else if (Integer.valueOf(wFile.getFrequency()) == null) {
			throw new WorldEventFileException(message.replaceAll("(f)", "Frequency"));
		} else if (Double.valueOf(wFile.getModifier()) == null) {
			throw new WorldEventFileException(message.replaceAll("(f)", "Modifier"));
		} else if (!wFile.getConfig().contains("darken-sky")) {
			wFile.getConfig().addDefault("darkensky", false);
		} else if (!wFile.getConfig().contains("create-fog")) {
			wFile.getConfig().addDefault("createfog", false);
		}
	}

	public WorldEventFile getFile(String name) {
		return files.containsKey(name.toLowerCase()) ? files.get(name.toLowerCase()) : null;
	}

	public class WorldEventFileException extends Exception {
		private static final long serialVersionUID = -4592724223026423923L;

		public String message;

		public WorldEventFileException(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}
