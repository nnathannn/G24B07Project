package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Item {
    private LocalDateTime date;

    public Item() {}

    public Item(LocalDateTime date) { this.date = date; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    public Item(String date) throws DateTimeParseException {
        try {
            this.date = LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            this.date = null;
        }
    }

    public LocalDateTime getDate() { return date; }

    // Returns the date as a string in the format "yyyy-MM-ddTHH:mm:ss"
    public String getDateString() { return date.toString(); }

    public void setDate(LocalDateTime date) { this.date = date; }

    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDate(String date) throws DateTimeParseException {
        try {
            this.date = LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            this.date = null;
        }
    }
}
