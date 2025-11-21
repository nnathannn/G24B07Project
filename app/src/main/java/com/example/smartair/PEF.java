package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class PEF extends Item {
    private double count;

    public PEF() {}

    // PEF needs to be a positive number
    public PEF(LocalDateTime date, double count) throws IllegalArgumentException {
        super(date);

        if (count <= 0) {
            throw new IllegalArgumentException("PEF should be a positive number");
        }
        this.count = count;
    }

    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    // PEF needs to be a positive number
    @RequiresApi(api = Build.VERSION_CODES.O)
    public PEF(String date, double count) throws DateTimeParseException, IllegalArgumentException {
        super(date);

        if (count <= 0) {
            throw new IllegalArgumentException("PEF should be a positive number");
        }
        this.count = count;
    }

    public double getPEF() { return count; }
    // do we need to return the PEF object?

    // PEF needs to be a positive number
    public void setPEF(LocalDateTime date, double count) throws IllegalArgumentException {
        super.setDate(date);

        if (count <= 0) {
            throw new IllegalArgumentException("PEF should be a positive number");
        }
        this.count = count;
    }

    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    // PEF needs to be a positive number
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setPEF(String date, double count) throws DateTimeParseException, IllegalArgumentException {
        super.setDate(date);

        if (count <= 0) {
            throw new IllegalArgumentException("PEF should be a positive number");
        }
        this.count = count;
    }
}
