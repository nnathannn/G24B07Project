package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdapterHistory extends ItemAdapter {

    public AdapterHistory(List<Item> itemList) {
        super(itemList);
    }

    public static class HistoryViewHolder extends ItemViewHolder {
        TextView name, date, field1, field2, field3;

        public HistoryViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.HistoryDataName);
            this.date = view.findViewById(R.id.HistoryDataDate);
            this.field1 = view.findViewById(R.id.HistoryDataField1);
            this.field2 = view.findViewById(R.id.HistoryDataField2);
            this.field3 = view.findViewById(R.id.HistoryDataField3);
        }
    }

    public static class HistoryItem extends Item {
        String field1, field2, field3;
        String type;

        public HistoryItem(String name, String date, String field1, String field2, String field3, String type) {
            super(name, date);
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.type = type;
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
        historyHolder.field3.setText(historyItem.field3);
    }

//    static void fetchData(ItemAdapter itemAdapter, String path) {
//
//    }
}