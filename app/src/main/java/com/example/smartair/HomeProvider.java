package com.example.smartair;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
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

public class HomeProvider extends AppCompatActivity {

    ActivityHomeProviderBinding binding;

    FirebaseAuth myauth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeProviderFragment());

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

//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_home_provider);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.providerHomeLayout, fragment);
        fragmentTransaction.commit();
    }

    public FirebaseUser getUser() { return myauth.getCurrentUser(); }
}