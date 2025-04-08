package com.projectkorra.rpg.storage;

import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.rpg.ProjectKorraRPG;

public class Storage extends DBConnection {

	public static void init() {
		if (!sql.tableExists("pkrpg_players")) {
			ProjectKorraRPG.getPlugin().getLogger().info("Creating pkrpg_players table");
			final String query = "CREATE TABLE `pkrpg_players` ("
					+ "`uuid` varchar(36) NOT NULL,"
					+ "`xp` INT NOT NULL DEFAULT 0,"
					+ "`level` INT NOT NULL DEFAULT 1,"
					+ "PRIMARY KEY (`uuid`),"
					+ "FOREIGN KEY (`uuid`) REFERENCES `pk_players`(`uuid`) ON DELETE CASCADE"
					+ ");";

			sql.modifyQuery(query, false);
		}
	}
}
