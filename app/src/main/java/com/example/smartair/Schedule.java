package com.example.smartair;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Schedule extends Item{
    //    Child child;
    private String scheduleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private final ArrayList<LocalDateTime> completionDates = new ArrayList<>();
    private int completionStreak;

    public Schedule() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule(LocalDateTime date, String childId, LocalDateTime startDate, LocalDateTime endDate, String scheduleId) {
        super(date, childId);
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
        this.scheduleId = scheduleId;
        resetSession();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule(String date, String childId, String startDate, String endDate, String scheduleId) throws DateTimeParseException {
        super(date, childId);
        try {
            LocalDateTime parsedStartDate = LocalDateTime.parse(startDate);
            LocalDateTime parsedEndDate = LocalDateTime.parse(endDate);
            if (parsedEndDate != null && parsedEndDate.isBefore(parsedStartDate)) {
                throw new IllegalArgumentException("End date cannot be before start date");
            }
            this.startDate = parsedStartDate;
            this.endDate = parsedEndDate;
            this.scheduleId = scheduleId;
            resetSession();
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }
    }

    public String getScheduleId() { return scheduleId; }
    public String getStartDateString() { return startDate.toString(); }
    public String getEndDateString() { return endDate.toString(); }
    public int getCompletionStreak() { return completionStreak; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void editSchedule(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Method for streaks
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addCompletionDate(LocalDateTime date) {
        if (!completionDates.contains(date)) {
            completionDates.add(date);
            // check if the previous day is in the array
            int dateIndex = completionDates.indexOf(date);
            if (dateIndex > 0) {
                LocalDateTime previousDate = completionDates.get(dateIndex - 1);
                if (previousDate.equals(date.minusDays(1))) {
                    completionStreak += 1;
                }
                else {
                    completionStreak = 0;
                }
            }
            else {
                completionStreak = 1;
            }
        }
    }

    public void resetSession() {
        completionDates.clear();
        completionStreak = 0;
    }

}