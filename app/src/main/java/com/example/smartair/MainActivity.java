package com.example.smartair;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.util.ExtraConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseAuth myAuth = FirebaseAuth.getInstance();
//    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
//            new FirebaseAuthUIActivityResultContract(),
//            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
//                @Override
//                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
//                    onSignInResult(result);
//                }
//            }
//        );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        DatabaseReference myRef = db.getReference("medicineLogs");

//        MedicineLog m = new MedicineLog("2025-11-25T05:41:39.972299", "id1", "Better", 10, true, 1);
//        m.addItem("medicineLogs");
//        m.put("child-id", 11);
//        m.put("date", "2025-11-21");
//        m.put("name", "test");
//        myRef.push().setValue(m);
//        myRef.push().setValue(m);
//        myRef.push().setValue(m);
//        myRef.push().setValue(m);
//        myRef.push().setValue(m);
//        myRef.push().setValue(m);
//        myRef.push().setValue(m);
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



    public void launchSignIn(View v) {
        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
    }

//    public void createSignInIntent() {
//        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
//        Intent signInIntent = AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .build();
//        signInLauncher.launch(signInIntent);
//    }
//
//    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
//        IdpResponse response = result.getIdpResponse();
//        if (result.getResultCode() == RESULT_OK) {
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            System.out.println("Signed in");
//        } else {
//            System.out.println("Error sign in");
//        }
//    }
//
//    public void signOut() {
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        System.out.println("Signed out");
//                    }
//                });
//    }
//}

    public void signUp(String email, String password) {
        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(MainActivity.this, SignIn.class);
                            startActivity(i);
                        } else {
                            System.out.println("Error sign up");
                        }
                    }
                });
    }

    public void signIn(String email, String password) {
        myAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(MainActivity.this, SignIn.class);
                            startActivity(i);
                        } else {
                            System.out.println("Error sign in");
                        }
                    }
                });
    }

    public void test(View v) {
        EditText email = findViewById(R.id.test1);
        String emailt = email.getText().toString();
        EditText pass = findViewById(R.id.test2);
        String passt = pass.getText().toString();
        signUp(emailt, passt);
    }

    public void testt(View v) {
        EditText email = findViewById(R.id.test1);
        String emailt = email.getText().toString();
        EditText pass = findViewById(R.id.test2);
        String passt = pass.getText().toString();
        signIn(emailt, passt);
    }
}
