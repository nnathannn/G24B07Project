package com.example.smartair;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileParentFragment extends Fragment {
    private FirebaseDatabase db;
    private String parentID;
    private TextView name;
    private TextView email;
    private EditText password;
    private Button signOut;

    public ProfileParentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_parent, container, false);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
        parentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        signOut = view.findViewById(R.id.sign_out);

        // show data
        showData();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email.setText(user.getEmail());

        // update password
        password.setOnClickListener(v -> changePassword());

        // sign out
        signOut.setOnClickListener(v -> signOutDialog());

        return view;
    }

    private void showData() {
        DatabaseReference childRef = db.getReference().child("parent-users").child(parentID);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nameValue = snapshot.child("name").getValue(String.class);
                    name.setText(nameValue);
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);

        EditText newPassInput = view.findViewById(R.id.new_pass_input);
        EditText confirmPassInput = view.findViewById(R.id.confirm_pass_input);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                .setView(view)
                .setCancelable(true)
                .create();

        saveButton.setOnClickListener(v -> {
            String newPass = newPassInput.getText().toString();
            String confirmPass = confirmPassInput.getText().toString();

            if (newPass.isEmpty() || confirmPass.isEmpty()){
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
            }
            else if (!newPass.equals(confirmPass)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else if (!passwordCheck(newPass)) {
                Toast.makeText(getContext(), "Please input new password as requirement below", Toast.LENGTH_LONG).show();
            }
            else {
                updatePassword(newPass);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean passwordCheck(String newPass) {
        boolean checkUpper = false;
        boolean checkLower = false;
        boolean checkNumber = false;
        boolean checkSpecial = false;
        for (int i = 0; i < newPass.length(); i++) {
            if (Character.isUpperCase(newPass.charAt(i))) checkUpper = true;
            else if (Character.isLowerCase(newPass.charAt(i))) checkLower = true;
            else if (Character.isDigit(newPass.charAt(i))) checkNumber = true;
            else if (String.valueOf(newPass.charAt(i)).matches("[!@#$%^&*()_+=<>?/{}~]")) checkSpecial = true;
        }
        return newPass.length() >= 8 && checkUpper && checkLower && checkNumber && checkSpecial;
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sign_out, null);

        Button yesButton = view.findViewById(R.id.yes_button);
        Button noButton = view.findViewById(R.id.no_button);

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle)
                .setView(view)
                .setCancelable(true)
                .create();

        yesButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // clear stack

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.parent_frame_layout, new SignInFragment())
                    .commit();
            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}