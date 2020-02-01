package com.github.manolo8.darkbot.core.objects.swf.group;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.objects.swf.Array;

import java.util.ArrayList;
import java.util.List;

import static com.github.manolo8.darkbot.Main.API;

public class Group extends Updatable {
    public List<GroupMember> groupMembers = new ArrayList<>();
    public GroupMember clickedGroupMember = new GroupMember();

    public int groupId;
    public int groupSize;
    public int someValue;     // was 8, max group size?
    public boolean canInvite; // is invite button triggered or no

    private Array array = new Array(0);

    @Override
    public void update() { // API.readMemoryLong(API.readMemoryLong(API.readMemoryLong(MapManager.eventAddress) + 0x48) + 0x30) == this.address
        groupId   = API.readMemoryInt(address + 0x1F);
        groupSize = API.readMemoryInt(address + 0x23);
        someValue = API.readMemoryInt(address + 0x27);
        canInvite = API.readMemoryBoolean(address + 0x2B);

        clickedGroupMember.update(API.readMemoryLong(address + 0x3F));
        clickedGroupMember.update();

        array.update(API.readMemoryLong(address + 0x37));
        array.update();

        List<GroupMember> groupMembers = new ArrayList<>();
        for (int i = 0; i < array.elements.length; i++) {
            GroupMember groupMember = new GroupMember();
            groupMember.update(array.elements[i]);
            groupMembers.add(groupMember);
        }
        this.groupMembers = groupMembers;
        this.groupMembers.forEach(GroupMember::update);
    }
}
