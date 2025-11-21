package com.example.smartair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
//        DatabaseReference myRef = db.getReference("badge");
//
//        Map<String, Object> m = new HashMap<>();
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void launchSignIn(View v) {
        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
    }


}