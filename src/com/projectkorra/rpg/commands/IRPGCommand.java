package com.projectkorra.rpg.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface IRPGCommand {

	public void execute(CommandSender sender, List<String> args);
}
