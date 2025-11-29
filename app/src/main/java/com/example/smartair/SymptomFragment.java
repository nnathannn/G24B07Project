package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SymptomFragment extends Fragment {

    private AutoCompleteTextView symptomDropdown;
    //private AutoCompleteTextView triggerDropdown;
    private RecyclerView recycler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 1) Inflate your fragment layout
        return inflater.inflate(R.layout.fragment_symptom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.recyclerViewSymptoms);
        List<Symptom> symptomList = new ArrayList<>();
        SymptomAdapter adapter = new SymptomAdapter(symptomList);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        //Fetch data

        // 2) Find the two dropdown views from the inflated layout
        symptomDropdown = view.findViewById(R.id.filledExposed);
        //triggerDropdown = view.findViewById(R.id.filledExposed2);

        // 3) Data for the first dropdown (Symptom type)
        String[] symptomTypes = new String[]{
                "Night waking",
                "Activity limits",
                "Cough",
                "Wheezing"
        };

        ArrayAdapter<String> symptomAdapter = new ArrayAdapter<>(
                requireContext(),                   // context
                R.layout.item_symptom_dropdown,     // row layout for each item
                symptomTypes                        // data
        );
        symptomDropdown.setAdapter(symptomAdapter);

        String[] triggers = new String[]{
                "Exercise",
                "Cold air",
                "Dust",
                "Pets",
                "Smoke",
                "Illness",
                "Strong odors"
        };

        ArrayAdapter<String> triggerAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.item_symptom_dropdown,
                triggers
        );
        //triggerDropdown.setAdapter(triggerAdapter);

        symptomDropdown.setOnItemClickListener((parent, v, position, id) -> {
            String selected = symptomDropdown.getText().toString();
            Toast.makeText(requireContext(),
                    "Symptom: " + selected,
                    Toast.LENGTH_SHORT).show();
        });

//        triggerDropdown.setOnItemClickListener((parent, v, position, id) -> {
//            String selected = triggerDropdown.getText().toString();
//            Toast.makeText(requireContext(),
//                    "Trigger: " + selected,
//                    Toast.LENGTH_SHORT).show();
//        });
    }


}
