package com.example.smartair;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class TechniqueFragment extends Fragment implements TechniqueStepFragment.VideoCompleteListener {


    private static final int TOTAL_STEPS = 5;

    private ViewPager2 viewPager;
    private Button buttonSkip;
    private Button buttonCompleted;

    private TechniqueAdapter adapter;

    private int currentStep = 0;
    private int completedSteps = 0;

    private final boolean[] videoFinished = new boolean[TOTAL_STEPS];

    // false = image, true = video
    private final boolean[] isVideoStep = new boolean[]{
            false,
            false,
            true,
            true,
            false
    };

    private final int[] mediaResIds = new int[]{
            R.drawable.technique1,
            R.drawable.technique2,
            R.raw.technique3,
            R.raw.technique4,
            R.drawable.technique5
    };


    private String uid;
    private DatabaseReference badgeRef;

    public TechniqueFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_technique, container, false);

        viewPager = view.findViewById(R.id.viewPagerContainer);
        buttonSkip = view.findViewById(R.id.buttonSkip);
        buttonCompleted = view.findViewById(R.id.buttonCompleted);



        UIDProvider uidProvider = (UIDProvider) requireActivity();
        uid = uidProvider.getUid();

        badgeRef = FirebaseDatabase
                .getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/")
                .getReference("badge")
                .child(uid)
                .child("high-quality");

        setupViewPager();
        setupButtons();

        return view;
    }

    private void setupViewPager() {
        adapter = new TechniqueAdapter(this, isVideoStep, mediaResIds);
        viewPager.setAdapter(adapter);

        viewPager.setUserInputEnabled(false);

        updateCompletedButtonState();
    }

    private void setupButtons() {
        buttonSkip.setOnClickListener(v -> goToNextStep(false));

        buttonCompleted.setOnClickListener(v -> {
            if (adapter.isVideo(currentStep) && !videoFinished[currentStep]) {
               return;
            }
            goToNextStep(true);
        });
    }

    private void goToNextStep(boolean markCompleted) {
        if (markCompleted) {
            completedSteps++;
        }

        if (currentStep < TOTAL_STEPS - 1) {
            currentStep++;
            viewPager.setCurrentItem(currentStep, true);
            updateCompletedButtonState();
        } else {
            onSessionFinished();
        }
    }

    private void updateCompletedButtonState() {
        boolean isVideo = adapter.isVideo(currentStep);
        boolean finished = videoFinished[currentStep];

        if (isVideo && !finished) {
            buttonCompleted.setEnabled(false);
            buttonCompleted.setAlpha(0.4f);
        } else {
            buttonCompleted.setEnabled(true);
            buttonCompleted.setAlpha(1f);
        }
    }

    @Override
    public void onVideoCompleted(int stepIndex) {
        if (stepIndex >= 0 && stepIndex < videoFinished.length) {
            videoFinished[stepIndex] = true;
        }
        if (stepIndex == currentStep) {
            updateCompletedButtonState();
        }
    }

    private void onSessionFinished() {
        boolean perfect = (completedSteps == TOTAL_STEPS);

        if (perfect && badgeRef != null) {
            badgeRef.child("completed").setValue(true);
        }

        String title = perfect
                ? "Finished High-Quality Technique Session"
                : "Finished Technique Session";

        String message = perfect
                ? "You completed all the steps without skipping. Awesome job!"
                : "You finished the session. You can try again another time to complete all the steps.";

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Back to Home", (dialog, which) -> {


                    Intent intent = new Intent(getActivity(), ChildActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .show();

        currentStep = 0;
        completedSteps = 0;
        Arrays.fill(videoFinished, false);
        checkThreshold();
    }

    private void checkThreshold() {
        DatabaseReference myref = FirebaseDatabase.getInstance().getReference("badge").child(uid);
        myref.child("high-quality/threshold").get().addOnSuccessListener(dataSnapshot -> {
            int threshold = dataSnapshot.getValue(Integer.class);
            checkBadge(threshold);
        });
    }

    private void checkBadge(int threshold) {
        FirebaseDatabase.getInstance().getReference("technique").get().addOnSuccessListener(snapshot -> {
            int count = 0;
            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                if (childSnapshot.child("child-id").getValue(String.class).equals(uid)) {
                    count++;
                }
            }
            if (count >= threshold) {
                FirebaseDatabase.getInstance().getReference("badge").child(uid).child("high-quality/completed").setValue(true);
            }
        });
    }
}