package com.github.manolo8.darkbot.core.entities;

import java.util.ArrayList;

public class Hangar {

    private String hangarID;
    private boolean hangarIsActive;

    public Hangar(String hangarID, boolean hangarIsActive) {
        this.hangarID = hangarID;
        this.hangarIsActive = hangarIsActive;
    }

    public String getHangarID() {
        return hangarID;
    }

    public boolean isHangar_is_active() {
        return hangarIsActive;
    }

}
