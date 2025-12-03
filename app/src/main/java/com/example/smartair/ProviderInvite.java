package com.example.smartair;

import com.google.firebase.database.PropertyName;

public class ProviderInvite {
    @PropertyName("code")
    private String code;
    @PropertyName("child-id")
    private String childId;
    @PropertyName("child-name")
    private String childName;
    @PropertyName("end-date")
    private String endDate;

    public ProviderInvite() {}

    public ProviderInvite(String code, String childId, String childName, String endDate) {
        this.code = code;
        this.childId = childId;
        this.childName = childName;
        this.endDate = endDate;
    }
    @PropertyName("code")
    public String getCode() {
        return code;
    }
    @PropertyName("child-id")
    public String getChildId() {
        return childId;
    }
    @PropertyName("child-name")
    public String getChildName() {
        return childName;
    }
    @PropertyName("end-date")
    public String getEndDate() {
        return endDate;
    }

    @PropertyName("code")
    public void setCode(String code) {
        this.code = code;
    }
    @PropertyName("child-id")
    public void setChildId(String childId) {
        this.childId = childId;
    }
    @PropertyName("child-name")
    public void setChildName(String childName) {
        this.childName = childName;
    }
    @PropertyName("end-date")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
