package com.projectkorra.rpg.ui.builder;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.Slot;
import com.projectkorra.rpg.ui.impl.BasicInventoryUI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryUIBuilder {
    private static final int MIN_ROWS = 1;
    private static final int MAX_ROWS = 6;
    private static final int COLUMNS = Slot.COLUMNS; // 9

    private final Map<Slot, ItemStack> items = new HashMap<>();
    private final Map<Slot, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<>();

    private final int rows;
    private final String title;

    private InventoryUIBuilder(int rows, String title) {
        this.rows = rows;
        this.title = title;
    }

    public static InventoryUIBuilder create(int rows, String title) {
        if (rows < MIN_ROWS || rows > MAX_ROWS) {
            throw new IllegalArgumentException("rows must be between " + MIN_ROWS + "-" + MAX_ROWS + "!");
        }
        return new InventoryUIBuilder(rows, ChatUtil.color(title));
    }

    @SuppressWarnings("UnusedReturnValue")
    public InventoryUIBuilder withItem(int x, int y, ItemStack item) {
        validateXY(x, y);
        items.put(Slot.of(x, y), item);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public InventoryUIBuilder withButton(int x, int y, ItemStack item, Consumer<InventoryClickEvent> onClick) {
        withItem(x, y, item);
        clickHandlers.put(Slot.of(x, y), onClick);
        return this;
    }

    private void validateXY(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Slot out of bounds! x=" + x + " (0-" + (getWidth()-1) + "), y=" + y + " (0-" + (getHeight()-1) + ")");
        }
    }

    public InventoryUIBuilder fill(ItemStack filler) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                withItem(x, y, filler);
            }
        }
        return this;
    }

    public InventoryUIBuilder fillLeftRight(ItemStack border, ItemStack fill) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (x == 0 || x == 8) {
                    withItem(x, y, border);
                    continue;
                }
                withItem(x, y, fill);
            }
        }
        return this;
    }

    public InventoryUIBuilder fillBorder(ItemStack borderItem) {
        int w = getWidth();
        int h = getHeight();

        for (int x = 0; x < w; x++) {
            withItem(x, 0, borderItem);
            withItem(x, h-1, borderItem);
        }

        for (int y = 1; y < h; y++) {
            withItem(0, y, borderItem);
            withItem(w - 1, y, borderItem);
        }

        return this;
    }

    public int getWidth() {
        return COLUMNS;
    }

    public int getHeight() {
        return rows;
    }

    public InventoryUI build() {
        return new BasicInventoryUI(rows, title, items, clickHandlers);
    }
}
