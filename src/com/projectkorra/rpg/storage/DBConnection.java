package com.projectkorra.rpg.storage;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.configuration.ConfigManager;

public class DBConnection {

	public static Database sql;

	private static String engine;
	private static String host;
	private static int port;
	private static String db;
	private static String user;
	private static String pass;
	private static boolean isOpen;

	public static void init() {
		open();
		if (!isOpen)
			return;
		String createQuery = "CREATE TABLE pk_avatars (id INTEGER PRIMARY KEY, uuid %uuid%, player %player%, elements %elements%)";
		if (host.equalsIgnoreCase("mysql")) {
			createQuery = createQuery.replace("%uuid%", "varchar(255)");
			createQuery = createQuery.replace("%player%", "varchar(255)");
			createQuery = createQuery.replace("%elements%", "varchar(255)");
		} else {
			createQuery = createQuery.replace("%uuid%", "TEXT(255)");
			createQuery = createQuery.replace("%player%", "TEXT(255)");
			createQuery = createQuery.replace("%elements%", "TEXT(255)");
		}
		if (!sql.tableExists("pk_avatars")) {
			ProjectKorraRPG.log.info("Creating pk_avatars table.");
			sql.modifyQuery(createQuery);
		}
	}

	public static void close() {
		isOpen = false;
		sql.close();
	}

	public static void open() {
		engine = ConfigManager.rpgConfig.get().getString("Storage.engine");
		host = ConfigManager.rpgConfig.get().getString("Storage.MySQL.host");
		port = ConfigManager.rpgConfig.get().getInt("Storage.MySQL.port");
		pass = ConfigManager.rpgConfig.get().getString("Storage.MySQL.pass");
		db = ConfigManager.rpgConfig.get().getString("Storage.MySQL.db");
		user = ConfigManager.rpgConfig.get().getString("Storage.MySQL.user");
		if (isOpen)
			return;
		if (engine.equalsIgnoreCase("mysql")) {
			sql = new MySQL(ProjectKorraRPG.log, "Establishing MySQL Connection... ", host, port, user, pass, db);
			if (((MySQL) sql).open() == null) {
				ProjectKorraRPG.log.severe("Failed to open the database.");
				return;
			}
			isOpen = true;
			ProjectKorraRPG.log.info("Database connection established.");
		} else {
			sql = new SQLite(ProjectKorraRPG.log, "Establishing SQLite Connection... ", "projectkorra.db", ProjectKorraRPG.plugin.getDataFolder().getAbsolutePath());
			if (((SQLite) sql).open() == null) {
				ProjectKorraRPG.log.severe("Disabling due to database error");
				return;
			}
			isOpen = true;
			ProjectKorraRPG.log.info("Database connection established.");
		}
	}

	public static boolean isOpen() {
		return isOpen;
	}
}
