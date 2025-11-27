package com.example.smartair;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class GetStartedActivity extends AppCompatActivity {

    private FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.GetStartedContainer, new RoleSelectionFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = myAuth.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Signed in");
            startActivity(new Intent(this, SignIn.class));
        }
    }

    public void signUp(String email, String password, String role, String name) {
        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            logUser(role, name);
                            Intent i = new Intent(GetStartedActivity.this, SignIn.class);
                            startActivity(i);
                        } else {
                            System.out.println("Error sign up");
                        }
                    }
                });
    }

    public void signIn(String user, String password) {
        String email = checkAccount(user);
        myAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(GetStartedActivity.this, SignIn.class);
                            startActivity(i);
                        } else {
                            System.out.println("Error sign in");
                        }
                    }
                });
    }

    private void logUser(String role, String name) {
        String uid = myAuth.getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("first-run", true);
        data.put("name", name);
        if (role.equals("parent")) {
            data.put("child-ids", "");
            db.getReference("parent-users").child(uid).setValue(data);
        } else if (role.equals("provider")) {
            data.put("access", "");
            db.getReference("provider-users").child(uid).setValue(data);
        } else {
            db.getReference("child-users").child(uid).setValue(data);
        }
    }

    private String checkAccount(String user) {
        if (user.contains("@")) {
            return user;
        } else {
            return user + "@g24b07project.examplefakedomain";
        }

    }
}