package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private FirebaseDatabase db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
    private String userID;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // update later : retrieve userID from the user
        userID = "11";

        // check user type and load the right fragment
        String [] userType = {"child", "parent", "provider"};
        DatabaseReference ref = db.getReference();
        for (String user : userType) {
            ref.child(user).child(userID).equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        if (user.equals("child")) transaction.replace(R.id.fragmentContainerView, new ProfileChildFragment());
                        if (user.equals("parent")) transaction.replace(R.id.parent_frame_layout, new ProfileParentFragment());
                        if (user.equals("provider")) transaction.replace(R.id.providerHomeLayout, new ProfileProviderFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // error handling
                }
            });
        }
    }
}