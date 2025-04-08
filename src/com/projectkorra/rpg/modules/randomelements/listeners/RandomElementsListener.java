package com.projectkorra.rpg.modules.randomelements.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.event.BendingPlayerLoadEvent;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.randomelements.methods.RandomElementMethods;
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

		boolean hybrid = ConfigManager.getConfig().getBoolean("Configuration.Modules.RandomElements.Element.Hybrid", true);
		List<Element> chosenElements = RandomElementMethods.getRandomizedElements(hybrid);

		StringBuilder message = new StringBuilder("You were born as a ");
		for (Element element : chosenElements) {
			bPlayer.addElement(element);
			message.append(element.getName()).append(" ");
		}
		message.append("bender!");
		player.sendMessage(message.toString().trim());
	}
}
