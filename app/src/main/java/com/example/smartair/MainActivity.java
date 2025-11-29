package com.example.smartair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final FirebaseDatabase db = FirebaseDatabase.getInstance();

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
    }

    public void launchSignIn(View v) {
        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
    }

    public void launchGetStartedPage(View v) {
        Intent i = new Intent(this, GetStartedActivity.class);
        startActivity(i);
    }


}
