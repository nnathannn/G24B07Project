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
import android.widget.Toast;

import org.w3c.dom.Text;

public class SignInFragment extends Fragment {

    SignInPresenter presenter;
    EditText user, password;
    Button submit;
    TextView sign_up, recovery;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = getActivity().findViewById(R.id.GetStartedTitle);
        view.setText("Welcome Back");
        presenter = new SignInPresenter(this, new SignInModel());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        user = view.findViewById(R.id.sign_in_user);
        password = view.findViewById(R.id.sign_in_password);
        submit = view.findViewById(R.id.sign_in_submit);
        sign_up = view.findViewById(R.id.sign_up);
        recovery = view.findViewById(R.id.Recovery);

        submit.setOnClickListener(v -> {
                String userText = user.getText().toString();
                String passwordText = password.getText().toString();
                presenter.attemptSignIn(userText, passwordText);
        });

        sign_up.setOnClickListener(v -> { replaceFragment(new RoleSelectionFragment()); });

        recovery.setOnClickListener(v -> { replaceFragment(new AccountRecoveryFragment()); });

        return view;
    }

    public void displayErrorToast(String message) { Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show(); }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.GetStartedContainer, fragment);
        fragmentTransaction.commit();
    }
}