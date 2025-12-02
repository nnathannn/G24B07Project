package com.example.smartair;

import android.annotation.SuppressLint;
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

public class AddChildFragment extends Fragment {

    public AddChildFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_child, container, false);
        EditText inputName = (EditText) view.findViewById(R.id.inputUsername);
        EditText inputDOB = (EditText) view.findViewById(R.id.inputDOB);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText inputPB = (EditText) view.findViewById(R.id.inputPB);
        EditText inputNote = (EditText) view.findViewById(R.id.inputNote);
        EditText inputPassw = (EditText) view.findViewById(R.id.inputPassw);
        Button submitButton = (Button) view.findViewById(R.id.addChildButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString();
                String dob = inputDOB.getText().toString();
                String pb = inputPB.getText().toString();
                String note = inputNote.getText().toString();
                String passw = inputPassw.getText().toString();
                HomeParent activity = (HomeParent) getActivity();
                if (name.isEmpty() || dob.isEmpty() || pb.isEmpty() || note.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else if (!(activity.passwordCheck(passw))) {

                }
                else if (!(activity.usernameCheck(name))) {

                }
                else {
                    activity.signUp(name + "@g24b07project.examplefakedomain", passw, name, dob, Integer.parseInt(pb), note);
                }
            }
        });

        return view;
    }
}