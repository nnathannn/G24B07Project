package com.example.smartair;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class OnboardingAdapter extends FragmentStateAdapter {
    private final List<Integer> images;
    public OnboardingAdapter(Fragment fragment, List<Integer> images) {
        super(fragment);
        this.images = images;
    }

    @Override
    public Fragment createFragment(int position) {
        int imageId = images.get(position);
        return OnboardingImageFragment.newInstance(imageId);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

}
