package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TriageHomeStepsAdapter extends RecyclerView.Adapter<TriageHomeStepsAdapter.TriageHomeStepsViewHolder> {
    private List<String> steps;

    public TriageHomeStepsAdapter(List<String> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public TriageHomeStepsAdapter.TriageHomeStepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_home_steps, parent, false);
        return new TriageHomeStepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TriageHomeStepsViewHolder holder, int position) {
        holder.stepTitle.setText("Step " + (position + 1));
        holder.stepDesc.setText(steps.get(position));
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    public static class TriageHomeStepsViewHolder extends RecyclerView.ViewHolder {
        TextView stepTitle, stepDesc;

        public TriageHomeStepsViewHolder(View itemView) {
            super(itemView);
            stepTitle = itemView.findViewById(R.id.step_title);
            stepDesc = itemView.findViewById(R.id.step_desc);
        }
    }

}
