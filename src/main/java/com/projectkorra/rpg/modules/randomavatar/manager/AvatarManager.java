/**
 * CLASS CHERRY-PICKED FROM CRASH CRINGLE
 */
package com.projectkorra.rpg.modules.randomavatar.manager;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.OfflineBendingPlayer;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.storage.TableCreator;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AvatarManager {
    private final ProjectKorraRPG plugin = ProjectKorraRPG.getPlugin();

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
        FileConfiguration config = ConfigManager.defaultConfig.get();
        enabled = config.getBoolean("Modules.RandomAvatar.Enabled");
        recentPlayers = new HashSet<>();
        maxAvatars = config.getInt("Modules.RandomAvatar.MaxAvatars");
        avatarDuration = RPGMethods.periodStringToDuration(config.getString("Modules.RandomAvatar.AvatarDuration"));
        plugin.getLogger().info("Avatar selection: Avatar duration set to " + avatarDuration + " hours.");
        loseAvatarOnDeath = config.getBoolean("Modules.RandomAvatar.LoseAvatarOnDeath");
        loseOnAvatarStateDeath = config.getBoolean("Modules.RandomAvatar.OnlyLoseAvatarOnAvatarStateDeath");
        includeAllSubElements = config.getBoolean("Modules.RandomAvatar.IncludeAllSubElements");
        clearOnSelect = config.getBoolean("Modules.RandomAvatar.ClearOnSelection");
        timeSinceLogonRequired = RPGMethods.periodStringToDuration(config.getString("Modules.RandomAvatar.TimeSinceLoginRequired"));
        repeatSelectionCooldown = RPGMethods.periodStringToDuration(config.getString("Modules.RandomAvatar.RepeatSelectionCooldown"));
        broadcastAvatarSelection = config.getBoolean("Modules.RandomAvatar.Broadcast.Enabled");
        publicBroadcast = config.getBoolean("Modules.RandomAvatar.Broadcast.Public");


        for (String subElementName : config.getStringList("Modules.RandomAvatar.SubElementBlacklist")) {
            if (Element.getElement(subElementName) != null && Element.getElement(subElementName) instanceof Element.SubElement subElement) {
                subElementBlacklist.add(subElement);
            }
        }
        for (String elementName : config.getStringList("Modules.RandomAvatar.Elements")) {
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
    }

    private void grantTempElements(OfflinePlayer p, Instant startTime) {
        long remaining = avatarDuration.toMillis() - Duration.between(startTime, Instant.now()).toMillis();
        OfflineBendingPlayer bp = new OfflineBendingPlayer(p);

        avatarElements.stream()
                .filter(el -> !bp.hasElement(el) && !bp.hasTempElement(el))
                .forEach(el -> Bukkit.getScheduler().runTaskLater(plugin,
                        () -> bp.addTempElement(el, null, remaining), 1L));
    }

    public void refreshRecentPlayersAsync() {
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
        return avatars.size() >= maxAvatars || startTime.plus(avatarDuration).isBefore(Instant.now());
    }

    public void checkAvatars() {
        avatars.clear();
        try {
            ResultSet rs =  DBConnection.sql.readQuery("SELECT uuid, player, startTime, elements FROM " + TableCreator.RPG_AVATAR_TABLE);
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
            plugin.getLogger().info("Avatar selection: Current avatars limit reached.");
            return false;
        }
        if (isCurrentRPGAvatar(uuid)) {
            plugin.getLogger().info("Avatar selection: Player is already the current avatar.");
            return false;
        }
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(Bukkit.getOfflinePlayer(uuid));
        if (bPlayer == null) {
            plugin.getLogger().info("Avatar selection: BendingPlayer not found for player.");
            return false;
        }
        if (isAvatarEligible(uuid)) {
            addRPGAvatar(uuid);
            return true;
        } else {
            plugin.getLogger().info("Avatar selection: Player is not eligible to become the avatar.");
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
        OfflineBendingPlayer bp = off.isOnline() ? BendingPlayer.getBendingPlayer(off) : BendingPlayer.getOfflineBendingPlayer(off.getName());

        if (bp == null) {
            plugin.getLogger().severe("Couldn't assign Avatar, BendingPlayer is null!");
            return;
        }

        Instant now = Instant.now();
		String elements = String.join(",", bp.getElements().stream().map(Element::getName).toArray(String[]::new));

        // Insert current avatar
        try {
            DBConnection.sql.modifyQuery("INSERT INTO " + TableCreator.RPG_AVATAR_TABLE + " (uuid, player, startTime, elements) VALUES ('" + uuid.toString() + "', '" + off.getName() + "', '" + Timestamp.from(now) + "', '" + elements + "')", false);
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
            String msg = Element.AVATAR.getColor() + "A new Avatar has been chosen" + (publicBroadcast ? ": " + off.getName() : "!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                ChatUtil.sendBrandingMessage(player, msg);
            }
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
                ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM " + TableCreator.RPG_AVATAR_TABLE + " WHERE uuid = '" + uuid.toString() + "'");
                if (rs.next()) {
                    Statement stmt = rs.getStatement();
                    rs.close();
                    stmt.close();
                    return true;
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error checking current avatar: " + e.getMessage());
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
        if (isCurrentRPGAvatar(uuid)) return true;

        final String sql = "SELECT uuid FROM " + TableCreator.RPG_PAST_LIVES_TABLE + " WHERE uuid = ?";

        try (Connection connection = DBConnection.sql.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("Error checking past avatar: " + exception.getMessage());
            return false;
        }
    }

    public boolean isAvatarEligible(UUID uuid) {
        if (uuid == null)
            return false;
        if (isCurrentRPGAvatar(uuid))
            return false;
        if (avatars.size() >= maxAvatars)
            return false;

        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(Bukkit.getOfflinePlayer(uuid));
        if (bPlayer == null) return false;

        // Check if they have played in the last timeSinceLogonRequired hours
        if (!bPlayer.isOnline() && (bPlayer.getPlayer().getLastPlayed() <= System.currentTimeMillis() - timeSinceLogonRequired.toMillis())) {
            return false;
        }

        final String sql = "SELECT endTime FROM " + TableCreator.RPG_PAST_LIVES_TABLE + " WHERE uuid = ? ORDER BY startTime DESC LIMIT 1";

        try (Connection connection = DBConnection.sql.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp endTime = rs.getTimestamp("endTime");
                    if (endTime != null) {
                        Instant eligibleAt = endTime.toInstant().plus(repeatSelectionCooldown);
                        if (eligibleAt.isAfter(Instant.now())) {
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("Error checking past avatar: " + exception.getMessage());
            return false;
        }

        return true;
    }

    private Set<UUID> fetchIneligiblePastAvatars() {
        Set<UUID> set = new HashSet<>();
        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT uuid, MAX(endTime) AS lastEnd FROM " + TableCreator.RPG_PAST_LIVES_TABLE + " GROUP BY uuid");
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
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(offlinePlayer);
        List<Element> originalElements = new ArrayList<>();
        List<Element.SubElement> originalSubElements = new ArrayList<>();
        Instant start = Instant.now();

        final String selectSql = "SELECT * FROM " + TableCreator.RPG_AVATAR_TABLE + " WHERE uuid = ?";

        try (Connection connection = DBConnection.sql.getConnection();
             PreparedStatement ps = connection.prepareStatement(selectSql)) {
            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    start = rs.getTimestamp("startTime").toInstant();
                    String elements = rs.getString("elements");
                    for (String elementName : elements.split(",")) {
                        Element element = Element.getElement(elementName);
                        if (element != null) {
                            if (element instanceof Element.SubElement sub) {
                                originalSubElements.add(sub);
                            }
                            originalElements.add(element);
                        }
                    }
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        final String deleteSql = "DELETE FROM" + TableCreator.RPG_AVATAR_TABLE + " WHERE uuid = ?";

        try (Connection connection = DBConnection.sql.getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteSql)) {
            connection.setAutoCommit(false);
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        // Restore perms and send message
        if (offlinePlayer.isOnline() && offlinePlayer instanceof Player player) {
            ChatUtil.sendBrandingMessage(player, "You feel the power of the Avatar leaving you.");
        }

        for (Element.SubElement element : Element.getSubElements()) {
            bendingPlayer.getTempSubElements().remove(element);
        }
        bendingPlayer.getSubElements().clear();

        for (Element element : Element.getMainElements()) {
            bendingPlayer.getTempElements().remove(element);
        }
        bendingPlayer.getElements().clear();

        if (!originalElements.isEmpty()) {
            originalElements.forEach(el -> {
                bendingPlayer.addElement(el);

                ChatUtil.sendBrandingMessage(bendingPlayer.getPlayer(), el.getColor() + "You are once again a " + el.getName() +  " bender.");
                originalElements.clear();
            });
        }

        if (!originalSubElements.isEmpty()) {
            originalSubElements.forEach(el -> {
                bendingPlayer.addSubElement(el);

                ChatUtil.sendBrandingMessage(bendingPlayer.getPlayer(), el.getColor() + "You are once again a " + el.getName() +  " bender.");
                originalSubElements.clear();
            });
        }

        plugin.getLogger().info(offlinePlayer.getName() + " is no longer the Avatar.");
        avatars.remove(offlinePlayer);

        final String insertSql = "INSERT INTO " + TableCreator.RPG_PAST_LIVES_TABLE + " (uuid, startTime, player, endTime, elements, endReason) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.sql.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, uuid.toString());
            ps.setTimestamp(2, Timestamp.from(start));
            ps.setString(3, offlinePlayer.getName());
            ps.setTimestamp(4, Timestamp.from(Instant.now()));
            ps.setString(5, String.join(",", originalElements.stream().map(Element::getName).toArray(String[]::new)));
            ps.setString(6, reason.name());

            ps.executeUpdate();
        } catch (SQLException exception) {
            plugin.getLogger().severe("Error recording past life: " + exception.getMessage());
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
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM " + TableCreator.RPG_PAST_LIVES_TABLE + " ORDER BY startTime DESC");
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
                ResultSet rs = DBConnection.sql.readQuery("SELECT startTime, elements FROM " + TableCreator.RPG_AVATAR_TABLE + " WHERE uuid = '" + off.getUniqueId() + "'");
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
