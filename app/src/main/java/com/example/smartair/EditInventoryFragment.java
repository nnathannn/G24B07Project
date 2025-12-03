package com.example.smartair;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class EditInventoryFragment extends Fragment {
    private String childId, inventoryId, updatedBy, childName;
    private DatabaseReference inventoryRef;


    public EditInventoryFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childId = getArguments().getString("child_id");
            inventoryId = getArguments().getString("inventory_id");
            updatedBy = getArguments().getString("updated_by");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_inventory, container, false);
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private void showDatePickerDialog(EditText dateEditText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(selectedYear, selectedMonth, selectedDay);
                    String formattedDate = dateFormat.format(selectedCal.getTime());
                    dateEditText.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText medName, purchaseDate, expiryDate, amountLeft;
        medName = view.findViewById(R.id.inputMedName);
        purchaseDate = view.findViewById(R.id.inputPurchase);
        expiryDate = view.findViewById(R.id.inputExpiry);
        amountLeft = view.findViewById(R.id.inputAmountLeft);
        Button editItem = view.findViewById(R.id.editItemButton);
        TextView inventoryIdentity = view.findViewById(R.id.inventoryIdentity);

        purchaseDate.setOnClickListener(v -> {
            showDatePickerDialog(purchaseDate);
        });
        expiryDate.setOnClickListener(v -> {
            showDatePickerDialog(expiryDate);
        });
        purchaseDate.setFocusable(false);
        expiryDate.setFocusable(false);

        if (inventoryId != null) {
            inventoryRef = FirebaseDatabase.getInstance().getReference("inventory").child(inventoryId);
        } else {
            Toast.makeText(getContext(), "Error: Missing Inventory ID", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("child-users").child(childId);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    childName = dataSnapshot.child("name").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch child name: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    inventoryIdentity.setText(childName + " | " + (dataSnapshot.child("rescue").getValue(Boolean.class) ? "Rescue" : "Controller"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch inventory data: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        editItem.setOnClickListener(v -> {
            String medNameText = medName.getText().toString();
            String purchaseDateText = purchaseDate.getText().toString();
            String expiryDateText = expiryDate.getText().toString();
            String amountLeftText = amountLeft.getText().toString();
            if(medNameText.isEmpty() || purchaseDateText.isEmpty() || expiryDateText.isEmpty() || amountLeftText.isEmpty()){
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Boolean isRescue = dataSnapshot.child("rescue").getValue(Boolean.class);
                        boolean rescueStatus = false;
                        if (isRescue != null && isRescue){
                            rescueStatus = true;
                        }
                        Double amountDouble = 0.0;
                        try {
                            amountDouble = Double.parseDouble(amountLeftText);
                        } catch (NumberFormatException e) {
                            amountLeft.setError("Amount must be a valid number.");
                            return;
                        }
                        if(amountDouble < 0.0 || amountDouble > 1.0){
                            amountLeft.setError("Amount must be a positive number between 0 and 1.");
                            return;
                        }
                        inventoryRef.setValue(new Inventory(childId, purchaseDateText, purchaseDateText, amountDouble, expiryDateText, rescueStatus, medNameText, updatedBy))
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Inventory updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        if(updatedBy.equals("Parent")){
                            replaceParentFragment(new ParentInventoryFragment());
                        }
                        else{
                            replaceChildFragment(new HomeChildFragment());
                        }

                    } else {
                        Toast.makeText(getContext(), "Inventory ID not found for update.", Toast.LENGTH_SHORT).show();
                        if(updatedBy.equals("Parent")){
                            replaceParentFragment(new ParentInventoryFragment());
                        }
                        else{
                            replaceChildFragment(new HomeChildFragment());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to update inventory: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void replaceParentFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.parent_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceChildFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}