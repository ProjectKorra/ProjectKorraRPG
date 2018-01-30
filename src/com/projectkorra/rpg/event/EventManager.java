package com.projectkorra.rpg.event;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.rpg.ProjectKorraRPG;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class EventManager implements Runnable {

	private static String message = com.projectkorra.rpg.configuration.ConfigManager.avatarConfig.get().getString("WorldEvents.SozinsComet.EndMessage");

	public static ConcurrentHashMap<World, String> marker = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<World, Boolean> skipper = new ConcurrentHashMap<>();
        
    public static Time time;

	@Override
	public void run() {
		for (World world : Bukkit.getServer().getWorlds()) {
			if (world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.THE_END) {
				continue;
			}

			if (ConfigManager.defaultConfig.get().getStringList("Properties.DisabledWorlds").contains(world.getName())) {
				continue;
			}
			
			if (Commands.isToggledForAll) {
				continue;
			}

			if (!marker.containsKey(world)) {
				marker.put(world, "");
			}

			if (!skipper.containsKey(world)) {
				skipper.put(world, false);
			}

			if (world.getTime() > 23500 || world.getTime() < 500) {
	            if (time != null){
	                if (time == Time.DAY){
	                    continue;
	                }
	            }
	            time = Time.DAY;
	            
	            ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new WorldSunRiseEvent(world));
			} else if (world.getTime() > 11500 && world.getTime() < 12500) {
	            if (time != null){
	                if (time == Time.NIGHT){
	                    continue;
	                }
	            }
	            time = Time.NIGHT;
	            
				ProjectKorraRPG.plugin.getServer().getPluginManager().callEvent(new WorldSunSetEvent(world));
			}
		}
	}

	public static void endEvent(World world) {
		if (marker.get(world).equals("SozinsComet")) {
			for (Player player : world.getPlayers()) {
				if (BendingPlayer.getBendingPlayer(player).hasElement(Element.FIRE)) {
					player.sendMessage(Element.COMBUSTION.getColor() + message);
				}
			}
		}
		marker.put(world, "");
	}
        
        enum Time {
            
            DAY,
            NIGHT;       
        }
}
