package com.projectkorra.rpg.modules.worldevents.models;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class AttributeRules {
    private final Map<String, Object> global; // attr -> value
    private final Map<String, Map<String, Object>> byElement; // element -> (attr -> value)
    private final Map<String, Map<String, Map<String, Object>>> byAbility; // element -> ability -> (attr -> value)

    public AttributeRules(Map<String, Object> global, Map<String, Map<String, Object>> byElement, Map<String, Map<String, Map<String, Object>>> byAbility) {
        this.global = Map.copyOf(Objects.requireNonNull(global));
        this.byElement = byElement.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> Map.copyOf(e.getValue())));
        this.byAbility = byAbility.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, ee -> Map.copyOf(ee.getValue())))));
    }

    public Object find(String element, String ability, String attribute) {
        // ability scope
        Map<String, Map<String, Object>> byElementMap = byAbility.get(element);
        if (byElementMap != null) {
            Map<String, Object> abilityMap = byElementMap.get(ability);
            if (abilityMap != null) {
                Object v = abilityMap.get(attribute);
                if (v != null) return v;
            }
        }

        // element scope
        Map<String, Object> elemAttrs = byElement.get(element);
        if (elemAttrs != null) {
            Object v = elemAttrs.get(attribute);
            if (v != null) return v;
        }

        // global
        return global.get(attribute);
    }
}