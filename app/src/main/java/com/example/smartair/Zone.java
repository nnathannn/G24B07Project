package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Zone extends Item {
    private double count;
    private String status;
    private double curPB;

    public Zone() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Zone (String date, String id, double count, double curPB) throws DateTimeParseException, IllegalArgumentException {
        super(date, id);
        if (count <= 0) {
            throw new IllegalArgumentException("PEF should be a positive number");
        }
        this.count = count;
        this.curPB = curPB;
        this.status = (count >= 0.8 * curPB ? "Green" : (count >= 0.5 * curPB ? "Yellow" : "Red"));
    }

    public double getCount() { return count; }

    public String getStatus() { return status; }

    public Double getCurPB() { return curPB; }

    // PEF needs to be a positive number
    public void setCount(double count) {
        if (count <= 0) {
            throw new IllegalArgumentException("PEF should be a positive number");
        }
        this.count = count;
    }

    public void setStatus(double count, double curPB) throws IllegalArgumentException {
        this.count = count;
        this.curPB = curPB;
        this.status = (count >= 0.8 * curPB ? "Green" : (count >= 0.5 * curPB ? "Yellow" : "Red"));
    }
}
