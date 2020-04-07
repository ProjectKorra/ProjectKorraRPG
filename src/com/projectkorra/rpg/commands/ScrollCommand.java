package com.projectkorra.rpg.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.rpg.ability.AbilityScroll;

public class ScrollCommand extends RPGCommand {

	public ScrollCommand() {
		super("scroll", "/bending rpg scroll <ability> [user]", "Gives the target a scroll for <ability>. Target refers to command sender or [user] if given.", new String[] {"scroll", "sc"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender)) {
			return;
		} else if (!correctLength(sender, args.size(), 1, 2)) {
			return;
		}
		
		CoreAbility ability = CoreAbility.getAbility(args.get(0));
		
		if (ability == null) {
			sender.sendMessage(ChatColor.RED + "Unknown ability!");
			return;
		}
		
		Player target = null;
		
		if (args.size() == 1) {
			if (!isPlayer(sender)) {
				return;
			}
			
			target = (Player) sender;
		} else {
			target = Bukkit.getPlayer(args.get(1));
		}
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Unknown target player!");
			return;
		}
		
		ItemStack is = new AbilityScroll(ability);
		target.getInventory().addItem(is);
		target.sendMessage(ChatColor.GREEN + "Obtained " + ability.getName() + " ability scroll!");
		if (!target.equals(sender)) {
			sender.sendMessage(ChatColor.GREEN + "Gave " + ability.getName() + " ability scroll to " + target.getName() + "!");
		}
	}
	
	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			return Arrays.asList(CoreAbility.getAbilities().stream().filter((ability) -> !ability.isHiddenAbility()).map(CoreAbility::getName).toArray(String[]::new));
		} else if (args.size() == 1) {
			return Arrays.asList(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));
		} else {
			return new ArrayList<>();
		}
	}
}
