package com.example.smartair;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Triage extends Item {
    private boolean emergency;
    private String endDate;
    private List<String> symptomList;
    private List<String> pefList;
    private List<String> rescueList;

    public Triage() {}

    public Triage(String id, String startDate, boolean emergency, String endDate,
                  List<String> symptomList, List<String> pefList, List<String> rescueList) {
        super(startDate, id);
        this.endDate = endDate;
        this.emergency = emergency;
        this.symptomList = symptomList;
        this.pefList = pefList;
        this.rescueList = rescueList;
    }

    public boolean getEmergency() { return emergency;}
    public String getEndDate() { return endDate;}
    public List<String> getSymptomList() { return symptomList;}
    public List<String> getPefList() { return pefList;}
    public List<String> getRescueList() { return rescueList;}

    public void setEmergency(boolean emergency) { this.emergency = emergency;}
    public void setEndDate(String endDate) { this.endDate = endDate;}
    public void setSymptomList(List<String> symptomList) { this.symptomList = symptomList;}
    public void setPefList(List<String> pefList) { this.pefList = pefList;}
    public void setRescueList(List<String> rescueList) { this.rescueList = rescueList;}

}
