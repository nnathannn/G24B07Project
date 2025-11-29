package com.example.smartair;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TriageInputPEFRescueFragment extends Fragment {
    private FirebaseDatabase db;
    private String childID;
    private String triageID;
    private EditText inputPEF;
    private EditText inputRescue;
    private Button submit;

    public TriageInputPEFRescueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            childID = getArguments().getString("childID");
            triageID = getArguments().getString("triageID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_triage_input_p_e_f_rescue, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");

        inputRescue = view.findViewById(R.id.input_rescue_box);
        inputPEF = view.findViewById(R.id.input_pef_box);
        submit = view.findViewById(R.id.submit_rescue_pef);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update rescue and pef in triage
                String rescue = inputRescue.getText().toString();
                String curPEF = inputPEF.getText().toString();
                if (rescue.isEmpty()) {
                    Toast.makeText(getContext(), "Please input number of rescue trial.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(rescue) < 0) {
                    Toast.makeText(getContext(), "Number of rescue trial cannot be negative.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> pefList = new ArrayList<>();
                DatabaseReference triageRef = db.getReference("triage").child(triageID);
                if (!curPEF.isEmpty() && Double.parseDouble(curPEF) <= 0) {
                    Toast.makeText(getContext(), "PEF should be a positive number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                pefList.add(curPEF);
                triageRef.child("rescueList").setValue(Integer.parseInt(rescue));
                triageRef.child("pefList").setValue(pefList);

                if (!curPEF.isEmpty()) {
                    // input current PEF
                    PEF pef = new PEF(LocalDateTime.now().toString(), childID, Double.parseDouble(curPEF));
                    db.getReference("pef").push().setValue(pef).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                    // retrieve current PB of the child and input current zone
                    DatabaseReference pb = db.getReference("child-users").child(childID).child("PB");
                    pb.get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            double curPB = dataSnapshot.getValue(Double.class);
                            Zone zone = new Zone(LocalDateTime.now().toString(), childID, Double.parseDouble(curPEF), curPB);
                            DatabaseReference zoneRef = db.getReference("zone").push();
                            zoneRef.setValue(zone).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                            // input child-zones
                            DatabaseReference childZonesRef = db.getReference("child-zones").child(childID);
                            childZonesRef.child(zoneRef.getKey()).setValue(true).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                        else {
                            Toast.makeText(getContext(), "Failed to get current PB.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // navigate to decision card fragment
                TriageDecisionCardFragment fragment = new TriageDecisionCardFragment();
                Bundle args = new Bundle();
                args.putString("childID", childID);
                args.putString("triageID", triageID);
                fragment.setArguments(args);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerView, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}