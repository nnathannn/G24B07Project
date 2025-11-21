package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Inventory extends Item{
    private double amountLeft;
    private LocalDateTime expiryDate;g
    public Inventory() {}
    public Inventory(String id, LocalDateTime purchaseDate, double amountLeft, LocalDateTime expiryDate) throws IllegalArgumentException {
        super (purchaseDate, id);
    }


}
