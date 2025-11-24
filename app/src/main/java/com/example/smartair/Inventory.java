package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.PropertyName;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Inventory extends Item{
    private static final double pumpPercentage = 0.0075;
    private double amountLeft;  //percentage 0.0-1.0
    private String expiryDate;
    private String purchaseDate;
    private boolean rescue;
    private String medName;
    public Inventory() {}

    public Inventory(String childId, String date, String purchaseDate, double amountLeft, String expiryDate, boolean rescue, String medName) throws IllegalArgumentException {
        super(date, childId);
        if (amountLeft < 0 || amountLeft > 1) {
            throw new IllegalArgumentException("Amount Left must be between 0.0 and 1.0");
        }

        this.purchaseDate = purchaseDate;
        this.amountLeft = amountLeft;
        this.expiryDate = expiryDate;
        this.rescue = rescue;
        this.medName = medName;

    }


    @PropertyName("purchase-date")
    public String getPurchaseDate() {return purchaseDate;}
    @PropertyName("amount-left")
    public double getAmountLeft() {return amountLeft;}
    @PropertyName("expiry-date")
    public String getExpiryDate() {return expiryDate;}
    @PropertyName("rescue")
    public boolean getRescue() {return rescue;}
    @PropertyName("med-name")
    public String getMedName() {return medName;}

    @PropertyName("amount-left")
    public void setAmountLeft(double amountLeft) {
        if (amountLeft < 0 || amountLeft > 1) {
            throw new IllegalArgumentException(("Amount Left must be between 0.0 and 1.0"));
        }
        this.amountLeft = amountLeft;
    }

    @PropertyName("expiry-date")
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    @PropertyName("rescue")
    public void setRescue(boolean rescue) { this.rescue = rescue;}

    @PropertyName("med-name")
    public void setMedName(String medName){
        this.medName = medName;
    }


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
