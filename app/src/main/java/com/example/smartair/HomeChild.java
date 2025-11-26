package com.example.smartair;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeChild extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_child);
        EdgeToEdge.enable(this);
        String childId = "id1"; //TO BE UPDATED
        if (savedInstanceState == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("child-users").child(childId)
                    .child("first-run");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isFirstRun = snapshot.getValue(Boolean.class);
                    if (snapshot.getValue(Boolean.class) == null) {
                        isFirstRun = true;
                    }
                    if (isFirstRun) {
                        Bundle args = new Bundle();
                        args.putString("role", "child");
                        OnboardingFragment fragment = new OnboardingFragment();
                        fragment.setArguments(args);
                        loadFragment(fragment);
                        ref.setValue(false);
                    } else {
                        loadFragment(new HomePageFragment());
                    }
                }
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomeChild.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    loadFragment(new HomePageFragment());
                }
            });
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}