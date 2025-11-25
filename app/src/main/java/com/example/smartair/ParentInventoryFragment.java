package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ParentInventoryFragment extends Fragment {

    String temporaryParentId = "parent1"; //to be edited once logged in



    public ParentInventoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parent_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getParentFragmentManager().findFragmentById(R.id.inventoryListContainer) == null) {
            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.inventoryListContainer, new InventoryListFragment())
                    .commit();
        }
    }



// Automatically generated when creating fragment:
//    public static ParentInventoryFragment newInstance(String param1, String param2) {
//        ParentInventoryFragment fragment = new ParentInventoryFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_parent_inventory, container, false);
//    }
}