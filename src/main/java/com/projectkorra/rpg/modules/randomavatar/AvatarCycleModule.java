package com.projectkorra.rpg.modules.randomavatar;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.Module;
import com.projectkorra.rpg.modules.randomavatar.commands.AvatarCommand;
import com.projectkorra.rpg.modules.randomavatar.listeners.AvatarListener;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class AvatarCycleModule extends Module {
    private AvatarManager avatarManager;
    private AvatarListener avatarListener;

    public AvatarCycleModule() {
        super("RandomAvatar");
    }

    @Override
    public void enable() {
        this.avatarManager = new AvatarManager();
        this.avatarListener = new AvatarListener();

        new AvatarCommand(this.avatarManager);

        registerListeners(
                this.avatarListener
        );

        avatarManager.refreshRecentPlayersAsync();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ProjectKorraRPG.getPlugin(), () -> {
            ProjectKorraRPG.getPlugin().getLogger().info("Avatar selection: Checking for new avatars.");
            avatarManager.checkAvatars();
        }, 0L, 20L * 30); // Every 30s (For Testing)
    }

    @Override
    public void disable() {
        if (this.avatarListener != null) {
            HandlerList.unregisterAll(this.avatarListener);
            this.avatarListener = null;
        }
    }

    public AvatarManager getAvatarManager() {
        return avatarManager;
    }
}
