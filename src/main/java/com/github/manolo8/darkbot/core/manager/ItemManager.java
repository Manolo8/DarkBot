package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.itf.Installable;
import com.github.manolo8.darkbot.core.objects.Item;
import com.github.manolo8.darkbot.core.objects.ItemCategory;
import com.github.manolo8.darkbot.core.objects.swf.SwfArray;
import com.github.manolo8.darkbot.core.objects.swf.SwfDictionary;

import static com.github.manolo8.darkbot.core.manager.Core.API;

/**
 * Using SWF objects:
 * CategoryBarVo:
 * - 32 String
 * - 40 String
 * - 48 Postion
 * - 56 Vector(CategoryVo)
 * CategoryVo:
 * - 32 String (name)
 * - 40 Vector[ItemVo]
 * ItemVo:
 * - 64 String (name)
 */
public class ItemManager
        implements Installable {

    private final SwfArray      itemCategories;
    private final SwfDictionary highlightCategories;

    private final SwfDictionary itemsCooldown;

    private final ItemCategory lasers;
    private final ItemCategory rockets;

    public Item selectedLaser;
    public Item selectedRocket;

    private long itemsControlAddress;

    public ItemManager() {

        this.itemCategories = new SwfArray(0);
        this.highlightCategories = new SwfDictionary(0);
        this.itemsCooldown = new SwfDictionary(0);

        this.lasers = new ItemCategory(itemsCooldown);
        this.rockets = new ItemCategory(itemsCooldown);

        register();
    }

    private void register() {
        highlightCategories.addLazy("standardSlotBar", this.itemsCooldown::update);
    }

    void update() {
        updateHighlightCategories();
        updateItemsCooldown();
        updateCategories();

        if (selectedLaser == null || !selectedLaser.selected)
            selectedLaser = lasers.getSelectedItem();

        if (selectedRocket == null || !selectedRocket.selected)
            selectedRocket = rockets.getSelectedItem();
    }

    @Override
    public void install(BotInstaller installer) {
        installer.itemsControlProxyAddress.subscribe(value -> this.itemsControlAddress = value);
        installer.highLightProxyAddress.subscribe(this::updateHighlightCategories);
    }

    private void updateCategories() {

        if (itemsControlAddress == 0) {
            lasers.update(0);
            rockets.update(0);
        } else {
            long temp = API.readMemoryLong(API.readMemoryLong(itemsControlAddress + 88) + 56);

            itemCategories.update(temp);
            itemCategories.update();

            if (itemCategories.size == 10) {
                lasers.update(itemCategories.elements[0]);
                rockets.update(itemCategories.elements[1]);
                lasers.update();
                rockets.update();
            }
        }
    }

    private void updateHighlightCategories(long value) {
        highlightCategories.update(API.readMemoryLong(value + 48) - 1);
    }

    private void updateHighlightCategories() {
        highlightCategories.update();
    }

    private void updateItemsCooldown() {
        itemsCooldown.update();
    }
}
