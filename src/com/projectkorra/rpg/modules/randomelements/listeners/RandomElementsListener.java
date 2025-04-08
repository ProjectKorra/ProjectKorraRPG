package com.projectkorra.rpg.modules.randomelements.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.event.BendingPlayerLoadEvent;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.randomelements.methods.RandomElementMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class RandomElementsListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBendingPlayerCreation(final BendingPlayerLoadEvent e) {
		BendingPlayer bPlayer = (BendingPlayer) e.getBendingPlayer();

		if (!bPlayer.getElements().isEmpty()) return;

		Player player = bPlayer.getPlayer();

		boolean hybrid = ConfigManager.getFileConfig().getBoolean("Configuration.Modules.RandomElements.Element.Hybrid", true);
		List<Element> chosenElements = RandomElementMethods.getRandomizedElements(hybrid);
		List<Element.SubElement> chosenSubElements = RandomElementMethods.getRandomizedSubElements(chosenElements.get(0));

		StringBuilder message = new StringBuilder(ChatColor.YELLOW + "You were born as a ");
		for (Element element : chosenElements) {
			bPlayer.addElement(element);
			message.append(element.getColor()).append(element.getName()).append(" ");
		}

		message.append(ChatColor.YELLOW).append("bender! ");

		if (!chosenSubElements.isEmpty()) {
			message.append(ChatColor.GOLD).append("You were born lucky and also received: ");
			for (Element.SubElement subElement : chosenSubElements) {
				bPlayer.addSubElement(subElement);
				message.append(subElement.getSubColor()).append(subElement.getName()).append(" ");
			}
			message.append(ChatColor.GOLD).append("Sub elements!");
		}
		player.sendMessage(message.toString().trim());
	}
}
