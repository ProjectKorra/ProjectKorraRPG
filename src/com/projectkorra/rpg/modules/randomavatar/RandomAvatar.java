package com.projectkorra.rpg.modules.randomavatar;

import com.projectkorra.rpg.commands.AvatarCommand;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;

public class RandomAvatar extends Module {
    private AvatarManager avatarManager;

    public RandomAvatar() {
        super("RandomAvatar");
    }

    @Override
    public void enable() {
        this.avatarManager = new AvatarManager();

        new AvatarCommand();
    }

    @Override
    public void disable() {

    }

    public AvatarManager getAvatarManager() {
        return avatarManager;
    }

    public void setAvatarManager(AvatarManager avatarManager) {
        this.avatarManager = avatarManager;
    }
}
