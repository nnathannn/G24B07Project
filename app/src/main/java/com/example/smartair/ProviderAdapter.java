package com.example.smartair;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Provider;
import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(Pair<String, String> clickedString);
    }

    //first is provider id, second is child id
    private final List<Pair<String, String>> providerList;
    private final OnItemClickListener listener;

    public ProviderAdapter(List<Pair<String, String>> providerList, OnItemClickListener listener) {
        this.providerList = providerList;
        this.listener = listener;
    }

    public static class ProviderViewHolder extends RecyclerView.ViewHolder {
        public TextView providerNameText, childNameText;
        public CardView cardView;

        public ProviderViewHolder(View providerView) {
            super(providerView);
            providerNameText = providerView.findViewById(R.id.providerName);
            childNameText = providerView.findViewById(R.id.childProviderName);
            cardView = (CardView) itemView;
        }

        public void bind(Pair<String, String> providerString, OnItemClickListener listener) {
            String childName = providerString.second;
            DatabaseReference providerRef = FirebaseDatabase.getInstance().getReference("provider-users");
            providerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String providerName = dataSnapshot.child(providerString.first).child("name").getValue(String.class);
                    providerNameText.setText(providerName);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("Provider name not found: " + databaseError.getCode());

                }
            });
            DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("child-users");
            childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String childName = dataSnapshot.child(providerString.second).child("name").getValue(String.class);
                    childNameText.setText(childName);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("Child name not found: " + databaseError.getCode());
                }
            });
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(new Pair<>(providerString.first, providerString.second));
                }
            });
        }
    }

    @NonNull
    @Override
    public ProviderAdapter.ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View providerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_card, parent, false);
        return new ProviderAdapter.ProviderViewHolder(providerView);
    }

    @Override
    public void onBindViewHolder(ProviderAdapter.ProviderViewHolder holder, int position) {
        Pair<String, String> provider = providerList.get(position);
        holder.bind(provider, listener);
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }
}
