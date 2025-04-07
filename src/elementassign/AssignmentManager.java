package elementassign;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.ConfigManager;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.projectkorra.projectkorra.object.Preset.config;
import static com.projectkorra.rpg.ProjectKorraRPG.api;

public class AssignmentManager {
    // Map every Group to a weight
    private List<AssignmentGroup> groups = new ArrayList<>();
    private double totalWeight = 0;
    double chance = 0.2;
    boolean changeOnDeathEnabled = true; // Allow changing element on death
    boolean changeOnDeathBypass = false; // Allow bypassing cooldowns for changing elements on death (if true, will ignore cooldowns)
    String changeOnDeathPermission;
    String defaultElement = "None"; // Default element to assign if no group is found
    Set<String> permissionGroups = new HashSet<>();

    public AssignmentManager() {

        if (ConfigManager.rpgConfig.get().getBoolean("ElementAssign.Enabled")) {
            // Get the default group from the configuration
            defaultElement = ConfigManager.rpgConfig.get().getString("ElementAssign.Default");
            changeOnDeathEnabled = ConfigManager.rpgConfig.get().getBoolean("ElementAssign.ChangeOnDeath.Enabled");
            changeOnDeathBypass = ConfigManager.rpgConfig.get().getBoolean("ElementAssign.ChangeOnDeath.Bypass");
            changeOnDeathPermission = ConfigManager.rpgConfig.get().getString("ElementAssign.ChangeOnDeath.Permission");
            chance = ConfigManager.rpgConfig.get().getDouble("ElementAssign.ChangeOnDeath.Chance");

            // Create a list to hold enabled groups with their weights
            ConfigurationSection groupsSection = ConfigManager.rpgConfig.get().getConfigurationSection("ElementAssign.Groups");
            // Loop through each group in the configuration
            for (String groupKey : groupsSection.getKeys(false)) {
                boolean groupEnabled = ConfigManager.rpgConfig.get().getBoolean("ElementAssign.Groups." + groupKey + ".Enabled");
                if (groupEnabled) {
                    double weight = ConfigManager.rpgConfig.get().getDouble("ElementAssign.Groups." + groupKey + ".Weight");
                    List<String> elements = ConfigManager.rpgConfig.get().getStringList("ElementAssign.Groups." + groupKey + ".Elements");
                    String prefix = ConfigManager.rpgConfig.get().getString("ElementAssign.Groups." + groupKey + ".Prefix");
                    List<String> commandsToRun = ConfigManager.rpgConfig.get().getStringList("ElementAssign.Groups." + groupKey + ".Commands");
                    String permissionGroup = ConfigManager.rpgConfig.get().getString("ElementAssign.Groups." + groupKey + ".PermissionGroup");
                    permissionGroups.add(permissionGroup);
                    totalWeight = totalWeight + weight;
                    AssignmentGroup group = new AssignmentGroup(groupKey, elements, weight, groupEnabled, prefix, commandsToRun, permissionGroup);
                    groups.add(group);
                    ProjectKorraRPG.plugin.getLogger().info("ElementAssign: " + groupKey + " is enabled with weight: " + weight);
                }
            }

        } else {
            ProjectKorraRPG.plugin.getLogger().info("ElementAssign is disabled in the config.yml. Please enable it to use this feature.");
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
                    api.getUserManager().getUser(player.getUniqueId()).data().remove(Node.builder("group." + group).build());
                }
            }
            // Add the new group permission
            api.getUserManager().getUser(player.getUniqueId()).data().add(Node.builder("group." + assignmentGroup.getPermissionGroup()).build());
            api.getUserManager().saveUser(api.getUserManager().getUser(player.getUniqueId()));
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
}
