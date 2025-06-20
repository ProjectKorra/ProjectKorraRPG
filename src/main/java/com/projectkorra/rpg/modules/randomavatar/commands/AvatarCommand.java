package com.projectkorra.rpg.modules.randomavatar.commands;

import com.projectkorra.rpg.commands.RPGCommand;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AvatarCommand extends RPGCommand {
	private final AvatarManager avatarManager;

    public AvatarCommand(AvatarManager avatarManager) {
        super("avatar", "/bending rpg avatar <Player>", "This command defines a player as the avatar and gives them all the elements and other perks.", new String[]{"avatar", "av", "avy"});

		this.avatarManager = avatarManager;
	}

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!correctLength(sender, args.size(), 1, 1))
            return;

        if (sender instanceof Player) {
            if (!hasPermission(sender))
                return;
        }

		if (args.getFirst().equalsIgnoreCase("list")) {
			List<String> avatars =  this.avatarManager.getPastLives();
			sender.sendMessage(ChatColor.BOLD + "Avatar Past Lives:");
			for (String s : avatars) {
				sender.sendMessage(s);
			}
			return;
		}
		Player target = Bukkit.getPlayer(args.getFirst());
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Player not found!");
		} else {
			boolean succesful = this.avatarManager.makeAvatar(target.getUniqueId());
			if (!succesful) {
				sender.sendMessage(ChatColor.RED + "Failed to declare avatar!");
			} else {
				sender.sendMessage(ChatColor.DARK_PURPLE + target.getName() + " has been declared the Avatar!");
			}
		} 
	}
	
	@Override
	protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (!args.isEmpty()) return new ArrayList<>();
		List<String> players = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			players.add(p.getName());
		}
		return players;
	}
}
