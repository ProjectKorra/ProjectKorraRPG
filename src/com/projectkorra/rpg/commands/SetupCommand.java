package com.projectkorra.rpg.commands;

import com.projectkorra.projectkorra.command.PKCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetupCommand extends RPGCommand {
	public SetupCommand(String name, String properUse, String description, String[] aliases) {
		super(name, properUse, description, aliases);
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {

	}
}
