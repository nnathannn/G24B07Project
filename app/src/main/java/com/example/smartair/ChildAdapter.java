package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(String clickedString);
    }

    private final List<String> childList;
    private final OnItemClickListener listener;
    private List<Boolean> redZone;

    public ChildAdapter(List<String> childList, OnItemClickListener listener, List<Boolean> redZone) {
        this.childList = childList;
        this.listener = listener;
        this.redZone = redZone;
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public CardView cardView;
        public LinearLayout cardBackground;

        public ChildViewHolder(View childView) {
            super(childView);
            nameTextView = childView.findViewById(R.id.medName);
            cardView = childView.findViewById(R.id.child_card);
            cardBackground = childView.findViewById(R.id.child_card_background);
        }

        public void bind(String childString, OnItemClickListener listener) {
            nameTextView.setText(childString);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(childString);
                }
            });
        }
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View childView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_card, parent, false);
        return new ChildViewHolder(childView);
    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, int position) {
        String childString = childList.get(position);
        holder.bind(childString, listener);

        if (redZone == null) {
            holder.cardBackground.setBackgroundResource(R.drawable.gradient_blue);
            return;
        }
        if (redZone.size() > position && redZone.get(position)) holder.cardBackground.setBackgroundResource(R.drawable.gradient_red);
        else holder.cardBackground.setBackgroundResource(R.drawable.gradient_blue);
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

}
