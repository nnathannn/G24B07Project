package com.example.smartair;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProviderAccessFragment extends Fragment {
    private static final String ARG_PARAM1 = "provider_id";
    private static final String ARG_PARAM2 = "child_id";
    private String providerId;
    private String childId;

    public ProviderAccessFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            providerId = getArguments().getString(ARG_PARAM1);
            childId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_access, container, false);
    }
}