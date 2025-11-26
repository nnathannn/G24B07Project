package com.example.smartair;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Schedule extends Item {
    String startDate;
    String endDate;

    public Schedule() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule(String date, String childId, String startDate, String endDate) {
        super(date, childId);
        if (LocalDateTime.parse(startDate).isAfter(LocalDateTime.parse(endDate))) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void editSchedule(String startDate, String endDate) {
        if (LocalDateTime.parse(startDate).isAfter(LocalDateTime.parse(endDate))) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public double adherenceCalculator() {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        long diff = ChronoUnit.DAYS.between(start, end);
        final int[] countControl = {0};
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
        DatabaseReference ref = db.getReference("medicine");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    MedicineLog medicine = child.getValue(MedicineLog.class);
                    if (medicine != null && !medicine.getRescue()) {
                        countControl[0]++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        return (double) countControl[0] / diff;
    }
}