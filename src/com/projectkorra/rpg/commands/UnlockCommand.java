package com.projectkorra.rpg.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.rpg.player.RPGPlayer;

public class UnlockCommand extends RPGCommand {

	public UnlockCommand() {
		super("unlock", "/bending rpg unlock <ability> [user]", "Unlock <ability> for the target. Target refers to command sender or [user] if given.", new String[] {"unlock", "ul"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 1, 2)) {
			return;
		}
		
		Player target = null;
		
		if (args.size() == 1) {
			if (!isPlayer(sender) || !hasPermission(sender)) {
				return;
			}
			
			target = (Player) sender;
		} else {
			if (!hasPermission(sender, "others")) {
				return;
			}
			
			target = Bukkit.getPlayer(args.get(1));
		}
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Unknown player!");
			return;
		}
		
		RPGPlayer player = RPGPlayer.get(target);
		
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "Player has no data!");
			return;
		}
		
		CoreAbility ability = CoreAbility.getAbility(args.get(0));
		
		if (ability == null) {
			sender.sendMessage(ChatColor.RED + "Unknown ability!");
			return;
		}
		
		if (player.unlock(ability)) {
			target.sendMessage(ability.getElement().getColor() + ability.getName() + ChatColor.WHITE + " has been unlocked!");
			
			if (!target.equals(sender)) {
				sender.sendMessage(ability.getElement().getColor() + ability.getName() + ChatColor.WHITE + " unlocked for " + target.getName() + "!");
			}
		} else {
			
			if (!target.equals(sender)) {
				sender.sendMessage(ChatColor.RED + "They already have that ability unlocked!");
			} else {
				target.sendMessage(ChatColor.RED + "That ability is already unlocked!");
			}
		}
	}
	
	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			return Arrays.asList(CoreAbility.getAbilities().stream().filter((ability) -> !ability.isHiddenAbility()).map(CoreAbility::getName).toArray(String[]::new));
		} else if (args.size() == 1 && hasPermission(sender, "others")) {
			return Arrays.asList(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));
		} else {
			return new ArrayList<>();
		}
	}
}
