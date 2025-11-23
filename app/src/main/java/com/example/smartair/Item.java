package com.example.smartair;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Item {
    private LocalDateTime date;

    public Item() {}

    public Item(LocalDateTime date) { this.date = date; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    public Item(String date) throws DateTimeParseException {
        try {
            this.date = LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            this.date = null;
        }
    }

    public LocalDateTime getDate() { return date; }

    // Returns the date as a string in the format "yyyy-MM-ddTHH:mm:ss"
    public String getDateString() { return date.toString(); }

    public void setDate(LocalDateTime date) { this.date = date; }

    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    public void setDate(String date) throws DateTimeParseException {
        try {
            this.date = LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
            this.date = null;
public class Item {
    private String date;
    private String childId;
    private String itemId; // unique key

    public Item() {}
    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    public Item(String date, String childId) {
        this.date = date;
        this.childId = childId;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = this.childId; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }


    public void addItem(String path) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
        DatabaseReference ref = db.getReference(path);
        String itemsId = ref.push().getKey();
        if (itemsId == null) { return; }
        setItemId(itemsId);
        ref.child(itemsId).setValue(this);
    }

    public void deleteItem(String path) {
        if (itemId == null) {
            return;
        }
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
        DatabaseReference ref = db.getReference(path);
        ref.child(itemId).removeValue();
    }
}
