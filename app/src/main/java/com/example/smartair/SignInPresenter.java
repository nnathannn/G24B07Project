package com.example.smartair;

import static java.security.AccessController.getContext;

import android.app.Activity;
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
            showError("Please fill in all fields.", "logic");
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

    public void showError(String message, String type) {
        if (type.equals("logic")) {
            message = "Logic error: " + message;
         } else if (type.equals("database")) {
            message = "Database error: " + message;
        }
        view.displayErrorToast(message);
    }

    public Activity getActivity() { return view.getActivity(); }
}
