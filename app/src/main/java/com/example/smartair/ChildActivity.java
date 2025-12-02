package com.example.smartair;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChildActivity extends AppCompatActivity implements UIDProvider {

    private String childId;
    private FirebaseAuth myauth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_child);

        checkUser();

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
                        args.putString("childId", childId);
                        OnboardingFragment fragment = new OnboardingFragment();
                        fragment.setArguments(args);
                        loadFragment(fragment);
                        ref.setValue(false);
                    } else {
                        HomeChildFragment fragment = new HomeChildFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("childId", childId);
                        fragment.setArguments(bundle);
                        loadFragment(fragment);
                    }
                }
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChildActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    HomeChildFragment fragment = new HomeChildFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("childId", childId);
                    fragment.setArguments(bundle);
                    loadFragment(fragment);
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

    public String getUid() { return childId; }

    public void checkUser() {
        if (getIntent().getStringExtra("childId") == null) {
            childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            childId = getIntent().getStringExtra("childId");
        }
    }
}