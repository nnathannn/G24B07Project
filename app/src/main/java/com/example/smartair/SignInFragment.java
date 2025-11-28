package com.example.smartair;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = getActivity().findViewById(R.id.GetStartedTitle);
        view.setText("Welcome Back");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_sign_in, container, false);
        EditText user = view.findViewById(R.id.sign_in_user);
        EditText password = view.findViewById(R.id.sign_in_password);
        CheckBox check = view.findViewById(R.id.sign_in_check);
        Button submit = view.findViewById(R.id.sign_in_submit);
        TextView sign_up = view.findViewById(R.id.sign_up);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userText = user.getText().toString();
                String passwordText = password.getText().toString();
                GetStartedActivity activity = (GetStartedActivity) getActivity();
                activity.signIn(userText, passwordText);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new RoleSelectionFragment());
            }
        });


        return view;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.GetStartedContainer, fragment);
        fragmentTransaction.commit();
    }
}