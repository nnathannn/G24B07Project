package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InventoryListFragment extends Fragment {

    private String parentUserId;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private DatabaseReference parentChildrenRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentUserId = "parent1"; // to be updated
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_list, container, false);
        recyclerView = view.findViewById(R.id.inventoryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        adapter = new InventoryAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // ItemAdapter.fetchData(adapter, "inventory");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(parentUserId == null || parentUserId.isEmpty()){
            Toast.makeText(getContext(), "Parent user ID does not exist", Toast.LENGTH_LONG).show();
        }
        else {
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
                if(!ids.isEmpty()){
                    fetchInventory(ids);
                }
                else{
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
        childrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (String childId : childIds) {
                    if (dataSnapshot.hasChild(childId)) {
                        DataSnapshot childNode = dataSnapshot.child(childId);
                        if(childNode.exists()){
                            for(DataSnapshot itemSnapshot : childNode.getChildren()){
                                String inventoryId = itemSnapshot.getValue(String.class);

                                //TO BE COMPLETED
                                if(inventoryId != null){
                                    DatabaseReference childInventoryRef = FirebaseDatabase.getInstance().getReference("inventory").child(inventoryId);
//                                    itemList.add(new Inventory(childId, childInventoryRef.child("purchase-date").get(String.class))
                                }




                            }
                        }

//                        if (childNode.hasChild("username")) {
//                            String childName = childNode.child("username").getValue(String.class);
//                            if (childName != null) {
//                                DatabaseReference childInventoryRef = FirebaseDatabase.getInstance().getReference("child-inventory").child(childId);
//
//                                itemList.add(childName);
//                            }
//                        }
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

    //TO BE COMPLETED: ONCLICK LISTENER
//    @Override
//    public void onItemClick(String clickedString) {
//        Toast.makeText(getContext(), "[Code will be completed to redirect to a new activity] Clicked: " + clickedString, Toast.LENGTH_LONG).show();
//    }
}