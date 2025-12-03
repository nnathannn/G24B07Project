package com.example.smartair;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChildListFragment extends Fragment implements ChildAdapter.OnItemClickListener {

    private String parentUserId;
    private DatabaseReference parentChildrenRef;
    private ChildAdapter adapter;
    private List<String> childList;
    private RecyclerView recyclerView;
    private List<Boolean> redZone;

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
        childList = new ArrayList<>();
        redZone = new ArrayList<>();
        adapter = new ChildAdapter(childList, this, redZone);
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
                if (!ids.isEmpty()) fetchChildNames(ids);
                else {
                    childList.clear();
                    redZone.clear();
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
                childList.clear();
                for (String childId : childIds) {
                    if (dataSnapshot.hasChild(childId)) {
                        DataSnapshot childNode = dataSnapshot.child(childId);
                        if (childNode.hasChild("name")) {
                            String childName = childNode.child("name").getValue(String.class);
                            if (childName != null) {
                                childList.add(childName);
                            }
                        }
                    }
                }

                // Initialize redZone list with false values
                redZone.clear();
                for (int i = 0; i < childList.size(); i++) redZone.add(false);

                adapter.notifyDataSetChanged();
                // Fetch child zones
                fetchChildZones(childIds);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch child names: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
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
                        // Parse full timestamp and get only the date
                        LocalDate date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                .toLocalDate();

                        // Update redZone if it's Red for today
                        if (status.equals("Red") && LocalDate.now().isEqual(date)) {
                            redZone.set(position, true);
                            adapter.notifyItemChanged(position);
                            showRedZoneAlert();
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // prevents crash if date format is wrong
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void showRedZoneAlert() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_child_alert, null);
        TextView title = view.findViewById(R.id.child_alert_title);
        TextView message = view.findViewById(R.id.child_alert_message);
        Button ok = view.findViewById(R.id.ok_button);

        title.setText("Red-Zone Alert");
        message.setText("Child experiencing Red-Zone");

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                .setView(view)
                .create();

        ok.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onItemClick(String clickedString) {
        Toast.makeText(getContext(), "[Code will be completed to redirect to a new activity] Clicked: " + clickedString, Toast.LENGTH_LONG).show();
    }
}