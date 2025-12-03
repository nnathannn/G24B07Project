package com.example.smartair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProviderInviteAdapter extends RecyclerView.Adapter<ProviderInviteAdapter.ProviderInviteViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(ProviderInvite providerInvite);
    }

    private OnItemClickListener listener;
    private List<ProviderInvite> providerInvites;

    public ProviderInviteAdapter(List<ProviderInvite> providerInvites, OnItemClickListener listener) {
        this.providerInvites = providerInvites;
        this.listener = listener;
    }

    public static class ProviderInviteViewHolder extends RecyclerView.ViewHolder {
        TextView inviteCode, childName, endDate;
        CardView cardView;

        public ProviderInviteViewHolder(View itemView) {
            super(itemView);
            inviteCode = itemView.findViewById(R.id.invite_code_text);
            childName = itemView.findViewById(R.id.invite_child_text);
            endDate = itemView.findViewById(R.id.invite_end_date);
            cardView = (CardView) itemView;
        }

        public void bind(ProviderInvite providerInvite, OnItemClickListener listener) {
            inviteCode.setText("Code: " + providerInvite.getCode());
            childName.setText("Child: " + providerInvite.getChildName());
            endDate.setText("Invite Expiration Date: " + providerInvite.getEndDate());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(providerInvite);
                }
            });
        }
    }

    @Override
    public ProviderInviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_invite_card, parent, false);
        return new ProviderInviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProviderInviteViewHolder holder, int position) {
        ProviderInvite providerInvite = providerInvites.get(position);
        holder.bind(providerInvite, listener);
    }

    @Override
    public int getItemCount() {
        return providerInvites.size();
    }
}
