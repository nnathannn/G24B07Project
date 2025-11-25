package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RoleSelectionFragment extends Fragment {


    public RoleSelectionFragment() {}

//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        if(getChildFragmentManager().findFragmentById(R.id.child_list_fragment_container) == null) {
//            Bundle bundle = new Bundle();
//            bundle.putString("parent_user_id", temporaryParentId);
//
//            getChildFragmentManager().beginTransaction()
//                    .setReorderingAllowed(true)
//                    .add(R.id.child_list_fragment_container, ChildListFragment.class, bundle)
//                    .commit();
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_role_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button parentSignUpButton = view.findViewById(R.id.ParentSignUpButton);
        Button childSignUpButton = view.findViewById(R.id.ChildSignInButton);
        Button providerSignUpButton = view.findViewById(R.id.ProviderSignUpButton);
        parentSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ParentSignUpFragment());
            }
        });
        childSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ChildSignInFragment());
            }
        });
        providerSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ProviderSignUpFragment());
            }
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.GetStartedContainer, fragment);
        fragmentTransaction.commit();
    }
}