    package com.example.smartair;

    import android.os.Bundle;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.navigation.fragment.NavHostFragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class ChildListFragment extends Fragment implements ChildAdapter.OnItemClickListener {

        private String parentUserId;
        private DatabaseReference parentChildrenRef;
        private ChildAdapter adapter;
        private List<String> childNameList, childIdList;
        private Map<String, String> childNameToId;
        private RecyclerView recyclerView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            parentUserId = requireArguments().getString("parent_user_id");
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_child_list, container, false);
            recyclerView = view.findViewById(R.id.child_recycler_view);
            childNameList = new ArrayList<>();
            childNameToId = new HashMap<>();
            adapter = new ChildAdapter(childNameList, this);
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
                        fetchChildNames(ids);
                    }
                    else{
                        childNameList.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "No children found. Go add children.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load IDs: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        private void fetchChildNames(List<String> childIds) {
            DatabaseReference childrenRef = FirebaseDatabase.getInstance().getReference("child-users");
            childrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    childNameList.clear();
                    childNameToId.clear();
                    for (String childId : childIds) {
                        if (dataSnapshot.hasChild(childId)) {
                            DataSnapshot childNode = dataSnapshot.child(childId);
                            if (childNode.hasChild("name")) {
                                String childName = childNode.child("name").getValue(String.class);
                                if (childName != null) {
                                    childNameList.add(childName);
                                    childNameToId.put(childName, childId);
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
        public void onItemClick(String clickedString) {
    //        Toast.makeText(getContext(), "[Code will be completed to redirect to a new activity] Clicked: " + clickedString, Toast.LENGTH_LONG).show();
            String clickedId = childNameToId.get(clickedString);
            if(clickedId != null){
                Bundle bundle = new Bundle();
                bundle.putString("uid", clickedId);
                Fragment fragment = new ChildDashboardFragment();
                fragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.child_list_fragment_container, fragment).addToBackStack(null).commit();
            }
        }
    }