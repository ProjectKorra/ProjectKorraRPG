package com.projectkorra.rpg.object;

import org.bukkit.entity.Creature;

import com.projectkorra.projectkorra.Element;

public interface MobAbility {

	/**
	 * Name of the ability
	 * @return ability name
	 */
	public String getName();
	
	/**
	 * Creature using the ability
	 * @return ability creature
	 */
	public Creature getCreature();
	
	/**
	 * Element of the ability
	 * @return ability element
	 */
	public Element getElement();
	
	/**
	 * Progresses the ability one iteration forward
	 * @return true if progress was successful
	 */
	public boolean progress();
	
	/**
	 * Removes the ability from instance map(s)
	 */
	public void remove();
	
	/**
	 * The id for the ability
	 * @return ability id
	 */
	public int getID();
	
	/**
	 * Starts the ability progression
	 * @return true if started
	 */
	public boolean start();
}
