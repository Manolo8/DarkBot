package com.github.manolo8.darkbot.core.objects.slotbars;

import com.github.manolo8.darkbot.core.objects.swf.ObjArray;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.github.manolo8.darkbot.Main.API;

public class CategoryBar extends MenuBar {
    public Map<Category, List<Item>> categories = new EnumMap<>(Category.class);

    private ObjArray categoriesArr = ObjArray.ofVector(true);
    private ObjArray itemsArr = ObjArray.ofVector(true);

    @Override
    public void update() {
        super.update();
        this.categoriesArr.update(API.readMemoryLong(address + 56));

        for (int i = 0; i < categoriesArr.getSize(); i++) {
            long address = categoriesArr.get(i);
            String categoryId = API.readMemoryString(address, 32);
            List<Item> items = categories.computeIfAbsent(Category.get(categoryId), c -> new ArrayList<>());

            this.itemsArr.update(API.readMemoryLong(address + 40));
            this.itemsArr.sync(items, Item::new, null);
        }
    }

    private enum Category {
        LASERS,
        ROCKETS,
        ROCKET_LAUNCHERS,
        SPECIAL_ITEMS,
        MINES,
        CPUS,
        BUY_NOW,
        TECH_ITEMS,
        SHIP_ABILITIES,
        DRONE_FORMATIONS,
        UNKNOWN;

        static Category get(String categoryId) {
            for (Category cat : Category.values())
                if (cat.name().toLowerCase().equals(categoryId)) return cat;
            return Category.UNKNOWN;
        }
    }
}