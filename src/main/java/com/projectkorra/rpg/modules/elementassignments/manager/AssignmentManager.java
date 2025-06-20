package com.projectkorra.rpg.modules.elementassignments.manager;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.elementassignments.util.AssignmentGroup;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssignmentManager {
    double chance = 0.2;
    boolean changeOnDeathEnabled = true; // Allow changing element on death
    boolean changeOnDeathBypass = false; // Allow bypassing cooldowns for changing elements on death (if true, will ignore cooldowns)
    String changeOnDeathPermission;
    String defaultElement = "None"; // Default element to assign if no group is found
    Set<String> permissionGroups = new HashSet<>();

    // Map every Group to a weight
    private List<AssignmentGroup> groups = new ArrayList<>();
    private double totalWeight = 0;
    private boolean enabled = false;

    public AssignmentManager() {

        if (ConfigManager.defaultConfig.get().getBoolean("Modules.ElementAssignments.Enabled")) {
            setEnabled(true);
            // Get the default group from the configuration
            defaultElement = ConfigManager.defaultConfig.get().getString("Modules.ElementAssignments.Default");
            changeOnDeathEnabled = ConfigManager.defaultConfig.get().getBoolean("Modules.ElementAssignments.ChangeOnDeath.Enabled");
            changeOnDeathBypass = ConfigManager.defaultConfig.get().getBoolean("Modules.ElementAssignments.ChangeOnDeath.Bypass");
            changeOnDeathPermission = ConfigManager.defaultConfig.get().getString("Modules.ElementAssignments.ChangeOnDeath.Permission");
            chance = ConfigManager.defaultConfig.get().getDouble("Modules.ElementAssignments.ChangeOnDeath.Chance");

            // Create a list to hold enabled groups with their weights
            ConfigurationSection groupsSection = ConfigManager.defaultConfig.get().getConfigurationSection("Modules.ElementAssignments.Groups");
            // Loop through each group in the configuration
			for (String groupKey : groupsSection.getKeys(false)) {
                boolean groupEnabled = ConfigManager.defaultConfig.get().getBoolean("Modules.ElementAssignments.Groups." + groupKey + ".Enabled");
                if (groupEnabled) {
                    double weight = ConfigManager.defaultConfig.get().getDouble("Modules.ElementAssignments.Groups." + groupKey + ".Weight");
                    List<String> elements = ConfigManager.defaultConfig.get().getStringList("Modules.ElementAssignments.Groups." + groupKey + ".Elements");
                    String prefix = ConfigManager.defaultConfig.get().getString("Modules.ElementAssignments.Groups." + groupKey + ".Prefix");
                    List<String> commandsToRun = ConfigManager.defaultConfig.get().getStringList("Modules.ElementAssignments.Groups." + groupKey + ".Commands");
                    String permissionGroup = ConfigManager.defaultConfig.get().getString("Modules.ElementAssignments.Groups." + groupKey + ".PermissionGroup");
                    permissionGroups.add(permissionGroup);
                    totalWeight = totalWeight + weight;
                    AssignmentGroup group = new AssignmentGroup(groupKey, elements, weight, true, prefix, commandsToRun, permissionGroup);
                    groups.add(group);
                    ProjectKorraRPG.getPlugin().getLogger().info("ElementAssignments: " + groupKey + " is enabled with weight: " + weight);
                }
            }
        } else {
            ProjectKorraRPG.getPlugin().getLogger().info("ElementAssignments is disabled in the config.yml. Please enable it to use this feature.");
            setEnabled(false);
        }
    }

    public AssignmentGroup getRandomGroup() {
        if (groups.isEmpty() || totalWeight <= 0) {
            return null;
        }
        double randomValue = Math.random() * totalWeight;
        double cumulative = 0;

        for (AssignmentGroup group : groups) {
            cumulative += group.getWeight();
            if (randomValue < cumulative) {
                return group;
            }
        }
        return null;
    }

    /**
     * Assigns a group to a player, assigning and removing elements as needed.
     *
     * @param assignmentGroup
     * @param bendingPlayer
     */
    public void assignGroup(AssignmentGroup assignmentGroup, BendingPlayer bendingPlayer) {
        if (bendingPlayer == null) {
            return;
        }

        // Remove all elements from the player
        bendingPlayer.getElements().clear();

        for (Element element : assignmentGroup.getElements()) {
            if (element instanceof Element.SubElement) {
                bendingPlayer.addSubElement((Element.SubElement) element);
            } else {
                bendingPlayer.addElement(element);
            }
            ChatUtil.sendBrandingMessage(bendingPlayer.getPlayer(), element.getColor() + "You are now a " + element.getName() + "bender.");
        }

        for (String command : assignmentGroup.getCommandsToRun()) {
            String formattedCommand = command.replace("%player%", bendingPlayer.getName());
            if (bendingPlayer.isOnline()) {
                ProjectKorraRPG.getPlugin().getServer().dispatchCommand(ProjectKorra.plugin.getServer().getConsoleSender(), formattedCommand);
            }
        }

        Player player = bendingPlayer.getPlayer();

        if (!assignmentGroup.getPermissionGroup().isEmpty()) {
            for (String group : permissionGroups) {
                if (!group.equalsIgnoreCase(assignmentGroup.getPermissionGroup()) && player.hasPermission("group." + group)) {
                    // Remove the old group permission
                    RPGMethods.removePermission(player, "group." + group);
                }
            }
            // Add the new group permission
            RPGMethods.addPermission(player, "group." + assignmentGroup.getPermissionGroup());
        }

        if (bendingPlayer.isOnline()) {
            ProjectKorraRPG.getPlugin().getLogger().info(player.getName() + " has been assigned the " + assignmentGroup.getName() + " group.");
        }
    }

    public void assignRandomGroup(BendingPlayer bp, boolean onDeath) {
        if (onDeath) {
            // If change on death isn't enabled, do nothing
            if (!changeOnDeathEnabled)
                return;
            if (!(changeOnDeathBypass && bp.getPlayer().hasPermission(changeOnDeathPermission))) {
                if (Math.random() > chance) {
                    return;
                }
            }
        }

        AssignmentGroup group = getRandomGroup();
        if (group != null) {
            assignGroup(group, bp);
        } else {
            if (bp.isOnline()) {
                ChatUtil.sendBrandingMessage(bp.getPlayer(), "No group could be assigned.");
            }
        }
    }

    public List<AssignmentGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<AssignmentGroup> groups) {
        this.groups = groups;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
