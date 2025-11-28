package com.example.smartair;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PEFFragment extends Fragment {
    private RecyclerView recyclerView;
    private PEFAdapter adapter;
    private List<Zone> list;
    private FirebaseDatabase db;
    private EditText inputPEF;
    private Button submitPEF;
    private String childID;

    public PEFFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_p_e_f, container, false);
        recyclerView = view.findViewById(R.id.pef_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new PEFAdapter(list);
        recyclerView.setAdapter(adapter);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");

        // update later : retrieve childID from the user
        childID = "11";
        inputPEF = view.findViewById(R.id.submit_pef_box);
        submitPEF = view.findViewById(R.id.submit_pef_button);

        fetchData();
        submitPEF.setOnClickListener(v -> addData());

        return view;
    }

    private void fetchData() {
        DatabaseReference ref = db.getReference("zone");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Zone item = dataSnapshot.getValue(Zone.class);
                    if (item != null) list.add(item);
                }
                Collections.reverse(list); // Show most recent items first
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addData() {
        String input = inputPEF.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(getContext(), "No input", Toast.LENGTH_SHORT).show();
            return;
        }

        double count;
        try {
            count = Double.parseDouble(input);
            if (count <= 0) {
                Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = db.getReference("child-users").child(childID).child("PB");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double curPB = snapshot.getValue(Double.class);

                    // Only push the Zone object, since it contains all the necessary data.
                    DatabaseReference zoneref = db.getReference("zone");
                    Zone zone = new Zone(LocalDateTime.now().toString(), childID, count, curPB);
                    DatabaseReference zoneRefPush = zoneref.push();
                    zoneRefPush.setValue(zone).addOnSuccessListener(aVoid -> {
                        inputPEF.setText("");
                        // The ValueEventListener in fetchData() will automatically update the UI
                    }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                    // input zone id to child-zones
                    DatabaseReference childZoneRef = db.getReference("child-zones").child(childID).child(zoneRefPush.getKey());
                    childZoneRef.setValue("true").addOnFailureListener
                            (e -> Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getContext(), "Could not find Personal Best for child.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to get PB: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}