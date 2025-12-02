package com.example.smartair;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileProviderFragment extends Fragment {
    private FirebaseDatabase db;
    private String userID;
    private String type;
    private EditText name;
    private EditText email;
    private EditText password;
    private String curName;
    private String curEmail;

    public ProfileProviderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_provider, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");

        // update later
        userID = "11";

        // check user type
        String [] userType = {"child-users", "parent-users", "provider-users"};
        for (String user : userType) {
            db.getReference().child(user).child(user).equalTo(userID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (user.equals("child")) type = "child";
                                if (user.equals("parent")) type = "parent";
                                if (user.equals("provider")) type = "provider";
                            }
                            else {
                                Toast.makeText(getContext(), "Error: User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);

        // set edit text to non-editable
        disableEdit(name);
        disableEdit(email);
        // !!! password !!!

        // show data
        showData();

        // edit data
        enableEdit(name);
        enableEdit(email);
        // !!! password !!!

        // save data
        name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                name.setFocusable(false);
                name.setCursorVisible(false);
                String nameValue = name.getText().toString();

                if (!nameValue.equals(curName)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm Change")
                            .setMessage("Are you sure you want to change your name?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                saveData("name", nameValue);
                                curName = nameValue;
                                Toast.makeText(getContext(), "Name changed to " + nameValue, Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                name.setText(curName);
                                dialog.dismiss();
                            })
                            .show();
                }
            }
        });

        email.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                email.setFocusable(false);
                email.setCursorVisible(false);
                String emailValue = email.getText().toString();

                if (!emailValue.equals(curEmail)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm Change")
                            .setMessage("Are you sure you want to change your email?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                saveData("email", emailValue);
                                curEmail = emailValue;
                                Toast.makeText(getContext(), "Name changed to " + emailValue, Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                email.setText(curEmail);
                                dialog.dismiss();
                            })
                            .show();
                }
            }
        });

        // show manage children when the parent open the profile
        //
    }

    private void showData() {
        DatabaseReference userRef = db.getReference().child(type + "-users").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    curName = snapshot.child("name").getValue(String.class);
                    curEmail = snapshot.child("email").getValue(String.class);
                    name.setText(curName);
                    email.setText(curEmail);
                }
                else {
                    Toast.makeText(getContext(), "Error: User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // !!! show password !!!
    }

    private void disableEdit(EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);   // allows clicking to enable editing
        editText.setCursorVisible(false);
        editText.setKeyListener(null); // disable keyboard until clicked
    }

    private void enableEdit(EditText editText) {
        editText.setOnClickListener(v -> {
            editText.setFocusableInTouchMode(true);
            editText.setFocusable(true);
            editText.setCursorVisible(true);
            editText.setKeyListener(new EditText(getContext()).getKeyListener()); // display keyboard
        });
    }

    private void saveData(String type, String data) {
        DatabaseReference typeRef = db.getReference().child(type + "-users").child(userID);
        typeRef.child(type).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Data saved", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}