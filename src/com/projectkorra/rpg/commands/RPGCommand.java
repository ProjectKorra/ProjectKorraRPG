package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.command.SubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class RPGCommand implements SubCommand{
	
	public static HashMap<String, RPGCommand> instances = new HashMap<>();

	private String name;
	private String properUse;
	private String description;
	private String[] aliases;
	
	public RPGCommand(String name, String properUse, String description, String[] aliases) {
		this.name = name;
		this.properUse = properUse;
		this.description = description;
		this.aliases = aliases;
		instances.put(name, this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getProperUse() {
		return properUse;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String[] getAliases() {
		return aliases;
	}
	

	public void help(CommandSender sender, boolean description) {
		sender.sendMessage(ChatColor.GOLD + "Proper Usage: " + ChatColor.DARK_AQUA + properUse);
		if (description) {
			sender.sendMessage(ChatColor.YELLOW + this.description);
		}
	}

	/**
	 * Checks if the {@link CommandSender} has permission to execute the
	 * command. The permission is in the format 'bending.command.
	 * {@link PKCommand#name name}'. If not, they are told so.
	 * 
	 * @param sender The CommandSender to check
	 * @return True if they have permission, false otherwise
	 */
	protected boolean hasPermission(CommandSender sender) {
		if (sender.hasPermission("bending.command.rpg." + name)) {
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
			return false;
		}
	}

	/**
	 * Checks if the {@link CommandSender} has permission to execute the
	 * command. The permission is in the format 'bending.command.
	 * {@link PKCommand#name name}.extra'. If not, they are told so.
	 * 
	 * @param sender The CommandSender to check
	 * @param extra The additional node to check
	 * @return True if they have permission, false otherwise
	 */
	protected boolean hasPermission(CommandSender sender, String extra) {
		if (sender.hasPermission("bending.command.rpg." + name + "." + extra)) {
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
			return false;
		}
	}

	/**
	 * Checks if the argument length is within certain parameters, and if not,
	 * informs the CommandSender of how to correctly use the command.
	 * 
	 * @param sender The CommandSender who issued the command
	 * @param size The length of the arguments list
	 * @param min The minimum acceptable number of arguments
	 * @param max The maximum acceptable number of arguments
	 * @return True if min < size < max, false otherwise
	 */
	protected boolean correctLength(CommandSender sender, int size, int min, int max) {
		if (size < min || size > max) {
			help(sender, false);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checks if the CommandSender is an instance of a Player. If not, it tells
	 * them they must be a Player to use the command.
	 * 
	 * @param sender The CommandSender to check
	 * @return True if sender instanceof Player, false otherwise
	 */
	protected boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to use that command.");
			return false;
		}
	}
	
	/**Gets a list of valid arguments that can be used in tabbing.*/
	protected List<String> getTabCompletion(CommandSender sender, List<String> args)
	{
		return new ArrayList<String>();
	}
}
