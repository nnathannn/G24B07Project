package com.example.smartair;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParentHistoryFragment extends Fragment {

    private ArrayAdapter<String> filterAdapter, childrenAdapter;
    private Spinner spinner, childspinner;
    private RecyclerView recycler;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private DatabaseReference myref = MainActivity.db.getReference();
    private Map<String, String> childrenNames = new HashMap<>();
    private List<String> nameSelection = new ArrayList<>();
    private LocalDate date;
    private Button applyFilter, resetFilter, startDate, endDate;


    public ParentHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        List<String> filters = Arrays.asList("Zones", "Triggers", "Symptoms", "Triages", "Medicines");
        filterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, filters);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        childrenAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, nameSelection);
        childrenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        itemList = new ArrayList<>();
        itemAdapter = new AdapterHistory(itemList);

        date = LocalDate.now().plusDays(1);

        getChildrenNames();
        return inflater.inflate(R.layout.fragment_parent_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(filterAdapter);
        childspinner = view.findViewById(R.id.HistoryChildSelect);
        childspinner.setAdapter(childrenAdapter);

        recycler = view.findViewById(R.id.HistoryRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setHasFixedSize(true);
        recycler.setAdapter(itemAdapter);

        applyFilter = view.findViewById(R.id.HistoryFilterSubmit);
        resetFilter = view.findViewById(R.id.HistoryFilterReset);
        startDate = view.findViewById(R.id.HistoryStartDate);
        endDate = view.findViewById(R.id.HistoryEndDate);


        applyFilter.setOnClickListener(v -> {
            String filter = spinner.getSelectedItem().toString();
            String start = startDate.getText().toString();
            String end = endDate.getText().toString();
            String child = childspinner.getSelectedItem().toString();
        });
        resetFilter.setOnClickListener(v -> {
            spinner.setSelection(0);
            startDate.setText("Start Date");
            endDate.setText("End Date");
            childspinner.setSelection(0);
        });
        startDate.setOnClickListener(v -> showDatePicker(v));
        endDate.setOnClickListener(v -> showDatePicker(v));
    }

    private void changeAdapter(String type, String start, String end) {
        String path;
        switch (type) {
            case "Symptoms":
                path = "symptoms";
                break;
            case "Triggers":
                path = "triggers";
                break;
            case "Zones":
                path = "zone";
                break;
            case "Triages":
                path = "triage";
                break;
            case "Medicines":
                path = "medicineLogs";
                break;
            default:
                path = "medicineLogs";
        }
        getData(path, start, end);

    }

    private void getChildrenNames() {
        List<String> children = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference parentRef = myref.child("parent-users").child(uid).child("child-ids");
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    children.add(childSnapshot.getKey());
                }
                fetchChildNames(children);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load IDs: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchChildNames(List<String> ids) {
        DatabaseReference childRef = myref.child("child-users");
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childrenNames.clear();
                nameSelection.clear();
                for (String id : ids) {
                    childrenNames.put(id, dataSnapshot.child(id).child("name").getValue(String.class));
                }

                nameSelection.addAll(childrenNames.values());
                if (childrenNames.isEmpty()) {
                    nameSelection.add(0, "No children yet");
                } else {
                    nameSelection.add(0, "All Children");
                }
                childrenAdapter.notifyDataSetChanged();
                changeAdapter("Zones", "2000-01-01", date.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch child names: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDatePicker(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    date = date.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                    Button b = (Button) v;
                    b.setText(date.format(formatter));
                },
                date.getYear(),
                date.getMonthValue() - 1,
                date.getDayOfMonth());
        datePickerDialog.show();
    }

    private void getAllId(String path, String start, String end) {

    }

    private void getData(String path, String start, String end) {
        DatabaseReference childRef = myref.child(path);
        childRef.orderByChild("date").startAt(start).endAt(end).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (childrenNames.containsKey(dataSnapshot.child("child-id").getValue(String.class))) {
                        AdapterHistory.HistoryItem item = createItem(dataSnapshot, path);
                        itemList.add(item);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private AdapterHistory.HistoryItem createItem(DataSnapshot snapshot, String type) {
        switch (type) {
            case "symptoms":
                Symptom symptom = snapshot.getValue(Symptom.class);
                String author = symptom.getParent() ? "Parent" : "Child";
                String situation = (symptom.getTriageId() == null) ? "Daily Check-in" : "Triage";
                return new AdapterHistory.HistoryItem(childrenNames.get(symptom.getChildId()), symptom.getDate(),
                        symptom.getName(), author, situation, type);
            case "zone":
                Zone zone = snapshot.getValue(Zone.class);
                return new AdapterHistory.HistoryItem(childrenNames.get(zone.getChildId()), zone.getDate(),
                        String.valueOf(zone.getCount()), String.valueOf(zone.getCurPB()), zone.getStatus(), type);
            case "triage":
                Triage triage = snapshot.getValue(Triage.class);
                List<String> symptoms = triage.getSymptomList();
                String emergency = triage.getEmergency() ? "Emergency" : "Non-Emergency";
                String rescue = "1";
                return new AdapterHistory.HistoryItem(childrenNames.get(triage.getChildId()), triage.getDate(),
                        symptoms.get(0), emergency, rescue, type);
            case "medicineLogs":
                MedicineLog medicine = snapshot.getValue(MedicineLog.class);
                String name = medicine.getRescue() ? "Rescue" : "Controller";
                String status = "better";
                return new AdapterHistory.HistoryItem(childrenNames.get(medicine.getChildId()), medicine.getDate(),
                        name, String.valueOf(medicine.getDose()), status, type);
            default:
                return null;
        }
    };
}