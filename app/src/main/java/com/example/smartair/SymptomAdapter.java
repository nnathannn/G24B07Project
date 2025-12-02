package com.example.smartair;

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
import java.util.ArrayList;
import java.util.List;

public class SymptomAdapter extends RecyclerView.Adapter<SymptomAdapter.SymptomViewHolder> {

    private List<Symptom> symptoms;

    public SymptomAdapter(List<Symptom> symptoms){
        this.symptoms = symptoms;
    }

    public static class SymptomViewHolder extends RecyclerView.ViewHolder{
        public CardView cardViewSymptom;
        public TextView textSymptom;
        public TextView textSymptomDate;
        public List<String> triggerList = new ArrayList<>();
        public TriggerListAdapter adapter;
        public RecyclerView recycler;

        public SymptomViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewSymptom = itemView.findViewById(R.id.cardViewSymptom);
            textSymptom = itemView.findViewById(R.id.textSymptom);
            textSymptomDate = itemView.findViewById(R.id.textSymptomDate);

            recycler = itemView.findViewById(R.id.recyclerViewSymptoms);
            triggerList = new ArrayList<>();

            recycler.setHasFixedSize(true);
            recycler.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext(),
                            LinearLayoutManager.HORIZONTAL, false));
            adapter = new TriggerListAdapter(triggerList);
            recycler.setAdapter(adapter);
        }


    }

    @NonNull
    @Override
    public SymptomAdapter.SymptomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_symptom, parent, false);
        return new SymptomViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull SymptomAdapter.SymptomViewHolder holder, int position) {
        Symptom symptom = symptoms.get(position);

        // triggers
        holder.triggerList.clear();
        if (symptom.getTriggerList() != null) {
            holder.triggerList.addAll(symptom.getTriggerList());
        }
        holder.adapter.notifyDataSetChanged();

        // symptom name
        holder.textSymptom.setText(symptom.getName());

        // ---- formatted date ----
        String rawDate = symptom.getDate();
        String displayDate = rawDate;   // fallback

        if (rawDate != null) {
            try {
                LocalDateTime dt = LocalDateTime.parse(rawDate); // parses ISO string
                DateTimeFormatter fmt =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                displayDate = dt.format(fmt);
            } catch (Exception ignored) {
                // if some old row is just "2025-11-19", we just show raw string
            }
        }

        holder.textSymptomDate.setText(displayDate);
    }






    @Override
public int getItemCount() {
        return symptoms != null ? symptoms.size() : 0;
        }



}
