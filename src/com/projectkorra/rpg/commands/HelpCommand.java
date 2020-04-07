package com.projectkorra.rpg.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import com.projectkorra.rpg.ability.AbilityTiers.AbilityTier;
import com.projectkorra.rpg.player.RPGPlayer;
import com.projectkorra.rpg.worldevent.WorldEvent;

public class HelpCommand extends RPGCommand {

	private String modifiers = ChatColor.GOLD + "Commands: <required> [optional]";
	private String help = null;

	public HelpCommand() {
		super("help", "/bending rpg help <command/worldevent>", "Shows all helpful information for rpg", new String[] { "help", "h", "?" });
		
		StringBuilder help = new StringBuilder(modifiers);
		for (RPGCommand command : RPGCommand.instances.values()) {
			help.append(ChatColor.YELLOW + "\n" + command.getProperUse());
		}
		
		this.help = help.toString();
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!correctLength(sender, args.size(), 0, 1)) {
			return;
		}

		if (args.size() == 0) {
			sender.sendMessage(help);
		} else if (args.size() == 1) {
			for (RPGCommand command : RPGCommand.instances.values()) {
				if (command instanceof HelpCommand)
					continue;
				if (Arrays.asList(command.getAliases()).contains(args.get(0).toLowerCase())) {
					sender.sendMessage(ChatColor.GOLD + "Proper use: " + ChatColor.DARK_AQUA + command.getProperUse());
					sender.sendMessage(ChatColor.YELLOW + command.getDescription());
					if (command.getName().equalsIgnoreCase("avatar")) {
						if (!hasPermission(sender, "avatar"))
							return;
						sender.sendMessage(Element.AVATAR.getColor() + "Avatar - The avatar is the only bender who can wield all four elements. He or she is the bridge between the spirit and physical world, keeping balance in the physical world. The avatar is the most powerful bender, and the cycle for which element is to be the next avatar goes Fire, Air, Water, Earth, starting with the first firebending avatar, Wan");
					}
					return;
				}
			}

			for (WorldEvent event : WorldEvent.getEvents()) {
				if (args.get(0).equalsIgnoreCase(event.getName()) || event.getAliases().contains(args.get(0).toLowerCase())) {
					sender.sendMessage(ChatColor.BOLD + event.getName());
					sender.sendMessage(event.getTextColor() + event.getDescription());
					sender.sendMessage(ChatColor.WHITE + "Affected Elements:");
					for (Element e : event.getElements()) {
						sender.sendMessage("- " + e.getColor() + e.getName());
					}
					return;
				}
			}
			
			CoreAbility ability = CoreAbility.getAbility(args.get(0));
			
			if (ability != null) {
				AbilityTier tier = ProjectKorraRPG.getAbilityTiers().getAbilityTier(ability);
				ChatColor color = ability.getElement().getColor();
				
				sender.sendMessage(color + ability.getName());
				sender.sendMessage("Tier: " + tier.getDisplay() + ChatColor.WHITE + "(" + tier.getColor() + tier.getLevel() + (tier == AbilityTier.MASTER ? "" : "+") + ChatColor.WHITE + ")");
				sender.sendMessage("Scrolls: " + tier.getRequiredScrolls());
				sender.sendMessage("Attributes: " + RPGMethods.getListCommaSeparated(RPGMethods.getAttributes(ability)));
				
				if (sender instanceof Player) {
					RPGPlayer player = RPGPlayer.get((Player) sender);
					
					if (player == null) {
						return;
					}
					
					String lock = player.hasUnlocked(ability) ? ChatColor.GREEN + "Unlocked" : ChatColor.RED + "Locked";
					sender.sendMessage("Status: " + lock);
				}
				
				return;
			}
		}
	}

	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() >= 1)
			return new ArrayList<String>();
		List<String> l = new ArrayList<String>();
		for (RPGCommand cmd : RPGCommand.instances.values()) {
			if (!cmd.getName().equals("help")) {
				l.add(cmd.getName());
			}
		}
		Collections.sort(l);
		l.addAll(WorldEvent.getEventNames());
		l.addAll(CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collector.of(ArrayList::new, ArrayList::add, (left, right) -> {left.addAll(right); return left;})));
		return l;
	}
}
