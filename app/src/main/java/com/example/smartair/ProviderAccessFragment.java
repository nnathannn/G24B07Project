package com.example.smartair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProviderAccessFragment extends Fragment {
    private static final String ARG_PARAM1 = "provider_id", ARG_PARAM2 = "child_id";
    private String providerId, childId;
    private DatabaseReference childRef, providerRef;
    private final HashMap<String, Switch> switchMap = new HashMap<>();


    public ProviderAccessFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerId = getArguments().getString(ARG_PARAM1);
            childId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Switch rescueSwitch, controllerSwitch, symptomsSwitch, triggerSwitch, PEFToggleSwitch, triageSwitch, summarySwitch;
        rescueSwitch = view.findViewById(R.id.rescueSwitch);
        controllerSwitch = view.findViewById(R.id.controllerSwitch);
        symptomsSwitch = view.findViewById(R.id.symptomsSwitch);
        triggerSwitch = view.findViewById(R.id.triggerSwitch);
        PEFToggleSwitch = view.findViewById(R.id.PEFToggleSwitch);
        triageSwitch = view.findViewById(R.id.triageSwitch);
        summarySwitch = view.findViewById(R.id.summarySwitch);
        switchMap.put("rescue", rescueSwitch);
        switchMap.put("controller", controllerSwitch);
        switchMap.put("symptom", symptomsSwitch);
        switchMap.put("trigger", triggerSwitch);
        switchMap.put("pef", PEFToggleSwitch);
        switchMap.put("triage", triageSwitch);
        switchMap.put("summary", summarySwitch);
        childRef = FirebaseDatabase.getInstance().getReference("child-users").child(childId).child("provider").child(providerId);
        providerRef = FirebaseDatabase.getInstance().getReference("provider-users").child(providerId).child("access").child(childId);
        listenInitialValues();

        Button revoke = view.findViewById(R.id.revokeProviderButton);

        revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRevokeConfirmationDialog();
            }
        });
        ImageButton download = view.findViewById(R.id.downloadButton);
        download.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Download button clicked", Toast.LENGTH_LONG).show();
        });
    }

    private void showRevokeConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Revocation")
                .setMessage("Are you sure you want to permanently revoke access? This action cannot be undone.")
                .setCancelable(false);
        builder.setPositiveButton("Revoke", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Map<String, Object> updates = new HashMap<>();
                @SuppressLint("RestrictedApi") String childPath = childRef.getPath().toString();
                updates.put(childPath, null);
                @SuppressLint("RestrictedApi") String providerPath = providerRef.getPath().toString();
                updates.put(providerPath, null);
                ref.updateChildren(updates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("FirebaseUpdate", "Provider access revoked successfully");
                            }
                            else {
                                Toast.makeText(getContext(), "Update failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            getParentFragmentManager().popBackStack();
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getContext(), "Revocation cancelled.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void listenInitialValues() {
        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                removeAllListeners();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    Boolean val = dataSnapshot.getValue(Boolean.class);
                    Switch currentSwitch = switchMap.get(key);
                    if(currentSwitch != null && val != null) {
                        currentSwitch.setChecked(val);
                    }
                }
                addAllListeners();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }
    private void addAllListeners(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        for(Map.Entry<String, Switch> entry: switchMap.entrySet()) {
            String key = entry.getKey();
            Switch currentSwitch = entry.getValue();
            currentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Map<String, Object> updates = new HashMap<>();
                @SuppressLint("RestrictedApi") String childPath = childRef.child(key).getPath().toString();
                updates.put(childPath, isChecked);
                @SuppressLint("RestrictedApi") String providerPath = providerRef.child(key).getPath().toString();
                updates.put(providerPath, isChecked);
                ref.updateChildren(updates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("FirebaseUpdate", key + " updated successfully");
                            } else {
                                Toast.makeText(getContext(), "Update failed for " + key + ": " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            });
        }
    }
    private void removeAllListeners() {
        for(Switch currentSwitch: switchMap.values()) {
            currentSwitch.setOnCheckedChangeListener(null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_access, container, false);
    }
}