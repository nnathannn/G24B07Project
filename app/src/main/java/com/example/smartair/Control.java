package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Control extends PEF {
    private String prePostStatus;
    private double rating;

    public Control() {}

    public Control(LocalDateTime date, String id, double count, String prePostStatus, double rating) throws IllegalArgumentException {
        super(date, id, count);
        this.prePostStatus = prePostStatus;
        if (rating < 0 || rating >10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }
        this.rating = rating;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Control(String date, String id, double count, String prePostStatus, double rating) throws DateTimeParseException, IllegalArgumentException {
        super(date, id, count);
        this.prePostStatus = prePostStatus;
        if (rating < 0 || rating >10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }
        this.rating = rating;
    }

    public String getPrePostStatus() {return prePostStatus;}
    public double getRating() {return rating;}

    public void setPrePostStatus(String prePostStatus) {
        if (!prePostStatus.equals("Better") && !prePostStatus.equals("Same") && !prePostStatus.equals("Worse")) {
            throw new IllegalArgumentException("prePostStatus must be better, same, or worse");
        }
        this.prePostStatus = prePostStatus;
    }

    public void setRating(double rating) {
        if (rating<0 || rating>10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }
        this.rating = rating;
    }

}
