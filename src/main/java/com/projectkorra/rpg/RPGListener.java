package com.projectkorra.rpg;

import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.commands.HelpCommand;
import com.projectkorra.rpg.commands.RPGCommandBase;
import com.projectkorra.rpg.configuration.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class RPGListener implements Listener {
	private final ProjectKorraRPG plugin;

	public RPGListener(final ProjectKorraRPG plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBendingConfigReload(final BendingReloadEvent event) {
		// Disable all enabled modules for clean module start
		plugin.getModuleManager().disableModules();

		// Reload configs
		ConfigManager.defaultConfig.reload();
		ConfigManager.languageConfig.reload();

		// Instantiate BaseCommands (tick later because of Bukkit "limitation")
		new BukkitRunnable() {
			@Override
			public void run() {
				new RPGCommandBase();
				new HelpCommand();
            }
		}.runTaskLater(plugin, 20);

		// Re-Enable all modules for clean start
		plugin.getModuleManager().enableModules();

        event.getSender().sendMessage(ChatUtil.color("&bRPG Addon reloaded!"));
    }
}
