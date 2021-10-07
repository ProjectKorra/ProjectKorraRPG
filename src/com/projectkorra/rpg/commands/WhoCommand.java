package com.projectkorra.rpg.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.player.ChakraStats;
import com.projectkorra.rpg.player.ChakraStats.Chakra;
import com.projectkorra.rpg.player.RPGPlayer;
import com.projectkorra.rpg.util.XPControl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class WhoCommand extends RPGCommand {

	public WhoCommand() {
		super("who", "/bending rpg who [user]", "List information about the target. Target refers to command sender or [user] if given.", new String[] {"who", "whois"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender)) {
			return;
		} else if (!correctLength(sender, args.size(), 0, 1)) {
			return;
		}
		
		Player target = null;
		
		if (args.size() == 0) {
			if (!isPlayer(sender)) {
				return;
			}
			
			target = (Player) sender;
		} else {
			target = Bukkit.getPlayer(args.get(0));
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
		
		String tier = player.getCurrentTier().getDisplay();
		String level = ChatColor.GOLD + "Level " + ChatColor.DARK_AQUA + player.getLevel();
		String xp = ChatColor.DARK_AQUA + "" + player.getXP() + ChatColor.DARK_GRAY + " XP";
		String next = player.getLevel() < XPControl.MAX_LEVEL ? (" ," + ChatColor.DARK_AQUA + "" + (XPControl.getXPRequired(player.getLevel()) - player.getXP()) + ChatColor.DARK_GRAY + " xp until next level") : "";

		sender.sendMessage("\n" + ChatColor.GOLD + "" + ChatColor.BOLD + target.getName() + ChatColor.DARK_GRAY + " [" + tier + ChatColor.DARK_GRAY + "]");
		sender.sendMessage(level + ChatColor.DARK_GRAY + " (" + xp + next + ChatColor.DARK_GRAY + ")");
		sender.sendMessage("");
		for (Chakra chakra : Chakra.values()) {
			sender.spigot().sendMessage(new ComponentBuilder(getChakraBar(player, chakra)).append(" " + chakra.getDisplay()).create());
		}
		
		if (RPGMethods.isCurrentAvatar(target.getUniqueId())) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Current Avatar");
		} else if (RPGMethods.hasBeenAvatar(target.getUniqueId())) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "Past Life Avatar");
		}

		sender.sendMessage("");
	}
	
	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			return Arrays.asList(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));
		} else {
			return new ArrayList<>();
		}
	}

	public TextComponent getChakraBar(RPGPlayer player, Chakra chakra) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < ChakraStats.MAX_POINTS; ++i) {
			builder.append("▮");
		}

		builder.insert(player.getStats().getPoints(chakra), ChatColor.BLACK);
		builder.insert(0, chakra.getColor());

		TextComponent comp = new TextComponent(builder.toString());

		comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(chakra.getColor() + "" + player.getStats().getPoints(chakra) + ChatColor.WHITE + "/" + ChakraStats.MAX_POINTS)));
		return comp;
	}
	
}
