package com.example.smartair;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private String childID;
    private TextView name;
    private TextView dob;
    private EditText password;
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
        childID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        name = view.findViewById(R.id.name);
        dob = view.findViewById(R.id.dob);
        password = view.findViewById(R.id.password);
        signOut = view.findViewById(R.id.sign_out);

        // show data
        showData();

        // update password
        password.setOnClickListener(v -> changePassword());

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

    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Password");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        EditText newPassInput = view.findViewById(R.id.new_pass_input);
        EditText confirmPassInput = view.findViewById(R.id.confirm_pass_input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String newPass = newPassInput.getText().toString();
            String confirmPass = confirmPassInput.getText().toString();

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            updatePassword(newPass);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> { dialog.dismiss(); });
        builder.create().show();
    }

    private void updatePassword(String newPass) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "Error: User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        user.updatePassword(newPass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // clear stack

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new ChildSignInFragment())
                    .commit();
            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> { dialog.dismiss(); });
        builder.create().show();
    }

}