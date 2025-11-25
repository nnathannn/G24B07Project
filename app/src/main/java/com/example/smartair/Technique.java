package com.example.smartair;

public class Technique extends Item {
    private boolean highQuality;
    private boolean[] feedbacks = new boolean[5];

    public Technique() {
    }

    public Technique(String date, String childId, boolean highQuality) {
        super(date, childId);
        this.highQuality = highQuality;
    }

    public boolean getHighQuality() { return highQuality; }
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
        highQuality = value;
    }
}