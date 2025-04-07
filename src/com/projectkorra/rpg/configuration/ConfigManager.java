package com.projectkorra.rpg.configuration;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.configuration.ConfigType;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    public static final ConfigType AVATARS = new ConfigType("RPG_Avatars");
    public static final ConfigType RPG_DEFAULT = new ConfigType("RPG_Default");
    public static Config rpgConfig;
    public static Config avatarConfig;

    public ConfigManager() {
        rpgConfig = new Config(new File("RPG_config.yml"));
        avatarConfig = new Config(new File("RPG_avatars.yml"));

        configCheck(RPG_DEFAULT);
        configCheck(AVATARS);
    }

    private static void addDefaultElementAssignGroup(String groupName, boolean enabled, List<String> elements, double weight, String prefix, List<String> commands, String permissionGroup) {
        if (rpgConfig == null) {
            return; // Ensure rpgConfig is initialized
        }
        FileConfiguration config = rpgConfig.get();

        config.addDefault("ElementAssign.Groups." + groupName + ".Enabled", enabled);
        config.addDefault("ElementAssign.Groups." + groupName + ".Elements", elements);
        config.addDefault("ElementAssign.Groups." + groupName + ".Weight", weight);
        config.addDefault("ElementAssign.Groups." + groupName + ".Prefix", prefix);
        config.addDefault("ElementAssign.Groups." + groupName + ".Commands", commands);
        config.addDefault("ElementAssign.Groups." + groupName + ".PermissionGroup", permissionGroup);
    }

    public void configCheck(ConfigType type) {
        FileConfiguration config;
        if (type == RPG_DEFAULT) {
            config = rpgConfig.get();

            config.addDefault("WorldEvents.FullMoon.Enabled", true);
            config.addDefault("WorldEvents.FullMoon.Factor", 3.0);
            config.addDefault("WorldEvents.FullMoon.Message", "A full moon is rising, empowering waterbending like never before.");

            config.addDefault("WorldEvents.LunarEclipse.Enabled", true);
            config.addDefault("WorldEvents.LunarEclipse.Frequency", 40);
            config.addDefault("WorldEvents.LunarEclipse.Message", "A lunar eclipse is out! Waterbenders are temporarily powerless.");

            config.addDefault("WorldEvents.SolarEclipse.Enabled", true);
            config.addDefault("WorldEvents.SolarEclipse.Frequency", 20);
            config.addDefault("WorldEvents.SolarEclipse.Message", "A solar eclipse is out! Firebenders are temporarily powerless.");

            config.addDefault("WorldEvents.SozinsComet.Enabled", true);
            config.addDefault("WorldEvents.SozinsComet.Frequency", 100);
            config.addDefault("WorldEvents.SozinsComet.Factor", 5.0);
            config.addDefault("WorldEvents.SozinsComet.Message", "Sozin's Comet is passing overhead! Firebending is now at its most powerful.");
            config.addDefault("WorldEvents.SozinsComet.EndMessage", "Sozin's Comet has passed.");

            config.addDefault("Abilities.AvatarStateOnFinalBlow", true);

            config.addDefault("ElementAssign.Enabled", true);
            config.addDefault("ElementAssign.Default", "None");
            config.addDefault("ElementAssign.ChangeOnDeath.Enabled", true); // Allow changing element on death
            config.addDefault("ElementAssign.ChangeOnDeath.Chance", 0.2); // 20% chance to change element on death if
            // enabled
            config.addDefault("ElementAssign.ChangeOnDeath.Bypass", false); // Allow bypassing cooldowns for changing
            // elements on death (if true, will ignore
            // cooldowns)
            config.addDefault("ElementAssign.ChangeOnDeath.Permission", "projectkorrarpg.elementassign.bypass");
            // Empty Set
            Set<String> elementNames = new HashSet<>();

            // No Element
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

            config.addDefault("Storage.engine", "sqlite");

            config.addDefault("Storage.MySQL.host", "localhost");
            config.addDefault("Storage.MySQL.port", 3306);
            config.addDefault("Storage.MySQL.pass", "");
            config.addDefault("Storage.MySQL.db", "minecraft");
            config.addDefault("Storage.MySQL.user", "root");
            config.options().copyDefaults(true);
            rpgConfig.save();
        } else if (type == AVATARS) {
            // Nothing needs to be set in the avatars.yml yet
        }
    }
}
