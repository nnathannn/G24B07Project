package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.PropertyName;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class MedicineLog extends Item {
    @PropertyName("prePostStatus")
    private String prePostStatus;
    @PropertyName("rating")
    private double rating;
    @PropertyName("rescue")
    private boolean rescue;
    @PropertyName("dose")
    private int dose;

    public MedicineLog() {}

    public MedicineLog(String date, String id, String prePostStatus, double rating, boolean rescue, int dose) throws IllegalArgumentException {
        super(date, id);
        this.prePostStatus = prePostStatus;
        if (rating < 0 || rating >10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }
        this.rating = rating;
        this.rescue = rescue;
        if (dose <= 0) {
            throw new IllegalArgumentException("Dose must be a positive number");
        }
        this.dose = dose;
    }

    @PropertyName("prePostStatus")
    public String getPrePostStatus() {return prePostStatus;}
    @PropertyName("rating")
    public double getRating() {return rating;}
   @PropertyName("rescue")
    public boolean getRescue() {return rescue;}
    @PropertyName("dose")
    public int getDose() {return dose;}

    @PropertyName("prePostStatus")
    public void setPrePostStatus(String prePostStatus) {
        if (!prePostStatus.equals("Better") && !prePostStatus.equals("Same") && !prePostStatus.equals("Worse")) {
            throw new IllegalArgumentException("prePostStatus must be better, same, or worse");
        }
        this.prePostStatus = prePostStatus;
    }

    @PropertyName("rating")
    public void setRating(double rating) {
        if (rating<0 || rating>10) {
            throw new IllegalArgumentException("Rating must be between 0 and 10");
        }
        this.rating = rating;
    }
   @PropertyName("rescue")
    public void setRescue(boolean rescue) {this.rescue = rescue;}

    @PropertyName("dose")
    public void setDose(int dose){
        if (dose<= 0) {
            throw new IllegalArgumentException(("Dose must be a positive number"));
        }
        this.dose = dose;
    }

}
