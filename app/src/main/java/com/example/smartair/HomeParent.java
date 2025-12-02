package com.example.smartair;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class HomeParent extends AppCompatActivity implements UIDProvider {

    ActivityHomeParentBinding binding;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseAuth myauth = FirebaseAuth.getInstance();
    String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeParentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getUser() == null){
            Toast.makeText(this, "Error: Parent session lost.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, GetStartedActivity.class));
            return;
        }
        parentId = getUser().getUid();

        if (savedInstanceState == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("parent-users").child(parentId)
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

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.parent_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public String getUid() { return parentId; }

    public FirebaseUser getUser() { return myauth.getCurrentUser(); }

    public void signUp(final String email, final String password, final String name, final String date, final int pb, final String notes) {
        FirebaseUser currentParentUser = myauth.getCurrentUser();
        if (currentParentUser == null) {
            Toast.makeText(this, "Error: Parent session lost.", Toast.LENGTH_LONG).show();
            return;
        }

        currentParentUser.getIdToken(false).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                myauth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(HomeParent.this, "The username is already in use by another account.", Toast.LENGTH_LONG).show();
                                } else {
                                    if (task.isSuccessful()) {
                                        AuthResult authResult = task.getResult();
                                        FirebaseUser newUser = authResult.getUser();
                                        assert newUser != null;
                                        String newUid = newUser.getUid();
                                        Toast.makeText(HomeParent.this, "New child added succesfully.", Toast.LENGTH_LONG).show();
                                        logChildUser(newUid, name, date, pb, notes);
                                        myauth.updateCurrentUser(currentParentUser)
                                                .addOnCompleteListener(HomeParent.this, updateTask -> {
                                                    if (updateTask.isSuccessful()) {
                                                        Toast.makeText(HomeParent.this, "Parent session restored.", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(HomeParent.this, "Parent session failed to restore. Please sign in again.", Toast.LENGTH_LONG).show();
                                                        myauth.signOut();
                                                        startActivity(new Intent(HomeParent.this, GetStartedActivity.class));
                                                    }
                                                    replaceFragment(new HomeParentFragment());
                                                });
                                    } else {
                                        Toast.makeText(HomeParent.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        replaceFragment(new HomeParentFragment());
                                    }
                                }
                            }
                        });
            } else {
                Toast.makeText(HomeParent.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logChildUser(String uid, String name, String date, int pb, String notes) {
        Map<String, Object> data = new HashMap<>();
        data.put("first-run", true);
        data.put("name", name);
        data.put("DOB", date);
        data.put("PB", pb);
        data.put("notes", notes);
        db.getReference("child-users").child(uid).setValue(data);
        String rescue= db.getReference("child-inventory").child(uid).push().getKey();
        String controller = db.getReference("child-inventory").child(uid).push().getKey();
        if (rescue != null) {
            db.getReference("child-inventory").child(uid).child(rescue).setValue(true);
            db.getReference("inventory").child(rescue).child("child-id").setValue(uid);
            db.getReference("inventory").child(rescue).child("rescue").setValue(true);
        }
        if (controller != null) {
            db.getReference("child-inventory").child(uid).child(controller).setValue(true);
            db.getReference("inventory").child(controller).child("child-id").setValue(uid);
            db.getReference("inventory").child(controller).child("rescue").setValue(false);
        }
        db.getReference("parent-users").child(parentId).child("child-ids").child(uid).setValue(true);
        db.getReference("badge").child(uid).child("high-quality").child("completed").setValue(false);
        db.getReference("badge").child(uid).child("high-quality").child("treshold").setValue(10);
        db.getReference("badge").child(uid).child("low-rescue").child("completed").setValue(false);
        db.getReference("badge").child(uid).child("low-rescue").child("treshold").setValue(4);
        db.getReference("badge").child(uid).child("perfect-controller").child("completed").setValue(false);
        db.getReference("badge").child(uid).child("perfect-controller").child("treshold").setValue(7);
    }

    public boolean passwordCheck(String password) {
        if (password.length() < 8) {
            Toast.makeText(HomeParent.this, "Password must be 8 characters minimum", Toast.LENGTH_LONG).show();
            return false;
        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#]).*")) {
            Toast.makeText(HomeParent.this, "Weak password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean usernameCheck(String username) {
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            Toast.makeText(HomeParent.this, "Invalid username", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}