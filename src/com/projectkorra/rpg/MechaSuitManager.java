package com.projectkorra.rpg;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;

public class MechaSuitManager implements Runnable{

	@Override
	public void run() {
		if(RPGListener.riding.isEmpty()) System.out.println("Empty");
		for(String suuid : RPGListener.riding.keySet()) {
			UUID uuid = UUID.fromString(suuid);
			Player rider = Bukkit.getPlayer(uuid);
			
			IronGolem suit = RPGListener.riding.get(rider.getUniqueId().toString());
			
			suit.teleport(rider);
			suit.setTarget(null);
		}
		for(IronGolem un : RPGListener.unmounted.keySet()) {
			un.teleport(RPGListener.unmounted.get(un));
			un.setTarget(null);
		}
	}

}
