package com.projectkorra.rpg.storage;

import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.MySQL;
import com.projectkorra.rpg.ProjectKorraRPG;

public class TableCreator extends DBConnection {
    public static final String RPG_PLAYER_TABLE = "pkrpg_players";
    public static final String RPG_SCHEDULE_TABLE = "pkrpg_schedule";
    public static final String RPG_AVATAR_TABLE = "pkrpg_avatars";
    public static final String RPG_PASTLIVES_TABLE = "pkrpg_pastlives";

    public TableCreator() {
        this.createRpgPlayerTable();
        this.createScheduleTable();
        this.createRpgAvatarTable();
        this.createRpgPastLivesTable();
    }

    private void createRpgPlayerTable() {
        if (sql instanceof MySQL) {
            if (!sql.tableExists(RPG_PLAYER_TABLE)) {
                ProjectKorraRPG.getPlugin().getLogger().info("Creating " + RPG_PLAYER_TABLE + " table");

                final String query = "CREATE TABLE `" + RPG_PLAYER_TABLE + "` ("
                        + "`uuid` varchar(36) NOT NULL,"
                        + "`xp` INT NOT NULL DEFAULT 0,"
                        + "`level` INT NOT NULL DEFAULT 1,"
                        + "PRIMARY KEY (`uuid`),"
                        + "FOREIGN KEY (`uuid`) REFERENCES `pk_players`(`uuid`) ON DELETE CASCADE"
                        + ");";

                sql.modifyQuery(query, false);
            }
        } else {
            if (!sql.tableExists(RPG_PLAYER_TABLE)) {
                ProjectKorraRPG.getPlugin().getLogger().info("Creating " + RPG_PLAYER_TABLE + " table");

                final String query = "CREATE TABLE " + RPG_PLAYER_TABLE + "("
                        + "uuid TEXT NOT NULL, "
                        + "xp INTEGER NOT NULL DEFAULT 0, "
                        + "level INTEGER NOT NULL DEFAULT 1, "
                        + "PRIMARY KEY (uuid), "
                        + "FOREIGN KEY (uuid) REFERENCES pk_players(uuid) ON DELETE CASCADE"
                        + ");";

                sql.modifyQuery(query, false);
            }
        }
    }

    private void createScheduleTable() {
        if (sql instanceof MySQL) {
            if (!sql.tableExists(RPG_SCHEDULE_TABLE)) {
                ProjectKorraRPG.getPlugin().getLogger().info("Creating " + RPG_SCHEDULE_TABLE + " table");

                final String query = "CREATE TABLE `" + RPG_SCHEDULE_TABLE + "` ("
                        + "`id` INT NOT NULL AUTO_INCREMENT,"
                        + "`worldevent` TEXT NOT NULL,"
                        + "`last_triggered` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                        + "PRIMARY KEY (`id`))";

                sql.modifyQuery(query, false);
            }
        } else {
            if (!sql.tableExists(RPG_SCHEDULE_TABLE)) {
                ProjectKorraRPG.getPlugin().getLogger().info("Creating " + RPG_SCHEDULE_TABLE + " table");

                final String query = "CREATE TABLE " + RPG_SCHEDULE_TABLE + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "worldevent TEXT NOT NULL,"
                        + "last_triggered TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ")";

                sql.modifyQuery(query, false);
            }
        }
    }

    private void createRpgAvatarTable() {
        if (sql instanceof MySQL) {
            if (!sql.tableExists(RPG_AVATAR_TABLE)) {
                ProjectKorraRPG.getPlugin().getLogger().info("Creating " + RPG_AVATAR_TABLE + " table");

                final String query = "CREATE TABLE `" + RPG_AVATAR_TABLE + "` ("
                        + "`uuid` varchar(36) NOT NULL,"
                        + "`main_element` varchar(255) NOT NULL,"
                        + "`sub_elements` varchar(255) NOT NULL,"
                        + "`chosen_time` datetime NOT NULL,"
                        + "PRIMARY KEY (`uuid`)"
                        + ");";

                sql.modifyQuery(query, false);
            }
        } else {
            if (!sql.tableExists(RPG_AVATAR_TABLE)) {
                ProjectKorraRPG.getPlugin().getLogger().info("Creating " + RPG_AVATAR_TABLE + " table");

                final String query = "CREATE TABLE " + RPG_AVATAR_TABLE + " ("
                        + "uuid TEXT NOT NULL, "
                        + "main_element TEXT NOT NULL, "
                        + "sub_elements TEXT NOT NULL, "
                        + "chosen_time TIMESTAMP NOT NULL, "
                        + "PRIMARY KEY (uuid)"
                        + ");";

                sql.modifyQuery(query, false);
            }
        }
    }

    private void createRpgPastLivesTable() {
        if (sql instanceof MySQL) {
            if (!sql.tableExists(RPG_PASTLIVES_TABLE)) {
                ProjectKorraRPG.getPlugin().getLogger().info("Creating " + RPG_PASTLIVES_TABLE + " table");

                final String query = "CREATE TABLE `" + RPG_PASTLIVES_TABLE + "` ("
                        + "`uuid` varchar(36) NOT NULL,"
                        + "`main_element` varchar(255) NOT NULL,"
                        + "`sub_elements` varchar(255) NOT NULL,"
                        + "`chosen_time` datetime NOT NULL,"
                        + "`end_time` datetime NOT NULL,"
                        + "`end_reason` varchar(255) DEFAULT NULL,"
                        + "PRIMARY KEY (`uuid`)"
                        + ");";

                sql.modifyQuery(query, false);
            }
        } else {
            if (!sql.tableExists(RPG_PASTLIVES_TABLE)) {
                ProjectKorraRPG.getPlugin().getLogger().info("Creating " + RPG_PASTLIVES_TABLE + " table");

                final String query = "CREATE TABLE " + RPG_PASTLIVES_TABLE + " ("
                        + "uuid TEXT NOT NULL, "
                        + "main_element TEXT NOT NULL, "
                        + "sub_elements TEXT NOT NULL, "
                        + "chosenTime TIMESTAMP NOT NULL, "
                        + "endTime TIMESTAMP NOT NULL, "
                        + "endReason TEXT DEFAULT NULL, "
                        + "PRIMARY KEY (uuid)"
                        + ");";

                sql.modifyQuery(query, false);
            }
        }
    }

//    private void createRpgAvatarTable() {
//        if (sql instanceof MySQL) {
//            if (!sql.tableExists(RPG_AVATAR_TABLE)) {
//                ProjectKorra.log.info("Creating " + RPG_AVATAR_TABLE + " table");
//
//                final String query = "CREATE TABLE `" + RPG_AVATAR_TABLE + "` ("
//                        + "`uuid` varchar(36) NOT NULL,"
//                        + "`player` varchar(255) NOT NULL,"
//                        + "`startTime" + "` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
//                        + "`elements` varchar(255) NOT NULL,"
//                        + "PRIMARY KEY (`uuid`)"
//                        + ");";
//
//                sql.modifyQuery(query, false);
//            }
//        } else {
//            if (!sql.tableExists(RPG_AVATAR_TABLE)) {
//                ProjectKorra.log.info("Creating " + RPG_AVATAR_TABLE + " table");
//
//                final String query = "CREATE TABLE `" + RPG_AVATAR_TABLE + "` ("
//                        + "`uuid` TEXT(36) PRIMARY KEY,"
//                        + "`player` TEXT(16) NOT NULL,"
//                        + "`startTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
//                        + "`elements` TEXT(255) NOT NULL"
//                        + ");";
//
//                sql.modifyQuery(query, false);
//            }
//        }
//    }
//
//    private void createRpgPastLivesTable() {
//        if (sql instanceof MySQL) {
//            if (!sql.tableExists(RPG_PASTLIVES_TABLE)) {
//                ProjectKorra.log.info("Creating " + RPG_PASTLIVES_TABLE + " table");
//
//                final String query = "CREATE TABLE `" + RPG_PASTLIVES_TABLE + "` ("
//                        + "`uuid` varchar(36) NOT NULL,"
//                        + "`startTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
//                        + "`player` varchar(255) NOT NULL,"
//                        + "`endTime` datetime DEFAULT NULL,"
//                        + "`elements` varchar(255) NOT NULL,"
//                        + "`endReason` varchar(255) DEFAULT NULL,"
//                        + "PRIMARY KEY (`uuid`, `startTime`)"
//                        + ");";
//
//                sql.modifyQuery(query, false);
//            }
//        } else {
//            if (!sql.tableExists(RPG_PASTLIVES_TABLE)) {
//                ProjectKorra.log.info("Creating " + RPG_PASTLIVES_TABLE + " table");
//
//                final String query = "CREATE TABLE `" + RPG_PASTLIVES_TABLE + "` ("
//                        + "`uuid` TEXT(36) NOT NULL,"
//                        + "`startTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
//                        + "`player` TEXT(16) NOT NULL,"
//                        + "`endTime` datetime DEFAULT NULL,"
//                        + "`elements` TEXT(255) NOT NULL,"
//                        + "`endReason` TEXT(255) DEFAULT NULL,"
//                        + "PRIMARY KEY (`uuid`, `startTime`)"
//                        + ");";
//
//                sql.modifyQuery(query, false);
//            }
//        }
//    }
}
