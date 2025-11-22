package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Zone extends PEF {
    private String status;
    private double curPB;

    public Zone() {}

    public Zone (String date, String id, double count, double curPB) throws IllegalArgumentException {
        super(date, id, count);
        this.curPB = curPB;
        this.status = (count >= 0.8 * curPB ? "Green" : (count >= 0.5 * curPB ? "Yellow" : "Red"));
    }

    public String getStatus() { return status; }

    public Double getCurPB() { return curPB; }

    public void setStatus(double count, double curPB) throws IllegalArgumentException {
        super.setCount(count);
        this.curPB = curPB;
        this.status = (count >= 0.8 * curPB ? "Green" : (count >= 0.5 * curPB ? "Yellow" : "Red"));
    }
}
