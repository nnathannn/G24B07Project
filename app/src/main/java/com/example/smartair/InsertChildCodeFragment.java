package com.example.smartair;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InsertChildCodeFragment extends Fragment {
    String providerId;
    FirebaseAuth myauth = FirebaseAuth.getInstance();

    public InsertChildCodeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        providerId = myauth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insert_child_code, container, false);
        EditText insertInviteCode = view.findViewById(R.id.insert_invite_code);
        Button addChildButton = view.findViewById(R.id.insert_child_button);
        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inviteCode = insertInviteCode.getText().toString();
                if(inviteCode.isEmpty()){
                    insertInviteCode.setError("Please enter an invite code");
                    return;
                }
                else{
                    checkInviteCode(inviteCode);
                }
            }
        });
        return view;
    }

    private void checkInviteCode(String inviteCode){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("invite-code");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String parentId = ds.getKey();
                    checkParent(parentId, inviteCode);
                }
//                Toast.makeText(getContext(), "No child matches the invite code.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }

    private void checkParent(String parentId, String inviteCode){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("invite-code")
                .child(parentId).child(inviteCode);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate currentDate = LocalDate.now();
                String today = currentDate.format(formatter);
                if (dataSnapshot.exists() && today.compareTo(Objects.requireNonNull(dataSnapshot.child("end-date").getValue(String.class))) <= 0) {
                    ProviderInvite providerInvite = dataSnapshot.getValue(ProviderInvite.class);
                    logProvider(providerInvite, parentId);
                } else if (dataSnapshot.exists() && today.compareTo(Objects.requireNonNull(dataSnapshot.child("end-date").getValue(String.class))) > 0){
                    Toast.makeText(getContext(), "Invite code has expired.", Toast.LENGTH_SHORT).show();
                }
                else {
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }
    private void logProvider(ProviderInvite providerInvite, String parentId){
        DatabaseReference childRef = FirebaseDatabase.getInstance().
                getReference("child-users").child(providerInvite.getChildId()).
                child("provider").child(providerId);

        //Update child-users
        Map<String, Object> updates = new HashMap<>();
        updates.put("controller", false);
        updates.put("pef", false);
        updates.put("rescue", false);
        updates.put("summary", false);
        updates.put("symptom", false);
        updates.put("triage", false);
        updates.put("trigger", false);
        childRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("fields updated to child-users successfully");
            } else {
                System.err.println("failed to update fields into child-users " + task.getException().getMessage());
            }
        });

        //Update provider-users
        DatabaseReference providerRef = FirebaseDatabase.getInstance().
                getReference("provider-users").child(providerId).
                child("access").child(providerInvite.getChildId());
        providerRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("fields updated to provider-users successfully");
            } else {
                System.err.println("failed to update fields into provider-users " + task.getException().getMessage());
            }
        });

        //Update invite-code
        DatabaseReference inviteRef = FirebaseDatabase.getInstance().
                getReference("invite-code").child(parentId).child(providerInvite.getCode());
        inviteRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("fields updated to invite-code successfully");
            } else {
                System.err.println("failed to update fields into invite-code " + task.getException().getMessage());
            }
        });

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.providerHomeLayout, new HomeProviderFragment());
        fragmentTransaction.commit();
    }


}