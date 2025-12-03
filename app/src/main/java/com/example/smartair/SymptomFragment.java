package com.example.smartair;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SymptomFragment extends Fragment {

    private AutoCompleteTextView symptomDropdown;
    private MaterialAutoCompleteTextView triggerDropdown;
    private RecyclerView recyclerViewSymptoms;
    private Button buttonDate, buttonTime, buttonSubmit;

    private DatabaseReference databaseReference;
    private String childId;

    private List<Symptom> symptomLogList;
    private SymptomAdapter symptomAdapter;

    private final String[] symptomTypes = new String[]{
            "Night waking",
            "Activity limits",
            "Cough",
            "Wheezing",
            "Can't speak full sentences",
            "Chest pulling in",
            "Blue/gray lips/nails"
    };

    private final String[] triggers = new String[]{
            "Exercise",
            "Cold air",
            "Dust",
            "Pets",
            "Smoke",
            "Illness",
            "Strong odors"
    };

    private boolean[] triggerSelected;
    private LocalDateTime dateTime;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_symptom, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        databaseReference = FirebaseDatabase
                .getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/")
                .getReference("symptom");

        activity = getActivity();

        if (activity instanceof HomeParent) {
            childId = getArguments().getString("child-id");
        } else {
            childId = ((UIDProvider) getActivity()).getUid();
        }

        dateTime = LocalDateTime.now();

        setupRecyclerView();
        setupClickListeners();
        fetchDataFromFirebase();
    }





    private void initializeViews(View view) {
        recyclerViewSymptoms = view.findViewById(R.id.recyclerViewSymptoms);

        symptomDropdown = view.findViewById(R.id.filledExposed);
        triggerDropdown = view.findViewById(R.id.filledExposed2);

        buttonDate = view.findViewById(R.id.buttonDate);
        buttonTime = view.findViewById(R.id.buttonTime);
        buttonSubmit = view.findViewById(R.id.buttonSymptomSubmit);
    }



    private void setupRecyclerView() {
        recyclerViewSymptoms.setLayoutManager(new LinearLayoutManager(getContext()));
        symptomLogList = new ArrayList<>();
        symptomAdapter = new SymptomAdapter(symptomLogList);
        recyclerViewSymptoms.setAdapter(symptomAdapter);
    }



    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                symptomLogList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Object childIdObj = snapshot.child("child-id").getValue();
                        if (childIdObj == null) {
                            continue;
                        }
                        String childIdStr = String.valueOf(childIdObj);

                        if (!childIdStr.equals(childId)) {
                            continue;
                        }

                        Symptom log = snapshot.getValue(Symptom.class);
                        if (log == null) {
                            continue;
                        }

                        if (log.getTriggerList() == null) {
                            log.setTriggerList(Arrays.asList("No triggers logged"));
                        }

                        symptomLogList.add(log);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Collections.reverse(symptomLogList);
                symptomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),
                        "Failed to load symptom logs.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupClickListeners() {
        ArrayAdapter<String> symptomAdapterDrop = new ArrayAdapter<>(
                requireContext(),
                R.layout.item_symptom_dropdown,
                symptomTypes
        );
        symptomDropdown.setAdapter(symptomAdapterDrop);

        symptomDropdown.setOnItemClickListener((parent, v, position, id) -> {
            String selected = symptomDropdown.getText().toString();
            Toast.makeText(requireContext(),
                    "Symptom: " + selected,
                    Toast.LENGTH_SHORT).show();
        });

        setupTriggerDropdown();


        buttonDate.setOnClickListener(v -> showDatePicker());
        buttonTime.setOnClickListener(v -> showTimePicker());


        buttonSubmit.setOnClickListener(v -> submitSymptomLog());
    }


    private void setupTriggerDropdown() {
        triggerSelected = new boolean[triggers.length];

        final ArrayAdapter<String> triggerAdapter = new ArrayAdapter<String>(
                requireContext(),
                R.layout.item_trigger_dropdown,
                R.id.text,
                triggers
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View row = convertView;
                if (row == null) {
                    row = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_trigger_dropdown, parent, false);
                }

                TextView text = row.findViewById(R.id.text);
                CheckBox checkBox = row.findViewById(R.id.checkBox);

                text.setText(triggers[position]);

                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(triggerSelected[position]);

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    triggerSelected[position] = isChecked;
                    updateTriggerText();
                });

                return row;
            }
        };

        triggerDropdown.setAdapter(triggerAdapter);

        triggerDropdown.setOnItemClickListener((parent, v, position, id) -> {
            triggerSelected[position] = !triggerSelected[position];
            triggerAdapter.notifyDataSetChanged();
            updateTriggerText();
            triggerDropdown.showDropDown();
        });
    }

    private void updateTriggerText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < triggers.length; i++) {
            if (triggerSelected[i]) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(triggers[i]);
            }
        }
        triggerDropdown.setText(sb.toString(), false);
        triggerDropdown.setSelection(triggerDropdown.getText().length());
    }

    private List<String> getSelectedTriggers() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < triggers.length; i++) {
            if (triggerSelected[i]) {
                list.add(triggers[i]);
            }
        }
        return list;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    dateTime = dateTime.withYear(year)
                            .withMonth(month + 1)
                            .withDayOfMonth(dayOfMonth);

                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("MMM d, yyyy");
                    buttonDate.setText(dateTime.format(formatter));
                },
                dateTime.getYear(),
                dateTime.getMonthValue() - 1,
                dateTime.getDayOfMonth()
        );
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    dateTime = dateTime.withHour(hourOfDay).withMinute(minute);

                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("h:mm a");
                    buttonTime.setText(dateTime.format(formatter));
                },
                dateTime.getHour(),
                dateTime.getMinute(),
                false
        );
        timePickerDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void submitSymptomLog() {

        String symptomName = symptomDropdown.getText().toString().trim();
        List<String> selectedTriggers = getSelectedTriggers();

        if (symptomName.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please choose a symptom",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTriggers.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please choose at least one trigger",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean parentFlag = false;   // accessed through child page
        if (activity instanceof HomeParent) { parentFlag = true; }

        String triageId = "";

        Symptom log = new Symptom(
                childId,
                dateTime.toString(),
                parentFlag,
                symptomName,
                triageId,
                selectedTriggers
        );

        databaseReference.push().setValue(log)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(),
                                "Symptom saved!",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to save: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}
