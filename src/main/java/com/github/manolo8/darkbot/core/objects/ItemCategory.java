package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.objects.swf.SwfArray;
import com.github.manolo8.darkbot.core.objects.swf.SwfDictionary;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class ItemCategory
        extends Updatable {

    private static final Item NEVER_NULL = new Item("NOT_SELECTED");

    private final SwfDictionary cooldownDictionary;
    private final SwfArray      array;

    public Item[] items;

    public ItemCategory(SwfDictionary cooldownDictionary) {
        this.array = new SwfArray(0);
        this.items = new Item[0];
        this.cooldownDictionary = cooldownDictionary;
    }

    @Override
    public void update(long address) {
        super.update(address);

        if (address == 0)
            return;

        array.update(API.readMemoryLong(address + 40));
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        array.update();

        int size = array.size;

        if (size != items.length) {
            items = new Item[size];
            for (int i = 0; i < size; i++) {
                String name = API.readMemoryString(API.readMemoryLong(array.elements[i] + 64));
                Item   item = new Item(name);
                cooldownDictionary.addUniqueLazy(name, item::updateCooldownAddress);
                items[i] = item;
            }
        }

        for (int i = 0; i < size; i++) {
            Item item = items[i];
            item.update(array.elements[i]);
            item.update();
        }
    }

    public Item getSelectedItem() {
        for (Item item : items)
            if (item.selected)
                return item;

        return NEVER_NULL;
    }
}
