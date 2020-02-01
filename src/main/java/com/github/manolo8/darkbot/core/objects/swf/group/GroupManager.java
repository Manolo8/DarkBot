package com.github.manolo8.darkbot.core.objects.swf.group;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.objects.swf.Dictionary;

import java.util.ArrayList;
import java.util.List;

import static com.github.manolo8.darkbot.Main.API;

public class GroupManager extends Updatable {
    public Group group = new Group();
    public List<Invite> invites = new ArrayList<>();

    Dictionary dictionary = new Dictionary(0);

    @Override
    public void update() {
        group.update(API.readMemoryLong(address + 0x30));
        group.update();

        long dictionaryAddress = API.readMemoryLong(address + 0x48);
        if (dictionary.address != dictionaryAddress) dictionary.address = dictionaryAddress;
        dictionary.update();

        List<Invite> invites = new ArrayList<>(); // replace somehow?
        for (Dictionary.Entry entry : dictionary.elements) {
            if (entry != null) {
                Invite invite = new Invite();
                invite.update(entry.value);
                invites.add(invite);
            }
        }
        this.invites = invites;
        this.invites.forEach(Invite::update);
    }


    public static class Invite extends Updatable {
        GroupMember inviter = new GroupMember();
        GroupMember invited = new GroupMember();

        @Override
        public void update() {
            inviter.update(API.readMemoryLong(address + 0x20)); //only id and username is updated
            invited.update(API.readMemoryLong(address + 0x28)); //only id and username is updated
            inviter.update();
            invited.update();

            //probably better is just to change inviterId = int, inviterUsername = str etc.
        }
    }
}