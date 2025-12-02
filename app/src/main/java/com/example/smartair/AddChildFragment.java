package com.example.smartair;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddChildFragment extends Fragment {

    public AddChildFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_child, container, false);
        EditText inputName = (EditText) view.findViewById(R.id.inputUsername);
        EditText inputDOB = (EditText) view.findViewById(R.id.inputDOB);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText inputPB = (EditText) view.findViewById(R.id.inputPB);
        EditText inputNote = (EditText) view.findViewById(R.id.inputNote);
        EditText inputPassw = (EditText) view.findViewById(R.id.inputPassw);
        Button submitButton = (Button) view.findViewById(R.id.addChildButton);

        inputDOB.setOnClickListener(v -> {
            showDatePickerDialog(inputDOB);
        });
        inputDOB.setFocusable(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString();
                String dob = inputDOB.getText().toString();
                String pb = inputPB.getText().toString();
                String note = inputNote.getText().toString();
                String passw = inputPassw.getText().toString();
                HomeParent activity = (HomeParent) getActivity();
                if (name.isEmpty() || dob.isEmpty() || pb.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
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