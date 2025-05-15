/**
 * CLASS CHERRY-PICKED FROM CRASH CRINGLE
 */
package com.projectkorra.rpg.modules.randomavatar.manager;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.OfflineBendingPlayer;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.storage.TableCreator;
import com.projectkorra.rpg.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AvatarManager {
    private final JavaPlugin plugin = ProjectKorraRPG.getPlugin();

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
        avatarDuration = periodStringToDuration(config.getString("Modules.RandomAvatar.AvatarDuration"));
        plugin.getLogger().info("Avatar selection: Avatar duration set to " + avatarDuration + " hours.");
        loseAvatarOnDeath = config.getBoolean("Modules.RandomAvatar.LoseAvatarOnDeath");
        loseOnAvatarStateDeath = config.getBoolean("Modules.RandomAvatar.OnlyLoseAvatarOnAvatarStateDeath");
        includeAllSubElements = config.getBoolean("Modules.RandomAvatar.IncludeAllSubElements");
        clearOnSelect = config.getBoolean("Modules.RandomAvatar.ClearOnSelection");
        timeSinceLogonRequired = periodStringToDuration(config.getString("Modules.RandomAvatar.TimeSinceLoginRequired"));
        repeatSelectionCooldown = periodStringToDuration(config.getString("Modules.RandomAvatar.RepeatSelectionCooldown"));
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
        BendingPlayer bp = BendingPlayer.getBendingPlayer(p);
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
        if (isCurrentRPGAvatar(uuid))
            return true;
        ResultSet rs = DBConnection.sql.readQuery("SELECT uuid FROM " + TableCreator.RPG_PASTLIVES_TABLE + " WHERE uuid = '" + uuid.toString() + "'");
        boolean valid;
        try {
            valid = rs.next();
            Statement stmt = rs.getStatement();
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error checking past avatar: " + e.getMessage());
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
            ResultSet rs = DBConnection.sql.readQuery("SELECT endTime FROM " + TableCreator.RPG_PASTLIVES_TABLE + " WHERE uuid = '" + uuid + "' ORDER BY startTime DESC LIMIT 1");
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
            plugin.getLogger().severe("Error checking past avatar: " + e.getMessage());
            return false;
        }
        return true;
    }

    private Set<UUID> fetchIneligiblePastAvatars() {
        Set<UUID> set = new HashSet<>();
        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT uuid, MAX(endTime) AS lastEnd FROM " + TableCreator.RPG_PASTLIVES_TABLE + " GROUP BY uuid");
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
        Instant start = Instant.now();

        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM " + TableCreator.RPG_AVATAR_TABLE + " WHERE uuid = '" + uuid + "'");
            if (rs.next()) {
                start = rs.getTimestamp("startTime").toInstant();
                String elements = rs.getString("elements");
                for (String elementName : elements.split(",")) {
                    Element element = Element.getElement(elementName);
                    if (element != null) {
                        originalElements.add(element);
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
        try {
            DBConnection.sql.getConnection().setAutoCommit(false);
            DBConnection.sql.modifyQuery("DELETE FROM " + TableCreator.RPG_AVATAR_TABLE + " WHERE uuid = '" + uuid + "'");
            DBConnection.sql.getConnection().commit();
            DBConnection.sql.getConnection().setAutoCommit(true);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
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

                if (el instanceof Element.SubElement) {
                    bendingPlayer.addSubElement((Element.SubElement) el);
                }

                ChatUtil.sendBrandingMessage(bendingPlayer.getPlayer(), el.getColor() + "You are once again a " + el.getName() +  " bender.");
            });
        }

        plugin.getLogger().info(offlinePlayer.getName() + " is no longer the Avatar.");
        avatars.remove(offlinePlayer);

        // Record past life
        try {
            DBConnection.sql.modifyQuery("INSERT INTO " + TableCreator.RPG_PASTLIVES_TABLE + " (uuid, startTime, player, endTime, elements, endReason) VALUES ('" + uuid + "', '" + Timestamp.from(start) + "', '" + offlinePlayer.getName() + "', '" + Timestamp.from(Instant.now()) + "', '" + String.join(",", originalElements.stream().map(Element::getName).toArray(String[]::new)) + "', '" + reason + "')", false);
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
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM " + TableCreator.RPG_PASTLIVES_TABLE + " ORDER BY startTime DESC");
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

    /**
     * @param period String to convert to duration
     * @return Duration in the period string
     * @author CrashCringle
     * @Description This method converts a period string like 3d4h to a duration object
     */
    private Duration periodStringToDuration(String period) {
        // Can be in the formats like: 1s, 1m, 1h, 1d, 2d1h10s etc etc.
        Duration duration = Duration.ZERO;
        if (period == null || period.isEmpty()) {
            plugin.getLogger().info("Invalid period string: " + period);
            return duration;
        }
        String[] parts = period.split("(?<=\\D)(?=\\d)");
        for (String part : parts) {
            String unit = part.replaceAll("\\d", "");
            double value = Double.parseDouble(part.replaceAll("\\D", ""));
            duration = switch (unit) {
                case "w" -> duration.plusHours((long) (value * 168));
                case "d" -> duration.plusHours((long) (value * 24));
                case "h" -> duration.plusHours((long) value);
                case "m" -> duration.plusMinutes((long) value);
                case "s" -> duration.plusSeconds((long) value);
                default -> duration;
            };
        }
        return duration;
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
