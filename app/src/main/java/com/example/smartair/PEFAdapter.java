package com.example.smartair;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PEFAdapter extends RecyclerView.Adapter<PEFAdapter.PEFViewHolder> {
    private List<Zone> zones;

    public PEFAdapter(List<Zone> zones) {
        this.zones = zones;
    }
    public static class PEFViewHolder extends RecyclerView.ViewHolder {
        TextView date, PB, count, percent;
        CardView cardView;

        public PEFViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.pef_date);
            PB = itemView.findViewById(R.id.pef_curPB);
            count = itemView.findViewById(R.id.pef_count);
            percent = itemView.findViewById(R.id.pef_percentage);
            cardView = itemView.findViewById(R.id.cardPEF);
        }
    }

    @NonNull
    @Override
    public PEFAdapter.PEFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_pef, parent, false);
        return new PEFViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PEFAdapter.PEFViewHolder holder, int position) {
        Zone zone = zones.get(position);
        String date = zone.getDate();
        String newDate = date.substring(0, 10) + " " + date.substring(11, 16);
        double pef = zone.getCount();
        double curPB = zone.getCurPB();
        String status = zone.getStatus();
        int percent = (int) (pef / curPB * 100);
        holder.date.setText(newDate);
        holder.PB.setText("PB: " + curPB);
        holder.count.setText("PEF: " + pef);
        holder.percent.setText(percent + "%");
        if (status.equals("Green")) {
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#48D718")));;
        } else if (status.equals("Yellow")) {
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F4C945")));
        } else {
            holder.cardView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EC3131")));
        }
    }

    @Override
    public int getItemCount() {
        return zones != null ? zones.size() : 0;
    }
}