package com.projectkorra.rpg;

import com.projectkorra.rpg.storage.Database;
import com.projectkorra.rpg.storage.MySQL;
import com.projectkorra.rpg.storage.SQLite;

public class DBConnection {

	public static Database sql;

	public static String engine;
	public static String host;
	public static int port;
	public static String db;
	public static String user;
	public static String pass;

	public static void init() {
		if (engine.equalsIgnoreCase("mysql")) {
			sql = new MySQL(ProjectKorraRPG.log, "[ProjectKorra] Establishing MySQL Connection...", host, port, user, pass, db);
			((MySQL) sql).open();
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
			sql = new SQLite(ProjectKorraRPG.log, "[ProjectKorra] Establishing SQLite Connection.", "projectkorra.db", ProjectKorraRPG.plugin.getDataFolder().getAbsolutePath());
			((SQLite) sql).open();

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
}