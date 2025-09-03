package com.projectkorra.rpg.ui;

public record Slot(int x, int y) {
    public static final int COLUMNS = 9;
    public int index() {
        return y * COLUMNS + x;
    }

    public static Slot of(int x, int y) {
        return new Slot(x, y);
    }
}
