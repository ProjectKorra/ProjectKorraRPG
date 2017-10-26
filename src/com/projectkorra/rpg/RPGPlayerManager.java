package com.projectkorra.rpg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.projectkorra.rpg.object.RPGPlayer;

public class RPGPlayerManager {
	
	public Map<UUID, RPGPlayer> onlinePlayers;

	public RPGPlayerManager() {
		onlinePlayers = new HashMap<>();
	}
	
	public boolean loadPlayer(Player player) {
		RPGPlayer rPlayer = new RPGPlayer(player.getUniqueId());
		onlinePlayers.put(player.getUniqueId(), rPlayer);
		//TODO: Add more info based on a database that still needs set up
		return true;
	}
}
