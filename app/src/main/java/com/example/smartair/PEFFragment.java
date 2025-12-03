package com.example.smartair;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
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

import com.google.firebase.auth.FirebaseAuth;
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
    private EditText preMed;
    private EditText postMed;
    private AppCompatButton submitPEF;
    private String childID;
    private int totalSnapshot;
    private int loadedSnapshot;

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

        childID = ((UIDProvider) getActivity()).getUid();
        inputPEF = view.findViewById(R.id.submit_pef_box);
        preMed = view.findViewById(R.id.pre_med);
        postMed = view.findViewById(R.id.post_med);
        submitPEF = view.findViewById(R.id.submit_pef_button);

        fetchData();
        submitPEF.setOnClickListener(v -> addData());

        return view;
    }

    private void fetchData() {
        DatabaseReference ref = db.getReference("child-zones").child(childID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                if (snapshot.exists()) {
                    totalSnapshot = (int) snapshot.getChildrenCount();
                    loadedSnapshot = 0;

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String zoneID = ds.getKey();

                        db.getReference("zone").child(zoneID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot zoneSnapshot) {
                                        loadedSnapshot++;

                                        if (zoneSnapshot.exists()) {
                                            Zone zone = zoneSnapshot.getValue(Zone.class);
                                            if (zone != null) list.add(zone);
                                            adapter.notifyDataSetChanged();
                                        }

                                        if (loadedSnapshot == totalSnapshot) {
                                            Collections.sort(list, (a, b) -> b.getDate().compareTo(a.getDate()));
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Failed to get zones: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to get zones: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addData() {
        String pef = inputPEF.getText().toString().trim();
        String pre = preMed.getText().toString().trim();
        String post = postMed.getText().toString().trim();

        if (pef.isEmpty()) {
            Toast.makeText(getContext(), "No PEF input", Toast.LENGTH_SHORT).show();
            return;
        }

        double count;
        try {
            count = Double.parseDouble(pef);
            if (count <= 0) {
                Toast.makeText(getContext(), "Invalid PEF input", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid PEF input", Toast.LENGTH_SHORT).show();
            return;
        }

        int preVal;
        if (pre.isEmpty()) preVal = 0;
        else {
            try {
                preVal = Integer.parseInt(pre);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid pre-medication input", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int postVal;
        if (post.isEmpty()) postVal = 0;
        else {
            try {
                postVal = Integer.parseInt(post);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid post-medication input", Toast.LENGTH_SHORT).show();
                return;
            }
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
                    Zone zone = new Zone(LocalDateTime.now().toString(), childID, count, curPB, preVal, postVal);
                    DatabaseReference zoneRefPush = zoneref.push();
                    zoneRefPush.setValue(zone).addOnSuccessListener(aVoid -> {
                        inputPEF.setText("");
                        preMed.setText("");
                        postMed.setText("");
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