package com.projectkorra.rpg.modules.randomavatar;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.OfflineBendingPlayer;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.MySQL;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.projectkorra.rpg.RPGMethods.periodStringToDuration;

public class AvatarManager {

    private final JavaPlugin plugin = ProjectKorraRPG.plugin;

    // In-memory caches
    public final Set<OfflinePlayer> recentPlayers;
    private Set<OfflinePlayer> avatars;

    // Configuration
    private final boolean enabled;
    private final int maxAvatars;
    private final Duration avatarDuration;
    private final Duration timeSinceLogonRequired;
    private final Duration repeatSelectionCooldown;
    private final boolean loseAvatarOnDeath;
    private final boolean loseOnAvatarStateDeath;
    private final boolean includeAllSubElements;
    private final boolean clearOnSelect;
    private final boolean broadcastAvatarSelection;
    private final boolean publicBroadcast;
    private final Set<Element> avatarElements = new HashSet<>();
    private final Set<Element.SubElement> subElementBlacklist = new HashSet<>();

    public AvatarManager() {
        FileConfiguration cfg = ConfigManager.config.get();
        enabled = cfg.getBoolean("Modules.RandomAvatar.Enabled");
        recentPlayers = new HashSet<>();
        maxAvatars = cfg.getInt("Modules.RandomAvatar.MaxAvatars");
        avatarDuration = periodStringToDuration(cfg.getString("Modules.RandomAvatar.AvatarDuration"));
        ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Avatar duration set to " + avatarDuration + " hours.");
        loseAvatarOnDeath = cfg.getBoolean("Modules.RandomAvatar.LoseAvatarOnDeath");
        loseOnAvatarStateDeath = cfg.getBoolean("Modules.RandomAvatar.OnlyLoseAvatarOnAvatarStateDeath");
        includeAllSubElements = cfg.getBoolean("Modules.RandomAvatar.IncludeAllSubElements");
        clearOnSelect = cfg.getBoolean("Modules.RandomAvatar.ClearOnSelection");
        timeSinceLogonRequired = periodStringToDuration(cfg.getString("Modules.RandomAvatar.TimeSinceLoginRequired"));
        repeatSelectionCooldown = periodStringToDuration(cfg.getString("Modules.RandomAvatar.RepeatSelectionCooldown"));
        broadcastAvatarSelection = cfg.getBoolean("Modules.RandomAvatar.Broadcast.Enabled");
        publicBroadcast = cfg.getBoolean("Modules.RandomAvatar.Broadcast.Public");


        for (String subElementName : cfg.getStringList("Modules.RandomAvatar.SubElementBlacklist")) {
            if (Element.getElement(subElementName) != null && Element.getElement(subElementName) instanceof Element.SubElement subElement) {
                subElementBlacklist.add(subElement);
            }
        }
        for (String elementName : cfg.getStringList("Modules.RandomAvatar.Elements")) {
            Element element = Element.getElement(elementName);
            if (element != null) {
                avatarElements.add(element);
                if (includeAllSubElements) {
                    for (Element.SubElement subElement : Element.getSubElements()) {
                        // Exclude blacklisted subelements
                        if (!subElementBlacklist.contains(subElement)) {
                            avatarElements.add(subElement);
                        }
                    }
                }
            }
        }
        avatars = new HashSet<>();

        initialize();
    }


    public void initialize() {
        refreshRecentPlayersAsync();
        createRPGTables();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ProjectKorraRPG.plugin, () -> {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Checking for new avatars.");
            checkAvatars();
        }, 0L, 20L * 30); // Every 30s (For Testing)

        Bukkit.getServer().getPluginManager().registerEvents(new AvatarListener(), ProjectKorraRPG.plugin);

        checkAvatars();
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

    private void grantTempElements(OfflinePlayer p, Instant startTime) {
        long remaining = avatarDuration.toMillis() - Duration.between(startTime, Instant.now()).toMillis();
        BendingPlayer bp = BendingPlayer.getBendingPlayer(p);
        avatarElements.stream()
                .filter(el -> !bp.hasElement(el) && !bp.hasTempElement(el))
                .forEach(el -> Bukkit.getScheduler().runTaskLater(ProjectKorraRPG.plugin,
                        () -> bp.addTempElement(el, null, remaining), 1L));
    }


    private void refreshRecentPlayersAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            long cutoff = System.currentTimeMillis() - timeSinceLogonRequired.toMillis();
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                if (p.isOnline() || p.getLastPlayed() >= cutoff) {
                    recentPlayers.add(p);
                }
            }
        });
    }

    private boolean shouldRevoke(Instant startTime) {
        return avatars.size() >= maxAvatars
                || startTime.plus(avatarDuration).isBefore(Instant.now());
    }

    public void checkAvatars() {
        avatars.clear();
        try {
            ResultSet rs =  DBConnection.sql.readQuery("SELECT uuid, player, startTime, elements FROM pk_rpg_avatars");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                Instant start = rs.getTimestamp("startTime").toInstant();
                OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);

                if (shouldRevoke(start)) {
                    revokeAvatarAsync(uuid, RemovalReason.EXPIRED);
                } else {
                    grantTempElements(p, start);
                    avatars.add(p);
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error checking avatars: " + ex.getMessage());
        }
        if (avatars.isEmpty()) {
            plugin.getLogger().info("Avatar selection: No current avatars.");
        }
        chooseAvatarsAsync();
    }
    private void chooseAvatarsAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::chooseAvatars);
    }


    private void chooseAvatars() {
        if (avatars.size() >= maxAvatars) {
            return;
        }

        Set<UUID> ineligible = fetchIneligiblePastAvatars();
        List<OfflinePlayer> candidates = recentPlayers.stream()
                .filter(p -> !avatars.contains(p))
                .filter(p -> !ineligible.contains(p.getUniqueId()))
                .collect(Collectors.toList());
        Collections.shuffle(candidates);

        int slots = maxAvatars - avatars.size();
        for (int i = 0; i < slots && i < candidates.size(); i++) {
            makeAvatarAsync(candidates.get(i).getUniqueId());
        }
    }


    public boolean makeAvatar(UUID uuid) {
        if (avatars.size() >= maxAvatars) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Current avatars limit reached.");
            return false;
        }
        if (isCurrentRPGAvatar(uuid)) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Player is already the current avatar.");
            return false;
        }
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(Bukkit.getOfflinePlayer(uuid));
        if (bPlayer == null) {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: BendingPlayer not found for player.");
            return false;
        }
        if (isAvatarEligible(uuid)) {
            addRPGAvatar(uuid);
            return true;
        } else {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Player is not eligible to become the avatar.");
            return false;
        }

    }

    private void makeAvatarAsync(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (avatars.size() < maxAvatars && isAvatarEligible(uuid)) {
                addRPGAvatar(uuid);
            }
        });
    }
    /**
     * Sets a player to the avatar giving them all the elements (excluding
     * chiblocking) and the extra perks such as almost death avatarstate.
     *
     * @param uuid UUID of player being set as the avatar
     */
    private void addRPGAvatar(UUID uuid) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        OfflineBendingPlayer bp = off.isOnline() ? BendingPlayer.getBendingPlayer(off)
                : BendingPlayer.getOfflineBendingPlayer(off.getName());

        Instant now = Instant.now();
        String elements = String.join(",", bp.getElements().stream().map(Element::getName).toArray(String[]::new));

        // Insert current avatar
        try {
            DBConnection.sql.modifyQuery("INSERT INTO pk_rpg_avatars (uuid, player, startTime, elements) VALUES ('" + uuid.toString() + "', '" + off.getName() + "', '" + Timestamp.from(now) + "', '" + elements + "')", false);
            DBConnection.sql.getConnection().setAutoCommit(true);

        } catch (SQLException ex) {
            plugin.getLogger().severe("Error inserting avatar: " + ex.getMessage());
            return;
        }

        if (clearOnSelect) bp.getElements().clear();
        long ms = avatarDuration.toMillis();
        avatarElements.stream()
                .filter(el -> !bp.hasElement(el))
                .forEach(el -> Bukkit.getScheduler().runTaskLater(plugin,
                        () -> bp.addTempElement(el, null, ms), 1L));
        avatars.add(off);
        if (broadcastAvatarSelection) {
            String msg = Element.AVATAR.getColor() + "A new Avatar has been chosen"
                    + (publicBroadcast ? ": " + off.getName() : "!");
            Bukkit.broadcastMessage(msg);
        }
        if (off.getPlayer() != null) {
            off.getPlayer().sendMessage(Element.AVATAR.getColor() + "You feel the power of the Avatar");
        }
    }

    /**
     * Checks if player with uuid is a current avatar. Returns false  if there
     * is no current avatar
     *
     * @param uuid UUID of player being checked
     * @return if player with uuid is a current avatar
     */
    public boolean isCurrentRPGAvatar(UUID uuid) {
        if (avatars != null) {
            for (OfflinePlayer p : avatars) {
                if (p != null && p.getUniqueId().equals(uuid)) {
                    return true;
                }
            }
        } else {
            // In theory we should never have to check the db for this but just in case
            try {
                ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM pk_rpg_avatars WHERE uuid = '" + uuid.toString() + "'");
                if (rs.next()) {
                    Statement stmt = rs.getStatement();
                    rs.close();
                    stmt.close();
                    return true;
                }
            } catch (SQLException e) {
               ProjectKorraRPG.plugin.getLogger().severe("Error checking current avatar: " + e.getMessage());
                return false;
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
            ProjectKorraRPG.plugin.getLogger().severe("Error checking past avatar: " + e.getMessage());
            valid = false;
        }
        return valid;
    }



    public boolean isAvatarEligible(UUID uuid) {
        if (uuid == null)
            return false;
        if (isCurrentRPGAvatar(uuid))
            return false;
        if (avatars.size() >= maxAvatars)
            return false;

        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(Bukkit.getOfflinePlayer(uuid));
        if (bPlayer == null)
            return false;
        // Check if they have played in the last timeSinceLogonRequired hours
        if (!bPlayer.isOnline() && (bPlayer.getPlayer().getLastPlayed() <= System.currentTimeMillis() - timeSinceLogonRequired.toMillis())) {
            return false;
        }
        // Check if they have been avatar recently
        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT endTime FROM pk_rpg_pastlives WHERE uuid = '" + uuid.toString() + "' ORDER BY startTime DESC LIMIT 1");
            if (rs.next()) {
                Timestamp endTime = rs.getTimestamp("endTime");
                if (endTime != null && endTime.toInstant().plus(repeatSelectionCooldown).isAfter(Instant.now())) {
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
            ProjectKorraRPG.plugin.getLogger().severe("Error checking past avatar: " + e.getMessage());
            return false;
        }
        return true;
    }

    private Set<UUID> fetchIneligiblePastAvatars() {
        Set<UUID> set = new HashSet<>();
        try {
             ResultSet rs = DBConnection.sql.readQuery("SELECT uuid, MAX(endTime) AS lastEnd FROM pk_rpg_pastlives GROUP BY uuid");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                Instant lastEnd = rs.getTimestamp("lastEnd").toInstant();
                if (lastEnd.plus(repeatSelectionCooldown).isAfter(Instant.now())) {
                    set.add(uuid);
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error fetching past avatars: " + ex.getMessage());
        }
        return set;
    }

    public void revokeAvatarAsync(UUID uuid, RemovalReason reason) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> revokeRPGAvatar(uuid, reason));
    }
    /**
     * Removes the rpg avatar permissions for the player with the matching uuid,
     * if they are the current avatar
     *
     * @param uuid UUID of player being checked
     */
    private void revokeRPGAvatar(UUID uuid, RemovalReason reason) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        BendingPlayer bp = BendingPlayer.getBendingPlayer(off);
        List<Element> original = new ArrayList<>();
        Instant start = Instant.now();
        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM pk_rpg_avatars WHERE uuid = '" + uuid + "'");
            if (rs.next()) {
                start = rs.getTimestamp("startTime").toInstant();
                String elements = rs.getString("elements");
                for (String elementName : elements.split(",")) {
                    Element element = Element.getElement(elementName);
                    if (element != null) {
                        original.add(element);
                    }
                }
                Statement stmt = rs.getStatement();
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Delete
        try  {
            DBConnection.sql.getConnection().setAutoCommit(false);
            DBConnection.sql.modifyQuery("DELETE FROM pk_rpg_avatars WHERE uuid = '" + uuid + "'");
            DBConnection.sql.getConnection().commit();
            DBConnection.sql.getConnection().setAutoCommit(true);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        // Restore perms and send message
        if (off.isOnline()) off.getPlayer().sendMessage("You feel the power of the Avatar leaving you.");
        original.forEach(el -> {
            if (!bp.hasElement(el)) {
                bp.addElement(el);
                bp.getPlayer().sendMessage(el.getColor() + "You are once again a " + el.getName() + "bender.");
            }
        });
        ProjectKorraRPG.plugin.getLogger().info(off.getName() + " is no longer the Avatar.");
        // If the player has the element still remove it
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            original.forEach(el -> {
                if (bp.hasTempElement(el)) {
                    bp.removeTempElement(el, null);
                }
            });
        }, 1L);

        avatars.remove(off);

        // Record past life
        try {
            DBConnection.sql.modifyQuery("INSERT INTO pk_rpg_pastlives (uuid, startTime, player, endTime, elements, endReason) VALUES ('" + uuid + "', '" + Timestamp.from(start) + "', '" + off.getName() + "', '" + Timestamp.from(Instant.now()) + "', '" + String.join(",", original.stream().map(Element::getName).toArray(String[]::new)) + "', '" + reason + "')", false);
            DBConnection.sql.getConnection().setAutoCommit(true);
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error recording past life: " + ex.getMessage());
        }
    }


    public boolean handleAvatarDeath(BendingPlayer bp) {
        if (bp == null || !loseAvatarOnDeath || !isCurrentRPGAvatar(bp.getUUID())) {
            return false;
        }
        if (bp.isAvatarState()) {
            revokeAvatarAsync(bp.getUUID(), RemovalReason.AVATAR_STATE_DEATH);
        } else if (loseOnAvatarStateDeath) {
            return false;
        } else {
            revokeAvatarAsync(bp.getUUID(), RemovalReason.DEATH);
        }
        checkAvatars();
        return true;
    }


    public List<String> getPastLives() {
        List<String> list = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");

        // Past lives
        try {
             ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM pk_rpg_pastlives ORDER BY startTime DESC");
            while (rs.next()) {
                String player = rs.getString("player");
                String elems = rs.getString("elements");
                Instant st = rs.getTimestamp("startTime").toInstant();
                Timestamp et = rs.getTimestamp("endTime");
                String end = et == null ? "PRESENT" : et.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(fmt);
                String reason = Optional.ofNullable(rs.getString("endReason")).orElse("N/A");
                list.add(ChatColor.AQUA + "Avatar: " + player + " | " + ChatColor.BLUE + elems
                        + " | " + st.atZone(ZoneId.systemDefault()).toLocalDateTime().format(fmt)
                        + " - " + end + " | End Reason: " + reason);
            }
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error fetching past lives: " + ex.getMessage());
        }

        // Current avatars
        avatars.forEach(off -> {
            try {
                ResultSet rs = DBConnection.sql.readQuery("SELECT startTime, elements FROM pk_rpg_avatars WHERE uuid = '" + off.getUniqueId().toString() + "'");
                if (rs.next()) {
                    String elems = rs.getString("elements");
                    Instant st = rs.getTimestamp("startTime").toInstant();
                    list.add(ChatColor.GOLD + "Avatar: " + off.getName()
                            + " | Elements: " + elems
                            + " | " + st.atZone(ZoneId.systemDefault()).toLocalDateTime().format(fmt)
                            + " - PRESENT");
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe("Error fetching current avatar: " + ex.getMessage());
            }
        });

        return list;
    }

    public Set<OfflinePlayer> getRecentPlayers() {
        return recentPlayers;
    }

    public Set<OfflinePlayer> getAvatars() {
        return avatars;
    }

    public void setAvatars(Set<OfflinePlayer> avatars) {
        this.avatars = avatars;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Duration getAvatarDuration() {
        return avatarDuration;
    }

    public Duration getTimeSinceLogonRequired() {
        return timeSinceLogonRequired;
    }

    public Duration getRepeatSelectionCooldown() {
        return repeatSelectionCooldown;
    }

    public boolean isLoseAvatarOnDeath() {
        return loseAvatarOnDeath;
    }

    public boolean isLoseOnAvatarStateDeath() {
        return loseOnAvatarStateDeath;
    }

    public boolean isIncludeAllSubElements() {
        return includeAllSubElements;
    }

    public boolean isClearOnSelect() {
        return clearOnSelect;
    }

    public boolean isBroadcastAvatarSelection() {
        return broadcastAvatarSelection;
    }

    public boolean isPublicBroadcast() {
        return publicBroadcast;
    }

    public Set<Element> getAvatarElements() {
        return avatarElements;
    }

    public Set<Element.SubElement> getSubElementBlacklist() {
        return subElementBlacklist;
    }

    // Enum RemovalReason
    public enum RemovalReason {
        DEATH,
        AVATAR_STATE_DEATH,
        COMMAND,
        EXPIRED
    }

    /**
     * Maximum number of avatars that can be active at once
     */
    public int getMaxAvatars() {
        return maxAvatars;
    }


}
