package com.projectkorra.rpg.modules.randomavatar;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;

public class RandomAvatar extends Module {
	public RandomAvatar() {
		super("RandomAvatar");
	}

	@Override
	public void enable() {
		ProjectKorraRPG.plugin.setAvatarManager(new AvatarManager());
	}

	@Override
	public void disable() {

	}
}
