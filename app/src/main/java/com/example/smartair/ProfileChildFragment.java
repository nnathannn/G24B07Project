package com.example.smartair;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileChildFragment extends Fragment {
    private FirebaseDatabase db;
    private String userID;
    private String childID;
    private TextView name;
    private TextView dob;
    private Button signOut;

    public ProfileChildFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_child, container, false);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        childID = ((UIDProvider) getActivity()).getUid();
        name = view.findViewById(R.id.name);
        dob = view.findViewById(R.id.dob);
        signOut = view.findViewById(R.id.sign_out);

        // show data
        showData();

        // sign out
        signOut.setOnClickListener(v -> signOutDialog());

        return view;
    }

    private void showData() {
        DatabaseReference childRef = db.getReference().child("child-users").child(childID);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nameValue = snapshot.child("name").getValue(String.class);
                    String dobValue = snapshot.child("DOB").getValue(String.class);
                    name.setText(nameValue);
                    dob.setText(dobValue);
                }
                else {
                    Toast.makeText(getContext(), "Error: Child not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void signOutDialog() {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sign_out, null);
//
//        Button yesButton = view.findViewById(R.id.yes_button);
//        Button noButton = view.findViewById(R.id.no_button);
//
//        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
//                .setView(view)
//                .setCancelable(true)
//                .create();
//
//        yesButton.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            requireActivity().getSupportFragmentManager()
//                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // clear stack
//
//            Log.d("ProfileChildFragment", "userID: " + userID + ", childID: " + childID);
//            if (childID.equals(userID)) {
//                requireActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragmentContainerView, new SignInFragment())
//                        .commitAllowingStateLoss();
//            }
//            else {
//                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                transaction.replace(R.id.parent_frame_layout, new HomeParentFragment());
//                transaction.commit();
//            }
//
//            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
//            dialog.dismiss();
//        });
//
//        noButton.setOnClickListener(v -> dialog.dismiss());
//
//        dialog.show();
//    }

    private void signOutDialog() {
        Log.d("ProfileChildFragment", "signOutDialog: called");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sign_out, null);
        Log.d("ProfileChildFragment", "signOutDialog: dialog view inflated");

        Button yesButton = view.findViewById(R.id.yes_button);
        Button noButton = view.findViewById(R.id.no_button);

        if (yesButton == null) Log.d("ProfileChildFragment", "signOutDialog: yesButton is null!");
        if (noButton == null) Log.d("ProfileChildFragment", "signOutDialog: noButton is null!");

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                .setView(view)
                .setCancelable(true)
                .create();

        yesButton.setOnClickListener(v -> {
            Log.d("ProfileChildFragment", "signOutDialog: yesButton clicked");

            FirebaseAuth.getInstance().signOut();
            Log.d("ProfileChildFragment", "signOutDialog: FirebaseAuth.signOut() called");

            requireActivity().getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // clear stack
            Log.d("ProfileChildFragment", "signOutDialog: back stack cleared");

            Log.d("ProfileChildFragment", "userID: " + userID + ", childID: " + childID);

            if (childID.equals(userID)) {
                Log.d("ProfileChildFragment", "signOutDialog: childID equals userID, replacing with SignInFragment");
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new SignInFragment())
                        .commitAllowingStateLoss();
            } else {
                Log.d("ProfileChildFragment", "signOutDialog: childID != userID, replacing with HomeParentFragment");
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.parent_frame_layout, new HomeParentFragment());
                transaction.commit();
            }

            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
            Log.d("ProfileChildFragment", "signOutDialog: dialog dismissed after sign-out");
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            Log.d("ProfileChildFragment", "signOutDialog: noButton clicked, dialog dismissed");
            dialog.dismiss();
        });

        Log.d("ProfileChildFragment", "signOutDialog: showing dialog");
        dialog.show();
    }

}