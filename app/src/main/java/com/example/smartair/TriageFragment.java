package com.example.smartair;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class TriageFragment extends Fragment {
    private FirebaseDatabase db;
    private String childID;
    private TextView childName;
    private CheckBox flag1;
    private CheckBox flag2;
    private CheckBox flag3;
    private Button submitFlag;

    public TriageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_triage, container, false);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");

        childID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // set up child data
        childName = view.findViewById(R.id.child_name);
        fetchChildData();

        // check flags; input symptom, triage, child-symptoms, child-triages; navigate to right page
        flag1 = view.findViewById(R.id.flag1);
        flag2 = view.findViewById(R.id.flag2);
        flag3 = view.findViewById(R.id.flag3);
        submitFlag = view.findViewById(R.id.submit_triage);
        checkFlag();

        return view;
    }

    private void fetchChildData() {
        DatabaseReference ref = db.getReference("child-users").child(childID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null) {
                        childName.setText(name);
                    }
                    else {
                        Toast.makeText(getContext(), "Could not find child's data.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Could not find child's data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to get child's data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkFlag() {
        submitFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag1.isChecked() && !flag2.isChecked() && !flag3.isChecked()) {
                    Toast.makeText(getContext(), "Please select at least one flag.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // input triage and child-triages
                List<String> symptomList = new ArrayList<>();
                if (flag1.isChecked()) symptomList.add(flag1.getText().toString());
                if (flag2.isChecked()) symptomList.add(flag2.getText().toString());
                if (flag3.isChecked()) symptomList.add(flag3.getText().toString());

                DatabaseReference triageRef = db.getReference("triage");
                DatabaseReference childTriagesRef = db.getReference("child-triages").child(childID);
                Triage triage = new Triage(childID, LocalDateTime.now().toString(),
                        flag3.isChecked() ? "Emergency" : "",
                        "", symptomList, 1.0, 0);
                DatabaseReference triageRefPush = triageRef.push();
                triageRefPush.setValue(triage).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
                childTriagesRef.child(triageRefPush.getKey()).setValue(true).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

                // input symptom and child-symptoms
                DatabaseReference symptomRef = db.getReference("symptom");
                DatabaseReference childSymptomsRef = db.getReference("child-symptoms").child(childID);
                if (flag1.isChecked()) {
                    Symptom symptom = new Symptom(childID, LocalDateTime.now().toString(), false,
                            flag1.getText().toString(), triageRefPush.getKey(), null);
                    DatabaseReference symptomRefPush = symptomRef.push();
                    symptomRefPush.setValue(symptom).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    childSymptomsRef.child(symptomRefPush.getKey()).setValue(true).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
                if (flag2.isChecked()) {
                    Symptom symptom = new Symptom(childID, LocalDateTime.now().toString(), false,
                            flag2.getText().toString(), triageRefPush.getKey(), null);
                    DatabaseReference symptomRefPush = symptomRef.push();
                    symptomRefPush.setValue(symptom).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    childSymptomsRef.child(symptomRefPush.getKey()).setValue(true).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
                if (flag3.isChecked()) {
                    Symptom symptom = new Symptom(childID, LocalDateTime.now().toString(), false,
                            flag3.getText().toString(), triageRefPush.getKey(), null);
                    DatabaseReference symptomRefPush = symptomRef.push();
                    symptomRefPush.setValue(symptom).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    childSymptomsRef.child(symptomRefPush.getKey()).setValue(true).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                // navigate to another fragment
                if (flag3.isChecked()) {
                    TriageEmergencyFragment fragment = new TriageEmergencyFragment();
                    Bundle args = new Bundle();
                    args.putString("triageID", triageRefPush.getKey());
                    fragment.setArguments(args);
                    loadFragment(fragment);
                }
                else {
                    TriageInputPEFRescueFragment fragment = new TriageInputPEFRescueFragment();
                    Bundle args = new Bundle();
                    args.putString("triageID", triageRefPush.getKey());
                    fragment.setArguments(args);
                    loadFragment(fragment);
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}