package com.example.smartair;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.example.smartair.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeParent extends AppCompatActivity {
    String temporary_parent_id = "parent1";
    ActivityHomeParentBinding binding;

    FirebaseAuth myauth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeParentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("parent-users").child(temporary_parent_id)
                    .child("first-run");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isFirstRun = snapshot.getValue(Boolean.class);
                    if (snapshot.getValue(Boolean.class) == null) {
                        isFirstRun = true;
                    }
                    if (isFirstRun) {
                        Bundle args = new Bundle();
                        args.putString("role", "parent");
                        View parentBottomNavView = findViewById(R.id.parentBottomNavView);
                        parentBottomNavView.setVisibility(View.INVISIBLE);
                        OnboardingFragment fragment = new OnboardingFragment();
                        fragment.setArguments(args);
                        replaceFragment(fragment);
                        ref.setValue(false);
                    } else {
                        replaceFragment(new HomeParentFragment());
                    }
                }
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomeParent.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    replaceFragment(new HomeParentFragment());
                }
            });
        }

//        replaceFragment(new HomeParentFragment());

        binding.parentBottomNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home){
                replaceFragment(new HomeParentFragment());
                return true;
            }else if(itemId == R.id.provider){
                replaceFragment(new ManageProviderAccessFragment());
                return true;
            }
            else if(itemId == R.id.inventory){
                replaceFragment(new ParentInventoryFragment());
                return true;
            }
            else if(itemId == R.id.history){
                replaceFragment(new ParentHistoryFragment());
                return true;
            }
            return false;
        });

//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_home_parent);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.parent_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public FirebaseUser getUser() { return myauth.getCurrentUser(); }

}