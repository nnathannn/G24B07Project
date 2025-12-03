package com.example.smartair;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.List;

public class ChildListFragment extends Fragment implements ChildAdapter.OnItemClickListener {

    private String parentUserId;
    private DatabaseReference parentChildrenRef;
    private ChildAdapter adapter;
    private List<String> childList;
    private RecyclerView recyclerView;
    private boolean redZone;

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
        adapter = new ChildAdapter(childList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        redZone = false;

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
                    fetchChildZones(ids);
                }
                else{
                    childList.clear();
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
                adapter.notifyDataSetChanged();
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
                for (String childId : childIds) {
                    if (snapshot.hasChild(childId)) {
                        DataSnapshot childZoneSnapshot = snapshot.child(childId);
                        if (childZoneSnapshot.exists()) {
                            String zoneID = childZoneSnapshot.getKey();
                            if (zoneID != null) checkZone(zoneID, childId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkZone(String zoneID, String childID) {
        DatabaseReference zoneRef = FirebaseDatabase.getInstance().getReference("zone").child(zoneID);
        zoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String zone = snapshot.child("status").getValue(String.class);
                    if (zone != null) {
                        LocalDate today = LocalDate.now();
                        LocalDate date = LocalDate.parse(snapshot.child("date").getValue(String.class));
                        if (today.isEqual(date) && zone.equals("Red")) {
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_child_alert, null);

                            TextView childAlertTitle = view.findViewById(R.id.child_alert_title);
                            TextView childAlertMessage = view.findViewById(R.id.child_alert_message);
                            Button okButton = view.findViewById(R.id.ok_button);

                            AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                                    .setView(view)
                                    .create();
                            dialog.show();

                            childAlertTitle.setText("Red-Zone Day");
                            childAlertMessage.setText("Child experiencing Red Zone today");
                            okButton.setOnClickListener(v -> dialog.dismiss());

                            redZone = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to check zone: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(String clickedString) {
        Toast.makeText(getContext(), "[Code will be completed to redirect to a new activity] Clicked: " + clickedString, Toast.LENGTH_LONG).show();
    }
}