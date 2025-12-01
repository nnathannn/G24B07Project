package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import java.util.List;

public class TriggerListAdapter extends RecyclerView.Adapter<TriggerListAdapter.TriggerViewHolder> {

    private List<String> triggers;

    public TriggerListAdapter(List<String> triggers) {
        this.triggers = triggers;
    }

    public static class TriggerViewHolder extends RecyclerView.ViewHolder {
        public CardView cardViewTrigger;
        public TextView textTrigger;

        public TriggerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewTrigger = itemView.findViewById(R.id.cardViewTrigger);
            textTrigger = itemView.findViewById(R.id.textTrigger);
        }
    }

    @NonNull
    @Override
    public TriggerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_trigger_list, parent, false);
        return new TriggerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TriggerViewHolder holder, int position) {
        String trigger = triggers.get(position);
        holder.textTrigger.setText(trigger);   // ⭐ THIS LINE
    }

    @Override
    public int getItemCount() {
        return triggers != null ? triggers.size() : 0;
    }
}
