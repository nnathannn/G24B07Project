package com.example.smartair;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;

public class TriageEmergencyFragment extends Fragment {
    private FirebaseDatabase db;
    private String triageID;
    private AppCompatButton homeButton;

    public TriageEmergencyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_triage_emergency, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) triageID = getArguments().getString("triageID");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");

        DatabaseReference triageRef = db.getReference("triage").child(triageID);
        triageRef.child("emergency").setValue("Emergency");
        triageRef.child("endDate").setValue(LocalDateTime.now().toString());

        // call 911

        // back to home page
        homeButton = view.findViewById(R.id.home_button);
        homeButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView, new HomePageFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}