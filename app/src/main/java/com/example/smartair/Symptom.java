package com.example.smartair;

import java.time.LocalDateTime;
import java.util.List;

public class Symptom extends Item {
    private boolean parent;
    private String name;
    private String triageId;
    private List<String> triggerList;

    public Symptom() {}

    public Symptom(String id, String startDate, boolean parent, String name,
                   String triageId, List<String> triggerList) {
        super(startDate, id);

        this.parent = parent;
        this.name = name;
        this.triageId = triageId;
        this.triggerList = triggerList;
    }

    public boolean getParent() { return parent;}
    public String getName() { return name;}
    public String getTriageId() { return triageId;}
    public List<String> getTriggerList() { return triggerList;}

    public void setParent(boolean parent) { this.parent = parent;}
    public void setName(String name) { this.name = name;}
    public void setTriageId(String triageId) { this.triageId = triageId;}
    public void setTriggerList(List<String> triggerList) { this.triggerList = triggerList;}

}
