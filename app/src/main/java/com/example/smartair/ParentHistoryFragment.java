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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParentHistoryFragment extends Fragment {

    private List<String> filters;
    private ArrayAdapter<String> filterAdapter;
    private Spinner spinner;
    private RecyclerView recycler;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;

    public ParentHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        filters = Arrays.asList("Symptoms", "Triggers", "Zones", "Triages", "Medicines");
        filterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, filters);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemList = new ArrayList<>();

        return inflater.inflate(R.layout.fragment_parent_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(filterAdapter);
        recycler = view.findViewById(R.id.HistoryRecycler);
        recycler.setHasFixedSize(True);
        recycler.setAdapter(createAdapter("Symptoms", "2000-01-01", LocalDate.now().toString()));

        Button applyFilter = view.findViewById(R.id.HistoryFilterSubmit);
        Button resetFilter = view.findViewById(R.id.HistoryFilterReset);

        applyFilter.setOnClickListener(v -> {
            String filter = spinner.getSelectedItem().toString();
            String start = view.findViewById(R.id.HistoryStartDate).getText().toString();
            String end = view.findViewById(R.id.HistoryEndDate).getText().toString();

        }
    }

    private ItemAdapter createAdapter(String type, String start, String end) {
        itemList.clear();
        switch (type) {
            case "Symptoms":
                itemAdapter = new HistorySymptomAdapter(itemList);
            case "Zones":
                itemAdapter = new HistoryZoneAdapter(itemList);
            case "Triages":
                itemAdapter = new HistoryTriageAdapter(itemList);
            case "Medicines":
                itemAdapter = new HistoryMedicineAdapter(itemList);
        }
    }

    private List<String> getChildrenNames() {
        List<String> children = new ArrayList<>();

    }
}