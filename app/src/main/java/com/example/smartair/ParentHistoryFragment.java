package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ParentHistoryFragment extends Fragment {

    private ArrayAdapter<String> filterAdapter;
    private ArrayAdapter<String> childrenAdapter;
    private Spinner spinner;
    private RecyclerView recycler;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private DatabaseReference myref = MainActivity.db.getReference();
    private Map<String, String> childrenNames;

    public ParentHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        List<String> filters = Arrays.asList("Symptoms", "Triggers", "Zones", "Triages", "Medicines");
        filterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, filters);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        itemList = new ArrayList<>();

        getChildrenNames();
        List<String> nameSelection = new ArrayList<>(childrenNames.values());
        nameSelection.add(0, "All Children");
        if (childrenNames == null) { nameSelection.add(1, "No children yet"); }
        childrenAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, nameSelection);
        childrenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return inflater.inflate(R.layout.fragment_parent_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(filterAdapter);
        recycler = view.findViewById(R.id.HistoryRecycler);
        recycler.setHasFixedSize(true);
//        recycler.setAdapter(createAdapter("Symptoms", "2000-01-01", LocalDate.now().toString()));

        Button applyFilter = view.findViewById(R.id.HistoryFilterSubmit);
        Button resetFilter = view.findViewById(R.id.HistoryFilterReset);
        Button startDate = view.findViewById(R.id.HistoryStartDate);
        Button endDate = view.findViewById(R.id.HistoryEndDate);


        applyFilter.setOnClickListener(v -> {
            String filter = spinner.getSelectedItem().toString();
            String start = startDate.getText().toString();
            String end = endDate.getText().toString();

        });
    }

    private void createAdapter(String type, String start, String end) {
        itemList.clear();
        switch (type) {

        }
    }

    private void getChildrenNames() {
        List<String> children = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference parentRef = myref.child("parent-users").child(uid).child("child-ids");
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    children.add(childSnapshot.getValue(String.class));
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
                for (String id : ids) {
                    childrenNames.put(id, dataSnapshot.child(id).child("name").getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch child names: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}