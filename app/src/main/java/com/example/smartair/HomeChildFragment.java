package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HomeChildFragment extends Fragment {

    private LinearLayout badgeLayout;
    private String uid, controllerStreak, techniqueStreak;
    private TextView streakTitle, streakNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        uid = ((UIDProvider) requireActivity()).getUid();
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        badgeLayout = view.findViewById(R.id.badgeHolder);
        fetchBadge();

        streakTitle = view.findViewById(R.id.StreakTitle);
        streakNumber = view.findViewById(R.id.StreakNumber);
        ConstraintLayout streakLayout = view.findViewById(R.id.streakLayout);
        fetchStreak();

        Button buttonTriage = view.findViewById(R.id.buttonTriage);
        Button buttonMedicine = view.findViewById(R.id.buttonMedicine);
        Button buttonDaily = view.findViewById(R.id.buttonDaily);
        Button buttonTechnique = view.findViewById(R.id.buttonTechnique);
        Button buttonPEF = view.findViewById(R.id.buttonPEF);
        Button buttonProfile = view.findViewById(R.id.buttonProfile);

        buttonTriage.setOnClickListener(v -> { loadFragment(new TriageFragment()); });
        buttonMedicine.setOnClickListener(v -> { loadFragment(new MedicineFragment()); });
        buttonDaily.setOnClickListener(v -> { loadFragment(new SymptomFragment()); });
        buttonTechnique.setOnClickListener(v -> { loadFragment(new TechniqueFragment()); });
        buttonPEF.setOnClickListener(v -> { loadFragment(new PEFFragment()); });
        buttonProfile.setOnClickListener(v -> { loadFragment(new ProfileFragment()); });
        streakLayout.setOnClickListener(v -> {
            if (streakTitle.getText().toString().equals("Controller Streak")) {
                streakTitle.setText("Technique Streak");
                streakNumber.setText(techniqueStreak);
            } else {
                streakTitle.setText("Controller Streak");
                streakNumber.setText(controllerStreak);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fetchBadge() {
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("badge").child(uid);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String opacity1, opacity2, opacity3;
                    opacity1 = dataSnapshot.child("perfect-controller/completed").getValue(Boolean.class) ? "1.0" : "0.3";
                    opacity2 = dataSnapshot.child("high-quality/completed").getValue(Boolean.class) ? "1.0" : "0.3";;
                    opacity3 = dataSnapshot.child("low-rescue/completed").getValue(Boolean.class) ? "1.0" : "0.3";;
                    badgeLayout.getChildAt(0).setAlpha(Float.parseFloat(opacity1));
                    badgeLayout.getChildAt(1).setAlpha(Float.parseFloat(opacity2));
                    badgeLayout.getChildAt(2).setAlpha(Float.parseFloat(opacity3));
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
            }
        });
    }

    private void fetchStreak() {
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference();
        myref.child("medicineLogs").orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> dates = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.child("child-id").getValue(String.class).equals(uid) && !childSnapshot.child("rescue").getValue(Boolean.class)) {
                        String date = childSnapshot.child("date").getValue(String.class).split("T")[0];
                        if (!dates.contains(date)) {
                            dates.add(date);
                        }
                    }
                }
                Collections.reverse(dates);
                LocalDate startDate = LocalDate.now();
                int streak = 0;
                if (!dates.isEmpty()) {
                    if (dates.getFirst().equals(startDate.toString())) {
                        streak++;
                        dates.remove(0);
                    }
                    for (String date : dates) {
                        if (LocalDate.parse(date).equals(startDate.minusDays(1))) {
                            streak++;
                            startDate = LocalDate.parse(date);
                        } else {
                            controllerStreak = String.valueOf(streak);
                            streakNumber.setText(controllerStreak);
                            return;
                        }
                    }
                }
                controllerStreak = String.valueOf(streak);
                streakNumber.setText(controllerStreak);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        myref.child("technique").orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> dates = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.child("child-id").getValue(String.class).equals(uid)) {
                        String date = childSnapshot.child("date").getValue(String.class).split("T")[0];
                        if (!dates.contains(date)) {
                            dates.add(date);
                        }
                    }
                }
                Collections.reverse(dates);
                LocalDate startDate = LocalDate.now();
                int streak = 0;
                if (!dates.isEmpty()) {
                    if (dates.getFirst().equals(startDate.toString())) {
                        streak++;
                        dates.remove(0);
                    }
                    for (String date : dates) {
                        if (LocalDate.parse(date).equals(startDate.minusDays(1))) {
                            streak++;
                            startDate = LocalDate.parse(date);
                        } else {
                            techniqueStreak = String.valueOf(streak);
                            return;
                        }
                    }
                }
                techniqueStreak = String.valueOf(streak);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}