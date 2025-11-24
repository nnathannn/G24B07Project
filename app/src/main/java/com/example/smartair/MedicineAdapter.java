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

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private List<MedicineLog> medicineLogs;

    public MedicineAdapter(List<MedicineLog> medicineLogs) {
        this.medicineLogs = medicineLogs;
    }
    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView textCardViewMedicineType, textCardViewMedicineDate,
                textCardViewMedicineCheck, textCardViewMedicineRating;
        CardView cardViewMedicine;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            textCardViewMedicineType = itemView.findViewById(R.id.textCardViewMedicineType);
            textCardViewMedicineDate = itemView.findViewById(R.id.textCardViewMedicineDate);
            textCardViewMedicineCheck = itemView.findViewById(R.id.textCardViewMedicineCheck);
            textCardViewMedicineRating = itemView.findViewById(R.id.textCardViewMedicineRating);
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

        String doseText = medicineType + " Dose: " + Integer.toString(medicineLog.getDose());
        holder.textCardViewMedicineType.setText(doseText);
        holder.textCardViewMedicineRating.setText("Short Breath Rating: " + Double.toString(medicineLog.getRating()));
        LocalDateTime dateTime = LocalDateTime.parse(medicineLog.getDate());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
        holder.textCardViewMedicineDate.setText(dateTime.format(formatter));
        if (medicineLog.getPrePostStatus().equals("Worse")) {
            holder.textCardViewMedicineCheck.setText("Worse");
            holder.textCardViewMedicineCheck.setBackgroundColor(Color.parseColor("#EC3131"));
        }
        else if (medicineLog.getPrePostStatus().equals("Same")) {
            holder.textCardViewMedicineCheck.setText("Same");
            holder.textCardViewMedicineCheck.setBackgroundColor(Color.parseColor("#F4C945"));
        }
        else {
            holder.textCardViewMedicineCheck.setText("Better");
            holder.textCardViewMedicineCheck.setBackgroundColor(Color.parseColor("#31D219"));
        }
    }

    @Override
    public int getItemCount() {
        return medicineLogs != null ? medicineLogs.size() : 0;
    }
}
