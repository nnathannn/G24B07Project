    package com.example.smartair;

    import android.app.AlertDialog;
    import android.os.Build;
    import android.os.Bundle;

    import androidx.annotation.NonNull;
    import androidx.annotation.RequiresApi;
    import androidx.fragment.app.Fragment;
    import androidx.navigation.fragment.NavHostFragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.Toast;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.time.Duration;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class ChildListFragment extends Fragment implements ChildAdapter.OnItemClickListener {

        private String parentUserId;
        private DatabaseReference parentChildrenRef;
        private ChildAdapter adapter;
        private List<String> childNameList, childIdList;
        private Map<String, String> childNameToId;
        private RecyclerView recyclerView;
        private List<Boolean> redZone;
        private boolean triageAlertShown = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            parentUserId = requireArguments().getString("parent_user_id");
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_child_list, container, false);
            recyclerView = view.findViewById(R.id.child_recycler_view);
            childNameList = new ArrayList<>();
            childNameToId = new HashMap<>();
            redZone = new ArrayList<>();
            adapter = new ChildAdapter(childNameList, this, redZone);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            if(parentUserId == null || parentUserId.isEmpty()){
                Toast.makeText(getContext(), "Parent user ID does not exist", Toast.LENGTH_LONG).show();
            }
            else {
                parentChildrenRef = FirebaseDatabase.getInstance().getReference("parent-users").child(parentUserId).child("child-ids");
                loadChildIds();
            }

            return view;
        }

        private void loadChildIds() {
            parentChildrenRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> ids = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                            String childId = idSnapshot.getKey();
                            if (childId != null) {
                                ids.add(childId);
                            }
                        }
                    }
                    if(!ids.isEmpty()){
                        fetchChildNames(ids);
                    }
                    else{
                        childNameList.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "No children found. Go add children.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load IDs: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        private void fetchChildNames(List<String> childIds) {
            DatabaseReference childrenRef = FirebaseDatabase.getInstance().getReference("child-users");
            childrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    childNameList.clear();
                    childNameToId.clear();
                    for (String childId : childIds) {
                        if (dataSnapshot.hasChild(childId)) {
                            DataSnapshot childNode = dataSnapshot.child(childId);
                            if (childNode.hasChild("name")) {
                                String childName = childNode.child("name").getValue(String.class);
                                if (childName != null) {
                                    childNameList.add(childName);
                                    childNameToId.put(childName, childId);
                                }
                            }
                        }
                    }

                    // Initialize redZone list with false
                    redZone.clear();
                    for (int i = 0; i < childNameList.size(); i++) redZone.add(false);

                    adapter.notifyDataSetChanged();

                    // Now fetch zones
                    fetchChildZones(childIds);

                    // Now fetch triages
                    monitorChildTriages(childIds);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        private void fetchChildZones(List<String> childIds) {
            DatabaseReference childZonesRef = FirebaseDatabase.getInstance().getReference("child-zones");
            childZonesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (int i = 0; i < childIds.size(); i++) {
                        String childId = childIds.get(i);
                        if (snapshot.hasChild(childId)) {
                            DataSnapshot zonesSnapshot = snapshot.child(childId);

                            String lastZoneId = null;
                            for (DataSnapshot zoneEntry : zonesSnapshot.getChildren()) {
                                lastZoneId = zoneEntry.getKey(); // iterates to get the last zone-id
                            }

                            if (lastZoneId != null) {
                                checkZone(lastZoneId, i); // check only the last zone-id
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        private void checkZone(String zoneId, int position) {
            DatabaseReference zoneRef = FirebaseDatabase.getInstance().getReference("zone").child(zoneId);
            zoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) return;

                    String status = snapshot.child("status").getValue(String.class);
                    String dateStr = snapshot.child("date").getValue(String.class);

                    if (status != null && dateStr != null) {
                        try {
                            LocalDate date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                    .toLocalDate();

                            if (status.equals("Red") && LocalDate.now().isEqual(date)) {
                                redZone.set(position, true);
                                adapter.notifyItemChanged(position); // <-- update color
                                showRedZoneAlert(); // optional: show alert only once per day
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // prevents crash
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        private void showRedZoneAlert() {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_red_zone_day_alert, null);

            Button ok = view.findViewById(R.id.ok_button);

            AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                    .setView(view)
                    .create();

            ok.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }

        private void monitorChildTriages(List<String> childIds) {
            DatabaseReference childTriagesRef = FirebaseDatabase.getInstance()
                    .getReference("child-triages");

            childTriagesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Reset all redZone flags
                    for (int i = 0; i < redZone.size(); i++) redZone.set(i, false);

                    for (int i = 0; i < childIds.size(); i++) {
                        String childId = childIds.get(i);

                        if (snapshot.hasChild(childId)) {
                            DataSnapshot childTriageSnap = snapshot.child(childId);

                            for (DataSnapshot triageEntry : childTriageSnap.getChildren()) {
                                String triageId = triageEntry.getKey();
                                checkTriage(triageId, i);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

            triageAlertShown = false;
        }

        private void checkTriage(String triageId, int i) {
            DatabaseReference triageRef = FirebaseDatabase.getInstance()
                    .getReference("triage")
                    .child(triageId);

            triageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot triageSnap) {

                    if (!triageSnap.exists()) return;

                    try {
                        String startStr = triageSnap.child("date").getValue(String.class);
                        String endStr = triageSnap.child("endDate").getValue(String.class);

                        if (startStr == null || startStr.isEmpty()) return;

                        LocalDateTime start = LocalDateTime.parse(startStr);
                        LocalDateTime now = LocalDateTime.now();

                        LocalDateTime end = null;
                        if (endStr != null && !endStr.isEmpty()) {
                            end = LocalDateTime.parse(endStr);
                        }

                        boolean inTriage =
                                (((now.isEqual(start) || now.isAfter(start)) && end == null) ||
                                ((now.isEqual(start) || now.isAfter(start)) && (now.isEqual(end) || now.isBefore(end))));

                        if (inTriage) {
                            if (!redZone.get(i)) {
                                redZone.set(i, true);
                                adapter.notifyItemChanged(i);
                                showTriageAlertOnce();  // prevent alert spam
                            }
                        } else {
                            redZone.set(i, false);
                            adapter.notifyItemChanged(i);
                        }

                    } catch (Exception e) {
                        Log.e("TRIAGE", "Error parsing date/endDate: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        private void showTriageAlertOnce() {
            if (triageAlertShown) return; // prevents repeats
            triageAlertShown = true;

            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_triage_alert, null);
            Button ok = view.findViewById(R.id.ok_button);

            AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                    .setView(view)
                    .create();

            ok.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }

        @Override
        public void onItemClick(String clickedString) {
    //        Toast.makeText(getContext(), "[Code will be completed to redirect to a new activity] Clicked: " + clickedString, Toast.LENGTH_LONG).show();
            String clickedId = childNameToId.get(clickedString);
            if(clickedId != null){
                Bundle bundle = new Bundle();
                bundle.putString("uid", clickedId);
                Fragment fragment = new ChildDashboardFragment();
                fragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.parent_frame_layout, fragment).addToBackStack(null).commit();
            }
        }
    }