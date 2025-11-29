package com.example.smartair;

import static com.firebase.ui.auth.data.model.User.getUser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProviderListFragment extends Fragment implements ProviderAdapter.OnItemClickListener {

    private String parentUserId;
    private DatabaseReference parentChildrenRef;
    private ProviderAdapter adapter;
    private List<Pair<String, String>> providerList;
    private RecyclerView recyclerView;
    FirebaseAuth myauth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getUser()!=null){
            parentUserId = getUser().getUid();
        }
        else{
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provider_list, container, false);
        recyclerView = view.findViewById(R.id.providerRecycler);
        providerList = new ArrayList<>();
        adapter = new ProviderAdapter(providerList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(parentUserId == null || parentUserId.isEmpty()){
            Toast.makeText(getContext(), "Parent user ID does not exist", Toast.LENGTH_LONG).show();
        }
        else {
            parentChildrenRef = FirebaseDatabase.getInstance().getReference("parent-users").child(parentUserId).child("child-ids");
            loadChildIds();
        }

        return view;
    }

    private void loadChildIds() {
        parentChildrenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> ids = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                        String childId = idSnapshot.getKey();
                        if (childId != null) {
                            ids.add(childId);
                        }
                    }
                }
                if(!ids.isEmpty()){
                    fetchChildProvider(ids);
                }
                else{
                    providerList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "No children found. Please add children.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load IDs: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchChildProvider(List<String> childIds) {
        DatabaseReference childrenRef = FirebaseDatabase.getInstance().getReference("child-users");
        childrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                providerList.clear();
                for (String childId : childIds) {
                    if (dataSnapshot.hasChild(childId)) {
                        DataSnapshot childNode = dataSnapshot.child(childId);
                        if(childNode.hasChild("provider")){
                            for(DataSnapshot providerSnapshot : childNode.child("provider").getChildren()){
                                providerList.add(new Pair<>(providerSnapshot.getKey(), childId));
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch child names: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onItemClick(Pair<String, String> clickedString) {
        Bundle bundle = new Bundle();
        bundle.putString("child_id", clickedString.second);
        bundle.putString("provider_id", clickedString.first);
        Fragment fragment = new ProviderAccessFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.providerListContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public FirebaseUser getUser() { return myauth.getCurrentUser(); }

}