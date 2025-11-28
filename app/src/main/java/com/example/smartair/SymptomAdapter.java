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
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        public SymptomViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewSymptom = itemView.findViewById(R.id.cardViewSymptom);
            textSymptom = itemView.findViewById(R.id.textSymptom);
            textSymptomDate = itemView.findViewById(R.id.textSymptomDate);
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

        //date text
        LocalDateTime dateTime = LocalDateTime.parse(symptom.getDate());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        holder.textSymptomDate.setText(dateTime.format(formatter));





    }

@Override
public int getItemCount() {
        return symptoms != null ? symptoms.size() : 0;
        }



}
