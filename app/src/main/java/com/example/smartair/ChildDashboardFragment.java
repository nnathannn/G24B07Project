package com.example.smartair;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildDashboardFragment extends Fragment {

    private SwitchMaterial switchDays;
    private String uid; // child Id
    private TextView textChildName, textChildDOB, textChildNotes, textZonePercentage, textZonePB, textZonePEF, timeLastRescue, countWeeklyRescue;
    private LocalDateTime currDate;
    private MaterialButton buttonDailyCheckIn, buttonLogin, buttonSettings, buttonDelete;
    private MaterialCardView cardZone;
    private List<MedicineLog> medicineLogs;
    boolean is30Days = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_dashboard, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }

        if (uid == null || uid.isEmpty()) {
            Toast.makeText(getContext(), "Child ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        currDate = LocalDateTime.now();
        medicineLogs = new ArrayList<>();
        initializeView(view);
        setupClickListeners();
        fetchDataFromDatabase();
        loadTrendChart(is30Days);
    }


    private void initializeView(View view) {
        textChildName = view.findViewById(R.id.textChildName);
        textChildDOB = view.findViewById(R.id.textChildDOB);
        textChildNotes = view.findViewById(R.id.textChildNotes);
        textZonePercentage = view.findViewById(R.id.textZonePercentage);
        textZonePB = view.findViewById(R.id.textZonePB);
        textZonePEF = view.findViewById(R.id.textZonePEF);
        timeLastRescue = view.findViewById(R.id.timeLastRescue);
        countWeeklyRescue = view.findViewById(R.id.countWeeklyRescue);
        switchDays = view.findViewById(R.id.switchDays);
        buttonDailyCheckIn = view.findViewById(R.id.buttonDailyCheckIn);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonSettings = view.findViewById(R.id.buttonSettings);
        cardZone = view.findViewById(R.id.cardZone);
        buttonDelete = view.findViewById(R.id.buttonDelete);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchDataFromDatabase() {
        fetchChildData();
        fetchZoneData();
        fetchRescueData();
    }

    private void fetchChildData() {
        DatabaseReference childReference = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/").getReference("child-users").child(uid);
        childReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String dob = snapshot.child("DOB").getValue(String.class);
                    String notes = snapshot.child("notes").getValue(String.class);
                    Integer PB = snapshot.child("PB").getValue(Integer.class);

                    textChildName.setText(name);

                    int age = calculateAge(dob);
                    textChildDOB.setText("Date of Birth: " + dob + " (" + age + " years old)");
                    textChildNotes.setText("Notes: " + notes);
                    textZonePB.setText("PB: " + PB);
                } else {
                    Toast.makeText(getContext(), "Child data not found.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchZoneData() {
        DatabaseReference childZoneReference = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/").getReference("child-zones").child(uid);
        childZoneReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot newest = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    newest = dataSnapshot;
                }
                if (newest != null) {
                    String zoneKey = newest.getKey();
                    setZoneValues(zoneKey);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setZoneValues(String zoneKey) {
        if (zoneKey == null) { return; }
        DatabaseReference zoneReference = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/").getReference("zone").child(zoneKey);
        zoneReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer curPB = snapshot.child("curPB").getValue(Integer.class);
                Integer count = snapshot.child("count").getValue(Integer.class);
                String status = snapshot.child("status").getValue(String.class);
                String dateStr = snapshot.child("date").getValue(String.class);

                if(count == null || status == null || dateStr == null) return;

                LocalDateTime date = LocalDateTime.parse(dateStr);
                if (date.getDayOfYear() == currDate.getDayOfYear() && date.getYear() == currDate.getYear()) {
                    textZonePEF.setText("PEF: " + count);
                    int percentage = (int) ((count * 100) / curPB);
                    textZonePercentage.setText(percentage + "%");
                    if (status.equals("Green")) {
                        cardZone.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#31D219")));
                    } else if (status.equals("Yellow")) {
                        cardZone.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F4C945")));
                    } else {
                        cardZone.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EC3131")));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchRescueData() {
        DatabaseReference medicineReference = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/").getReference("child-medicineLogs").child(uid);
        medicineReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medicineLogs.clear();
                List<String> logKeys = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    logKeys.add(key);
                }
                setRescueValues(logKeys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setRescueValues(List<String> logKeys) {
        DatabaseReference medicineRef = FirebaseDatabase.getInstance().getReference("medicineLogs");
        medicineLogs.clear();
        for (String key : logKeys) {
            medicineRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    MedicineLog log = snapshot.getValue(MedicineLog.class);
                    if (log == null) {
                        return;
                    }
                    medicineLogs.add(log);
                    if (medicineLogs.size() == logKeys.size()) {
                        processRescueLogs(medicineLogs);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processRescueLogs(List<MedicineLog> medicineLogs) {
        LocalDateTime lastDate = null;
        LocalDateTime startOfLastWeek = currDate.minusDays(currDate.getDayOfWeek().getValue() - 1).minusWeeks(1);
        int rescueCount = 0;
        for (MedicineLog medicineLog : medicineLogs) {
            String dateStr = medicineLog.getDate().substring(0, 16);
            LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            if (date.isAfter(startOfLastWeek)) {
                rescueCount++;
                if (lastDate == null || date.isAfter(lastDate)) {
                    lastDate = date;
                }
            }
        }
        if (lastDate != null) {
            String formattedDate = lastDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy'\n'HH:mm"));
            timeLastRescue.setText(formattedDate);
        } else {
            timeLastRescue.setText("N/A");
        }
        countWeeklyRescue.setText(String.valueOf(rescueCount));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int calculateAge(String dob) {
        if (dob == null || dob.isEmpty()) {
            return 0;
        }
        try {
            LocalDate birthDate = LocalDate.parse(dob);
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    private void loadTrendChart(boolean is30Days) {
        ArrayList<String> labels = createDateLabels(is30Days);
        ArrayList<Float> values = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            values.add(0f);
        }
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("child-medicineLogs").child(uid);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showChart(labels, values);
                    return;
                }
                List<String> medicineKeys = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    medicineKeys.add(dataSnapshot.getKey());
                }
                if (medicineKeys.isEmpty()) {
                    showChart(labels, values);
                    return;
                }
                DatabaseReference medicineRef = FirebaseDatabase.getInstance().getReference("medicineLogs");
                final int [] count = {0};
                for (String key : medicineKeys) {
                    Log.d("debug", "key: " + key);
                    medicineRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            Boolean rescue = snapshot2.child("rescue").getValue(Boolean.class);
                            Log.d("debug", "rescue: " + rescue);
                            String dateStr = snapshot2.child("date").getValue(String.class);
                            Log.d("debug", "date: " + dateStr);
                            if (rescue != null && dateStr != null) {
                                LocalDate logDate;
                                try {
                                    logDate = LocalDate.parse(dateStr.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                    Log.d("debug", logDate.toString());
                                    for (int i = 0; i < labels.size(); i++) {
                                        LocalDate labelDate = LocalDate.now().minusDays(labels.size() - i - 1);
                                        if (logDate.isEqual(labelDate)) {
                                            values.set(i, values.get(i) + 1f);
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.d("debug", "Error parsing date: " + e.getMessage());
                                    return;
                                }
                            }
                            count[0]++;
                            if (count[0] == medicineKeys.size()) {
                                showChart(labels, values);
                                }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void showChart(ArrayList<String> labels, ArrayList<Float> values) {
        LineChartFragment fragment = LineChartFragment.newInstance("Rescue Medicine Use per Day", labels, values);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.containerLineChart, fragment)
                .commitAllowingStateLoss();
    }

    private ArrayList<String> createDateLabels(boolean is30Days) {
        ArrayList<String> labels = new ArrayList<>();
        int days = is30Days ? 30 : 7;
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            labels.add(date.format(DateTimeFormatter.ofPattern("dd-MM")));
        }
        return labels;
    }

    private void setupClickListeners() {
        buttonDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this child?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String parent_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference childRef = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/")
                                .getReference("parent-users")
                                .child(parent_id)
                                .child("child-ids")
                                .child(uid);
                        childRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Child deleted successfully.", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to delete child: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        buttonSettings.setOnClickListener(v -> {
            View editView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_child, null);
            EditText editPB = editView.findViewById(R.id.editPB);
            EditText editNotes = editView.findViewById(R.id.editNotes);
            EditText editStreak = editView.findViewById(R.id.editStreak);
            EditText editSessions = editView.findViewById(R.id.editSessions);
            EditText editRescueDays = editView.findViewById(R.id.editRescueDays);
            DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("child-users").child(uid);
            DatabaseReference badgesRef = FirebaseDatabase.getInstance().getReference("badge").child(uid);
            new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                    .setTitle("Edit Child Information")
                    .setView(editView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newPB = editPB.getText().toString();
                        String newNotes = editNotes.getText().toString();
                        String newStreak = editStreak.getText().toString();
                        String newSessions = editSessions.getText().toString();
                        String newRescueDays = editRescueDays.getText().toString();
                        Map<String, Object> childUpdates = new HashMap<>();
                        Map<String, Object> streakUpdates = new HashMap<>();
                        Map<String, Object> sessionsUpdates = new HashMap<>();
                        Map<String, Object> rescueDaysUpdates = new HashMap<>();
                        if (!newPB.isEmpty()) {
                            try {
                                int newPBValue = Integer.parseInt(newPB);
                                childUpdates.put("PB", newPBValue);
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Invalid PB value.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!newNotes.isEmpty()) {
                            childUpdates.put("notes", newNotes);
                            textChildNotes.setText("Notes: " + newNotes);
                        }
                        if (!newStreak.isEmpty()) {
                            try {
                                int newStreakValue = Integer.parseInt(newStreak);
                                streakUpdates.put("threshold", newStreakValue);
                                badgesRef.child("perfect-controller").updateChildren(streakUpdates);
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Invalid Streak value.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!newSessions.isEmpty()) {
                            try {
                                int newSessionsValue = Integer.parseInt(newSessions);
                                sessionsUpdates.put("threshold", newSessionsValue);
                                badgesRef.child("high-quality").updateChildren(sessionsUpdates);
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Invalid Sessions value.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!newRescueDays.isEmpty()) {
                            try {
                                int newRescueDaysValue = Integer.parseInt(newRescueDays);
                                rescueDaysUpdates.put("threshold", newRescueDaysValue);
                                badgesRef.child("low-rescue").updateChildren(rescueDaysUpdates);
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Invalid Rescue Days value.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!childUpdates.isEmpty()) {
                            childRef.updateChildren(childUpdates)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Child information updated successfully.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to update child information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                        else if (!streakUpdates.isEmpty() || !sessionsUpdates.isEmpty() || !rescueDaysUpdates.isEmpty()) {
                            Toast.makeText(getContext(), "Child badge thresholds updated successfully.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "No changes made.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        buttonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChildActivity.class);
            intent.putExtra("childId", uid);
            startActivity(intent);
        });
        buttonDailyCheckIn.setOnClickListener(v -> {
            boolean isParent = true;

            SymptomFragment fragment = new SymptomFragment();

            Bundle args = new Bundle();
            args.putString("uid", uid);
            args.putBoolean("isParent", isParent);
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.parent_frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        switchDays.setOnCheckedChangeListener((buttonView, isChecked) -> {
            is30Days = isChecked;
            loadTrendChart(is30Days);
        });
    }
}
