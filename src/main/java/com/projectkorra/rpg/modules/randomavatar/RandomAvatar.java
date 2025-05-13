package com.projectkorra.rpg.modules.randomavatar;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.randomavatar.commands.AvatarCommand;
import com.projectkorra.rpg.modules.randomavatar.listeners.AvatarListener;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import org.bukkit.Bukkit;

public class RandomAvatar extends Module {
    private AvatarManager avatarManager;

    public RandomAvatar() {
        super("RandomAvatar");
    }

    @Override
    public void enable() {
        this.avatarManager = new AvatarManager();

        new AvatarCommand();

        registerListeners(
                new AvatarListener()
        );

        avatarManager.refreshRecentPlayersAsync();
        avatarManager.createRPGTables();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ProjectKorraRPG.plugin, () -> {
            ProjectKorraRPG.plugin.getLogger().info("Avatar selection: Checking for new avatars.");
            avatarManager.checkAvatars();
        }, 0L, 20L * 30); // Every 30s (For Testing)

        avatarManager.checkAvatars();
    }

    @Override
    public void disable() {}

    public AvatarManager getAvatarManager() {
        return avatarManager;
    }

    public void setAvatarManager(AvatarManager avatarManager) {
        this.avatarManager = avatarManager;
    }
}
