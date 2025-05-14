package com.projectkorra.rpg.storage;

import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.MySQL;
import com.projectkorra.rpg.ProjectKorraRPG;

public class TableCreator extends DBConnection {
    public static final String RPG_PLAYER_TABLE = "pkrpg_players";
    public static final String RPG_SCHEDULE_TABLE = "pkrpg_schedule";

    public TableCreator() {
        this.createRpgPlayerTable();
        this.createScheduleTable();
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
}
