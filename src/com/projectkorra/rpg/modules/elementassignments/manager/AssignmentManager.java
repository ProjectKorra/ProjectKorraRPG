package com.projectkorra.rpg.modules.elementassignments.manager;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.elementassignments.listeners.AssignmentListener;
import com.projectkorra.rpg.modules.elementassignments.util.AssignmentGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssignmentManager {
    // Map every Group to a weight
    private List<AssignmentGroup> groups = new ArrayList<>();
    private double totalWeight = 0;
    private boolean enabled = false;
    double chance = 0.2;
    boolean changeOnDeathEnabled = true; // Allow changing element on death
    boolean changeOnDeathBypass = false; // Allow bypassing cooldowns for changing elements on death (if true, will ignore cooldowns)
    String changeOnDeathPermission;
    String defaultElement = "None"; // Default element to assign if no group is found
    Set<String> permissionGroups = new HashSet<>();

    public AssignmentManager() {

        if (ConfigManager.config.get().getBoolean("Modules.ElementAssignments.Enabled")) {
            ProjectKorraRPG.log.info("ElementAssignments is enabled in the config.yml.");
            setEnabled(true);
            // Get the default group from the configuration
            defaultElement = ConfigManager.config.get().getString("Modules.ElementAssignments.Default");
            changeOnDeathEnabled = ConfigManager.config.get().getBoolean("Modules.ElementAssignments.ChangeOnDeath.Enabled");
            changeOnDeathBypass = ConfigManager.config.get().getBoolean("Modules.ElementAssignments.ChangeOnDeath.Bypass");
            changeOnDeathPermission = ConfigManager.config.get().getString("Modules.ElementAssignments.ChangeOnDeath.Permission");
            chance = ConfigManager.config.get().getDouble("Modules.ElementAssignments.ChangeOnDeath.Chance");

            // Create a list to hold enabled groups with their weights
            ConfigurationSection groupsSection = ConfigManager.config.get().getConfigurationSection("Modules.ElementAssignments.Groups");
            // Loop through each group in the configuration
            for (String groupKey : groupsSection.getKeys(false)) {
                boolean groupEnabled = ConfigManager.config.get().getBoolean("Modules.ElementAssignments.Groups." + groupKey + ".Enabled");
                if (groupEnabled) {
                    double weight = ConfigManager.config.get().getDouble("Modules.ElementAssignments.Groups." + groupKey + ".Weight");
                    List<String> elements = ConfigManager.config.get().getStringList("Modules.ElementAssignments.Groups." + groupKey + ".Elements");
                    String prefix = ConfigManager.config.get().getString("Modules.ElementAssignments.Groups." + groupKey + ".Prefix");
                    List<String> commandsToRun = ConfigManager.config.get().getStringList("Modules.ElementAssignments.Groups." + groupKey + ".Commands");
                    String permissionGroup = ConfigManager.config.get().getString("Modules.ElementAssignments.Groups." + groupKey + ".PermissionGroup");
                    permissionGroups.add(permissionGroup);
                    totalWeight = totalWeight + weight;
                    AssignmentGroup group = new AssignmentGroup(groupKey, elements, weight, groupEnabled, prefix, commandsToRun, permissionGroup);
                    groups.add(group);
                    ProjectKorraRPG.plugin.getLogger().info("ElementAssignments: " + groupKey + " is enabled with weight: " + weight);
                }
            }
            Bukkit.getServer().getPluginManager().registerEvents(new AssignmentListener(), ProjectKorraRPG.plugin);

        } else {
            ProjectKorraRPG.plugin.getLogger().info("ElementAssignments is disabled in the config.yml. Please enable it to use this feature.");
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
     * @param assignmentGroup
     * @param bp
     */
    public void assignGroup(AssignmentGroup assignmentGroup, BendingPlayer bp) {
        // Remove all elements from the player
        if (bp == null) {
            return;
        }

        bp.getElements().clear();
        Player player = bp.getPlayer();

        for (Element element : assignmentGroup.getElements()) {
            bp.addElement(element);
            bp.getPlayer().sendMessage(ChatColor.GOLD + "ProjectKorraRPG " + element.getColor() + "You are now a " + element.getName() + "bender.");
        }

        for (String command : assignmentGroup.getCommandsToRun()) {
            String formattedCommand = command.replace("%player%", bp.getName());
            if (bp.isOnline()) {
                ProjectKorraRPG.plugin.getServer().dispatchCommand(ProjectKorra.plugin.getServer().getConsoleSender(), formattedCommand);
            }
        }
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

        if (bp.isOnline()) {
            ProjectKorraRPG.plugin.getLogger().info(player.getName() + " has been assigned the " + assignmentGroup.getName() + " group.");
        }
    }

    public void assignRandomGroup(BendingPlayer bp, boolean onDeath) {
        if (onDeath) {
            // If change on death isn't enabled do nothing
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
                bp.getPlayer().sendMessage("No group could be assigned.");
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
