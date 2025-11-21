package com.example.smartair;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Triage extends Item {
    private boolean emergency;
    private LocalDateTime endDate;
    private List<String> symptompList;
    private List<String> pefList;
    private List<String> rescueList;

    public Triage() {}

    public Triage(String id, LocalDateTime startDate, boolean emergency, String endDate,
                  List<String> symptompList, List<String> pefList, List<String> rescueList) {
        super(startDate, id);
        try {
            this.endDate = LocalDateTime.parse(endDate);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            this.endDate = null;
        }
        this.emergency = emergency;
        this.symptompList = symptompList;
        this.pefList = pefList;
        this.rescueList = rescueList;
    }

    public boolean getEmergency() { return emergency;}
    public LocalDateTime getEndDate() { return endDate;}
    public List<String> getSymptompList() { return symptompList;}
    public List<String> getPefList() { return pefList;}
    public List<String> getRescueList() { return rescueList;}

    public void setEmergency(boolean emergency) { this.emergency = emergency;}
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate;}
    public void setSymptompList(List<String> symptompList) { this.symptompList = symptompList;}
    public void setPefList(List<String> pefList) { this.pefList = pefList;}
    public void setRescueList(List<String> rescueList) { this.rescueList = rescueList;}

}
