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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryListFragment extends Fragment implements InventoryAdapter.OnItemClickListener {
    private FirebaseDatabase db;
    private String parentUserId;
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<Item> itemList;
    private DatabaseReference parentChildrenRef;
    private List<Item> lowCanister;
    private List<Item> expired;
    FirebaseAuth myauth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentUserId = getUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_list, container, false);
        recyclerView = view.findViewById(R.id.inventoryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
        itemList = new ArrayList<>();
        lowCanister = new ArrayList<>();
        expired = new ArrayList<>();
        adapter = new InventoryAdapter(itemList, this, lowCanister, expired);
        recyclerView.setAdapter(adapter);

        // ItemAdapter.fetchData(adapter, "inventory");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (parentUserId == null || parentUserId.isEmpty()) {
            Toast.makeText(getContext(), "Parent user ID does not exist", Toast.LENGTH_LONG).show();
        } else {
            parentChildrenRef = FirebaseDatabase.getInstance().getReference("parent-users").child(parentUserId).child("child-ids");
            loadInventoryList();
        }

        return view;
    }

    //to be discussed: redundancy with childListFragment
    private void loadInventoryList() {
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
                if (!ids.isEmpty()) {
                    fetchInventory(ids);
                } else {
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "No children found. Please add children.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load IDs: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchInventory(List<String> childIds) {
        DatabaseReference childrenRef = FirebaseDatabase.getInstance().getReference("child-inventory");
        itemList.clear();
        lowCanister.clear();
        expired.clear();

        childrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String childId : childIds) {
                    if (dataSnapshot.hasChild(childId)) {
                        DataSnapshot childNode = dataSnapshot.child(childId);
                        if (childNode.exists()) {
                            for (DataSnapshot itemSnapshot : childNode.getChildren()) {
                                String inventoryId = itemSnapshot.getKey();
                                addInventory(inventoryId);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch inventory IDs: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void addInventory(String inventoryId) {
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("inventory");
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(inventoryId)) {
                    DataSnapshot inventoryNode = dataSnapshot.child(inventoryId);
                    if (inventoryNode.exists()) {
                        Inventory item = inventoryNode.getValue(Inventory.class);

                        if (item == null) return;

                        itemList.add(item);

                        if (item.isLowCanister()) {
                            AlertDialog dialog = new AlertDialog.Builder(getContext())
                                    .setTitle("Low Canister Alert")
                                    .setMessage("Low Canister Alert: " + item.getMedName() + " is low in quantity")
                                    .setPositiveButton("Done", null)
                                    .create();
                            dialog.show();

                            lowCanister.add(item);
                        }

                        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
                            LocalDate today = LocalDate.now();
                            LocalDate expiryDate = LocalDate.parse(item.getExpiryDate());
                            if (!today.isBefore(expiryDate)) {
                                AlertDialog dialog = new AlertDialog.Builder(getContext())
                                        .setTitle("Expired Alert")
                                        .setMessage("Expired Alert: " + item.getMedName() + " has expired")
                                        .setPositiveButton("Done", null)
                                        .create();
                                dialog.show();

                                expired.add(item);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch inventory: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onItemClick(Inventory clickedInventory) {
        Toast.makeText(getContext(), "[Code will be completed to redirect to a new activity] Clicked: " + clickedInventory.getMedName(), Toast.LENGTH_LONG).show();
    }

    public FirebaseUser getUser() {
        return myauth.getCurrentUser();
    }

}