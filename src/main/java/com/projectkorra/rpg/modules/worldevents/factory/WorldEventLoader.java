package com.projectkorra.rpg.modules.worldevents.factory;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.modules.worldevents.display.*;
import com.projectkorra.rpg.modules.worldevents.models.AttributeRules;
import com.projectkorra.rpg.modules.worldevents.models.ScheduleSpecifications;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.schedule.ScheduleType;
import com.projectkorra.rpg.modules.worldevents.util.ScheduleParser;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public final class WorldEventLoader {
    private final ProjectKorraRPG plugin;

    public WorldEventLoader(ProjectKorraRPG plugin) {
        this.plugin = plugin;
    }

    public Map<NamespacedKey, WorldEvent> loadEventsFromFolder() {
        Map<NamespacedKey, WorldEvent> result = new HashMap<>();

        File directory = new File(plugin.getDataFolder(), "WorldEvents");
        if (!directory.isDirectory()) {
            plugin.getLogger().warning("'WorldEvents' folder was not found!");
            return result;
        }

        File[] files = directory.listFiles((file, name) -> name.toLowerCase(Locale.ROOT).endsWith(".yml"));
        if (files == null || files.length == 0) {
            plugin.getLogger().info("'WorldEvents' folder does not contain any WorldEvent files.");
            return result;
        }

        int loaded = 0, skipped = 0;

        for (File file : files) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                // GATHER VALUES FROM CONFIG
                NamespacedKey key = new NamespacedKey(plugin, file.getName().substring(0, file.getName().length() - 4).toLowerCase(Locale.ROOT));
                String title = config.getString("Title");
                long duration = config.getLong("Duration");

                List<World> scheduledWorlds = config.getStringList("Worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList());
                List<World> disabledWorlds = config.getStringList("DisabledWorlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).toList();

                IChatDisplay chatDisplay = getChatDisplay(config);
                IBossBarDisplay bossBarDisplay = getBossBarDisplay(config, key, title);
                ISoundDisplay soundDisplay = getSoundDisplay(config);

                AttributeRules attributeRules = parseAttributeRules(config);
                ScheduleSpecifications scheduleSpecifications = parseSchedule(config);

                // CREATE BUILDER
                WorldEventBuilder builder = WorldEventBuilder.create()
                        .key(key)
                        .title(title)
                        .duration(duration)
                        .scheduledWorlds(scheduledWorlds)
                        .chatDisplay(chatDisplay)
                        .bossBarDisplay(bossBarDisplay)
                        .soundDisplay(soundDisplay)
                        .disabledWorlds(disabledWorlds)
                        .schedule(scheduleSpecifications)
                        .attributes(attributeRules);

                // TRY TO BUILD
                Optional<WorldEvent> built = builder.tryBuild(msg -> plugin.getLogger().warning("Skipping '" + file.getName() + "': " + msg));
                if (built.isPresent()) {
                    // NON-NULL VALUES = Success
                    result.put(key, built.get());
                    loaded++;
                } else {
                    skipped++;
                }
            } catch (Exception exception) {
                plugin.getLogger().severe("Failed to load " + file.getName() + ": " + exception.getMessage());
                skipped++;
            }
        }

        plugin.getLogger().info("Loaded " + loaded + " WorldEvent(s), skipped " + skipped + ".");
        return result;
    }

    private @Nullable IChatDisplay getChatDisplay(FileConfiguration config) {
        if (!config.isConfigurationSection("DisplayMethods.Chat")) return null;
        if (!config.getBoolean("DisplayMethods.Chat.Enabled")) return null;

        String startMsg = config.getString("DisplayMethods.Chat.EventStartMessage");
        String stopMsg = config.getString("DisplayMethods.Chat.EventStopMessage");
        String runningMsg = config.getString("DisplayMethods.Chat.EventCurrentlyRunning");

        List<String> missing = new ArrayList<>();
        if (startMsg == null) {
            missing.add("DisplayMethods.Chat.EventStartMessage");
        }
        if (stopMsg == null) {
            missing.add("DisplayMethods.Chat.EventStopMessage");
        }
        if (runningMsg == null) {
            missing.add("DisplayMethods.Chat.EventCurrentlyRunning");
        }

        if (!missing.isEmpty()) {
            plugin.getLogger().warning("Chat display enabled but following entries are missing/blank:");
            missing.forEach(name -> plugin.getLogger().warning(" - " + name));
            return null;
        }

        return new ChatDisplay(startMsg, stopMsg, runningMsg);
    }

    private @Nullable IBossBarDisplay getBossBarDisplay(FileConfiguration config, NamespacedKey key, String title) {
        if (!config.isConfigurationSection("DisplayMethods.BossBar")) return null;
        if (!config.getBoolean("DisplayMethods.BossBar.Enabled")) return null;

        String colorRaw = config.getString("DisplayMethods.BossBar.Color");
        String styleRaw = config.getString("DisplayMethods.BossBar.Style");

        List<String> missing = new ArrayList<>();
        if (colorRaw == null || colorRaw.isBlank()) {
            missing.add("DisplayMethods.BossBar.Color");
        }
        if (styleRaw == null || styleRaw.isBlank()) {
            missing.add("DisplayMethods.BossBar.Style");
        }

        if (!missing.isEmpty()) {
            plugin.getLogger().warning("BossBar display enabled but following entries are missing/blank:");
            missing.forEach(name -> plugin.getLogger().warning(" - " + name));
            return null;
        }

        BarColor color = RPGMethods.convertStringToBarColor(colorRaw);
        BarStyle style = RPGMethods.convertStringToBarStyle(styleRaw);
        boolean smooth = config.getBoolean("DisplayMethods.BossBar.Smooth", true);

        return new BossBarDisplay(key, title, color, style, smooth);
    }

    private @Nullable ISoundDisplay getSoundDisplay(FileConfiguration config) {
        Sound startSound = null;
        float startVolume = 1F;
        float startPitch = 1F;

        if (config.getBoolean("PlayEventStartSound")) {
            String soundId = config.getString("EventStart.Sound");
            if (soundId != null && !soundId.isBlank()) {
                startSound = RPGMethods.resolveSound(soundId);
                startVolume = (float) config.getDouble("EventStart.Volume");
                startPitch = (float) config.getDouble("EventStart.Pitch");
            }
        }

        Sound stopSound = null;
        float stopVolume = 1F;
        float stopPitch = 1F;

        if (config.getBoolean("PlayEventStopSound")) {
            String soundId = config.getString("EventStop.Sound");
            if (soundId != null && !soundId.isBlank()) {
                stopSound = RPGMethods.resolveSound(soundId);
                stopVolume = (float) config.getDouble("EventStop.Volume");
                stopPitch = (float) config.getDouble("EventStop.Pitch");
            }
        }

        if (startSound == null && stopSound == null) return null;
        return new SoundDisplay(startSound, startVolume, startPitch, stopSound, stopVolume, stopPitch);
    }

    private AttributeRules parseAttributeRules(FileConfiguration config) {
        ConfigurationSection root = config.getConfigurationSection("Abilities");
        if (root == null) {
            return new AttributeRules(Map.of(), Map.of(), Map.of());
        }

        Map<String, Object> global = Map.of();
        ConfigurationSection globalSec = root.getConfigurationSection("_All");
        if (globalSec != null) {
            global = globalSec.getValues(false);
        }

        Map<String, Map<String, Object>> byElement = new HashMap<>();
        Map<String, Map<String, Map<String, Object>>> byAbility = new HashMap<>();

        for (String elemKey : root.getKeys(false)) {
            if ("_All".equalsIgnoreCase(elemKey)) continue;

            ConfigurationSection elemSec = root.getConfigurationSection(elemKey);
            if (elemSec == null) continue;

            ConfigurationSection elemAll = elemSec.getConfigurationSection("_All");
            if (elemAll != null) {
                byElement.put(elemKey, elemAll.getValues(false));
            }

            Map<String, Map<String, Object>> abilityMap = new HashMap<>();
            for (String child : elemSec.getKeys(false)) {
                if ("_All".equalsIgnoreCase(child)) continue;
                ConfigurationSection abilitySec = elemSec.getConfigurationSection(child);
                if (abilitySec == null) continue;

                Map<String, Object> attrs = abilitySec.getValues(false);
                if (!attrs.isEmpty()) {
                    abilityMap.put(child, attrs);
                }
            }
            if (!abilityMap.isEmpty()) {
                byAbility.put(elemKey, abilityMap);
            }
        }

        return new AttributeRules(global, byElement, byAbility);
    }

    /**
     * TEMP METHOD
     */
    private @Nullable ScheduleSpecifications parseSchedule(FileConfiguration config) {
        ConfigurationSection sec = config.getConfigurationSection("Schedule");
        if (sec == null) {
            return null;
        }

        String rawCalendar = sec.getString("Calendar", "REALTIME");
        ScheduleType type = ScheduleType.fromString(rawCalendar);
        ScheduleSpecifications.Calendar calendar = (type == ScheduleType.IN_GAME_DAYS)
                ? ScheduleSpecifications.Calendar.IN_GAME_DAYS
                : ScheduleSpecifications.Calendar.REAL_DAYS;

        LocalTime at = ScheduleParser.parseTimeOfDay(sec.getString("At"), LocalTime.of(7, 0));
        Duration repeat = ScheduleParser.parseDuration(sec.getString("Repeat"), Duration.ofDays(7));
        Duration offset = ScheduleParser.parseDuration(sec.getString("Offset"), Duration.ZERO);
        Duration cooldown = ScheduleParser.parseDuration(sec.getString("Cooldown"), Duration.ofDays(1));
        double chance = sec.getDouble("TriggerChance", 0.5D);

        return new ScheduleSpecifications(calendar, at, repeat, offset, cooldown, chance);
    }
}
