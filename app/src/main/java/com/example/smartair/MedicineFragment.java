package com.example.smartair;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
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
    private EditText editDoseCount, editPreCheck, editPostCheck;
    private String medicineType, uid, prePostStatus;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        databaseReference = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/").getReference("medicineLogs");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dateTime = LocalDateTime.now();
        setupClickListeners();
        setupRecyclerView();
        fetchDataFromFirebase();
    }

    private void initializeViews(View view) {
        buttonSubmit = view.findViewById(R.id.buttonMedicineSubmit);
        buttonDate = view.findViewById(R.id.buttonDate);
        buttonTime = view.findViewById(R.id.buttonTime);
        buttonRescue = view.findViewById(R.id.buttonRescue);
        buttonController = view.findViewById(R.id.buttonController);
        buttonWorse = view.findViewById(R.id.buttonWorse);
        buttonSame = view.findViewById(R.id.buttonSame);
        buttonBetter = view.findViewById(R.id.buttonBetter);
        editDoseCount = view.findViewById(R.id.editDoseCount);
        editPreCheck = view.findViewById(R.id.editPreCheck);
        editPostCheck = view.findViewById(R.id.editPostCheck);
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
                    if (uid.equals(snapshot.child("child-id").getValue(String.class))) {
                        MedicineLog log = snapshot.getValue(MedicineLog.class);
                        if (log != null) {
                            medicineLogList.add(log);
                        }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
            buttonWorse.setStrokeColorResource(android.R.color.holo_blue_bright);
            // buttonWorse.setStrokeColorResource(R.color.red);
            buttonSame.setStrokeColorResource(R.color.pale_yellow);
            buttonBetter.setStrokeColorResource(R.color.green);
        });

        buttonSame.setOnClickListener(v -> {
            prePostStatus = "Same";
            buttonSame.setStrokeColorResource(android.R.color.holo_blue_bright);
            buttonWorse.setStrokeColorResource(R.color.red);
//            buttonSame.setStrokeColorResource(R.color.pale_yellow);
            buttonBetter.setStrokeColorResource(R.color.green);
        });

        buttonBetter.setOnClickListener(v -> {
            prePostStatus = "Better";
            buttonBetter.setStrokeColorResource(android.R.color.holo_blue_bright);
            buttonWorse.setStrokeColorResource(R.color.red);
            buttonSame.setStrokeColorResource(R.color.pale_yellow);
//            buttonBetter.setStrokeColorResource(R.color.green);
        });

        buttonDate.setOnClickListener(v -> showDatePicker());
        buttonTime.setOnClickListener(v -> showTimePicker());
        buttonSubmit.setOnClickListener(v -> submitMedicineLog());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void submitMedicineLog() {
        String doseCountStr = editDoseCount.getText().toString();
        String preCheckStr = editPreCheck.getText().toString();
        String postCheckStr = editPostCheck.getText().toString();

        if (medicineType == null) {
            Toast.makeText(getContext(), "Please select a medicine type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (prePostStatus == null) {
            Toast.makeText(getContext(), "Please select a pre-post status", Toast.LENGTH_SHORT).show();
            return;
        }
        if (doseCountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a dose count", Toast.LENGTH_SHORT).show();
            return;
        }
        if (preCheckStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a pre-check rating", Toast.LENGTH_SHORT).show();
            return;
        }
        if (postCheckStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a post-check rating", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(doseCountStr) <= 0) {
            Toast.makeText(getContext(), "Dose count must be a positive number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(preCheckStr) < 1 || Integer.parseInt(preCheckStr) > 5 || Integer.parseInt(postCheckStr) < 1 || Integer.parseInt(postCheckStr) > 5) {
            Toast.makeText(getContext(), "Rating must be between 1 and 5", Toast.LENGTH_SHORT).show();
            return;
        }

        int dose = Integer.parseInt(doseCountStr);
        int preCheck = Integer.parseInt(preCheckStr);
        int postCheck = Integer.parseInt(postCheckStr);
        boolean isRescue = "Rescue".equals(medicineType);


        MedicineLog log = new MedicineLog(dateTime.toString(), uid, prePostStatus, preCheck, postCheck, isRescue, dose, "");

        DatabaseReference mainRef = databaseReference.push();
        mainRef.setValue(log)
                .addOnSuccessListener(aVoid -> {
                    DatabaseReference childRef = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/")
                            .getReference("child-medicineLogs").child(uid).child(mainRef.getKey());
                    childRef.setValue(true)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), "Log saved successfully!", Toast.LENGTH_SHORT).show();
                                resetForm();
                                checkThreshold(isRescue);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to save log: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save log: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void resetForm() {
        medicineType = null;
        dateTime = LocalDateTime.now();

        editDoseCount.setText("");
        editPreCheck.setText("");
        editPostCheck.setText("");

        buttonDate.setText("Date");
        buttonTime.setText("Time");

        buttonRescue.setStrokeColorResource(android.R.color.white);
        buttonController.setStrokeColorResource(android.R.color.white);
        buttonWorse.setStrokeColorResource(R.color.red);
        buttonSame.setStrokeColorResource(R.color.pale_yellow);
        buttonBetter.setStrokeColorResource(R.color.green);
    }

    private void checkThreshold(Boolean isRescue) {
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("badge").child(uid);
        if (isRescue) {
            myref.child("low-rescue/threshold").get().addOnSuccessListener(dataSnapshot -> {
                int threshold = dataSnapshot.getValue(Integer.class);
                checkBadge(threshold, isRescue);
            });
        } else {
            myref.child("perfect-controller/threshold").get().addOnSuccessListener(dataSnapshot -> {
                int threshold = dataSnapshot.getValue(Integer.class);
                checkBadge(threshold, isRescue);
            });
        }
    }

    private void checkBadge(int threshold, Boolean isRescue) {
        String start;
        if (isRescue) {
            start = dateTime.minusDays(29).toString().split("T")[0];
        } else {
            start = dateTime.minusDays(threshold - 1).toString().split("T")[0];
        }
        databaseReference.orderByChild("date").startAt(start).endAt(dateTime.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                List<String> dates = new ArrayList<>();
                String date;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.child("rescue").getValue(Boolean.class) == isRescue) {
                        date = childSnapshot.child("date").getValue(String.class).split("T")[0];
                        if (!dates.contains(date)) {
                            dates.add(date);
                            count++;
                        } else if (isRescue) {
                            count++;
                        }
                    }
                }
                if (count >= threshold && !isRescue) {
                    FirebaseDatabase.getInstance().getReference("badge").child(uid).child("perfect-controller/completed").setValue(true);
                } else if (count <= threshold && isRescue) {
                    FirebaseDatabase.getInstance().getReference("badge").child(uid).child("low-rescue/completed").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
