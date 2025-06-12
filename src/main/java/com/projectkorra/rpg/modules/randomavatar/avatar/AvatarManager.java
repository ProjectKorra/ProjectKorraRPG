package com.projectkorra.rpg.modules.randomavatar.avatar;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.storage.TableCreator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AvatarManager {
    private static final FileConfiguration defaultConfig = ConfigManager.defaultConfig.get();
    private static final String CONFIG_PATH = "Modules.RandomAvatar.";

    private static HashMap<UUID, Avatar> CURRENT_AVATARS;
    private static HashMap<UUID, PreviousAvatar> PREVIOUS_AVATARS;

    public AvatarManager() {
        CURRENT_AVATARS = fillAvatarMap();
        PREVIOUS_AVATARS = fillPreviousAvatarMap();
    }

    public static boolean isEligableToBecomeAvatar(Player player) {
        if (player == null) {
            return false;
        }

        int maxAllowedAvatars = defaultConfig.getInt(CONFIG_PATH + "MaxAvatars", 1);
        Instant timeSinceLoginRequired = Instant.ofEpochMilli(RPGMethods.periodStringToDuration(defaultConfig.getString(CONFIG_PATH + "TimeSinceLoginRequired")).toMillis());

        if (CURRENT_AVATARS.size() >= maxAllowedAvatars) {
            System.out.println("Size limit reached");
            return false;
        }

        if (CURRENT_AVATARS.containsKey(player.getUniqueId())) {
            System.out.println("Already is avatar");
            return false;
        }

        if (player.getFirstPlayed() < timeSinceLoginRequired.toEpochMilli()) {
            System.out.println("Not played long enough");
            return false;
        }

        return true;
    }

    public static void storeAvatar(UUID uuid, Element mainElement, List<Element.SubElement> subElements, Instant chosenTime) {
        String subElementsStr = subElements.stream().map(Element::getName).collect(Collectors.joining(","));

        String query = "INSERT INTO " + TableCreator.RPG_AVATAR_TABLE + "(uuid, main_element, sub_elements, chosen_time) VALUES ('" +
                uuid.toString() + "', '" +
                mainElement.getName() + "', '" +
                subElementsStr + "', '" +
                Timestamp.from(chosenTime) + "')";

        DBConnection.sql.modifyQuery(query);

        CURRENT_AVATARS.put(uuid, new Avatar(uuid, mainElement, subElements, chosenTime));
    }

    /**
     * Temp method
     */
    public static void checkAvatars() {
        List<Player> candidates = Bukkit.getOnlinePlayers().stream()
                .filter(AvatarManager::isEligableToBecomeAvatar)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return;
        }

        Collections.shuffle(candidates);
        Player player = candidates.getFirst();
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

        if (bendingPlayer != null) {
            new Avatar(player.getUniqueId(), bendingPlayer.getElements().getFirst(), bendingPlayer.getSubElements()).handleInitiation();
        }
    }

    private HashMap<UUID, Avatar> fillAvatarMap() {
        HashMap<UUID, Avatar> avatars = new HashMap<>();

        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM " + TableCreator.RPG_AVATAR_TABLE);

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String mainElementName = rs.getString("main_element");
                String subElementNames = rs.getString("sub_elements");
                Instant chosenTime = rs.getTimestamp("chosen_time").toInstant();

                List<Element.SubElement> subElements = new ArrayList<>();
                if (subElementNames != null) {
                    for (String subElementName : subElementNames.split(",")) {
                        subElements.add((Element.SubElement) Element.fromString(subElementName));
                    }
                }

                avatars.put(uuid, new Avatar(uuid, Element.fromString(mainElementName), subElements, chosenTime));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return avatars;
    }

    private HashMap<UUID, PreviousAvatar> fillPreviousAvatarMap() {
        HashMap<UUID, PreviousAvatar> previousAvatars = new HashMap<>();

        try {
            ResultSet rs = DBConnection.sql.readQuery("SELECT * FROM " + TableCreator.RPG_PASTLIVES_TABLE);

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String mainElementName = rs.getString("main_element");
                String subElementNames = rs.getString("sub_elements");
                Instant chosenTime = rs.getTimestamp("chosen_time").toInstant();
                Instant endTime = rs.getTimestamp("end_time").toInstant();
                String endReason = rs.getString("end_reason");

                List<Element.SubElement> subElements = new ArrayList<>();
                if (subElementNames != null) {
                    for (String subElementName : subElementNames.split(",")) {
                        subElements.add((Element.SubElement) Element.fromString(subElementName));
                    }
                }

                previousAvatars.put(uuid, new PreviousAvatar(uuid, Element.fromString(mainElementName), subElements, chosenTime, endTime, endReason));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return previousAvatars;
    }

    public static HashMap<UUID, Avatar> getCurrentAvatars() {
        return CURRENT_AVATARS;
    }

    public static HashMap<UUID, PreviousAvatar> getPreviousAvatars() {
        return PREVIOUS_AVATARS;
    }
}
