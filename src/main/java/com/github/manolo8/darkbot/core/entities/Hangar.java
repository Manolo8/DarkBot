package com.github.manolo8.darkbot.core.entities;

import com.google.gson.annotations.SerializedName;

public class Hangar {

    @SerializedName("hangarID")
    private String hangarID;

    @SerializedName("hangar_is_active")
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
