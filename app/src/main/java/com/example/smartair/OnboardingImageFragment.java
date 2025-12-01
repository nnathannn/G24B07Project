package com.example.smartair;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class OnboardingImageFragment extends Fragment {

    public OnboardingImageFragment() {}

    public static OnboardingImageFragment newInstance(int imageId) {
        OnboardingImageFragment fragment = new OnboardingImageFragment();
        Bundle args = new Bundle();
        args.putInt("image_id", imageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_image, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int imageId = arguments.getInt("image_id", 0);
            if(imageId != 0){
                ImageView imageView = view.findViewById(R.id.onboardingImageFragmentContainer);
                imageView.setImageResource(imageId);
            }
        }
        return view;
    }
}