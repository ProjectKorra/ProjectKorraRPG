package com.projectkorra.rpg;

public class RPGMethods {

	static ProjectKorraRPG plugin;

	public RPGMethods(ProjectKorraRPG plugin) {
		RPGMethods.plugin = plugin;
	}
	
	public static boolean hasBeenAvatar(Player player) {
		BendingPlayer bPlayer = Methods.getBendingPlayer(player.getName());
		
		//check for avatar status
		//if(avatarCheck) return true;
		
		return false;
	}
}
