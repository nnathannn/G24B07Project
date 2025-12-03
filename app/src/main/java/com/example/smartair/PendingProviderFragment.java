package com.example.smartair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PendingProviderFragment extends Fragment implements ProviderInviteAdapter.OnItemClickListener {
    private String parentId;
    FirebaseAuth myauth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private ProviderInviteAdapter adapter;
    private List<ProviderInvite> inviteList;
    private DatabaseReference ref;



    public PendingProviderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentId = myauth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_provider, container, false);
        recyclerView = view.findViewById(R.id.invite_recycler);
        inviteList = new ArrayList<>();
        adapter = new ProviderInviteAdapter(inviteList, (ProviderInviteAdapter.OnItemClickListener) this); //check
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ref = FirebaseDatabase.getInstance().getReference("invite-code").child(parentId);
        loadInvites();
        return view;
    }
    private void loadInvites(){
        inviteList.clear();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        ProviderInvite providerInvite = ds.getValue(ProviderInvite.class);
                        inviteList.add(providerInvite);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(ProviderInvite providerInvite) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Revocation");
        builder.setMessage("Are you sure you want to revoke this invite code?");
        builder.setPositiveButton("Yes, Revoke", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteInviteCode(providerInvite);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteInviteCode(ProviderInvite providerInvite){
        DatabaseReference toDelete = ref.child(providerInvite.getCode());
        toDelete.removeValue().addOnSuccessListener(aVoid -> {
            loadInvites();
        });
        Toast.makeText(getContext(), "Invite Code Revoked", Toast.LENGTH_SHORT).show();
    }
}