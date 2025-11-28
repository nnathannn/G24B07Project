package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

public class TriageDecisionCardFragment extends Fragment {
    private Button emergencyCallButton;
    private Button homeStepsButton;

    public TriageDecisionCardFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_triage_decision_card, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // redirect to each page when choosing the button
        emergencyCallButton = view.findViewById(R.id.emergency_call_button);
        emergencyCallButton.setOnClickListener(v -> { loadFragment(new TriageEmergencyFragment()); });

        homeStepsButton = view.findViewById(R.id.home_steps_button);
        homeStepsButton.setOnClickListener(v -> { loadFragment(new TriageHomeStepsFragment()); });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}