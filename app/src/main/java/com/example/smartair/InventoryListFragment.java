package com.example.smartair;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InventoryListFragment extends Fragment {

    private String parentUserId;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private FirebaseDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentUserId = requireArguments().getString("parent_user_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_list, container, false);
        recyclerView = view.findViewById(R.id.inventoryRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));

        itemList = new ArrayList<>();
        adapter = new InventoryAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // ItemAdapter.fetchData(adapter, "inventory");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TO BE COMPLETED ONCE DATA FETCHING DONE //
//        if(parentUserId == null || parentUserId.isEmpty()){
//            Toast.makeText(getContext(), "Parent user ID does not exist", Toast.LENGTH_LONG).show();
//        }
//        else {
//            parentChildrenRef = FirebaseDatabase.getInstance().getReference("parent-users").child(parentUserId).child("child-ids");
//            loadChildIds();
//        }

        return view;
    }
}