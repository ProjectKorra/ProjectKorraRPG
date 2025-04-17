package com.projectkorra.rpg.util;

import com.projectkorra.projectkorra.attribute.Attribute;

import java.util.HashMap;
import java.util.Map;

public class AttributeParser {
    private static final Map<String, String> attributeMap = new HashMap<>();

    static {
        // Mapping normalized attribute names to the attribute constant strings.
        attributeMap.put("speed", Attribute.SPEED);
        attributeMap.put("range", Attribute.RANGE);
        attributeMap.put("selectrange", Attribute.SELECT_RANGE);
        attributeMap.put("damage", Attribute.DAMAGE);
        attributeMap.put("cooldown", Attribute.COOLDOWN);
        attributeMap.put("duration", Attribute.DURATION);
        attributeMap.put("radius", Attribute.RADIUS);
        attributeMap.put("chargetime", Attribute.CHARGE_DURATION);
        attributeMap.put("width", Attribute.WIDTH);
        attributeMap.put("height", Attribute.HEIGHT);
        attributeMap.put("knockback", Attribute.KNOCKBACK);
        attributeMap.put("knockup", Attribute.KNOCKUP);
        attributeMap.put("selfpush", Attribute.SELF_PUSH);
        attributeMap.put("fireticks", Attribute.FIRE_TICK);
        attributeMap.put("avatarstatetoggle", Attribute.AVATAR_STATE_TOGGLE);
    }

    /**
     * Parses a string into an Attribute instance (via ParsedAttribute).
     *
     * @param attributeStr The input from the config.
     * @return A ParsedAttribute with the corresponding constant, or null if none matches.
     */
    public static Attribute parseAttribute(String attributeStr) {
        if (attributeStr == null) {
            return null;
        }
        String key = attributeStr.trim().toLowerCase().replace(" ", "");
        String constant = attributeMap.get(key);
        if (constant != null) {
            return new ParsedAttribute(constant);
        }
        return null;
    }
}
