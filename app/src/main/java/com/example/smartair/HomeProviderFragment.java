package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeProviderFragment extends Fragment {

    String temporaryProviderId = "provider1";

    public HomeProviderFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_provider, container, false);
    }

    // TO BE DISCUSSED:
    // Provider data in firebase. Cannot fetch data before provider data is set up.

    //TO BE COMPLETED AFTER DATA IS SET UP IN FIREBASE
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        if(getChildFragmentManager().findFragmentById(R.id.providerChildListContainer) == null) {
//            Bundle bundle = new Bundle();
//
//            bundle.putString("provider_user_id", temporaryProviderId);
//
//            getChildFragmentManager().beginTransaction()
//                    .setReorderingAllowed(true)
//                    .add(R.id.providerChildListContainer, ProviderChildListFragment.class, bundle)
//                    .commit();
//        }
//    }
}