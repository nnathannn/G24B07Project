package com.example.smartair;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.PropertyName;

public class Item {
    private String date;

    @PropertyName("child-id")
    private String childId;
    @PropertyName("item-id")
    private String itemId; // unique key

    public Item() {}
    // String date needs to be in the format "yyyy-MM-ddTHH:mm:ss" with the letter 'T' as delimiter
    public Item(String date, String childId) {
        this.date = date;
        this.childId = childId;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    @PropertyName("child-id")
    public String getChildId() { return childId; }
    @PropertyName("child-id")
    public void setChildId(String childId) { this.childId = this.childId; }
    @PropertyName("item-id")
    public String getItemId() { return itemId; }
    @PropertyName("item-id")
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
