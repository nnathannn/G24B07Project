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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ProviderAccessFragment extends Fragment {
    private static final String ARG_PARAM1 = "provider_id", ARG_PARAM2 = "child_id";
    private String providerId, childId;
    private DatabaseReference childRef, providerRef;
    private String providerNameStr = "Provider";
    private String childNameStr = "Child";

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

        TextView providerName = view.findViewById(R.id.providerAccessName);
        TextView childName = view.findViewById(R.id.childAccessName);
        DatabaseReference findChild = FirebaseDatabase.getInstance().getReference("child-users").child(childId);
        DatabaseReference findProvider = FirebaseDatabase.getInstance().getReference("provider-users").child(providerId);

        findChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    childName.setText(snapshot.child("name").getValue(String.class));
                }
                else {
                    Toast.makeText(getContext(), "Child does not exist", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        findProvider.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    providerName.setText(snapshot.child("name").getValue(String.class));
                } else {
                    Toast.makeText(getContext(), "Provider does not exist", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        listenInitialValues();

        Button revoke = view.findViewById(R.id.revokeProviderButton);

        revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRevokeConfirmationDialog();
            }
        });
        ImageButton download = view.findViewById(R.id.downloadButton);
        download.setOnClickListener(v -> buildAndExportProviderReport());

    }


    private void buildAndExportProviderReport() {
        if (getContext() == null) return;

        if (childId == null || providerId == null) {
            Toast.makeText(getContext(),
                    "Missing child/provider id for report",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate end   = LocalDate.now();
        LocalDate start = end.minusDays(89);
        String startStr = start.toString();
        String endStr   = end.toString();

        ProviderReportData data = new ProviderReportData();
        data.childName   = childNameStr;
        data.providerName = providerNameStr;
        data.startDate   = startStr;
        data.endDate     = endStr;


        data.canSeeRescue         = getSwitchValue("rescue");
        data.canSeeController     = getSwitchValue("controller");
        data.canSeeSymptoms       = getSwitchValue("symptom");
        data.canSeeZones          = getSwitchValue("pef");
        data.canSeeTriages        = getSwitchValue("triage");
        data.canSeeSummaryCharts  = getSwitchValue("summary");


        fetchAndFillReportDataThenExport(data, startStr, endStr);
    }

    private boolean getSwitchValue(String key) {
        Switch s = switchMap.get(key);
        return s != null && s.isChecked();
    }


    private void fetchAndFillReportDataThenExport(ProviderReportData data,
                                                  String start, String end) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();


        if (!data.canSeeRescue && !data.canSeeController
                && !data.canSeeSymptoms && !data.canSeeZones
                && !data.canSeeTriages) {
            PDFExportButton.exportProviderReport(requireContext(), data);
            return;
        }

        if (data.canSeeRescue || data.canSeeController) {
            String endWithTime = end + "T23:59:59.999999";

            rootRef.child("medicineLogs")
                    .orderByChild("date")
                    .startAt(start)
                    .endAt(endWithTime)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int rescueCount = 0;

                            for (DataSnapshot ds : snapshot.getChildren()) {

                                String logChildId = ds.child("child-id").getValue(String.class);
                                if (logChildId == null || !logChildId.equals(childId)) continue;

                                Boolean isRescue = ds.child("rescue").getValue(Boolean.class);
                                if (Boolean.TRUE.equals(isRescue)) {
                                    rescueCount++;
                                }
                            }

                            data.rescueEventCount = rescueCount;

                            PDFExportButton.exportProviderReport(requireContext(), data);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(),
                                    "Failed to load report data: " + error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            PDFExportButton.exportProviderReport(requireContext(), data);
        }
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