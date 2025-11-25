package com.example.smartair;

import com.google.firebase.database.PropertyName;

public class Technique extends Item {
    @PropertyName("high-quality")
    private boolean highQuality;

    public Technique() {
    }

    public Technique(String date, String childId, boolean highQuality) {
        super(date, childId);
        this.highQuality = highQuality;
    }

    @PropertyName("high-quality")
    public boolean getHighQuality() { return highQuality; }
    @PropertyName("high-quality")
    public void setHighQuality(boolean highQuality) { this.highQuality = highQuality;}

}