package com.example.smartair;

import android.annotation.SuppressLint;
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

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<MedicineLog> medicineLogs;

    public MedicineAdapter(List<MedicineLog> medicineLogs) {
        this.medicineLogs = medicineLogs;
    }
    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView textMedicineType, textMedicineDate, textStatus, textPrePostRating;
        CardView cardViewMedicine, cardViewStatus;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            textMedicineType = itemView.findViewById(R.id.textMedicineType);
            textMedicineDate = itemView.findViewById(R.id.textMedicineDate);
            textStatus = itemView.findViewById(R.id.textStatus);
            textPrePostRating = itemView.findViewById(R.id.textPrePostRating);
            cardViewStatus = itemView.findViewById(R.id.cardStatus);
            cardViewMedicine = itemView.findViewById(R.id.cardViewMedicine);
        }
    }

    @NonNull
    @Override
    public MedicineAdapter.MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MedicineAdapter.MedicineViewHolder holder, int position) {
        MedicineLog medicineLog = medicineLogs.get(position);
        String medicineType;
        if (medicineLog.getRescue()) {
            medicineType = "Rescue";
            holder.cardViewMedicine.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#45A0F4")));
        }
        else {
            medicineType = "Controller";
            holder.cardViewMedicine.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#176EBE")));
        }

        // dose text
        String doseText = medicineType + " Dose: " + Integer.toString(medicineLog.getDose());
        holder.textMedicineType.setText(doseText);
        // pre post text
        String prePostText = "Pre Check Rating: " + Integer.toString(medicineLog.getPreStatus()) + "\nPost Check Rating: " + Integer.toString(medicineLog.getPostStatus());
        holder.textPrePostRating.setText(prePostText);
        // date text
        LocalDateTime dateTime = LocalDateTime.parse(medicineLog.getDate());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
        holder.textMedicineDate.setText(dateTime.format(formatter));
        // status text and card color
        if (medicineLog.getPrePostStatus().equals("Worse")) {
            holder.textStatus.setText("Worse");
            holder.cardViewStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EC3131")));
        }
        else if (medicineLog.getPrePostStatus().equals("Same")) {
            holder.textStatus.setText("Same");
            holder.cardViewStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F4C945")));
        }
        else {
            holder.textStatus.setText("Better");
            holder.cardViewStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#31D219")));
        }
    }

    @Override
    public int getItemCount() {
        return medicineLogs != null ? medicineLogs.size() : 0;
    }
}
