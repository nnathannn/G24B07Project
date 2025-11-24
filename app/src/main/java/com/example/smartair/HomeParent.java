package com.example.smartair;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smartair.databinding.ActivityHomeParentBinding;
import com.example.smartair.databinding.ActivityMainBinding;

public class HomeParent extends AppCompatActivity {
    String temporary_parent_id = "parent1";
    ActivityHomeParentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeParentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeParentFragment());

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

}