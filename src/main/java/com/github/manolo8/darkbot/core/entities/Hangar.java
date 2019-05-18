package com.github.manolo8.darkbot.core.entities;

import java.util.ArrayList;

public class Hangar {

    private String hangarID;
    private boolean hangar_is_active;

    public Hangar(String hangarID, boolean hangar_is_active) {
        this.hangarID = hangarID;
        this.hangar_is_active = hangar_is_active;
    }

    public String getHangarID() {
        return hangarID;
    }

    public boolean isHangar_is_active() {
        return hangar_is_active;
    }

}
