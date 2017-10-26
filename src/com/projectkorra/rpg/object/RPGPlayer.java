package com.projectkorra.rpg.object;

import java.util.UUID;

public class RPGPlayer {

	public UUID uuid;
	
	public RPGPlayer(UUID uuid) {
		this.uuid = uuid;
	}
	
	public UUID getUniqueID() {
		return uuid;
	}
	
	
}
