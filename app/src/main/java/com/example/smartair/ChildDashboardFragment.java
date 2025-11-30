package com.example.smartair;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.util.List;

public class ChildDashboardFragment extends Fragment {

    private SwitchMaterial switchDays;
    private String uid;
    private TextView textChildName, textChildData, textZonePercentage, textZoneData, timeLastRescue, countWeeklyRescue;
    private LocalDateTime currDate;
    private MaterialButton buttonDailyCheckIn, buttonLogin, buttonEdit, buttonDelete;
    private MaterialCardView cardZone;
    private List<MedicineLog> medicineLogs;

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
        fetchDataFromDatabase();
    }

    private void initializeView(View view) {
        textChildName = view.findViewById(R.id.textChildName);
        textChildData = view.findViewById(R.id.textChildData);
        textZonePercentage = view.findViewById(R.id.textZonePercentage);
        textZoneData = view.findViewById(R.id.textZoneData);
        timeLastRescue = view.findViewById(R.id.timeLastRescue);
        countWeeklyRescue = view.findViewById(R.id.countWeeklyRescue);
        switchDays = view.findViewById(R.id.switchDays);
        buttonDailyCheckIn = view.findViewById(R.id.buttonDailyCheckIn);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        cardZone = view.findViewById(R.id.cardZone);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchDataFromDatabase() {
        fetchChildData();
        fetchZoneData();
//        fetchRescueData();
    }

    private void fetchChildData() {
        DatabaseReference childReference = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/").getReference("child-users").child(uid);
        childReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String dob = snapshot.child("DOB").getValue(String.class);
                    String notes = snapshot.child("notes").getValue(String.class);

                    textChildName.setText(name);

                    int age = calculateAge(dob);
                    String childData = "Date of Birth: " + dob + " (" + age + " years old)\nNotes: " + notes;
                    textChildData.setText(childData);
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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer curPB = snapshot.child("curPB").getValue(Integer.class);
                Integer count = snapshot.child("count").getValue(Integer.class);
                String status = snapshot.child("status").getValue(String.class);
                String dateStr = snapshot.child("date").getValue(String.class);

                if(curPB == null || count == null || status == null || dateStr == null) return;

                LocalDateTime date = LocalDateTime.parse(dateStr);
                if (date.getDayOfYear() == currDate.getDayOfYear() && date.getYear() == currDate.getYear()) {
                    textZoneData.setText("PB: " + curPB + "\nPEF: " + count);
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

//    private void fetchRescueData() {
//        DatabaseReference medicineReference = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/").getReference("child-medicineLogs").child(uid);
//        medicineReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                medicineLogs.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    MedicineLog medicineLog = dataSnapshot.getValue(MedicineLog.class);
//                    if (medicineLog != null) {
//                        medicineLogs.add(medicineLog);
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }

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

}
