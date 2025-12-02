package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.PropertyName;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class MedicineLog extends Item {
    @PropertyName("prePostStatus")
    private String prePostStatus;
    @PropertyName("preStatus")
    private int preStatus;
    @PropertyName("postStatus")
    private int postStatus;
    @PropertyName("rescue")
    private boolean rescue;
    @PropertyName("dose")
    private int dose;

    @PropertyName("med-name")
    private String medName;

    public MedicineLog() {
    }

    public MedicineLog(String date, String id, String prePostStatus,int preStatus, int postStatus, boolean rescue, int dose, String medName) throws IllegalArgumentException {
        super(date, id);
        this.prePostStatus = prePostStatus;

        if (postStatus < 1 || postStatus > 5) {
            throw new IllegalArgumentException("Post-status must be between 1 and 5");
        }
        this.postStatus = postStatus;

        if (preStatus < 1 || preStatus > 5) {
            throw new IllegalArgumentException("Pre-status must be between 1 and 5");
        }
        this.preStatus = preStatus;
        this.rescue = rescue;
        if (dose <= 0) {
            throw new IllegalArgumentException("Dose must be a positive number");
        }
        this.dose = dose;
        this.medName = medName;
    }


    @PropertyName("prePostStatus")
    public String getPrePostStatus() {return prePostStatus;}
    @PropertyName("preStatus")
    public int getPreStatus() {
        return preStatus;
    }

    @PropertyName("postStatus")
    public int getPostStatus() {
        return postStatus;
    }

    @PropertyName("rescue")
    public boolean getRescue() {
        return rescue;
    }

    @PropertyName("dose")
    public int getDose() {
        return dose;
    }

    @PropertyName("med-name")
    public String getMedName() {
        return medName;
    }

    @PropertyName("prePostStatus")
    public void setPrePostStatus(String prePostStatus) {
        if (!prePostStatus.equals("Better") && !prePostStatus.equals("Same") && !prePostStatus.equals("Worse")) {
            throw new IllegalArgumentException("prePostStatus must be better, same, or worse");
        }
        this.prePostStatus = prePostStatus;
    }

    
    @PropertyName("preStatus")
    public void setPreStatus(int preStatus) {
        if (preStatus < 1 || preStatus > 5) {
            throw new IllegalArgumentException("Pre-status must be between 1 and 5");
        }
        this.preStatus = preStatus;
    }

    @PropertyName("postStatus")
    public void setPostStatus(int postStatus) {
        if (postStatus < 1 || postStatus > 5) {
            throw new IllegalArgumentException("Post-status must be between 1 and 5");
        }
        this.postStatus = postStatus;
    }

    @PropertyName("rescue")
    public void setRescue(boolean rescue) {
        this.rescue = rescue;
    }

    @PropertyName("dose")
    public void setDose(int dose) {
        if (dose <= 0) {
            throw new IllegalArgumentException(("Dose must be a positive number"));
        }
        this.dose = dose;
    }
    @PropertyName("med-name")
    public void setMedName (String medName){
        this.medName = medName;
        }

    }
