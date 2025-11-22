package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class MedicineLog extends PEF {
    private String prePostStatus;
    private double rating;
    private boolean rescue;
    private int dose;

    public MedicineLog() {}

    public MedicineLog(LocalDateTime date, String id, double count, String prePostStatus, double rating, boolean rescue, int dose) throws IllegalArgumentException {
        super(date, id, count);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MedicineLog(String date, String id, double count, String prePostStatus, double rating, boolean rescue, int dose) throws DateTimeParseException, IllegalArgumentException {
        super(date, id, count);
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

    public String getPrePostStatus() {return prePostStatus;}
    public double getRating() {return rating;}
    public boolean getRescue() {return rescue;}
    public int getDose() {return dose;}

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
    public void setRescue(boolean rescue) {this.rescue = rescue;}

    public void setDose(int dose){
        if (dose<= 0) {
            throw new IllegalArgumentException(("Dose must be a positive number"));
        }
    }

}
