package com.projectkorra.rpg.event;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.projectkorra.rpg.ProjectKorraRPG;

public enum WorldEvent {

	FullMoon,
	SolarEclipse, 
	LunarEclipse, 
	SozinsComet;
	
	public static WorldEvent getWorldEvent(String event) {
		if (event.equalsIgnoreCase(WorldEvent.FullMoon.toString()))
			return WorldEvent.FullMoon;
		else if (event.equalsIgnoreCase(WorldEvent.LunarEclipse.toString()))
			return WorldEvent.LunarEclipse;
		else if (event.equalsIgnoreCase(WorldEvent.SolarEclipse.toString()))
			return WorldEvent.SolarEclipse;
		else if (event.equalsIgnoreCase(WorldEvent.SozinsComet.toString()))
			return WorldEvent.SozinsComet;
		return null;
	}
	
	public static void start(World world, WorldEvent event) {
		if (event.equals(WorldEvent.FullMoon))
			Bukkit.getServer().getPluginManager().callEvent(new FullMoonEvent(world));
		if (event.equals(WorldEvent.LunarEclipse))
			Bukkit.getServer().getPluginManager().callEvent(new LunarEclipseEvent(world));
		if (event.equals(WorldEvent.SolarEclipse))
			Bukkit.getServer().getPluginManager().callEvent(new SolarEclipseEvent(world));
		if (event.equals(WorldEvent.SozinsComet))
			Bukkit.getServer().getPluginManager().callEvent(new SozinsCometEvent(world));
		EventManager.setEvent(world, event);
	}
	
	public static class FullMoonEvent extends Event implements Cancellable{
		
		private static final HandlerList handlers = new HandlerList();
		private boolean cancelled;
		private World world;
		private String message = ProjectKorraRPG.plugin.getConfig().getString("WorldEvents.FullMoon.Message");

		public FullMoonEvent(World world) {
			this.world = world;
		}
		
		public String getMessage() {
			return message;
		}
		
		public World getWorld() {
			return world;
		}
		
		public WorldEvent getWorldEvent() {
			return WorldEvent.FullMoon;
		}
		
		@Override
		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancelled) {
			// TODO Auto-generated method stub
			this.cancelled = cancelled;
		}

		@Override
		public HandlerList getHandlers() {
			// TODO Auto-generated method stub
			return handlers;
		}
		
		public static HandlerList getHandlerList() {
			return handlers;
		}
	}
	
	public static class SolarEclipseEvent extends Event implements Cancellable{
		
		private static final HandlerList handlers = new HandlerList();
		private boolean cancelled;
		private World world;
		private String message = ProjectKorraRPG.plugin.getConfig().getString("WorldEvents.SolarEclipse.Message");
		
		public SolarEclipseEvent(World world) {
			this.world = world;
		}
		
		public String getMessage() {
			return message;
		}
		
		public World getWorld() {
			return world;
		}
		
		public WorldEvent getWorldEvent() {
			return WorldEvent.SolarEclipse;
		}

		@Override
		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancelled) {
			// TODO Auto-generated method stub
			this.cancelled = cancelled;
		}

		@Override
		public HandlerList getHandlers() {
			// TODO Auto-generated method stub
			return handlers;
		}
		
		public static HandlerList getHandlerList() {
			return handlers;
		}
	}
	
	public static class LunarEclipseEvent extends Event implements Cancellable{

		private static final HandlerList handlers = new HandlerList();
		private boolean cancelled;
		private World world;
		private String message = ProjectKorraRPG.plugin.getConfig().getString("WorldEvents.LunarEclipse.Message");
		
		public LunarEclipseEvent(World world) {
			this.world = world;
		}
		
		public String getMessage() {
			return message;
		}
		
		public World getWorld() {
			return world;
		}
		
		public WorldEvent getWorldEvent() {
			return WorldEvent.LunarEclipse;
		}
		
		@Override
		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancelled) {
			// TODO Auto-generated method stub
			this.cancelled = cancelled;
		}

		@Override
		public HandlerList getHandlers() {
			// TODO Auto-generated method stub
			return handlers;
		}
		
		public static HandlerList getHandlerList() {
			return handlers;
		}
	}
	
	public static class SozinsCometEvent extends Event implements Cancellable{

		private static final HandlerList handlers = new HandlerList();
		private boolean cancelled;
		private World world;
		private String message = ProjectKorraRPG.plugin.getConfig().getString("WorldEvents.SozinsComet.Message");
		
		public SozinsCometEvent(World world) {
			this.world = world;
		}
		
		public String getMessage() {
			return message;
		}
		
		public World getWorld() {
			return world;
		}
		
		public WorldEvent getWorldEvent() {
			return WorldEvent.SozinsComet;
		}
		
		@Override
		public boolean isCancelled() {
			// TODO Auto-generated method stub
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancelled) {
			// TODO Auto-generated method stub
			this.cancelled = cancelled;
		}

		@Override
		public HandlerList getHandlers() {
			// TODO Auto-generated method stub
			return handlers;
		}
		
		public static HandlerList getHandlerList() {
			return handlers;
		}
	}
}
