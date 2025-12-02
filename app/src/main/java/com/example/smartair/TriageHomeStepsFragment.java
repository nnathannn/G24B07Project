package com.example.smartair;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class TriageHomeStepsFragment extends Fragment {
    private FirebaseDatabase db;
    private String childID;
    private String triageID;
    private TextView timer;
    private Button stopTimerButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 600000; // 10 minutes
    private List<String> steps;
    private RecyclerView recyclerView;
    private TriageHomeStepsAdapter adapter;

    public TriageHomeStepsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_triage_home_steps, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            triageID = getArguments().getString("triageID");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance("https://smartair-abd1d-default-rtdb.firebaseio.com/");

        childID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        timer = view.findViewById(R.id.timer_number);
        steps = new ArrayList<>();
        adapter = new TriageHomeStepsAdapter(steps);
        recyclerView = view.findViewById(R.id.steps_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // start the timer
        startTimer();

        // set background and each zone steps
        getPB(view);

        // stop button timer
        stopTimerButton = view.findViewById(R.id.stop_timer_button);
        stopTimerButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    // stop the timer
                    countDownTimer.cancel();
                    // update triage end time
                    DatabaseReference triageRef = db.getReference("triage").child(triageID);
                    triageRef.child("endDate").setValue(LocalDateTime.now().toString());
                    // navigate to home page
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainerView, new HomeChildFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
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

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFinish() {
                // Timer has finished
                timer.setText("00:00");
                // update triage end time
                DatabaseReference triageRef = db.getReference("triage").child(triageID);
                triageRef.child("endDate").setValue(LocalDateTime.now().toString());
                // navigate to emergency
                TriageEmergencyFragment fragment = new TriageEmergencyFragment();
                Bundle args = new Bundle();
                args.putString("triageID", triageID);
                fragment.setArguments(args);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerView, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timer.setText(timeLeftFormatted);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    private void getPB(View view) {
        DatabaseReference ref = db.getReference("child-users").child(childID).child("PB");
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
                Toast.makeText(getContext(), "Failed to get current PB.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPEF(View view, Double curPB) {
        DatabaseReference ref = db.getReference("child-zones").child(childID);
        ref.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String zoneID = childSnapshot.getKey();
                        if (zoneID != null) {
                            db.getReference("zone").child(zoneID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot zoneSnapshot) {
                                    if (zoneSnapshot.exists()) {
                                        String date = zoneSnapshot.child("date").getValue(String.class);
                                        LocalDate curDate = LocalDateTime.parse(date).toLocalDate();
                                        Double curPEF = zoneSnapshot.child("count").getValue(Double.class);
                                        if (curPEF != null) {
                                            if (date != null && curDate.equals(LocalDate.now())) {
                                                setZone(view, curPB, curPEF);
                                            }
                                            else {
                                                setZone(view, null, null);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Failed to get current PEF.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(getContext(), "Failed to get current PEF.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(getContext(), "Failed to get current PEF.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to get current PEF.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setZone(View view, Double curPB, Double curPEF) {
        if (curPB == null || curPEF == null) {
            db.getReference("triage").child(triageID).child("emergency").setValue("Non-Emergency");
            steps.add("Stop all physical activity immediately and sit upright in a comfortable, slightly forward-leaning position.");
            adapter.notifyDataSetChanged();
            steps.add("Give 1–2 puffs of the rescue inhaler using a spacer. Takes slow, deep breaths with each puff.");
            adapter.notifyDataSetChanged();
            steps.add("Take slow breathing through the nose and out through pursed lips for 1–2 minutes.");
            adapter.notifyDataSetChanged();
            steps.add("Loosen tight clothing around the neck or chest (jackets, scarves, heavy sweaters).");
            adapter.notifyDataSetChanged();
            steps.add("Sip warm water to help relax the chest and throat muscles.");
            adapter.notifyDataSetChanged();
            return;
        }

        int percentage = (int) (curPEF * 100 / curPB);
        String color = percentage >= 80 ? "green" : (percentage >= 50 ? "yellow" : "red");

        TextView curPercentage = view.findViewById(R.id.cur_percentage);
        curPercentage.setText(percentage + "%");

        DatabaseReference triageRef = db.getReference("triage").child(triageID);
        CardView curPEFBox = view.findViewById(R.id.cur_pef_box);

        switch (color) {
            case "green":
                // set emergency category in database
                triageRef.child("emergency").setValue("Green-zone Reassurance");
                // set steps and box background
                curPEFBox.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                steps.add("Stop all physical activity immediately and sit upright in a comfortable, slightly forward-leaning position.");
                adapter.notifyDataSetChanged();
                steps.add("Give 1–2 puffs of the rescue inhaler using a spacer. Takes slow, deep breaths with each puff.");
                adapter.notifyDataSetChanged();
                steps.add("Take slow breathing through the nose and out through pursed lips for 1–2 minutes.");
                adapter.notifyDataSetChanged();
                steps.add("Loosen tight clothing around the neck or chest (jackets, scarves, heavy sweaters).");
                adapter.notifyDataSetChanged();
                steps.add("Sip warm water to help relax the chest and throat muscles.");
                adapter.notifyDataSetChanged();
                break;
            case "yellow":
                // set emergency category in database
                triageRef.child("emergency").setValue("Yellow-zone Guidance");
                // set steps and box background
                curPEFBox.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pale_yellow)));
                steps.add("Sit upright; do not lie down (lying can make breathing worse).");
                adapter.notifyDataSetChanged();
                steps.add("Give 2–4 puffs of the rescue inhaler using a spacer.");
                adapter.notifyDataSetChanged();
                steps.add("Take slow, controlled breaths after each puff.");
                adapter.notifyDataSetChanged();
                steps.add("Keep the environment calm and quiet. Fright or crying worsens breathing. Anxiety also increases the severity of an asthma attack");
                adapter.notifyDataSetChanged();
                steps.add("Remove all possible triggers — move to clean air, away from strong smells, smoke, or cold wind.");
                adapter.notifyDataSetChanged();
                steps.add("Sip warm fluids (avoid cold drinks during attacks).");
                adapter.notifyDataSetChanged();
                break;
            case "red":
                // set emergency category in database
                triageRef.child("emergency").setValue("Red-zone Assistance");
                // set steps and box background
                curPEFBox.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                steps.add("Immediately stop all activity and sit upright, leaning slightly forward.");
                adapter.notifyDataSetChanged();
                steps.add("Give 4–6 puffs of the rescue inhaler through a spacer.");
                adapter.notifyDataSetChanged();
                steps.add("After each puff, take slow, deep breaths if possible.");
                adapter.notifyDataSetChanged();
                steps.add("Keep calm and comfortable. Panic increases airway constriction.");
                adapter.notifyDataSetChanged();
                steps.add("If possible, move into a quiet, warm, well-ventilated area. Avoid cold air or sudden temperature changes.");
                adapter.notifyDataSetChanged();
                steps.add("If the attack does not improve within 10–15 minutes, give another 4 puffs of the rescue medicine.");
                adapter.notifyDataSetChanged();
                break;
            default:
                curPEFBox.setBackgroundColor(getResources().getColor(R.color.white));
                break;
        }
    }
}