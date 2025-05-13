/**
 * MANY ASPECTS FROM CLASS COPIED FROM com.projectkorra.projectkorra.util.ChatUtil
 */
package com.projectkorra.rpg.util;

import com.google.common.base.Strings;
import com.projectkorra.rpg.configuration.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChatUtil {
	public static void sendBrandingMessage(final CommandSender receiver, final String message) {
		if (Strings.isNullOrEmpty(ChatColor.stripColor(message))) return;
		sendBrandingMessage(receiver, TextComponent.fromLegacyText(com.projectkorra.projectkorra.util.ChatUtil.color(message)));
	}

	/**
	 * Send a message prefixed with the ProjectKorraRPG branding to the provided receiver
	 * @param receiver The person to send the message to
	 * @param message The message to send
	 */
	public static void sendBrandingMessage(final CommandSender receiver, final BaseComponent[] message) {
		if (message == null || message.length == 0) return;

		BaseComponent newComp = new TextComponent();

		for (BaseComponent comp : message) {
			newComp.addExtra(comp);
		}
		sendBrandingMessage(receiver, newComp);
	}

	/**
	 * Send a message prefixed with the ProjectKorraRPG branding to the provided receiver
	 * @param receiver The person to send the message to
	 * @param message The message to send
	 */
	public static void sendBrandingMessage(final CommandSender receiver, final BaseComponent message) {
		FileConfiguration coreLanguageConfig = com.projectkorra.projectkorra.configuration.ConfigManager.languageConfig.get();
		FileConfiguration addonLanguageConfig = ConfigManager.language.get();

		ChatColor coreColor = ChatColor.of(coreLanguageConfig.getString("Chat.Branding.Color", "GOLD").toUpperCase());
		ChatColor addonColor = ChatColor.of(addonLanguageConfig.getString("Chat.Branding.Color", "LIGHT_PURPLE").toUpperCase());

		String start = addonLanguageConfig.getString("Chat.Branding.ChatPrefix.Prefix", "");
		String suffix = addonLanguageConfig.getString("Chat.Branding.ChatPrefix.Suffix", " \u00BB ");
		String coreText = coreLanguageConfig.getString("Chat.Branding.ChatPrefix.Main", "ProjectKorra");
		String addonText = addonLanguageConfig.getString("Chat.Branding.ChatPrefix.Main", "RPG");

		String prefix = addonColor + start + coreColor + coreText + addonColor + addonText + addonColor + suffix;

		if (!(receiver instanceof Player)) {
			receiver.sendMessage(prefix + message.toLegacyText());
		} else {
			TextComponent prefixComponent = new TextComponent(prefix);

			String hover = com.projectkorra.projectkorra.util.ChatUtil.multiline(addonColor + addonLanguageConfig.getString("Chat.Branding.ChatPrefix.Hover", addonColor + "Bending brought to you by ProjectKorra\\n" + addonColor + "Click for more info."));
			if (!hover.isEmpty()) {
				prefixComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
			}

			String click = addonLanguageConfig.getString("Chat.Branding.ChatPrefix.Click", "https://www.projectkorra.com");
			if (!click.isEmpty()) {
				ClickEvent.Action action = ClickEvent.Action.RUN_COMMAND;
				String lc = click.toLowerCase();
				if (lc.startsWith("http://") || lc.startsWith("https://") || lc.startsWith("www.")) {
					action = ClickEvent.Action.OPEN_URL;
				}
				prefixComponent.setClickEvent(new ClickEvent(action, click));
			}

			TextComponent messageComponent = new TextComponent(message);
			messageComponent.setColor(ChatColor.YELLOW);

			((Player) receiver).spigot().sendMessage(new TextComponent(prefixComponent, messageComponent));
		}
	}
}
