package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Inventory extends Item{
    private static final double pumpPercentage = 0.0075;
    private double amountLeft;  //percentage 0.0-1.0
    private String expiryDate;
    private boolean rescue;
    public Inventory() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Inventory(String childId, String purchaseDate, double amountLeft, String expiryDate, boolean rescue) throws DateTimeParseException, IllegalArgumentException {
        super(purchaseDate, childId);
        if (amountLeft < 0 || amountLeft > 1) {
            throw new IllegalArgumentException("Amount Left must be between 0.0 and 1.0");
        }


        this.amountLeft = amountLeft;
        this.expiryDate = expiryDate;
        this.rescue = rescue;

    }

    public double getAmountLeft() {return amountLeft;}
    public String getExpirydate() {return expiryDate;}
    public boolean getRescue() {return rescue;}

    public void setAmountLeft(double amountLeft) {
        if (amountLeft < 0 || amountLeft > 1) {
            throw new IllegalArgumentException(("Amount Left must be between 0.0 and 1.0"));
        }
        this.amountLeft = amountLeft;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setExpiryDate(String expiryDate) {

        this.expiryDate = expiryDate;
    }
    public void setRescue(boolean rescue) { this.rescue = rescue;}



    public void applyDose(double dose){
        if (dose < 0){
            throw new IllegalArgumentException("Dose cannot be negative");
        }
        double usedMedicine = dose * pumpPercentage;
        double amountNow = this.amountLeft - usedMedicine;

        if(amountNow < 0) amountNow = 0;
        this.amountLeft = amountNow;
    }

    public boolean isLowCanister(){
        return this.amountLeft <= 0.20;
    }
}
