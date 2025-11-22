package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Item {
    private String date;
    private String id;

    public Item() {}

    public Item(String date, String id) {
        this.date = date;
        this.id = id;
    }

    public String getDate() { return date; }

    public String getId() { return id; }

    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    public void setDate(String date) { this.date = date; }

    public void setId(String id) { this.id = id; }
}
