package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Technique extends Item {
    private String feedback;
    private int individualCompletions;
    // use for badges:
    private static int totalCompletions;
    private static int perfectStreak;

    public Technique() {
    }

    public Technique(LocalDateTime date, String childId, String feedback) {
        super(date, childId);
        this.feedback = feedback;
        resetSession();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Technique(String date, String childId, String feedback) throws DateTimeParseException {
        super(date, childId);
        this.feedback = feedback;
        resetSession();
    }

    /*
    Requirements:
    - count number of techniques
        - if N=10, unlock badge
        - if streaks (5, 10, 20), unlock badge
    - check quality
    - print (and shows) prompts
        - seal lips
        - slow deep breath
        - hold ~10s
        - wait 30-60s between puffs if directed
        - spacer/mask tips
    - included at least one embedded video/animation
     */

    //
    public String getFeedback() { return feedback; }
    public int getIndividualCompletions() { return individualCompletions; }
    // Sessions:
    public void resetSession() { individualCompletions = 0; }
    public void addIndividualCompletions() { individualCompletions += 1; }

    // set feedback and badges
    public void setFeedback() {
        if (individualCompletions == 5) {
            feedback = "Well done! You have learned and practiced every technique!";
            totalCompletions += 1;
            perfectStreak += 1;
        }
        else if (individualCompletions == 3 || individualCompletions == 4) {
            feedback = "Good job! With a little bit more practice, you will be able to master every technique";
            totalCompletions += 1;
            perfectStreak = 0;
        }
        else if (individualCompletions >= 0 && individualCompletions <= 2) {
            feedback = "It's okay, you'll do better next time!";
            totalCompletions += 1;
            perfectStreak = 0;
        }
        resetSession();
    }

}