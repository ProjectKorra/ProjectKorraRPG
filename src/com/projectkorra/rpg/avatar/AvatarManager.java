package com.projectkorra.rpg.avatar;

import com.projectkorra.projectkorra.*;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.MySQL;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.elementassign.AssignmentListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AvatarManager {
    private boolean isEnabled = true;
    private int maxAvatars = 1;
    private int currentAvatars = 0;
    private double avatarDuration = 168.0;
    private boolean loseAvatarOnDeath = true;
    private boolean loseAvatarOnAvatarStateDeath = true;
    private Set<Element> avatarElements;
    private boolean includeAllSubElements = true;
    private boolean clearOnSelect = true;
    private Set<Element.SubElement> subElementBlacklist;
    // Using a list since avatars may come and go frequently
    private Set<OfflinePlayer> avatars;
    private Set<UUID> avatarsToRemove = new HashSet<>();
    private double timeSinceLogonRequired = 12.0;

    private double repeatSelectionCooldown = 168.0;

    private boolean broadcastAvatarSelection = true;
    private boolean publicBroadcast = false;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /** Maximum number of avatars that can be active at once */
    public int getMaxAvatars() {
        return maxAvatars;
    }

    public void setMaxAvatars(int maxAvatars) {
        this.maxAvatars = maxAvatars;
    }

    /** Current number of active avatars */
    public int getCurrentAvatars() {
        return currentAvatars;
    }

    public void setCurrentAvatars(int currentAvatars) {
        this.currentAvatars = currentAvatars;
    }

    /** Max amount of time (hours) a player can be the avatar */
    public double getAvatarDuration() {
        return avatarDuration;
    }

    public void setAvatarDuration(double avatarDuration) {
        this.avatarDuration = avatarDuration;
    }

    public boolean isLoseAvatarOnDeath() {
        return loseAvatarOnDeath;
    }

    public void setLoseAvatarOnDeath(boolean loseAvatarOnDeath) {
        this.loseAvatarOnDeath = loseAvatarOnDeath;
    }

    public boolean onlyLoseAvatarOnAvatarStateDeath() {
        return loseAvatarOnAvatarStateDeath;
    }

    public void setLoseAvatarOnAvatarStateDeath(boolean loseAvatarOnAvatarStateDeath) {
        this.loseAvatarOnAvatarStateDeath = loseAvatarOnAvatarStateDeath;
    }

    public Set<Element> getAvatarElements() {
        return avatarElements;
    }

    public void setAvatarElements(Set<Element> avatarElements) {
        this.avatarElements = avatarElements;
    }

    public boolean isIncludeAllSubElements() {
        return includeAllSubElements;
    }

    public void setIncludeAllSubElements(boolean includeAllSubElements) {
        this.includeAllSubElements = includeAllSubElements;
    }

    public boolean isClearOnSelect() {
        return clearOnSelect;
    }

    public void setClearOnSelect(boolean clearOnSelect) {
        this.clearOnSelect = clearOnSelect;
    }

    public Set<Element.SubElement> getSubElementBlacklist() {
        return subElementBlacklist;
    }

    public void setSubElementBlacklist(Set<Element.SubElement> subElementBlacklist) {
        this.subElementBlacklist = subElementBlacklist;
    }

    public Set<OfflinePlayer> getAvatars() {
        return avatars;
    }

    public void setAvatars(Set<OfflinePlayer> avatars) {
        this.avatars = avatars;
    }

    public Set<UUID> getAvatarsToRemove() {
        return avatarsToRemove;
    }

    public void setAvatarsToRemove(Set<UUID> avatarsToRemove) {
        this.avatarsToRemove = avatarsToRemove;
    }

    public double getTimeSinceLogonRequired() {
        return timeSinceLogonRequired;
    }

    public void setTimeSinceLogonRequired(double timeSinceLogonRequired) {
        this.timeSinceLogonRequired = timeSinceLogonRequired;
    }

    public double getRepeatSelectionCooldown() {
        return repeatSelectionCooldown;
    }

    public void setRepeatSelectionCooldown(double repeatSelectionCooldown) {
        this.repeatSelectionCooldown = repeatSelectionCooldown;
    }

    public boolean isBroadcastAvatarSelection() {
        return broadcastAvatarSelection;
    }

    public void setBroadcastAvatarSelection(boolean broadcastAvatarSelection) {
        this.broadcastAvatarSelection = broadcastAvatarSelection;
    }

    public boolean isPublicBroadcast() {
        return publicBroadcast;
    }

    public void setPublicBroadcast(boolean publicBroadcast) {
        this.publicBroadcast = publicBroadcast;
    }

    // Enum RemovalReason
    public enum RemovalReason {
        DEATH,
        AVATAR_STATE_DEATH,
        COMMAND,
        EXPIRED
    }

    public AvatarManager() {
        if (ConfigManager.rpgConfig.get().getBoolean("Avatar.Randomization.Enabled")) {
            setEnabled(true);
            setMaxAvatars(ConfigManager.rpgConfig.get().getInt("Avatar.Randomization.MaxAvatars"));
            setAvatarDuration(ConfigManager.rpgConfig.get().getDouble("Avatar.Randomization.AvatarDuration"));
            setLoseAvatarOnDeath(ConfigManager.rpgConfig.get().getBoolean("Avatar.Randomization.LoseAvatarOnDeath"));
            setLoseAvatarOnAvatarStateDeath(ConfigManager.rpgConfig.get().getBoolean("Avatar.Randomization.OnlyLoseAvatarOnAvatarStateDeath"));
            setIncludeAllSubElements(ConfigManager.rpgConfig.get().getBoolean("Avatar.Randomization.IncludeAllSubElements"));
            setClearOnSelect(ConfigManager.rpgConfig.get().getBoolean("Avatar.Randomization.ClearOnSelection"));
            setTimeSinceLogonRequired(ConfigManager.rpgConfig.get().getDouble("Avatar.Randomization.TimeSinceLogonRequired"));
            setRepeatSelectionCooldown(ConfigManager.rpgConfig.get().getDouble("Avatar.Randomization.RepeatSelectionCooldown"));
            setBroadcastAvatarSelection(ConfigManager.rpgConfig.get().getBoolean("Avatar.Randomization.Broadcast.Enabled"));
            setPublicBroadcast(ConfigManager.rpgConfig.get().getBoolean("Avatar.Randomization.Broadcast.Public"));
            setSubElementBlacklist(new HashSet<>());
            for (String subElementName : ConfigManager.rpgConfig.get().getStringList("Avatar.Randomization.SubElementBlacklist")) {
                if (Element.getElement(subElementName) != null && Element.getElement(subElementName) instanceof Element.SubElement subElement) {
                    getSubElementBlacklist().add(subElement);
                }
            }
            setAvatarElements(new HashSet<>());
            for (String elementName : ConfigManager.rpgConfig.get().getStringList("Avatar.Randomization.Elements")) {
                Element element = Element.getElement(elementName);
                if (element != null) {
                    getAvatarElements().add(element);
                    if (isIncludeAllSubElements()) {
                        for (Element.SubElement subElement : Element.getSubElements()) {
                            // Exclude blacklisted subelements
                            if (!getSubElementBlacklist().contains(subElement)) {
                                getAvatarElements().add(subElement);
                            }
                        }
                    }
                }
            }
            setAvatars(new HashSet<>());
            createRPGTables();
            checkAvatars();
            Bukkit.getServer().getPluginManager().registerEvents(new AvatarListener(), ProjectKorraRPG.plugin);
        } else {
            setEnabled(false);
            ProjectKorraRPG.plugin.getLogger().info("Avatar Randomization is disabled in the config.yml. Please enable it to use this feature.");
        }
    }

    private void createRPGTables() {
        if (DBConnection.sql instanceof MySQL) {
            if (!DBConnection.sql.tableExists("pk_rpg_avatars")) {
                ProjectKorra.log.info("Creating pk_rpg_avatars table");
                final String query = "CREATE TABLE `pk_rpg_avatars` ("
                        + "`uuid` varchar(36) NOT NULL,"
                        + "`player` varchar(255) NOT NULL,"
                        + "`startTime" + "` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + "`elements` varchar(255) NOT NULL,"
                        + "PRIMARY KEY (`uuid`)"
                        + ");";
                DBConnection.sql.modifyQuery(query, false);
            }

            if (!DBConnection.sql.tableExists("pk_rpg_pastlives")) {
                ProjectKorra.log.info("Creating pk_rpg_pastlives table");
                final String query = "CREATE TABLE `pk_rpg_pastlives` ("
                        + "`uuid` varchar(36) NOT NULL,"
                        + "`startTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + "`player` varchar(255) NOT NULL,"
                        + "`endTime` datetime DEFAULT NULL,"
                        + "`elements` varchar(255) NOT NULL,"
                        + "`endReason` varchar(255) DEFAULT NULL,"
                        // Use composite key for uuid and startTime
                        + "PRIMARY KEY (`uuid`, `startTime`)"
                        + ");";
                DBConnection.sql.modifyQuery(query, false);
            }
        } else {
            //	final String query = "CREATE TABLE `pk_players` (" + "`uuid` TEXT(36) PRIMARY KEY," + "`player` TEXT(16)," + "`element` TEXT(255)," + "`subelement` TEXT(255)," + "`permaremoved` TEXT(5)," + "`slot1` TEXT(255)," + "`slot2` TEXT(255)," + "`slot3` TEXT(255)," + "`slot4` TEXT(255)," + "`slot5` TEXT(255)," + "`slot6` TEXT(255)," + "`slot7` TEXT(255)," + "`slot8` TEXT(255)," + "`slot9` TEXT(255));";

            if (!DBConnection.sql.tableExists("pk_rpg_avatars")) {
                ProjectKorra.log.info("Creating pk_rpg_avatars table");
                final String query = "CREATE TABLE `pk_rpg_avatars` ("
                        + "`uuid` TEXT(36) PRIMARY KEY,"
                        + "`player` TEXT(16) NOT NULL,"
                        + "`startTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + "`elements` TEXT(255) NOT NULL"
                        + ");";
                DBConnection.sql.modifyQuery(query, false);
            }

            if (!DBConnection.sql.tableExists("pk_rpg_pastlives")) {
                ProjectKorra.log.info("Creating pk_rpg_pastlives table");
                final String query = "CREATE TABLE `pk_rpg_pastlives` ("
                        + "`uuid` TEXT(36) NOT NULL,"
                        + "`startTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + "`player` TEXT(16) NOT NULL,"
                        + "`endTime` datetime DEFAULT NULL,"
                        + "`elements` TEXT(255) NOT NULL,"
                        + "`endReason` TEXT(255) DEFAULT NULL,"
                        + "PRIMARY KEY (`uuid`, `startTime`)"
                        + ");";
                DBConnection.sql.modifyQuery(query, false);
            }
        }
    }

    public void checkAvatars() {
        Set<OfflinePlayer> avatars = new HashSet<>();
        // Use a local variable here and assign it to currentAvatars later just
        // so we don't risk currentAvatars being inaccaurate at any point
        int curAvatars = 0;
        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM pk_rpg_avatars");
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                String playerName = rs.getString("player");
                UUID avatarUUID = UUID.fromString(uuid);

                // Remove expired avatars or avatars past the limit
                if (curAvatars >= maxAvatars || rs.getTimestamp("startTime").toInstant().plusSeconds((long) (avatarDuration * 3600)).isBefore(Instant.now())) {
                    revokeRPGAvatar(avatarUUID, RemovalReason.EXPIRED);
                    continue;
                }

                curAvatars += 1;
                currentAvatars = curAvatars;
                avatars.add(Bukkit.getOfflinePlayer(avatarUUID));
            }
            Statement stmt = rs.getStatement();
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        setAvatars(avatars);
        if (avatars.isEmpty()) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: No current avatars found.");
        }
        chooseAvatars();
    }

    public void chooseAvatars() {
        if (getCurrentAvatars() >= getMaxAvatars()) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Current avatars limit reached.");
            return;
        }
        // First Let's gather all our past avatars from the db and determine if any are eligible to be avatar again using RepeatSelectionCooldown
        List<OfflinePlayer> invalidPastAvatars = new ArrayList<>();
        try {
            // Get all past avatars order by latest endTime for each uuid
            ResultSet rs = DBConnection.sql.readQuery("SELECT uuid, MAX(endTime) as endTime FROM pk_rpg_pastlives GROUP BY uuid");
            while (rs.next()) {
                String uuid = rs.getString("uuid");
                UUID avatarUUID = UUID.fromString(uuid);
                if (!isCurrentRPGAvatar(avatarUUID)) {
                    // If it has been less than repeatSelectionCooldown hours since they were last avatar
                    if (rs.getTimestamp("endTime").toInstant().plusSeconds((long) (getRepeatSelectionCooldown() * 3600)).isAfter(Instant.now())) {
                        invalidPastAvatars.add(Bukkit.getOfflinePlayer(avatarUUID));
                    }
                }
            }
            Statement stmt = rs.getStatement();
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<OfflinePlayer> availablePlayers = new ArrayList<>();

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            // Check if they've played in the last timeSinceLogonRequired (hours)
            if (p.isOnline() || (p.getLastPlayed() > System.currentTimeMillis() - (getTimeSinceLogonRequired() * 3600000))) {
                if (!isCurrentRPGAvatar(p.getUniqueId())) {
                    if (!invalidPastAvatars.contains(p)) {
                        availablePlayers.add(p);
                    }
                }
            }
        }
        if (availablePlayers.isEmpty())
            return;
        // Shuffle the available players
        Collections.shuffle(availablePlayers);
        int avatarsToSelect = getMaxAvatars() - getCurrentAvatars();
        for (int i = 0; i < avatarsToSelect && i < availablePlayers.size(); i++) {
            addRPGAvatar(availablePlayers.get(i).getUniqueId());
            if (isBroadcastAvatarSelection()) {
                String message = ChatColor.DARK_PURPLE + "A new Avatar has been chosen" + (isPublicBroadcast() ? ": " +availablePlayers.get(i).getName() : "!");
                Bukkit.broadcastMessage(message);
            }
        }
    }
    public boolean makeAvatar(UUID uuid) {
        if (getCurrentAvatars() >= getMaxAvatars()) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Current avatars limit reached.");
            return false;
        }
        if (isCurrentRPGAvatar(uuid)) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Player is already the current avatar.");
            return false;
        }
        String playerName = Bukkit.getOfflinePlayer(uuid).getName();
        OfflineBendingPlayer bPlayer = BendingPlayer.getOfflineBendingPlayer(playerName);
        if (bPlayer == null) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: BendingPlayer not found for player.");
            return false;
        }
        if (isAvatarEligible(uuid)) {
            return addRPGAvatar(uuid);
        } else {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Player is not eligible to become the avatar.");
            return false;
        }

    }

    /**
     * Sets a player to the avatar giving them all the elements (excluding
     * chiblocking) and the extra perks such as almost death avatarstate.
     *
     * @param uuid UUID of player being set as the avatar
     */
    public boolean addRPGAvatar(UUID uuid) {
        if(uuid == null) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: UUID is null.");
            return false;
        }
        if (getCurrentAvatars() >= getMaxAvatars()) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Current avatars limit reached.");
            return false;
        }
        if (isCurrentRPGAvatar(uuid)) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Player is already the current avatar.");
            return false;
        }
        String playerName = Bukkit.getOfflinePlayer(uuid).getName();
        OfflineBendingPlayer bPlayer = BendingPlayer.getOfflineBendingPlayer(playerName);
        if (bPlayer == null) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: BendingPlayer not found for player.");
            return false;
        }

        try {
            Timestamp timestamp = Timestamp.from(Instant.now());
            DBConnection.sql.modifyQuery("INSERT INTO pk_rpg_avatars (uuid, player, startTime, elements) VALUES ('" + uuid.toString() + "', '" + playerName + "', '" + timestamp + "', '" + String.join(",", bPlayer.getElements().stream().map(Element::getName).toArray(String[]::new)) + "')", false);
            if (isClearOnSelect())
                bPlayer.getElements().clear();
            for (Element element : getAvatarElements()) {
                if (!bPlayer.hasElement(element))
                    bPlayer.addElement(element);
            }
            setCurrentAvatars(getCurrentAvatars() + 1);
            getAvatars().add(Bukkit.getOfflinePlayer(uuid));
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(ChatColor.DARK_PURPLE + "You feel the power of the Avatar.");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if player with uuid is a current avatar. Returns false  if there
     * is no current avatar
     *
     * @param uuid UUID of player being checked
     * @return if player with uuid is the current avatar
     */
    public boolean isCurrentRPGAvatar(UUID uuid) {
        if (getAvatars() == null)
            return false;
        for (OfflinePlayer p : getAvatars()) {
            if (p != null && p.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if player with name is a current avatar. Returns null if there
     * is no current avatar
     *
     * @param name Name of player being checked
     * @return if player with name is the current avatar
     *
     */
    public boolean isCurrentRPGAvatar(String name) {
        if (getAvatars() == null)
            return false;
        for (OfflinePlayer p : getAvatars()) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player with uuid has been the avatar. Returns true if
     * player is current avatar
     *
     * @param uuid UUID of player being checked
     * @return if player with uuid has been the avatar
     */
    public boolean hasBeenAvatar(UUID uuid) {
        if (isCurrentRPGAvatar(uuid))
            return true;
        ResultSet rs = DBConnection.sql.readQuery("SELECT uuid FROM pk_rpg_pastlives WHERE uuid = '" + uuid.toString() + "'");
        boolean valid;
        try {
            valid = rs.next();
            Statement stmt = rs.getStatement();
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            valid = false;
        }
        return valid;
    }

    /**
     * Checks if the player with name has been the avatar. Returns true if
     * player is current avatar
     *
     * @param name Name of player being checked
     * @return if player with uuid has been the avatar
     */
    public boolean hasBeenAvatar(String name) {
        if (isCurrentRPGAvatar(name))
            return true;
        ResultSet rs = DBConnection.sql.readQuery("SELECT uuid FROM pk_rpg_pastlives WHERE player = '" + name + "'");
        boolean valid;
        try {
            valid = rs.next();
            Statement stmt = rs.getStatement();
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            valid = false;
        }
        return valid;
    }

    public boolean isAvatarEligible(UUID uuid) {
        if (uuid == null)
            return false;
        if (isCurrentRPGAvatar(uuid))
            return false;
        if (getCurrentAvatars() >= getMaxAvatars())
            return false;
        String playerName = Bukkit.getOfflinePlayer(uuid).getName();
        OfflineBendingPlayer bPlayer = BendingPlayer.getOfflineBendingPlayer(playerName);
        if (bPlayer == null)
            return false;
        // Check if they have played in the last timeSinceLogonRequired hours
        if (!bPlayer.isOnline() && (bPlayer.getPlayer().getLastPlayed() <= System.currentTimeMillis() - (getTimeSinceLogonRequired() * 3600000))) {
            return false;
        }
        // Check if they have been avatar recently
        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM pk_rpg_pastlives WHERE uuid = '" + uuid.toString() + "' ORDER BY startTime DESC LIMIT 1");
            if (rs.next()) {
                Timestamp endTime = rs.getTimestamp("endTime");
                if (endTime != null && endTime.toInstant().plusSeconds((long) (getRepeatSelectionCooldown() * 3600)).isAfter(Instant.now())) {
                    Statement stmt = rs.getStatement();
                    rs.close();
                    stmt.close();
                    return false;
                }
            }
            Statement stmt = rs.getStatement();
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    /**
     * Removes the rpg avatar permissions for the player with the matching uuid,
     * if they are the current avatar
     *
     * @param uuid UUID of player being checked
     */
    public void revokeRPGAvatar(UUID uuid, RemovalReason reason) {
        if (uuid == null) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar revocation: UUID is null.");
            return;
        }
        if (!isCurrentRPGAvatar(uuid)) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar revocation: Player is not the current avatar.");
            return;
        }
        String playerName = Bukkit.getOfflinePlayer(uuid).getName();
        OfflineBendingPlayer bPlayer = BendingPlayer.getOfflineBendingPlayer(playerName);
        if (bPlayer == null) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar revocation: BendingPlayer not found for player.");
            return;
        }
        List<Element> elementsList = new ArrayList<>();
        Timestamp startTime = Timestamp.from(Instant.now());
        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM pk_rpg_avatars WHERE uuid = '" + uuid.toString() + "'");
            if (rs.next()) {
                String elements = rs.getString("elements");
                for (String s : elements.split(",")) {
                    Element element = Element.fromString(s);
                    if (element != null) {
                        elementsList.add(element);
                    }
                }
                startTime = rs.getTimestamp("startTime");
                Statement stmt = rs.getStatement();
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        BendingPlayer.getOfflineBendingPlayer(playerName).getElements().clear();
        /**
         * TODO: Propose changing BendingPlayer's elements to a Set instead of List
         * I very much think this would be preferable since really there should be no repeats
         * nor support for repeats.
         */
        Player player = Bukkit.getPlayer(uuid);
        // Send message if they're online
        if (player != null) {
            player.sendMessage("You feel the power of the Avatar leaving you.");
        }
        for (Element element : elementsList) {
            if (!BendingPlayer.getOfflineBendingPlayer(playerName).hasElement(element)) {
                BendingPlayer.getOfflineBendingPlayer(playerName).addElement(element);
                if (player != null) {
                    player.sendMessage(element.getColor() + "You are once again a " + element.getName() + "bender.");
                }
            }
        }
        DBConnection.sql.modifyQuery("DELETE FROM pk_rpg_avatars WHERE uuid = '" + uuid + "'", false);
        ProjectKorraRPG.plugin.getLogger().info("ProjectKorraRPG: " + playerName + " removed from avatar DB.");
        currentAvatars--;
        avatars.removeIf(p -> p.getUniqueId().equals(uuid));
        avatarsToRemove.add(uuid);

        // Update avatarcycle
        String endTime = Timestamp.from(Instant.now()).toString();
        String endReason = reason == null ? "Unknown" : reason.toString();
        try {
            DBConnection.sql.modifyQuery("INSERT INTO pk_rpg_pastlives (uuid, startTime, player, endTime, elements, endReason) VALUES ('" + uuid.toString() + "', '" + startTime + "', '" + playerName + "', '" + endTime + "', '" + String.join(",", elementsList.stream().map(Element::getName).toArray(String[]::new)) + "', '" + endReason + "')", false);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        if (reason != RemovalReason.EXPIRED) {
            checkAvatars();
        }
    }

    public boolean handleAvatarDeath(BendingPlayer bp) {
        if (bp == null) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar death: BendingPlayer is null.");
            return false;
        }
        if (!isLoseAvatarOnDeath() || !isCurrentRPGAvatar(bp.getUUID())) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar death: Player is not the current avatar or loseAvatarOnDeath is disabled.");
            return false;
        }
        if (bp.isAvatarState()) {
            revokeRPGAvatar(bp.getUUID(), RemovalReason.AVATAR_STATE_DEATH);
        } else if (onlyLoseAvatarOnAvatarStateDeath()) {
            return false;
        } else {
            revokeRPGAvatar(bp.getUUID(), RemovalReason.DEATH);
        }
        checkAvatars();
        return true;
    }

    public List<String> getPastLives() {
        checkAvatars();
        List<String> pastLives = new ArrayList<>();
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");

        // Process past lives
        try (ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM pk_rpg_pastlives ORDER BY startTime DESC");
             Statement stmt = rs.getStatement()) {

            while (rs.next()) {
                String playerName = rs.getString("player");
                String elements = rs.getString("elements");
                Timestamp startTime = rs.getTimestamp("startTime");
                Timestamp endTime = rs.getTimestamp("endTime");
                String endReason = rs.getString("endReason");

                String formattedStartTime = startTime.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(displayFormatter);
                String formattedEndTime = (endTime == null) ? "PRESENT" :
                        endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(displayFormatter);

                pastLives.add(ChatColor.AQUA + "Avatar: " + playerName + " | " + ChatColor.BLUE + elements +
                        " | " + formattedStartTime + " - " + formattedEndTime +
                        " | End Reason: " + (endReason == null ? "N/A" : endReason));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Process current avatars
        try {
            for (OfflinePlayer p : getAvatars()) {
                String playerName = p.getName();
                String query = "SELECT * FROM pk_rpg_avatars WHERE uuid = '" + p.getUniqueId().toString() + "'";
                try (ResultSet rs = DBConnection.sql.readQuery(query);
                     Statement stmt = rs.getStatement()) {

                    if (rs.next()) {
                        String elements = rs.getString("elements");
                        // Retrieve the startTime as a Timestamp
                        Timestamp startTime = rs.getTimestamp("startTime");
                        String formattedStartTime = startTime.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                                .format(displayFormatter);

                        pastLives.add(ChatColor.GOLD + "Avatar: " + playerName + " | Elements: " + elements +
                                " | " + formattedStartTime + " - PRESENT");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pastLives;
    }




}
