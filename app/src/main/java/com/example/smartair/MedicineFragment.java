package com.example.smartair;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MedicineFragment extends Fragment {

    private Button buttonSubmit, buttonDate, buttonTime;
    private MaterialButton buttonRescue, buttonController, buttonWorse, buttonSame, buttonBetter;
    private EditText editDoseCount, editShortBreathRating;
    private String medicineType, prePostStatus;
    private LocalDateTime dateTime;

    // RecyclerView components
    private RecyclerView recyclerViewMedicines;
    private MedicineAdapter medicineAdapter;
    private List<MedicineLog> medicineLogList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medicine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        databaseReference = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/").getReference("medicineLogs");
        dateTime = LocalDateTime.now();
        setupClickListeners();
        setupRecyclerView();
        fetchDataFromFirebase();
    }

    private void initializeViews(View view) {
        buttonSubmit = view.findViewById(R.id.buttonMedicineSubmit);
        buttonWorse = view.findViewById(R.id.buttonWorse);
        buttonSame = view.findViewById(R.id.buttonSame);
        buttonBetter = view.findViewById(R.id.buttonBetter);
        buttonDate = view.findViewById(R.id.buttonDate);
        buttonTime = view.findViewById(R.id.buttonTime);
        buttonRescue = view.findViewById(R.id.buttonRescue);
        buttonController = view.findViewById(R.id.buttonController);
        editDoseCount = view.findViewById(R.id.editDoseCount);
        editShortBreathRating = view.findViewById(R.id.editShortBreathRating);
        recyclerViewMedicines = view.findViewById(R.id.recyclerViewMedicines);
    }

    private void setupRecyclerView() {
        recyclerViewMedicines.setLayoutManager(new LinearLayoutManager(getContext()));
        medicineLogList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(medicineLogList);
        recyclerViewMedicines.setAdapter(medicineAdapter);
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicineLogList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MedicineLog log = snapshot.getValue(MedicineLog.class);
                    if (log != null) {
                        medicineLogList.add(log);
                    }
                }
                Collections.reverse(medicineLogList);
                medicineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load medicine logs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        buttonRescue.setOnClickListener(v -> {
            medicineType = "Rescue";
            buttonRescue.setStrokeColorResource(android.R.color.holo_blue_bright);
            buttonController.setStrokeColorResource(android.R.color.white);
        });

        buttonController.setOnClickListener(v -> {
            medicineType = "Controller";
            buttonController.setStrokeColorResource(android.R.color.holo_blue_bright);
            buttonRescue.setStrokeColorResource(android.R.color.white);
        });

        buttonWorse.setOnClickListener(v -> {
            prePostStatus = "Worse";
            int paleYellowColor = Color.parseColor("#F4C945");
            int greenColor = Color.parseColor("#31D219");
            buttonWorse.setStrokeColorResource(android.R.color.holo_blue_bright);
            buttonSame.setStrokeColor(ColorStateList.valueOf(paleYellowColor));
            buttonBetter.setStrokeColor(ColorStateList.valueOf(greenColor));
        });

        buttonSame.setOnClickListener(v -> {
            prePostStatus = "Same";
            int redColor = Color.parseColor("#EC3131");
            int greenColor = Color.parseColor("#31D219");
            buttonWorse.setStrokeColor(ColorStateList.valueOf(redColor));
            buttonSame.setStrokeColorResource(android.R.color.holo_blue_bright);
            buttonBetter.setStrokeColor(ColorStateList.valueOf(greenColor));
        });

        buttonBetter.setOnClickListener(v -> {
            prePostStatus = "Better";
            int redColor = Color.parseColor("#EC3131");
            int paleYellowColor = Color.parseColor("#F4C945");
            buttonWorse.setStrokeColor(ColorStateList.valueOf(redColor));
            buttonSame.setStrokeColor(ColorStateList.valueOf(paleYellowColor));
            buttonBetter.setStrokeColorResource(android.R.color.holo_blue_bright);
        });

        buttonDate.setOnClickListener(v -> showDatePicker());
        buttonTime.setOnClickListener(v -> showTimePicker());
        buttonSubmit.setOnClickListener(v -> submitMedicineLog());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                (view, year, month, dayOfMonth) -> {
                    dateTime = dateTime.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                    buttonDate.setText(dateTime.format(formatter));
                },
                dateTime.getYear(),
                dateTime.getMonthValue() - 1,
                dateTime.getDayOfMonth());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(Objects.requireNonNull(getContext()),
                (view, hourOfDay, minute) -> {
                    dateTime = dateTime.withHour(hourOfDay).withMinute(minute);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
                    buttonTime.setText(dateTime.format(formatter));
                },
                dateTime.getHour(),
                dateTime.getMinute(),
                false);
        timePickerDialog.show();
    }

    private void submitMedicineLog() {
        String doseCountStr = editDoseCount.getText().toString();
        String shortBreathCountStr = editShortBreathRating.getText().toString();

        if (medicineType == null) {
            Toast.makeText(getContext(), "Please select a medicine type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (doseCountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a dose count", Toast.LENGTH_SHORT).show();
            return;
        }
        if (prePostStatus == null) {
            Toast.makeText(getContext(), "Please select a pre-post status", Toast.LENGTH_SHORT).show();
            return;
        }
        if (shortBreathCountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a short breath rating", Toast.LENGTH_SHORT).show();
            return;
        }

        int dose = Integer.parseInt(doseCountStr);
        int rating = Integer.parseInt(shortBreathCountStr);
        boolean isRescue = "Rescue".equals(medicineType);

        String childId = "placeholder_child_id"; // TODO: Replace with actual child ID

        MedicineLog log = new MedicineLog(dateTime.toString(), childId, prePostStatus, rating, isRescue, dose);

        databaseReference.push().setValue(log)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Log saved successfully!", Toast.LENGTH_SHORT).show();
                    resetForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save log: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void resetForm() {
        medicineType = null;
        prePostStatus = null;
        dateTime = LocalDateTime.now();

        editDoseCount.setText("");
        editShortBreathRating.setText("");

        buttonDate.setText("Date");
        buttonTime.setText("Time");

        buttonRescue.setStrokeColorResource(android.R.color.white);
        buttonController.setStrokeColorResource(android.R.color.white);

        int redColor = Color.parseColor("#EC3131");
        int paleYellowColor = Color.parseColor("#F4C945");
        int greenColor = Color.parseColor("#31D219");
        buttonWorse.setStrokeColor(ColorStateList.valueOf(redColor));
        buttonSame.setStrokeColor(ColorStateList.valueOf(paleYellowColor));
        buttonBetter.setStrokeColor(ColorStateList.valueOf(greenColor));
    }
}
