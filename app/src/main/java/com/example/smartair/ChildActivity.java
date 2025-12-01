package com.example.smartair;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChildActivity extends AppCompatActivity implements UIDProvider {

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_child);

        checkUser();

        if (savedInstanceState == null) {
            loadFragment(new HomeChildFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public String getUid() { return uid; }

    public void checkUser() {
        if (getIntent().getStringExtra("childId") == null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = getIntent().getStringExtra("childId");
        }
    }
}