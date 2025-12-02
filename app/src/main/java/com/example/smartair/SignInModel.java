package com.example.smartair;

import android.content.Intent;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInModel {

    FirebaseAuth myauth;
    FirebaseDatabase db;
    DatabaseReference myref;

    public SignInModel() {
        myauth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        myref = db.getReference();
    }

    public void signIn(SignInPresenter presenter, String user, String password) {
        myauth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(presenter.view.getActivity(), t -> {
                        if (t.isSuccessful()) {
                            checkUserRole(presenter, t.getResult().getUser().getUid());
                        } else {
                            presenter.showError("Email/Username or Password does not match our records.", "database");
                        }
                    });
    }

    public void checkUserRole(SignInPresenter presenter, String uid) {
        myref.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.child("parent-users").child(uid).exists()) {
                presenter.startSignIn("parent");
            } else if (dataSnapshot.child("provider-users").child(uid).exists()) {
                    presenter.startSignIn("provider");
            } else {
                presenter.startSignIn("child");
            }
        });
    }
}
