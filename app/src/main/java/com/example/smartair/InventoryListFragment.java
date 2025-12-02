package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
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

import java.util.ArrayList;
import java.util.List;

public class InventoryListFragment extends Fragment implements InventoryAdapter.OnItemClickListener {

    private String parentUserId;
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<Pair<Inventory, String>> itemList;
    private DatabaseReference parentChildrenRef;
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

        itemList = new ArrayList<>();
        adapter = new InventoryAdapter(itemList, this);
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

    private void addInventory(String inventoryId){
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("inventory");
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(inventoryId)){
                    DataSnapshot inventoryNode = dataSnapshot.child(inventoryId);
                    if(inventoryNode.exists()) {
                        itemList.add(new Pair<>(inventoryNode.getValue(Inventory.class), inventoryId));
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
    public void onItemClick(Pair<Inventory, String> clickedInventory) {
        Toast.makeText(getContext(), "[Code will be completed to redirect to a new activity] Clicked: ", Toast.LENGTH_LONG).show();
        Bundle bundle = new Bundle();
        bundle.putString("child_id", clickedInventory.first.getChildId());
        bundle.putString("inventory_id", clickedInventory.second);
        bundle.putString("updated_by", "Parent");
        Fragment fragment = new EditInventoryFragment();
        fragment.setArguments(bundle);
        assert getActivity() != null;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.parent_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public FirebaseUser getUser() { return myauth.getCurrentUser(); }
}