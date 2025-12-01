package com.example.smartair;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartair.databinding.ActivityHomeParentBinding;
import com.example.smartair.databinding.ActivityHomeProviderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeProvider extends AppCompatActivity {

    ActivityHomeProviderBinding binding;

    FirebaseAuth myauth = FirebaseAuth.getInstance();
    String providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        providerId = getUser().getUid();
        if (savedInstanceState == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("provider-users").child(providerId)
                    .child("first-run");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isFirstRun = snapshot.getValue(Boolean.class);
                    if (snapshot.getValue(Boolean.class) == null) {
                        isFirstRun = true;
                    }
                    if (isFirstRun) {
                        Bundle args = new Bundle();
                        args.putString("role", "provider");
                        View parentBottomNavView = findViewById(R.id.providerNavBar);
                        parentBottomNavView.setVisibility(View.INVISIBLE);
                        OnboardingFragment fragment = new OnboardingFragment();
                        fragment.setArguments(args);
                        replaceFragment(fragment);
                        ref.setValue(false);
                    } else {
                        replaceFragment(new HomeProviderFragment());
                    }
                }
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomeProvider.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    replaceFragment(new HomeParentFragment());
                }
            });
        }

        binding.providerNavBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.providerHome){
                replaceFragment(new HomeProviderFragment());
                return true;
            }
            else if(itemId == R.id.providerHistory){
                replaceFragment(new ProviderHistoryFragment());
                return true;
            }
            return false;
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.providerHomeLayout, fragment);
        fragmentTransaction.commit();
    }

    public FirebaseUser getUser() { return myauth.getCurrentUser(); }
}