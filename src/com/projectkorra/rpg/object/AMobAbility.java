package com.projectkorra.rpg.object;

import com.projectkorra.rpg.MobAbilityManager;
import com.projectkorra.rpg.ProjectKorraRPG;

public abstract class AMobAbility implements MobAbility{
	
	protected static int idCounter = 0;
	protected static MobAbilityManager manager = ProjectKorraRPG.getAbilityManager();
	
	private int id = 0;

	@Override
	public void remove() {
		manager.removeAbility(this);
		idCounter--;
	}
	
	@Override
	public int getID() {
		return id;
	}

	@Override
	public boolean start() {
		id = idCounter;
		idCounter++;
		manager.startAbility(this);
		return true;
	}

}
