package com.example.smartair;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {
    //Pair: first is inventory item, second is inventory ID
    public interface OnItemClickListener {
        void onItemClick(Pair<Inventory, String> clickedInventory);
    }
    private final OnItemClickListener listener;
    private List<Pair<Inventory, String>> itemList;

    public InventoryAdapter(List<Pair<Inventory, String>> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView medName, medChildName, expiryDate, purchaseDate, amountLeft, lastUpdated;
        CardView cardView;

        public InventoryViewHolder(@NonNull View view) {
            super(view);
            this.medName = view.findViewById(R.id.medName);
            this.medChildName = view.findViewById(R.id.medChild);
            this.purchaseDate = view.findViewById(R.id.purchaseDate);
            this.expiryDate = view.findViewById(R.id.expiryDate);
            this.amountLeft = view.findViewById(R.id.amountLeft);
            this.lastUpdated = view.findViewById(R.id.lastUpdatedBy);
            this.cardView = (CardView) itemView;
        }
        public void bind(Pair<Inventory, String> inventory, OnItemClickListener listener) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(inventory);
                }
            });
        }
    }

    @NonNull
    @Override
    public InventoryAdapter.InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_card, parent, false);
        return new InventoryAdapter.InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryAdapter.InventoryViewHolder holder, int position) {
        Pair<Inventory, String> item = itemList.get(position);
        Inventory inventory = (Inventory) item.first;
        String inventoryId = item.second;
        InventoryAdapter.InventoryViewHolder inventoryHolder = (InventoryAdapter.InventoryViewHolder) holder;
        String childId = inventory.getChildId();
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("child-users").child(childId);
        childRef.child("name").get().addOnSuccessListener(dataSnapshot -> {
            String childName = dataSnapshot.getValue(String.class);
            if(childName!=null){
                inventoryHolder.medChildName.setText(childName);
            }
            else{
                inventoryHolder.medChildName.setText("Child Name");
            }
        }).addOnFailureListener(e -> {
            inventoryHolder.medChildName.setText("Child Name");
        });
        if(inventory.getRescue()){
            if(inventory.getMedName()==null || inventory.getMedName().isEmpty() || inventory.getMedName().equals("null")){
                inventoryHolder.medName.setText("Rescue");
            }
            else{
                inventoryHolder.medName.setText("Rescue | " + inventory.getMedName());
            }
        }
        else{
            if(inventory.getMedName()==null || inventory.getMedName().isEmpty() || inventory.getMedName().equals("null")){
                inventoryHolder.medName.setText("Controller");
            }
            else{
                inventoryHolder.medName.setText("Controller | " + inventory.getMedName());
            }
        }
        if (inventory.getPurchaseDate()==null || inventory.getPurchaseDate().isEmpty() || inventory.getPurchaseDate().equals("null")){
            inventoryHolder.purchaseDate.setText("Purchase date not set yet");
        }
        else{
            inventoryHolder.purchaseDate.setText("Purchase Date: " + inventory.getPurchaseDate());
        }
        if (inventory.getExpiryDate()==null || inventory.getExpiryDate().isEmpty() || inventory.getExpiryDate().equals("null")){
            inventoryHolder.expiryDate.setText("Expiry date not set yet");
        }
        else{
            inventoryHolder.expiryDate.setText("Expiry Date: " + inventory.getExpiryDate());
        }
        if (inventory.getUpdatedBy()==null || inventory.getUpdatedBy().isEmpty() || inventory.getUpdatedBy().equals("null")){
            inventoryHolder.lastUpdated.setText("Inventory has not been updated");
        }
        else{
            inventoryHolder.lastUpdated.setText("Last Updated by: " + inventory.getUpdatedBy());
        }
        inventoryHolder.amountLeft.setText("Amount Left: " + Double.toString(inventory.getAmountLeft()));
        inventoryHolder.bind(new Pair<>(inventory, inventoryId), listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
