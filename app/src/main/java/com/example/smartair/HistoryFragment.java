package com.example.smartair;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private ArrayAdapter<String> filterAdapter, childrenAdapter, symptomAdapter, triggerAdapter;
    private Spinner spinner, childspinner, searchspinner, layoutHelper;
    private RecyclerView recycler;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private DatabaseReference myref = MainActivity.db.getReference();
    private Map<String, String> idToName, nameToId;
    private List<String> filters, nameSelection, listSymptom, listTrigger;
    private LocalDate date;
    private Button applyFilter, resetFilter, startDate, endDate;
    private String user, uid;
    private LinearLayout layout;
    private Boolean triggerAccess = false;


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        idToName = new HashMap<>();
        nameToId = new HashMap<>();
        nameSelection = new ArrayList<>();
        filters = new ArrayList<>();
        listSymptom = Arrays.asList(
                "Any symptom",
                "Night waking",
                "Activity limits",
                "Cough",
                "Wheezing",
                "Can't speak full sentences",
                "Chest pulling in",
                "Blue/gray lips/nails");
        listTrigger = Arrays.asList(
                "Any trigger",
                "Exercise",
                "Cold air",
                "Dust",
                "Pets",
                "Smoke",
                "Illness",
                "Strong odors");

        //        filters = Arrays.asList("Zones", "Triggers", "Symptoms", "Triages", "Medicines");
        filterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, filters);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        childrenAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, nameSelection);
        childrenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        symptomAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listSymptom);
        symptomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        triggerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listTrigger);
        triggerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        itemList = new ArrayList<>();
        itemAdapter = new AdapterHistory(itemList);

        date = LocalDate.now();
        setupAllData();

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

        layout = view.findViewById(R.id.filterLayout);
        layoutHelper = view.findViewById(R.id.layoutHelper);

        childspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if (user == "provider" && position != 0) { getChildFilters(nameSelection.get(position)); }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ( layout.getChildCount() >= 3) {
                    layout.removeViewAt(2);
                    layoutHelper.setVisibility(INVISIBLE);
                    searchspinner = null;
                }
                if ( parent.getSelectedItem().toString().equals("Symptoms") ) {
                    searchspinner = new Spinner(getContext());
                    searchspinner.setAdapter(symptomAdapter);
                    searchspinner.setBackgroundResource(R.drawable.history_dropdown);
                    searchspinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    layoutHelper.setVisibility(GONE);
                    layout.addView(searchspinner);
                } else if ( parent.getSelectedItem().toString().equals("Triggers") ) {
                    searchspinner = new Spinner(getContext());
                    searchspinner.setAdapter(triggerAdapter);
                    searchspinner.setBackgroundResource(R.drawable.history_dropdown);
                    searchspinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    layoutHelper.setVisibility(GONE);
                    layout.addView(searchspinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        applyFilter.setOnClickListener(v -> {
            String filter = spinner.getSelectedItem().toString();
            String start = startDate.getText().toString();
            String end = endDate.getText().toString();
            String child = childspinner.getSelectedItem().toString();
            String search = "";

            if (start.equals("Start Date")) { start = date.minusDays(179).toString(); }
            if (end.equals("End Date")) { end = date.toString(); }

            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            int numberOfDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

            if ( numberOfDays < 90 || numberOfDays > 180) {
                Toast.makeText(getContext(), "Please select a date range between 3-6 months", Toast.LENGTH_LONG).show();
                return;
            }

            if (searchspinner != null) { search = searchspinner.getSelectedItem().toString(); }
            if (filter.equals("No data is shared")) {
                Toast.makeText(getContext(), "No data is shared", Toast.LENGTH_LONG).show();
                return;
            }

            getData(filter, start, end, child, search);
        });
        resetFilter.setOnClickListener(v -> {
            spinner.setSelection(0);
            startDate.setText("Start Date");
            endDate.setText("End Date");
            childspinner.setSelection(0);
            getData(spinner.getSelectedItem().toString(), date.minusDays(179).toString(), date.toString(), childspinner.getSelectedItem().toString(), "");
        });
        startDate.setOnClickListener(v -> showDatePicker(v));
        endDate.setOnClickListener(v -> showDatePicker(v));
    }

    private void setupAllData() {
        uid = ((UIDProvider) getActivity()).getUid();
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference();
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("parent-users").hasChild(uid)) {
                    user = "parent";
                    filters.addAll(Arrays.asList("Zones", "Triages", "Rescue", "Controller", "Symptoms", "Triggers"));
                    filterAdapter.notifyDataSetChanged();
                } else if (snapshot.child("child-users").hasChild(uid)) {
                    user = "child";
                } else {
                    user = "provider";

                }
                getChildrenIds();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChildrenIds() {
        List<String> children = new ArrayList<>();
        DatabaseReference parentRef = null;
        switch (user) {
            case "parent":
                parentRef = myref.child("parent-users").child(uid).child("child-ids");
                break;
            case "provider":
                parentRef = myref.child("provider-users").child(uid).child("access");
                break;
        }
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
                idToName.clear();
                nameToId.clear();
                nameSelection.clear();
                for (String id : ids) {
                    idToName.put(id, dataSnapshot.child(id).child("name").getValue(String.class));
                    nameToId.put(dataSnapshot.child(id).child("name").getValue(String.class), id);
                }

                nameSelection.addAll(idToName.values());

                if (user == "provider") { getChildFilters(nameSelection.get(0)); }
                if (idToName.isEmpty()) {
                    nameSelection.add(0, "No children yet");
                }

                childrenAdapter.notifyDataSetChanged();
                getData("Zones", date.minusDays(179).toString(), date.toString(), nameSelection.get(0), "");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch child names: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getPath(String type) {
        String path;
        switch (type) {
            case "Zones":
                return "zone";
            case "Triages":
                return "triage";
            case "Rescue":
            case "Controller":
                return "medicineLogs";
            case "Symptoms":
            case "Triggers":
                return "symptom";
            default:
                return "zone";
        }
    }

    private void getData(String type, String start, String end, String child, String search) {
        String path = getPath(type);
        DatabaseReference childRef = myref.child(path);
        end += "T23:59:59.999999";
        childRef.orderByChild("date").startAt(start).endAt(end).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("child-id").getValue(String.class).equals(nameToId.get(child))) {
                        if (path.equals("medicineLogs")) {
                            String dataType = dataSnapshot.child("rescue").getValue(Boolean.class) ? "rescue" : "controller";
                            if ( dataType.equals(type.toLowerCase()) ) {
                                AdapterHistory.HistoryItem item = createItem(dataSnapshot, type);
                                itemList.add(item);
                            }
                        } else if (path.equals("symptom")) {
                            if (search.equals("Any symptom") || search.equals("Any trigger")) {
                                AdapterHistory.HistoryItem item = createItem(dataSnapshot, type);
                                itemList.add(item);
                            } else if (type.equals("Symptoms") && search.equals(dataSnapshot.child("name").getValue(String.class))) {
                                AdapterHistory.HistoryItem item = createItem(dataSnapshot, type);
                                itemList.add(item);
                            } else if (type.equals("Triggers") && dataSnapshot.child("triggerList").exists()) {
                                GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>() {};
                                if (dataSnapshot.child("triggerList").getValue(typeIndicator).contains(search)) {
                                    AdapterHistory.HistoryItem item = createItem(dataSnapshot, type);
                                    itemList.add(item);
                                }
                            }
                        } else {
                            AdapterHistory.HistoryItem item = createItem(dataSnapshot, type);
                            itemList.add(item);
                        }
                    }
                }
                if (itemList.isEmpty()) {
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    Collections.reverse(itemList);
                    switch (type) {
                        case "Zones":
                            itemList.add(0, new AdapterHistory.HistoryItem("Date", "PEF", "PB", "Status", null));
                            break;
                        case "Triages":
                            itemList.add(0, new AdapterHistory.HistoryItem("Date", "Symptoms", "Emergency", "Rescue", "PEF"));
                            break;
                        case "Rescue":
                        case "Controller":
                            itemList.add(0, new AdapterHistory.HistoryItem("Date", "Dose", "Pre-Status", "Post-Status", null));
                            break;
                        case "Symptoms":
                        case "Triggers":
                            itemList.add(0, new AdapterHistory.HistoryItem("Date", "Symptom", "Author", "Triggers", null));
                            break;
                        default:
                            break;
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
        String date = "";
        switch (type) {
            case "Zones":
                Zone zone = snapshot.getValue(Zone.class);
                date = zone.getDate().split("T")[0];
                String count = String.valueOf(zone.getCount());
                String currPB = String.valueOf(zone.getCurPB());
                return new AdapterHistory.HistoryItem(date, count, currPB, zone.getStatus(), null);
            case "Triages":
                Triage triage = snapshot.getValue(Triage.class);
                date = triage.getDate().split("T")[0];
                List<String> symptoms = triage.getSymptomList();
                String emergency = triage.getEmergency();
                String rescue = String.valueOf(triage.getRescue());
                String pef = String.valueOf(triage.getPef());
                if (pef.equals("0")) { pef = "None"; }
                return new AdapterHistory.HistoryItem(date, String.join("\n", symptoms), emergency, rescue, pef);
            case "Controller":
            case "Rescue":
                MedicineLog medicine = snapshot.getValue(MedicineLog.class);
                date = medicine.getDate().split("T")[0];
                String dose = String.valueOf(medicine.getDose());
                String pre = String.valueOf(medicine.getPreStatus());
                String post = String.valueOf(medicine.getPostStatus());
                return new AdapterHistory.HistoryItem(date, dose, pre, post, null);
            case "Symptoms":
            case "Triggers":
                Symptom symptom = snapshot.getValue(Symptom.class);
                date = symptom.getDate().split("T")[0];
                String name = symptom.getName();
                String author = symptom.getParent() ? "Parent" : "Child";
                List<String> triggers = symptom.getTriggerList();
                if ( triggers == null ) { triggers = Arrays.asList("During Emergency"); }
                if ( user == "provider" && !triggerAccess) { triggers = Arrays.asList("Data not shared"); }
                return new AdapterHistory.HistoryItem(date, name, author, String.join("\n", triggers), null);
            default:
                return null;
        }
    };

    private void showDatePicker(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    LocalDate inputDate = date.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth);
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                    Button b = (Button) v;
//                    b.setText(date.format(formatter));
                    b.setText(inputDate.toString());
                },
                date.getYear(),
                date.getMonthValue() - 1,
                date.getDayOfMonth());
        datePickerDialog.show();
    }

    private void getChildFilters(String child) {
        filters.clear();
        triggerAccess = false;
        DatabaseReference parentRef = myref.child("provider-users").child(uid).child("access").child(nameToId.get(child));
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.getValue(Boolean.class)) {
                        String key = childSnapshot.getKey();
                        switch (key) {
                            case "pef":
                                filters.add("Zones");
                                break;
                            case "controller":
                                filters.add("Controller");
                                break;
                            case "rescue":
                                filters.add("Rescue");
                                break;
                            case "symptom":
                                filters.add("Symptoms");
                                break;
                            case "triage":
                                filters.add("Triages");
                                break;
                            case "trigger":
                                triggerAccess = true;
                                break;
                        }
                    }
                }
                if (filters.isEmpty()) { filters.add("No data is shared"); }
                filterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load filters: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}