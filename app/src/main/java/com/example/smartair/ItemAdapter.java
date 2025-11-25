package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public abstract class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    //Complete in future updates
    private static final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View view) {
            super(view);
        }
    }

    @NonNull
    public abstract ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @NonNull
    public abstract void onBindViewHolder(@NonNull ItemViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return itemList.size();
    }

     static void fetchData(ItemAdapter itemAdapter, String path) {
        DatabaseReference itemsRef = db.getReference(path);
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemAdapter.getItemList().clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    itemAdapter.getItemList().add(item);
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }
}