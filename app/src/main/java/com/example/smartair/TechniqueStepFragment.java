package com.example.smartair;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

public class TechniqueStepFragment extends Fragment {

    private static final String ARG_STEP_INDEX = "arg_step_index";
    private static final String ARG_IS_VIDEO   = "arg_is_video";
    private static final String ARG_RES_ID     = "arg_res_id";

    public interface VideoCompleteListener {
        void onVideoCompleted(int stepIndex);
    }

    private int stepIndex;
    private boolean isVideo;
    private int resId;

    private VideoCompleteListener callback;

    public TechniqueStepFragment() {
        // Required empty public constructor
    }

    public static TechniqueStepFragment newInstance(int stepIndex,
                                                    boolean isVideo,
                                                    int resId) {
        TechniqueStepFragment fragment = new TechniqueStepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP_INDEX, stepIndex);
        args.putBoolean(ARG_IS_VIDEO, isVideo);
        args.putInt(ARG_RES_ID, resId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Parent fragment implements VideoCompleteListener
        if (getParentFragment() instanceof VideoCompleteListener) {
            callback = (VideoCompleteListener) getParentFragment();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            stepIndex = args.getInt(ARG_STEP_INDEX, 0);
            isVideo   = args.getBoolean(ARG_IS_VIDEO, false);
            resId     = args.getInt(ARG_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_technique_step, container, false);

        ImageView imageStep = view.findViewById(R.id.imageStep);
        VideoView videoStep = view.findViewById(R.id.videoStep);

        if (isVideo) {
            imageStep.setVisibility(View.GONE);
            videoStep.setVisibility(View.VISIBLE);

            if (getContext() != null) {
                Uri videoUri = Uri.parse("android.resource://"
                        + requireContext().getPackageName() + "/" + resId);
                videoStep.setVideoURI(videoUri);

                videoStep.setOnPreparedListener(mp -> {
                    mp.setLooping(false);   // we want a real “finished” event
                    videoStep.start();
                });

                videoStep.setOnCompletionListener(mp -> {
                    if (callback != null) {
                        callback.onVideoCompleted(stepIndex);
                    }
                });
            }

        } else {
            videoStep.setVisibility(View.GONE);
            imageStep.setVisibility(View.VISIBLE);
            imageStep.setImageResource(resId);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        View view = getView();
        if (view != null) {
            VideoView videoStep = view.findViewById(R.id.videoStep);
            if (videoStep.getVisibility() == View.VISIBLE && videoStep.isPlaying()) {
                videoStep.pause();
            }
        }
    }
}
