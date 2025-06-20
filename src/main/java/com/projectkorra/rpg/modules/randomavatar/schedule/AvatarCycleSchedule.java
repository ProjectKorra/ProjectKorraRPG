package com.projectkorra.rpg.modules.randomavatar.schedule;

import com.projectkorra.rpg.ProjectKorraRPG;

public class AvatarCycleSchedule {
    private final ProjectKorraRPG plugin;

    public AvatarCycleSchedule(ProjectKorraRPG plugin) {
        this.plugin = plugin;

        this.startSchedule();
    }

    private void startSchedule() {
        // CAN BE DONE WITHOUT TASK TIMER, CHECK FOR DEATH EVENTS AND SIMILAR CONDITIONS
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                AvatarManager.checkAvatars();
//            }
//        }.runTaskTimerAsynchronously(plugin, 100, 100);
    }
}
