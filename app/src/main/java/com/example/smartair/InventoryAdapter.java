package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class InventoryAdapter extends ItemAdapter {

    public interface OnItemClickListener {
        void onItemClick(String clickedString);
    }
     private final OnItemClickListener listener;

    public InventoryAdapter(List<Item> itemList, OnItemClickListener listener) {
        super(itemList);
        this.listener = listener;
    }
    public static class InventoryViewHolder extends ItemViewHolder {
        TextView medName, medChildName, expiryDate, purchaseDate, amountLeft;
        CardView cardView;

        public InventoryViewHolder(@NonNull View view) {
            super(view);
            this.medName = view.findViewById(R.id.medName);
            this.medChildName = view.findViewById(R.id.medChild);
            this.purchaseDate = view.findViewById(R.id.purchaseDate);
            this.expiryDate = view.findViewById(R.id.expiryDate);
            this.amountLeft = view.findViewById(R.id.amountLeft);
            this.cardView = (CardView) itemView;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_card, parent, false);
        return (ItemViewHolder) new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = getItemList().get(position);
        Inventory inventory = (Inventory) item;
        InventoryAdapter.InventoryViewHolder inventoryHolder = (InventoryAdapter.InventoryViewHolder) holder;
        String childId = inventory.getChildId();
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("child-users").child(childId);
        childRef.child("username").get().addOnSuccessListener(dataSnapshot -> {
            String childName = dataSnapshot.getValue(String.class);
            inventoryHolder.medChildName.setText(childName);
        });
        //TO BE EDITED
        inventoryHolder.medName.setText("tes aja ni");
        inventoryHolder.purchaseDate.setText(inventory.getDate());
        inventoryHolder.expiryDate.setText(inventory.getExpirydate());
        inventoryHolder.amountLeft.setText(Double.toString(inventory.getAmountLeft()));
    }
}
