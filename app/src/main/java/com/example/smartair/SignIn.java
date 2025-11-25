package com.example.smartair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        System.out.println(myAuth.getCurrentUser());
    }

    public void launchChild(View v) {
        Intent i = new Intent(this, HomeChild.class);
        startActivity(i);
    }

    public void launchParent(View v) {
        Intent i = new Intent(this, HomeParent.class);
        startActivity(i);
    }

    public void launchProvider(View v) {
        Intent i = new Intent(this, HomeProvider.class);
        startActivity(i);
    }

    public void signOut(View v) {
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        myAuth.signOut();
        System.out.println("Signed out");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}