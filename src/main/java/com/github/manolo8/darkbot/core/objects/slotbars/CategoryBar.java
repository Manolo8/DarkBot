package com.github.manolo8.darkbot.core.objects.slotbars;

import com.github.manolo8.darkbot.core.itf.UpdatableAuto;
import com.github.manolo8.darkbot.core.objects.swf.ObjArray;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.github.manolo8.darkbot.Main.API;

public class CategoryBar extends MenuBar {
    public Map<CategoryType, Category> categories = new EnumMap<>(CategoryType.class);

    private ObjArray categoriesArr = ObjArray.ofVector(true);

    @Override
    public void update() {
        super.update();
        this.categoriesArr.update(API.readMemoryLong(address + 56));

        for (int i = 0; i < categoriesArr.getSize(); i++) {
            categories.computeIfAbsent(CategoryType.get(i), Category::new).update(categoriesArr.getPtr(i));
        }
    }

    public static class Category extends UpdatableAuto {
        public String categoryId;
        public List<Item> items = new ArrayList<>();

        private ObjArray itemsArr = ObjArray.ofVector(true);

        public Category(CategoryType categoryType) { }

        @Override
        public void update() {
            this.categoryId = API.readMemoryString(address, 32);
            this.itemsArr.update(API.readMemoryLong(address + 40));
            this.itemsArr.sync(this.items, Item::new, null);
        }
    }

    private enum CategoryType {
        LASERS(),
        ROCKETS(),
        ROCKET_LAUNCHERS(),
        SPECIAL_ITEMS(),
        MINES(),
        CPUS(),
        BUY_NOW(),
        TECH_ITEMS(),
        SHIP_ABILITIES(),
        DRONE_FORMATIONS();

        static CategoryType get(int ndx) {
            return CategoryType.values()[ndx];
        }
    }
}