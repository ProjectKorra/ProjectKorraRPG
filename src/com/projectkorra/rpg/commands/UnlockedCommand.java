package com.projectkorra.rpg.commands;

import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import com.projectkorra.rpg.player.RPGPlayer;

public class UnlockedCommand extends RPGCommand {

	public UnlockedCommand() {
		super("unlocked", "/b rpg unlocked", "Lists all abilities you have unlocked", new String[] {"unlocked"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!isPlayer(sender)) {
			return;
		} else if (!correctLength(sender, args.size(), 0, 0)) {
			return;
		}
		
		RPGPlayer player = RPGPlayer.get((Player) sender);
		
		if (player == null) {
			sender.sendMessage("Player data not found!");
			return;
		}
		
		sender.sendMessage("Unlocked:");
		
		if (player.getUnlockedAbilities().isEmpty()) {
			sender.sendMessage("None");
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		
		Iterator<String> iter = player.getUnlockedAbilities().iterator();
		while (iter.hasNext()) {
			CoreAbility ability = CoreAbility.getAbility(iter.next());
			
			if (ability == null) {
				continue;
			} else if (!ability.isEnabled()) {
				continue;
			} else if (!player.getBendingPlayer().hasElement(ability.getElement())) {
				continue;
			} else if (ability.isHiddenAbility() || ability instanceof PassiveAbility) {
				continue;
			}
			
			if (builder.length() != 0) {
				builder.append(ChatColor.WHITE + ", ");
			}
			
			builder.append(ability.getElement().getColor() + ability.getName());
		}
		
		sender.sendMessage(builder.toString());
	}

}
