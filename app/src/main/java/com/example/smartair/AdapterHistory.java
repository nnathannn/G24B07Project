package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdapterHistory extends ItemAdapter {

    public AdapterHistory(List<Item> itemList) {
        super(itemList);
    }

    public static class HistoryViewHolder extends ItemViewHolder {
        TextView name, date, field1, field2, field3;
        LinearLayout layout;

        public HistoryViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.HistoryDataName);
            this.date = view.findViewById(R.id.HistoryDataDate);
            this.field1 = view.findViewById(R.id.HistoryDataField1);
            this.field2 = view.findViewById(R.id.HistoryDataField2);
            this.layout = view.findViewById(R.id.HistoryItemLayout);
        }
    }

    public static class HistoryItem extends Item {
        String field1, field2, field3;

        public HistoryItem(String name, String date, String field1, String field2, String field3) {
            super(name, date);
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_history, parent, false);
        return (ItemViewHolder) new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = getItemList().get(position);
        HistoryItem historyItem = (HistoryItem) item;
        HistoryViewHolder historyHolder = (HistoryViewHolder) holder;
        historyHolder.name.setText(historyItem.getChildId());
        historyHolder.date.setText(historyItem.getDate());
        historyHolder.field1.setText(historyItem.field1);
        historyHolder.field2.setText(historyItem.field2);
        if (historyItem.field3 != null) {
            historyHolder.field3 = new TextView(historyHolder.itemView.getContext());
            historyHolder.field3.setText(historyItem.field3);
            historyHolder.field3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
            historyHolder.field3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            historyHolder.layout.addView(historyHolder.field3);
        }
    }

}