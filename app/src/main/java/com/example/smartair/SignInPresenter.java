package com.example.smartair;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SignInPresenter {

    SignInFragment view;
    SignInModel model;

    public SignInPresenter(SignInFragment view, SignInModel model) {
        this.view = view;
        this.model = model;
    }

    public void attemptSignIn(String user, String password) {
        if (user.isEmpty() || password.isEmpty()) {
            Toast.makeText(view.getContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
        } else {
            if (!user.contains("@")) {
                user += "@g24b07project.examplefakedomain";
            }
            model.signIn(this, user, password);
        }
    }

    public void startSignIn(String role) {
        Intent i;
        if (role.equals("parent")) {
            i = new Intent(view.getContext(), HomeParent.class);
        } else if (role.equals("provider")) {
            i = new Intent(view.getContext(), HomeProvider.class);
        } else {
            i = new Intent(view.getContext(), ChildActivity.class);
        }
        view.startActivity(i);
    }

    public void loadFragment(String fragment) {
        if (fragment.equals("sign_up")) {
            replaceFragment(new RoleSelectionFragment());
        } else if (fragment.equals("recovery")) {
            replaceFragment(new AccountRecoveryFragment());
        } else {
            Toast.makeText(view.getContext(), "Invalid fragment", Toast.LENGTH_LONG).show();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = view.getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.GetStartedContainer, fragment);
        fragmentTransaction.commit();
    }
}
