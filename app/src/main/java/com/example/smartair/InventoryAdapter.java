package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import java.util.List;

public class InventoryAdapter extends ItemAdapter {
    public InventoryAdapter(List<Item> itemList) {
        super(itemList);
    }

    public static class InventoryViewHolder extends ItemViewHolder {
        TextView medName;
        TextView medChildName;
        TextView expiryDate;
        TextView purchaseDate;
        TextView amountLeft;
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
        InventoryAdapter.InventoryViewHolder inventoryHolder = (InventoryAdapter.InventoryViewHolder) holder;
        //TO BE COMPLETED ONCE SUBCLASS INVENTORY FINISHED //
        //inventoryHolder.medName.setText(item.getName());
    }
}
