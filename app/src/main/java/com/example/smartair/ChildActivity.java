package com.example.smartair;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChildActivity extends AppCompatActivity {

    private FirebaseAuth myauth = FirebaseAuth.getInstance();

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
                        loadFragment(new HomeChildFragment());
                    }
                }
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomeChild.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    loadFragment(new HomeChildFragment());
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

    public FirebaseUser getUser() { return myauth.getCurrentUser(); }
}