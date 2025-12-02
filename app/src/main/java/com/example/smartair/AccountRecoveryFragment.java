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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


public class AccountRecoveryFragment extends Fragment {

    public AccountRecoveryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = getActivity().findViewById(R.id.GetStartedTitle);
        view.setText("Recover Your Account");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_recovery, container, false);

        EditText email = view.findViewById(R.id.recovery_email);
        Button submit = view.findViewById(R.id.recovery_submit);

        submit.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailText).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Email sent", Toast.LENGTH_LONG).show();
                }
            });
            replaceFragment(new SignInFragment());
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