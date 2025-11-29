package com.example.smartair;

import static java.security.AccessController.getContext;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TriggerListAdapter extends RecyclerView.Adapter<TriggerListAdapter.TriggerViewHolder> {

    private List<String> triggers;
    private RecyclerView recyclerView;


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
    public TriggerListAdapter.TriggerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_trigger_list, parent, false);
        return new TriggerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull TriggerListAdapter.TriggerViewHolder holder, int position) {
        String trigger = triggers.get(position);


    }

    @Override
    public int getItemCount() {
        return triggers != null ? triggers.size() : 0;
    }





















}
