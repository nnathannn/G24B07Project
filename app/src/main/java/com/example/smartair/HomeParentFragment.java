package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeParentFragment extends Fragment {
    String parentId;
    FirebaseAuth myauth = FirebaseAuth.getInstance();
    ImageButton account;

    public HomeParentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentId  = getUser().getUid();
        return inflater.inflate(R.layout.fragment_home_parent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getChildFragmentManager().findFragmentById(R.id.child_list_fragment_container) == null) {
            Bundle bundle = new Bundle();
            bundle.putString("parent_user_id", parentId);

            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.child_list_fragment_container, ChildListFragment.class, bundle)
                    .commit();
        }

        account = view.findViewById(R.id.imageButton3);
        account.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame_layout, new ProfileParentFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    public FirebaseUser getUser() { return myauth.getCurrentUser(); }
}
