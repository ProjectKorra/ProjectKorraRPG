package com.projectkorra.rpg.util;

import com.projectkorra.projectkorra.attribute.Attribute;

import java.lang.annotation.Annotation;

public class ParsedAttribute implements Attribute {

    private final String value;

    public ParsedAttribute(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Attribute.class;
    }

    @Override
    public String toString() {
        return "@" + Attribute.class.getName() + "(value=" + value + ")";
    }
}