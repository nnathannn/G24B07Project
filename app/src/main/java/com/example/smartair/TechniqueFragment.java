package com.example.smartair;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TechniqueFragment extends Fragment {

    private static final int TOTAL_STEPS = 5;

    // TODO: Rename and change types of parameters
    private ImageView stepImage;
    private TextView stepCaption;
    private Button buttonSkip;
    private Button buttonCompleted;

    private int currentStep = 0;
    private int completedStep = 0;


    public TechniqueFragment() { }

    /*private int[] stepImageContents = {
            R.drawable.step1,
            R.drawable.step2,
            R.drawable.step3,
            R.drawable.step4,
            R.drawable.step5
    }; */

    private String[] stepCaptionContents = {
            "Seal lips",
            "Take a slow and deep breath",
            "Hold breath for ~10 seconds",
            "Wait 30-60 seconds between puffs",
            "Use spacer/mask if needed"
    };



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_technique, container, false);

        stepImage = view.findViewById(R.id.stepImage);
        stepCaption = view.findViewById(R.id.stepCaption);
        buttonSkip = view.findViewById(R.id.buttonSkip);
        buttonCompleted = view.findViewById(R.id.buttonCompleted);


        //revise here
        showCurrentStep();

        buttonSkip.setOnClickListener(v -> {
            goToNextStep(false);
        });

        buttonCompleted.setOnClickListener(v -> {
            goToNextStep(true);
        });

        return view;
    }

    private void showCurrentStep(){
        //stepImage.setImageResource(stepImageContents[currentStep]);
        stepCaption.setText(stepCaptionContents[currentStep]);

    }

    private void goToNextStep(boolean isCompleted){
        if(isCompleted){
            completedStep++;
        }

        if(currentStep < TOTAL_STEPS - 1){
            currentStep++;
            showCurrentStep();
        } else {
            onSessionFinished();
        }

    }


    private void onSessionFinished(){
        boolean highQuality = (completedStep == TOTAL_STEPS);

        //send to database

        //reset
        currentStep = 0;
        completedStep = 0;

    }




}