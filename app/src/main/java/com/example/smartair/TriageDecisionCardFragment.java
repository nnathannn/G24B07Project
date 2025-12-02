package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;

public class TriageDecisionCardFragment extends Fragment {
    private String triageID, childId;
    private AppCompatButton emergencyCallButton;
    private AppCompatButton homeStepsButton;

    public TriageDecisionCardFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_triage_decision_card, container, false);

        if (getArguments() != null) {
            triageID = getArguments().getString("triageID");
        }

        checkUser();

        // navigate to each page when choosing the button
        emergencyCallButton = view.findViewById(R.id.emergency_call_button);
        emergencyCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriageEmergencyFragment fragment = new TriageEmergencyFragment();
                Bundle args = new Bundle();
                args.putString("triageID", triageID);
                args.putString("childId", childId);
                fragment.setArguments(args);
                loadFragment(fragment);
            }
        });

        homeStepsButton = view.findViewById(R.id.home_steps_button);
        homeStepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriageHomeStepsFragment fragment = new TriageHomeStepsFragment();
                Bundle args = new Bundle();
                args.putString("triageID", triageID);
                args.putString("childId", childId);
                fragment.setArguments(args);
                loadFragment(fragment);
            }
        });

        return view;
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void checkUser() {
        if (getArguments() == null) {
            childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            childId = getArguments().getString("childId");
        }
    }
}