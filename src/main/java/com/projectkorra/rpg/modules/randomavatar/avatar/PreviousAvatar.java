package com.projectkorra.rpg.modules.randomavatar.avatar;

import com.projectkorra.projectkorra.Element;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PreviousAvatar extends Avatar {
    private final Instant endTime;
    private final String endReason;

    public PreviousAvatar(UUID uuid, Element mainElement, List<Element.SubElement> subElements, Instant chosenTime, Instant endTime, String endReason) {
        super(uuid, mainElement, subElements, chosenTime);
        this.endTime = endTime;
        this.endReason = endReason;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public String getEndReason() {
        return endReason;
    }
}
