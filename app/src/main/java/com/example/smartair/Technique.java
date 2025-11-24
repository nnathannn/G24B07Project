package com.example.smartair;

import com.google.firebase.database.PropertyName;

public class Technique extends Item {
    @PropertyName("high-quality")
    private boolean highQuality;
    private boolean[] feedbacks = new boolean[5];

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

    public void newSession() {
        for (int i = 0; i < feedbacks.length; i++) {
            feedbacks[i] = false;
        }
    }

    public void setFeedbackAtIndex(int index, boolean value) {
        feedbacks[index] = value;
    }

    public void checkSessionQuality() {
        boolean value = true;
        for (boolean feedback : feedbacks) {
            if (!feedback) {
                value = false;
                break;
            }
        }
        setHighQuality(value);
    }
}