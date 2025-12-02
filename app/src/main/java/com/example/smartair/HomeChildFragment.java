package com.example.smartair;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Locale;
import java.util.Objects;

public class HomeChildFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter badgeAdapter;
    private List<Item> itemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        //Copy the code up until fetchData() and change with respective view holders for recycler view
        recyclerView = view.findViewById(R.id.badgeRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));

        itemList = new ArrayList<>();
        badgeAdapter = new BadgeAdapter(itemList);
        recyclerView.setAdapter(badgeAdapter);

        ItemAdapter.fetchData(badgeAdapter, "badge");

        Button buttonTriage = view.findViewById(R.id.buttonTriage);
        Button buttonMedicine = view.findViewById(R.id.buttonMedicine);
        Button buttonDaily = view.findViewById(R.id.buttonDaily);
        Button buttonTechnique = view.findViewById(R.id.buttonTechnique);
        Button buttonPEF = view.findViewById(R.id.buttonPEF);
        Button buttonProfile = view.findViewById(R.id.buttonProfile);
        Button buttonInventory = view.findViewById(R.id.childInventoryEditButton);

        buttonInventory.setOnClickListener(v -> {
            final String[] inventoryOptions = {"Rescue", "Controller"};
            new AlertDialog.Builder(getContext())
                    .setTitle("Which inventory do you want to update?")
                    .setItems(inventoryOptions, (dialog, which) -> {
                        // 0 for rescue, 1 for controller
                        String selectedInventory = inventoryOptions[which];
                        getId(selectedInventory);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        buttonTriage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new TriageFragment());
            }
        });

        buttonMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new MedicineFragment());
            }
        });

        buttonDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SymptomFragment());
            }
        });

        buttonTechnique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new TechniqueFragment());
            }
        });

        buttonPEF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PEFFragment());
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileFragment());
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void getId(String inventoryType) {
        final String[] Id = {null};
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        System.out.println("User: " + user);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("child-inventory").child(user);
        boolean rescue = inventoryType.equals("Rescue");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("Inventory ID: " + childSnapshot.getKey());
                    checkType(childSnapshot.getKey(), rescue);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load IDs: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkType(String id, boolean rescue) {
        System.out.println("ID: " + id);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inventory").child(id);
        final boolean[] result = {false};
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isRescue = dataSnapshot.child("rescue").getValue(Boolean.class);
                    if (isRescue != null && isRescue == rescue) {
                        System.out.println("YEYEYEYEYYE ID: " + id);
                        Fragment fragment = new EditInventoryFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("child_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        bundle.putString("updated_by", "Child");
                        bundle.putString("inventory_id", id);
                        fragment.setArguments(bundle);
                        loadFragment(fragment);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load IDs: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}