package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder> {
    //Complete in future updates
    private List<Item> itemList;

    public BadgeAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    public static class BadgeViewHolder extends RecyclerView.ViewHolder {
        //Complete in future updates
        TextView date;

        public BadgeViewHolder(@NonNull View view) {
            super(view);
            this.date = view.findViewById(R.id.textView2);
        }
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.badge_view_holder, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.date.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}