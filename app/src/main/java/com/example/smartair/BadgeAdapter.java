package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BadgeAdapter extends ItemAdapter {

    public BadgeAdapter(List<Item> itemList) {
        super(itemList);
    }

    public static class BadgeViewHolder extends ItemViewHolder {
        //Complete in future updates
        TextView date;

        public BadgeViewHolder(@NonNull View view) {
            super(view);
            this.date = view.findViewById(R.id.textView2);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.badge_view_holder, parent, false);
        return (ItemViewHolder) new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = getItemList().get(position);
        BadgeViewHolder badgeholder = (BadgeViewHolder) holder;
        badgeholder.date.setText(item.getDate());
    }

}