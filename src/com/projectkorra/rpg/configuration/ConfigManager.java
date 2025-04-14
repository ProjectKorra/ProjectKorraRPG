package com.projectkorra.rpg.configuration;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.configuration.ConfigType;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class ConfigManager {
    private static final ConfigType DEFAULT = new ConfigType("Default");
    private static final ConfigType LANGUAGE = new ConfigType("Language");
    private static final ConfigType WORLDEVENTS = new ConfigType("WorldEvents");

    public static Config config;
    public static Config language;
    public static Config sozinsComet;

    public ConfigManager() {
        config = new Config(new File("config.yml"));
        language = new Config(new File("language.yml"));
        sozinsComet = new Config(new File("WorldEvents/sozinscomet.yml"));

        configCheck(DEFAULT);
        configCheck(LANGUAGE);
        configCheck(WORLDEVENTS);
    }

    private static void addDefaultElementAssignGroup(String groupName, boolean enabled, List<String> elements, double weight, String prefix, List<String> commands, String permissionGroup) {
        FileConfiguration config = ConfigManager.config.get();

        config.addDefault("Modules.ElementAssignments.Groups." + groupName + ".Enabled", enabled);
        config.addDefault("Modules.ElementAssignments.Groups." + groupName + ".Elements", elements);
        config.addDefault("Modules.ElementAssignments.Groups." + groupName + ".Weight", weight);
        config.addDefault("Modules.ElementAssignments.Groups." + groupName + ".Prefix", prefix);
        config.addDefault("Modules.ElementAssignments.Groups." + groupName + ".Commands", commands);
        config.addDefault("Modules.ElementAssignments.Groups." + groupName + ".PermissionGroup", permissionGroup);
    }

    public void configCheck(ConfigType type) {
        FileConfiguration config;
        if (type == DEFAULT) {
            config = ConfigManager.config.get();

            // -------------------------------- Randomized Avatar  ---------------------------------

            config.addDefault("Modules.RandomAvatar.Enabled", true);
            config.addDefault("Modules.RandomAvatar.MaxAvatars", 1);
            config.addDefault("Modules.RandomAvatar.TimeSinceLoginRequired", "12h");
            config.addDefault("Modules.RandomAvatar.RepeatSelectionCooldown", "7d");
            config.addDefault("Modules.RandomAvatar.Broadcast.Enabled", true);
            config.addDefault("Modules.RandomAvatar.Broadcast.Public", false);
            config.addDefault("Modules.RandomAvatar.AvatarDuration", "7d");
            config.addDefault("Modules.RandomAvatar.LoseAvatarOnDeath", true);
            config.addDefault("Modules.RandomAvatar.OnlyLoseAvatarOnAvatarStateDeath", true);
            config.addDefault("Modules.RandomAvatar.ClearOnSelection", true);
            config.addDefault("Modules.RandomAvatar.Elements", List.of( "earth", "water", "fire", "air", "avatar"));
            config.addDefault("Modules.RandomAvatar.IncludeAllSubElements", true);
            config.addDefault("Modules.RandomAvatar.SubElementBlacklist", List.of("blood"));
            config.setComments("Modules.RandomAvatar.Enabled", List.of("Whether to enable the Avatar randomization system", "This gives every player a chance to become Avatar"));

            config.setComments("Modules.RandomAvatar.MaxAvatars", List.of("Maximum number of RPG Avatars that can exist at once"));
            config.setComments("Modules.RandomAvatar.TimeSinceLoginRequired", List.of("A player must have logged in within this time frame to be eligible for Avatar selection.", "By default we only consider players that have logged in within the last 12 hours. Can be formatted like 3d2h5m"));
            config.setComments("Modules.RandomAvatar.RepeatSelectionCooldown", List.of("Amount of time that must pass before a player can become Avatar again"));
            config.setComments("Modules.RandomAvatar.Broadcast.Enabled", List.of("Should we broadcast when a player becomes Avatar?"));
            config.setComments("Modules.RandomAvatar.Broadcast.Public", List.of("Should we include the Avatar's name in the broadcast?"));
            config.setComments("Modules.RandomAvatar.AvatarDuration", List.of("Maximum amount of time a player can be an RPG Avatar. ", "After this time, the player will lose Avatar and have their previous elements restored"));
            config.setComments("Modules.RandomAvatar.LoseAvatarOnDeath", List.of("Whether or not an Avatar should lose Avatar on death"));
            config.setComments("Modules.RandomAvatar.OnlyLoseAvatarOnAvatarStateDeath", List.of("This only has an effect if LoseAvatarOnDeath is true", "If true, an Avatar will only lose Avatar from dying in the Avatar State."));
            config.setComments("Modules.RandomAvatar.ClearOnSelection", List.of("Whether the player should have their elements scrubbed when becoming Avatar", "Setting to true guarantees the player will only have the Elements listed below"));
            config.setComments("Modules.RandomAvatar.Elements", List.of("Elements that the Avatar will always have"));
            config.setComments("Modules.RandomAvatar.IncludeAllSubElements", List.of("Whether or not each element's sub-elements should be added to the avatar", "If disabled, you'll need to manually specify subelements in the Elements list"));
            config.setComments("Modules.RandomAvatar.SubElementBlacklist", List.of("Subelements that will not be given to the Avatar"));

            // -------------------------------- Element Assignments  ---------------------------------

            config.addDefault("Modules.ElementAssignments.Enabled", true);
            config.addDefault("Modules.ElementAssignments.Default", "None");
            config.addDefault("Modules.ElementAssignments.ChangeOnDeath.Enabled", true); // Allow changing element on death
            config.addDefault("Modules.ElementAssignments.ChangeOnDeath.Chance", 0.2); // 20% chance to change element on death if
            config.addDefault("Modules.ElementAssignments.ChangeOnDeath.Bypass", false); // Allow bypassing cooldowns for changing
            config.addDefault("Modules.ElementAssignments.ChangeOnDeath.Permission", "projectkorra.rpg.elementassign.bypass");

            Set<String> elementNames = new HashSet<>();

            addDefaultElementAssignGroup("None", // Group Name
                    true, // Enabled
                    List.of(), // Elements (none)
                    0.05, // Default weight for None group
                    "", // Prefix (no prefix for None)
                    List.of(), // Commands to run (default empty)
                    "" // Permission Group to assign
            );

            // Add all the base elements
            addDefaultElementAssignGroup(Element.AIR.getName(), // Group Name (e.g. air)
                    true, // Enabled
                    List.of(Element.AIR.getName().toLowerCase()), // Elements (just the element itself)
                    0.1, // Default weight for this element group
                    "", // Prefix (can be overridden by the parent)
                    List.of(), // Commands to run (default empty)
                    "" // Permission Group to assign
            );
            elementNames.add(Element.AIR.getName().toLowerCase());

            addDefaultElementAssignGroup(Element.SPIRITUAL.getName(), true, List.of(Element.AIR.getName().toLowerCase(), Element.SPIRITUAL.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.FLIGHT.getName(), true, List.of(Element.AIR.getName().toLowerCase(), Element.FLIGHT.getName().toLowerCase()), 0.01, "", List.of(), "");

            addDefaultElementAssignGroup(Element.WATER.getName() + "_Standalone", // Group Name (e.g. water)
                    false, // Enabled
                    List.of(Element.WATER.getName().toLowerCase()), // Elements (just the element itself)
                    0.1, // Default weight for this element group
                    "", // Prefix (can be overridden by the parent)
                    List.of(), // Commands to run (default empty)
                    "" // Permission Group to assign
            );
            elementNames.add(Element.WATER.getName().toLowerCase());

            addDefaultElementAssignGroup(Element.WATER.getName(), // Group Name (e.g. water)
                    true, // Enabled
                    List.of(Element.WATER.getName().toLowerCase(), Element.ICE.getName().toLowerCase() // Include ice as a sub-element of water
                    ), 0.1, // Default weight for this element group
                    "", // Prefix (can be overridden by the parent)
                    List.of(), // Commands to run (default empty)
                    "" // Permission Group to assign
            );

            addDefaultElementAssignGroup(Element.PLANT.getName(), // Group Name (e.g. plant)
                    true, // Enabled
                    List.of(Element.WATER.getName().toLowerCase(), // Parent element (e.g. Water)
                            Element.PLANT.getName().toLowerCase() // The sub-element itself (e.g. Plant)
                    ), // Elements (Water and Plant)
                    0.05, // Default weight for this sub-element group
                    "", // Prefix (can be overridden by the parent)
                    List.of(), // Commands to run (default empty)
                    "" // Permission Group to assign
            );

            addDefaultElementAssignGroup(Element.BLOOD.getName(), true, List.of(Element.WATER.getName().toLowerCase(), Element.BLOOD.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.HEALING.getName(), true, List.of(Element.WATER.getName().toLowerCase(), Element.HEALING.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.EARTH.getName(), // Group Name (e.g. earth)
                    true, // Enabled
                    List.of(Element.EARTH.getName().toLowerCase()), // Elements (just the element itself)
                    0.1, // Default weight for this element group
                    "", // Prefix (can be overridden by the parent)
                    List.of(), // Commands to run (default empty)
                    "" // Permission Group to assign
            );
            elementNames.add(Element.EARTH.getName().toLowerCase());

            addDefaultElementAssignGroup(Element.METAL.getName(), true, List.of(Element.EARTH.getName().toLowerCase(), Element.METAL.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.SAND.getName(), true, List.of(Element.EARTH.getName().toLowerCase(), Element.SAND.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.LAVA.getName(), true, List.of(Element.EARTH.getName().toLowerCase(), Element.LAVA.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.FIRE.getName(), // Group Name (e.g. fire)
                    true, // Enabled
                    List.of(Element.FIRE.getName().toLowerCase()), // Elements (just the element itself)
                    0.1, // Default weight for this element group
                    "", // Prefix (can be overridden by the parent)
                    List.of(), // Commands to run (default empty)
                    "" // Permission Group to assign
            );
            elementNames.add(Element.FIRE.getName().toLowerCase());

            addDefaultElementAssignGroup(Element.LIGHTNING.getName(), true, List.of(Element.FIRE.getName().toLowerCase(), Element.LIGHTNING.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.BLUE_FIRE.getName(), true, List.of(Element.FIRE.getName().toLowerCase(), Element.BLUE_FIRE.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.COMBUSTION.getName(), true, List.of(Element.FIRE.getName().toLowerCase(), Element.COMBUSTION.getName().toLowerCase()), 0.05, "", List.of(), "");

            addDefaultElementAssignGroup(Element.CHI.getName(), true, List.of(Element.CHI.getName().toLowerCase()), 0.05, "", List.of(), "");

            Arrays.stream(Element.getAddonSubElements()).forEach(subElement -> {
                // We handle parent element stuff here (No subelement) just so the order is nicer
                // This way subelements are placed near their parent elements in the config file
                if (subElement.getParentElement() == null) {
                    // Skip if no parent element
                    return;
                }
                if (!elementNames.contains(subElement.getParentElement().getName().toLowerCase())) {
                    elementNames.add(subElement.getParentElement().getName().toLowerCase());
                    addDefaultElementAssignGroup(subElement.getParentElement().getName().toLowerCase(), // Group Name
                            true, // Enabled
                            List.of(subElement.getParentElement().getName().toLowerCase()), // Elements (just the parent element itself)
                            0.1, // Default weight for parent elements
                            subElement.getParentElement().getName().toLowerCase(), // Prefix
                            List.of(), // Commands to run (default empty)
                            "" // Permission Group to assign
                    );
                }

                addDefaultElementAssignGroup(subElement.getName(), // Group Name (e.g. ice)
                        true, // Enabled
                        List.of(subElement.getParentElement().getName().toLowerCase(), // Parent element (e.g.// Water for Ice)
                                subElement.getName().toLowerCase() // The sub-element itself (e.g. Ice)
                        ), // Elements
                        0.05, // Default weight for this sub-element group
                        "", // Prefix (can be overridden by the parent)
                        List.of(), // Commands to run (default empty)
                        "" // Permission Group to assign
                );
            });
            config.options().copyDefaults(true);
            config.options().parseComments(true);
            ConfigManager.config.save();

        } else if (type == LANGUAGE) {
            config = language.get();

            config.options().copyDefaults(true);
            language.save();
        } else if (type == WORLDEVENTS) {
            config = sozinsComet.get();

            config.addDefault("Title", "&cSozins Comet");
            config.addDefault("Duration", 5000);
            config.addDefault("Color", "RED");

            config.options().copyDefaults(true);
            sozinsComet.save();
        }
    }
    public static FileConfiguration getFileConfig() {
        return config.get();
    }

    public static Config getConfig() {
        return config;
    }

    // TEMP
    public static List<Config> getAllConfigs() {
        List<Config> configs = new ArrayList<>();
        configs.add(config);
        configs.add(language);
        configs.add(sozinsComet);
        return configs;
    }
}
