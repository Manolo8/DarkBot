package com.github.manolo8.darkbot.core.objects.slotbars;

import com.github.manolo8.darkbot.core.itf.UpdatableAuto;
import com.github.manolo8.darkbot.core.objects.Point;
import com.github.manolo8.darkbot.core.objects.swf.ObjArray;

import java.util.ArrayList;
import java.util.List;

import static com.github.manolo8.darkbot.Main.API;

public class SlotBar extends MenuBar {
    public boolean isVisible;
    public Point stickOffset = new Point();
    public List<Slot> slots = new ArrayList<>();

    private ObjArray slotsArr = ObjArray.ofVector(true);

    @Override
    public void update() {
        super.update();

        this.isVisible = API.readMemoryBoolean(address + 56);
        this.stickOffset.update(API.readMemoryLong(address + 72));
        this.stickOffset.update();

        this.slotsArr.update(API.readMemoryLong(address + 64));
        this.slotsArr.sync(slots, Slot::new, null);
    }

    public static class Slot extends UpdatableAuto {
        public int id;
        public boolean premium;
        public String slotBarId;
        public Item item = new Item();

        @Override
        public void update() {
            this.id = API.readMemoryInt(address + 32);
            this.premium = API.readMemoryBoolean(address + 36); //not sure is correct or nope
            this.slotBarId = API.readMemoryString(address, 48);
            this.item.update(API.readMemoryLong(address + 40));
        }
    }
}
