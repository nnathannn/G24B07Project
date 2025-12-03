package com.example.smartair;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
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
        setContentView(R.layout.activity_get_started);

        if (myAuth.getCurrentUser() != null) {
            String uid = myAuth.getCurrentUser().getUid();
            startUserPage(uid);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.GetStartedContainer, new RoleSelectionFragment());
        fragmentTransaction.commit();
    }

    public void signUp(String email, String password, String role, String name) {
        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            logUser(role, name);
                            Intent i = null;
                            if (role.equals("parent")) {
                                i = new Intent(GetStartedActivity.this, HomeParent.class);
                            } else if (role.equals("provider")) {
                                i = new Intent(GetStartedActivity.this, HomeProvider.class);
                            } else {
                                new Intent(GetStartedActivity.this, ChildActivity.class);
                            }
                            startActivity(i);
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
                            startUserPage(task.getResult().getUser().getUid());
                        } else {
                            Toast.makeText(GetStartedActivity.this, "Email/Username and Password does not match our records.", Toast.LENGTH_LONG).show();
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
    public boolean passwordCheck(String password) {
        if (password.length() < 8) {
            Toast.makeText(GetStartedActivity.this, "Password must be 8 characters minimum", Toast.LENGTH_LONG).show();
            return false;
        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#]).*")) {
            Toast.makeText(GetStartedActivity.this, "Weak password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean emailCheck(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z]+\\.[a-zA-Z]+$")) {
            Toast.makeText(GetStartedActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void startUserPage(String uid) {
        db.getReference().get().addOnSuccessListener(dataSnapshot -> {
            String role;
            Intent i = null;
            if (dataSnapshot.child("parent-users").child(uid).exists()) {
                i = new Intent(this, HomeParent.class);
            } else if (dataSnapshot.child("provider-users").child(uid).exists()) {
                i = new Intent(this, HomeProvider.class);
            } else {
                i = new Intent(this, ChildActivity.class);
            }
            startActivity(i);
        });
    }
}