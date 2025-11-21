package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Zone extends PEF {
    private String status;
    private double curPB;

    public Zone() {}

    public Zone (LocalDateTime date, double count, double curPB) throws IllegalArgumentException {
        super(date, count);
        this.curPB = curPB;
        this.status = (count >= 0.8 * curPB ? "Green" : (count >= 0.5 * curPB ? "Yellow" : "Red"));
    }
    // do we need to check PB if it is greater than 0? or it should be check from the input?

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Zone (String date, double count, double curPB) throws DateTimeParseException, IllegalArgumentException {
        super(date, count);
        this.curPB = curPB;
        this.status = (count >= 0.8 * curPB ? "Green" : (count >= 0.5 * curPB ? "Yellow" : "Red"));
    }

    public String getZone() { return status; }
    // do we need to return the Zone object?

    public void setZone(LocalDateTime date, double count, double curPB) throws IllegalArgumentException {
        super.setPEF(date, count);
        this.curPB = curPB;
        this.status = (count >= 0.8 * curPB ? "Green" : (count >= 0.5 * curPB ? "Yellow" : "Red"));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setZone(String date, double count, double curPB) throws DateTimeParseException, IllegalArgumentException {
        super.setPEF(date, count);
        this.curPB = curPB;
        this.status = (count >= 0.8 * curPB ? "Green" : (count >= 0.5 * curPB ? "Yellow" : "Red"));
    }
}
