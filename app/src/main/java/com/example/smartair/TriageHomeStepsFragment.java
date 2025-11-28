package com.example.smartair;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TriageHomeStepsFragment extends Fragment {
    private FirebaseDatabase db;
    private static final long START_TIME_IN_MILLIS = 600000;
    private TextView timer;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = START_TIME_IN_MILLIS;
    private Button stopTimerButton;
    private List<String> steps;
    private int currentIndex = 0;
    private TextView stepTitle;
    private TextView stepDesc;
    private Button nextStepButton;
    private String childID;

    public TriageHomeStepsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_triage_home_steps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");
        timer = view.findViewById(R.id.timer);

        // update later : retrieve childID from the user
        childID = "11";

        // start the timer
        startTimer();

        // set background and each zone steps
        fetchPB(view);

        // set the steps
        stepTitle = view.findViewById(R.id.step_title);
        stepDesc = view.findViewById(R.id.step_desc);
        nextStepButton = view.findViewById(R.id.next_step_button);
        updateStep();
        nextStepButton.setOnClickListener(v -> updateStep());

        // stop button timer
        stopTimerButton = view.findViewById(R.id.stop_timer_button);
        stopTimerButton.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                //
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                // Timer has finished
                timer.setText("00:00:00");
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        int milliseconds = (int) (timeLeftInMillis % 1000);

        String timeLeftFormatted = String.format("%02d:%02d%02d", minutes, seconds, milliseconds);
        timer.setText(timeLeftFormatted);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) { countDownTimer.cancel(); }
    }

    private void fetchPB(View view) {
        DatabaseReference ref = db.getReference("child-users")
                .child(childID)
                .child("PB");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double curPB = snapshot.getValue(Double.class);
                    if (curPB != null) getPEF(view, curPB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error handling
            }
        });
    }

    private void getPEF(View view, Double curPB) {
        DatabaseReference ref = db.getReference("pef").child("pef-id");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double curPEF = snapshot.getValue(PEF.class).getCount();
                    if (curPEF != null) updateBackground(view, curPB, curPEF);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // error handling
            }
        });
    }

    private void updateBackground(View view, Double curPB, Double curPEF) {
        int percentage = (int) (curPEF * 100 / curPB);
        String color = percentage >= 80 ? "green" : (percentage >= 50 ? "yellow" : "red");

        TextView curPercentage = view.findViewById(R.id.cur_percentage);
        curPercentage.setText(percentage + "%");

        View curPEFBox = view.findViewById(R.id.cur_pef_box);
        switch (color) {
            case "green":
                curPEFBox.setBackgroundColor(R.color.green);
                steps = new ArrayList<>();
                // add steps based on zone
                break;
            case "yellow":
                curPEFBox.setBackgroundColor(R.color.pale_yellow);
                steps = new ArrayList<>();
                // add steps based on zone
                break;
            case "red":
                curPEFBox.setBackgroundColor(R.color.red);
                steps = new ArrayList<>();
                // add steps based on zone
                break;
            default:
                curPEFBox.setBackgroundColor(R.color.white);
                steps = new ArrayList<>();
                break;
        }
    }

    private void updateStep() {
        if (!steps.isEmpty() && currentIndex < steps.size()) {
            String currentStep = steps.get(currentIndex);
            stepTitle.setText("Step " + (currentIndex + 1));
            stepDesc.setText(currentStep);
            currentIndex++;
            // if the timer still running and not pushing the stop button,
            // return to step 1 after finishing every step
            currentIndex %= steps.size();
        }
    }
}