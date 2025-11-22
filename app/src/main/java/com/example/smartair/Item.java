package com.example.smartair;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public abstract class Item {
    private LocalDateTime date;
    private String id;

    public Item() {}
    public Item(LocalDateTime date, String id) {
        this.date = date;
        this.id = id;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    public Item(String date, String id) throws DateTimeParseException {
        this.id = id;
        try {
            this.date = LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            this.date = null;
        }
    }

    public LocalDateTime getDate() { return date; }

    public String getId() { return id; }

    public void setDate(LocalDateTime date) { this.date = date; }

    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDate(String date) throws DateTimeParseException {
        try {
            this.date = LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            this.date = null;
        }
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
        DatabaseReference ref = db.getReference(path);
        ref.child(itemId).removeValue();
    }

    public void setId(String id) { this.id = id; }
}