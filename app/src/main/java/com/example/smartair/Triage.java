package com.example.smartair;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Triage extends Item {
    private String emergency;
    private String endDate;
    private List<String> symptomList;
    private double pef;
    private int rescue;

    public Triage() {}

    public Triage(String id, String startDate, String emergency, String endDate,
                  List<String> symptomList, double pef, int rescue) throws IllegalArgumentException {
        super(startDate, id);
        this.endDate = endDate;
        this.emergency = emergency;
        this.symptomList = symptomList;
        if (pef <= 0) {
            throw new IllegalArgumentException("PEF must be a positive number.");
        }
        this.pef = pef;
        if (rescue < 0) {
            throw new IllegalArgumentException("Rescue must be a non-negative integer.");
        }
        this.rescue = rescue;
    }

    public String getEmergency() { return emergency;}
    public String getEndDate() { return endDate;}
    public List<String> getSymptomList() { return symptomList;}
    public double getPef() { return pef;}
    public int getRescue() { return rescue;}

    public void setEmergency(String emergency) { this.emergency = emergency;}
    public void setEndDate(String endDate) { this.endDate = endDate;}
    public void setSymptomList(List<String> symptomList) { this.symptomList = symptomList;}
    public void setPef(double pef) {
        if (pef <= 0) {
            throw new IllegalArgumentException("PEF must be a positive number.");
        }
        this.pef = pef;
    }
    public void setRescue(int rescue) throws IllegalArgumentException {
        if (rescue < 0) {
            throw new IllegalArgumentException("Rescue must be a non-negative integer.");
        }
        this.rescue = rescue;
    }
}

