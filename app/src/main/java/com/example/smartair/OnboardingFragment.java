package com.example.smartair;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;


public class OnboardingFragment extends Fragment {

    private ViewPager2 viewPager;
    private Button nextButton;
    private Button prevButton;
    private String role;
    int length;
    private final List<Integer> onboardingParent = Arrays.asList(
            R.drawable.parent_1,
            R.drawable.parent_2,
            R.drawable.parent_3,
            R.drawable.parent_4,
            R.drawable.parent_5,
            R.drawable.parent_6,
            R.drawable.parent_7,
            R.drawable.parent_8,
            R.drawable.parent_9,
            R.drawable.parent_10);
    private final List<Integer> onboardingProvider = Arrays.asList(
            R.drawable.provider_1,
            R.drawable.provider_2,
            R.drawable.provider_3,
            R.drawable.provider_4,
            R.drawable.provider_5);

    private final List<Integer> onboardingChild = Arrays.asList(
            R.drawable.child_1,
            R.drawable.child_2,
            R.drawable.child_3,
            R.drawable.child_4,
            R.drawable.child_5,
            R.drawable.child_6,
            R.drawable.child_7,
            R.drawable.child_8);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            role = getArguments().getString("role");
        }
        else{
            //How should we handle error?
            role = "parent";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
        viewPager = view.findViewById(R.id.viewPagerContainer);
        nextButton = view.findViewById(R.id.nextImageButton);
        prevButton = view.findViewById(R.id.prevImageButton);

        if(role.equals("parent")){
            viewPager.setAdapter(new OnboardingAdapter(this, onboardingParent));
            length = onboardingParent.size();
        } else if (role.equals("provider")) {
            viewPager.setAdapter(new OnboardingAdapter(this, onboardingProvider));
            length = onboardingProvider.size();
        }
        else{
            viewPager.setAdapter(new OnboardingAdapter(this, onboardingChild));
            length = onboardingChild.size();
        }

        viewPager.setUserInputEnabled(false);

        updateButtonVisibility(0, length);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateButtonVisibility(position, length);
            }
        });

        nextButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if(currentItem < length - 1){
                viewPager.setCurrentItem(currentItem + 1, true);
            } else {
                if(role.equals("parent")){
                    Intent intent = new Intent(getActivity(), HomeParent.class);
                    startActivity(intent);
                }
                else if(role.equals("provider")){
                    Intent intent = new Intent(getActivity(), HomeProvider.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getActivity(), ChildActivity.class);
                    startActivity(intent);
                }
            }
        });

        prevButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if(currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true);
            }
        });

        return view;
    }

    private void updateButtonVisibility(int position, int length){
        if(position == 0){
            prevButton.setVisibility(View.INVISIBLE);
        } else {
            prevButton.setVisibility(View.VISIBLE);
        }

        if(position == length - 1){
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next");
        }
    }
}