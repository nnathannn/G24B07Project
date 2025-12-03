package com.example.smartair;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TechniqueAdapter extends FragmentStateAdapter {

    private final boolean[] isVideoStep;
    private final int[] mediaResIds;

    public TechniqueAdapter(@NonNull Fragment fragment,
                                 boolean[] isVideoStep,
                                 int[] mediaResIds) {
        super(fragment);
        this.isVideoStep = isVideoStep;
        this.mediaResIds = mediaResIds;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        boolean isVideo = isVideoStep[position];
        int resId = mediaResIds[position];
        return TechniqueStepFragment.newInstance(position, isVideo, resId);
    }

    @Override
    public int getItemCount() {
        return mediaResIds.length;
    }

    public boolean isVideo(int position) {
        return isVideoStep[position];
    }
}
