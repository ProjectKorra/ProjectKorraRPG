package com.projectkorra.rpg.worldevent;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldEventInstance {

	private WorldEvent event;
	private World world;
	private long startTime, endTime;
	private BukkitRunnable endTask;
	
	public WorldEventInstance(WorldEvent event, World world,  BukkitRunnable endTask) {
		this.event = event;
		this.world = world;
		this.startTime = world.getTime();
		this.endTime = startTime + event.getDuration();
		this.endTask = endTask;
	}
	
	public long getElapsedTime() {
		return world.getTime() - startTime;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public long getDuration() {
		return endTime - startTime;
	}
	
	public WorldEvent getEvent() {
		return event;
	}
	
	public BukkitRunnable getEndTask() {
		return endTask;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void remove() {
		endTask.cancel();
	}
}
