package com.projectkorra.rpg.storage;

import com.projectkorra.rpg.ProjectKorraRPG;

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
		engine = ProjectKorraRPG.plugin.getConfig().getString("Storage.engine");
		host = ProjectKorraRPG.plugin.getConfig().getString("Storage.MySQL.host");
		port = ProjectKorraRPG.plugin.getConfig().getInt("Storage.MySQL.port");
		pass = ProjectKorraRPG.plugin.getConfig().getString("Storage.MySQL.pass");
		db = ProjectKorraRPG.plugin.getConfig().getString("Storage.MySQL.db");
		user = ProjectKorraRPG.plugin.getConfig().getString("Storage.MySQL.user");
		if (engine.equalsIgnoreCase("mysql")) {
			sql = new MySQL(ProjectKorraRPG.log, "[ProjectKorra] Establishing MySQL Connection... ", host, port, user, pass, db);
			if (((MySQL) sql).open() == null) {
				ProjectKorraRPG.log.severe("Disabling due to database error");
				return;
			}

			isOpen = true;
			ProjectKorraRPG.log.info("[ProjectKorra] Database connection established.");

			if (!sql.tableExists("pk_avatars")) {
				ProjectKorraRPG.log.info("Creating pk_avatars table.");
				String query = "CREATE TABLE `pk_avatars` ("
						+ "`id` int(32) NOT NULL AUTO_INCREMENT,"
						+ "`uuid` varchar(255),"
						+ "`player` varchar(255),"
						+ "`element` varchar(255),"
						+ " PRIMARY KEY (id));";
				sql.modifyQuery(query);
			}
			
		} else {
			sql = new SQLite(ProjectKorraRPG.log, "[ProjectKorra] Establishing SQLite Connection... ", "projectkorra.db", ProjectKorraRPG.plugin.getDataFolder().getAbsolutePath());
			if (((SQLite) sql).open() == null) {
				ProjectKorraRPG.log.severe("Disabling due to database error");
				return;
			}

			isOpen = true;
			ProjectKorraRPG.log.info("[ProjectKorra] Database connection established.");

			if (!sql.tableExists("pk_avatars")) {
				ProjectKorraRPG.log.info("Creating pk_avatars table.");
				String query = "CREATE TABLE `pk_avatars` ("
						+ "`id` INTEGER PRIMARY KEY,"
						+ "`uuid` TEXT(255),"
						+ "`player` TEXT(255),"
						+ "`element` TEXT(255));";
				sql.modifyQuery(query);
			}
		}
	}
	
	public static boolean isOpen() {
		return isOpen;
	}
}