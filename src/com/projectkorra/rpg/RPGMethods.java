package com.projectkorra.rpg;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.Element.ElementType;
import com.projectkorra.projectkorra.Element.SubElement;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.attribute.AttributeModifier;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.rpg.ability.AbilityScroll;
import com.projectkorra.rpg.ability.AbilityTiers.AbilityTier;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.player.RPGPlayer;
import com.projectkorra.rpg.util.AvatarCycle;
import com.projectkorra.rpg.util.PreviousAvatar;

public class RPGMethods {

	private static LinkedList<Element> avatarCycle = new LinkedList<>();
	private static Map<String, AttributeModifier> lightAttributes;
	private static Random rand;

	static {
		lightAttributes = new HashMap<>();

		for (String s : ConfigManager.getConfig().getStringList("LightChakraAttributes")) {
			String[] ss = s.split("::");

			if (ss.length != 2) {
				continue;
			}

			AttributeModifier mod = AttributeModifier.valueOf(ss[1].toUpperCase());
			if (mod == null) {
				continue;
			}

			lightAttributes.put(ss[0], mod);
		}
		
		rand = new Random();
	}

	public static void loadAvatarCycle() {
		AvatarCycle cycle = AvatarCycle.load();
		avatarCycle.add(cycle.first);
		avatarCycle.add(cycle.second);
		avatarCycle.add(cycle.third);
		avatarCycle.add(cycle.fourth);
	}

	public static void saveAvatarCycle() {
		Element first = avatarCycle.get(0);
		Element second = avatarCycle.get(1);
		Element third = avatarCycle.get(2);
		Element fourth = avatarCycle.get(3);
		new AvatarCycle(first, second, third, fourth).save();
	}

	public static void cycleAvatar(UUID curr) {
		if (curr != null) {
			revokeAvatar(Bukkit.getOfflinePlayer(curr));
		}

		Element e = avatarCycle.poll();
		avatarCycle.add(e);

		if (Bukkit.getOnlinePlayers().size() <= 1) {
			if (Bukkit.getOnlinePlayers().size() == 1) {
				if (Bukkit.getPlayer(curr) != null) {
					new BukkitRunnable() {

						@Override
						public void run() {
							cycleAvatar(curr);
						}

					}.runTaskLater(ProjectKorraRPG.getPlugin(), ConfigManager.getConfig().getLong("Avatar.AutoCycle.Interval"));

					ProjectKorraRPG.getLog().info("Avatar cycle not viable, checking again in 30 mins!");
					return;
				}
			} else {
				return;
			}
		}

		List<Player> eligible = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (bPlayer == null) {
				continue;
			} else if (!hasBeenAvatar(player.getUniqueId()) && bPlayer.hasElement(avatarCycle.peek())) {
				eligible.add(player);
			}
		}

		Player chosen = eligible.get(new Random().nextInt(eligible.size()));
		setAvatar(chosen.getUniqueId());
		Bukkit.broadcastMessage(ChatColor.WHITE + chosen.getName() + ChatColor.DARK_PURPLE + " has been reincarnated as the next avatar!");
	}

	/**
	 * Randomly assigns an element to the param player if enabled in the config
	 * 
	 * @param bPlayer BendingPlayer being assigned an element to
	 */
	public static void randomAssignElement(BendingPlayer bPlayer) {
		double rand = Math.random();
		double chance = 0;
		Element assign = null;
		for (Element e : Element.getAllElements()) {
			if (!ConfigManager.getConfig().contains("ElementAssign.Percentages." + e.getName())) {
				continue;
			}

			chance += ConfigManager.getConfig().getDouble("ElementAssign.Percentages." + e.getName());
			if (rand < chance) {
				assign = e;
				break;
			}
		}

		assignElement(bPlayer, assign);
		if (ConfigManager.getConfig().getBoolean("SubElementAssign.Enabled")) {
			RPGMethods.randomAssignSubElements(bPlayer, assign);
		}
	}

	public static void randomAssignSubElements(BendingPlayer bPlayer, Element element) {
		if (Element.getSubElements(element).length == 0) {
			return;
		}

		double chance = 0;
		StringBuilder sb = new StringBuilder(ChatColor.YELLOW + "You have an affinity for ");
		ArrayList<String> sublist = new ArrayList<>();

		for (SubElement sub : Element.getSubElements(element)) {
			double rand = Math.random();
			chance = ConfigManager.getConfig().getDouble("SubElementAssign.Percentages." + sub.getName());

			if (rand < chance) {
				sublist.add(sub.getName());
				bPlayer.addSubElement(sub);
			}
		}

		GeneralMethods.saveSubElements(bPlayer);
		int size = sublist.size();
		ChatColor color = Element.getSubElements(element)[0].getColor();

		if (size >= 1) {
			for (String sub : sublist) {
				size--;
				if (size == 0) {
					sb.append(color + sub + ChatColor.YELLOW + ".");
				} else {
					sb.append(color + sub + ChatColor.YELLOW + ", and ");
				}
			}
		} else {
			sb = new StringBuilder(ChatColor.RED + "Unfortunately, you don't have any extra affinity for your element.");
		}

		Bukkit.getPlayer(bPlayer.getUUID()).sendMessage(sb.toString());
	}

	/**
	 * Sets the player's element as param e, sending a message on what they
	 * became.
	 * 
	 * @param player BendingPlayer which the element is being added to
	 * @param e Element being added to the player
	 * @param chiblocker if the player is becoming a chiblocker
	 */
	private static void assignElement(BendingPlayer bPlayer, Element e) {
		bPlayer.setElement(e);
		GeneralMethods.saveElements(bPlayer);
		Bukkit.getPlayer(bPlayer.getUUID()).sendMessage(ChatColor.YELLOW + "You have been born as " + (e.getType() == ElementType.BENDING ? "an " : "a ") + e.getColor() + e.getName() + e.getType().getBender() + ChatColor.YELLOW + "!");
	}

	/**
	 * Returns if there is an avatar, if he/she has already been choosen or not.
	 */
	public static boolean isAvatarChosen() {
		if (ConfigManager.getConfig().contains("Avatar.CurrentAvatar")) {
			if (!ConfigManager.getConfig().getString("Avatar.CurrentAvatar").equals("")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets a player to the avatar giving them all the elements (excluding
	 * chiblocking) and the extra perks such as avatarstate on fatal blow.
	 * 
	 * @param uuid UUID of player being set as the avatar
	 */
	public static void setAvatar(UUID uuid) {
		if (isAvatarChosen()) {
			UUID curr = null;
			try {
				curr = UUID.fromString(ConfigManager.getConfig().getString("Avatar.CurrentAvatar"));
			}
			catch (Exception e) {
				curr = null;
			}

			if (curr != null) {
				revokeAvatar(Bukkit.getOfflinePlayer(uuid));
			}
		}

		Player player = Bukkit.getPlayer(uuid);
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player.getName());
		new PreviousAvatar(uuid, player.getName(), bPlayer.getElements(), bPlayer.getSubElements()).save();

		Set<Element> elements = new HashSet<>();
		Set<SubElement> subs = new HashSet<>();
		for (Element e : Element.getAllElements()) {
			if (bPlayer.hasElement(e)) {
				elements.add(e);
				for (SubElement se : Element.getSubElements(e)) {
					subs.add(se);
				}
			} else if (e.getType() == ElementType.BENDING) {
				elements.add(e);
				for (SubElement se : Element.getSubElements(e)) {
					subs.add(se);
				}
			}
		}

		bPlayer.getElements().clear();
		bPlayer.getElements().addAll(elements);
		GeneralMethods.saveElements(bPlayer);

		bPlayer.getSubElements().clear();
		bPlayer.getSubElements().addAll(subs);
		GeneralMethods.saveSubElements(bPlayer);

		ConfigManager.getConfig().set("Avatar.CurrentAvatar", uuid.toString());
		ConfigManager.saveConfig();
	}

	/**
	 * Checks if player with uuid is they current avatar. Returns null if there
	 * is no current avatar
	 * 
	 * @param uuid UUID of player being checked
	 * @return if player with uuid is the current avatar
	 */
	public static boolean isCurrentAvatar(UUID uuid) {
		String currAvatar = ConfigManager.getConfig().getString("Avatar.CurrentAvatar");
		if (currAvatar == null) {
			return false;
		} else if (uuid.toString().equalsIgnoreCase(currAvatar)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the player with uuid has been the avatar. Returns true if
	 * player is current avatar
	 * 
	 * @param uuid UUID of player being checked
	 * @return if player with uuid has been the avatar
	 */
	public static boolean hasBeenAvatar(UUID uuid) {
		if (isCurrentAvatar(uuid)) {
			return true;
		}

		return ProjectKorraRPG.getStorage().hasFile(uuid.toString());
	}

	/**
	 * Removes the rpg avatar permissions for the player with the matching uuid,
	 * if they are the current avatar
	 * 
	 * @param uuid UUID of player being checked
	 */
	public static void revokeAvatar(OfflinePlayer player) {
		UUID uuid = player.getUniqueId();
		if (uuid == null) {
			return;
		} else if (!isCurrentAvatar(uuid)) {
			return;
		}

		List<Element> elements = new ArrayList<>();
		List<SubElement> subs = new ArrayList<>();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer == null) {
			return;
		}

		PreviousAvatar avatar = PreviousAvatar.load(uuid.toString());
		if (avatar == null) {
			bPlayer.getElements().clear();
			GeneralMethods.saveElements(bPlayer);

			bPlayer.getSubElements().clear();
			GeneralMethods.saveSubElements(bPlayer);

			ConfigManager.getConfig().set("Avatar.CurrentAvatar", "");
			ConfigManager.saveConfig();

			if (player.isOnline()) {
				((Player) player).sendMessage(ChatColor.RED + "Could not locate storage of your elements, your elements have been cleared!");
			}

			ProjectKorraRPG.getLog().info(ChatColor.RED + "Could not locate storage of previous elements for " + ChatColor.WHITE + player.getName() + ChatColor.RED + "(" + ChatColor.WHITE + player.getUniqueId() + ChatColor.RED + "), stored elements could not be set. Elements have been removed to allow rechoosing.");
			return;
		}

		elements = avatar.getElements();
		subs = avatar.getSubs();

		bPlayer.getElements().clear();
		bPlayer.getElements().addAll(elements);
		GeneralMethods.saveElements(bPlayer);

		bPlayer.getSubElements().clear();
		bPlayer.getSubElements().addAll(subs);
		GeneralMethods.saveSubElements(bPlayer);

		ConfigManager.getConfig().set("Avatar.CurrentAvatar", "");
		ConfigManager.saveConfig();
	}

	/**
	 * Clears the database of all previous avatars (excluding the current
	 * avatar), making it possible for them to become avatar again.
	 */
	public static boolean clearPastAvatars() {
		try {
			for (File file : ProjectKorraRPG.getStorage().getFolder().listFiles()) {
				if (!file.getName().equals("AvatarCycle.yml")) {
					file.delete();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean isLightChakraAttribute(String attr) {
		return lightAttributes.containsKey(attr);
	}

	public static AttributeModifier getLightChakraAttributeModifier(String attr) {
		if (lightAttributes.containsKey(attr)) {
			return lightAttributes.get(attr);
		}

		return AttributeModifier.MULTIPLICATION;
	}

	public static List<String> getAttributes(CoreAbility ability) {
		List<String> list = new ArrayList<>();

		for (Field f : ability.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Attribute.class)) {
				list.add(f.getAnnotation(Attribute.class).value());
			}
		}

		return list;
	}
	
	public static boolean hasEnoughScrolls(Player player, CoreAbility ability) {
		AbilityTier tier = ProjectKorraRPG.getAbilityTiers().getAbilityTier(ability);
		AbilityScroll scroll = new AbilityScroll(ability);
		
		return player.getInventory().containsAtLeast(scroll, tier.getRequiredScrolls());
	}
	
	public static boolean useScrolls(Player player, CoreAbility ability) {
		RPGPlayer rpgPlayer = RPGPlayer.get(player);
		
		if (rpgPlayer == null) {
			return false;
		}
		
		AbilityTier tier = ProjectKorraRPG.getAbilityTiers().getAbilityTier(ability);
		AbilityScroll scroll = new AbilityScroll(ability);
		
		scroll.setAmount(tier.getRequiredScrolls());
		
		if (rpgPlayer.unlock(ability)) {
			player.getInventory().removeItem(scroll);
			player.updateInventory();
			return true;
		}
		
		return false;
	}
	
	public static AbilityScroll getRandomScroll(AbilityTier...tiers) {
		List<CoreAbility> selection = new ArrayList<>(ProjectKorraRPG.getAbilityTiers().getAbilitiesFromTiers(tiers));
		
		if (selection.isEmpty()) {
			return null;
		}
		
		CoreAbility ability = selection.get(rand.nextInt(selection.size()));
		
		return new AbilityScroll(ability);
	}
	
	public static int getMobDropXP(EntityType type) {
		if (!type.isAlive()) {
			return 0;
		}
		
		return ConfigManager.getConfig().getInt("MobDrops." + type.toString() + ".XP");
	}
	
	public static List<AbilityScroll> getMobDropScrolls(EntityType type) {
		List<AbilityScroll> scrolls = new ArrayList<>();
		
		if (!type.isAlive()) {
			return scrolls;
		}
		
		for (String s : ConfigManager.getConfig().getStringList("MobDrops." + type.toString() + ".DefiniteDrops")) {
			CoreAbility ability = CoreAbility.getAbility(s);
			
			if (ability != null) {
				scrolls.add(new AbilityScroll(ability));
			}
		}
		
		int chance = ConfigManager.getConfig().getInt("MobDrops." + type.toString() + ".RandomChance");
		List<String> list = ConfigManager.getConfig().getStringList("MobDrops." + type.toString() + ".RandomDropTiers");
		
		if (!list.isEmpty() && chance > rand.nextInt(100)) {
			AbilityTier[] tiers = list.stream().map(String::toUpperCase).map(AbilityTier::valueOf).toArray(AbilityTier[]::new);
			scrolls.add(getRandomScroll(tiers));
		}
		
		return scrolls;
	}
	
	public static int getAbilityID(CoreAbility ability) {
		return getAbilityID(ability.getName());
	}
	
	public static String getListCommaSeparated(Collection<String> group) {
		StringBuilder build = new StringBuilder();
		for (String s : group) {
			if (build.length() != 0) {
				build.append(", ");
			}
			
			build.append(s);
		}
		
		return build.toString();
	}

	public static int getAbilityID(String ability) {
		ResultSet r = DBConnection.sql.readQuery("SELECT id FROM rpg_ability_ids WHERE name = '" + ability + "';");
		int id = -1;

		try {
			if (!r.next()) {
				DBConnection.sql.modifyQuery("INSERT INTO rpg_ability_ids (name) VALUES ('" + ability + "');", false);
				r = DBConnection.sql.readQuery("SELECT id FROM rpg_ability_ids WHERE name = '" + ability + "';");
				r.next();
			}

			id = r.getInt("id");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}
}
