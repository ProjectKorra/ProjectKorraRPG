package com.projectkorra.rpg.storage;

import com.projectkorra.projectkorra.ProjectKorra;
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
		open();
		if (!isOpen) return;
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
		engine = ProjectKorraRPG.plugin.getConfig().getString("Storage.engine");
		host = ProjectKorraRPG.plugin.getConfig().getString("Storage.MySQL.host");
		port = ProjectKorraRPG.plugin.getConfig().getInt("Storage.MySQL.port");
		pass = ProjectKorraRPG.plugin.getConfig().getString("Storage.MySQL.pass");
		db = ProjectKorraRPG.plugin.getConfig().getString("Storage.MySQL.db");
		user = ProjectKorraRPG.plugin.getConfig().getString("Storage.MySQL.user");
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
			sql = new SQLite(ProjectKorraRPG.log, "Establishing SQLite Connection... ", "projectkorra_rpg.db", ProjectKorra.plugin.getDataFolder().getAbsolutePath());
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