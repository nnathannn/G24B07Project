package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


public class ProviderChildDashboard extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String childId;
    private String providerId;
    private TextView title;
    private LinearLayout layout;
    private DatabaseReference myref = FirebaseDatabase.getInstance().getReference();

    public ProviderChildDashboard() {
        // Required empty public constructor
    }

    public static ProviderChildDashboard newInstance(String param1, String param2) {
        ProviderChildDashboard fragment = new ProviderChildDashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childId = getArguments().getString(ARG_PARAM1);
            providerId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_child_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = view.findViewById(R.id.ChildTitle);
        layout = view.findViewById(R.id.ChildLayout);

        checkAccess();
    }

    private void checkAccess() {
        DatabaseReference childref = myref.child("child-users").child(childId);
        childref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                title.setText(snapshot.child("name").getValue(String.class) + "'s Data");
                DataSnapshot accessNode = snapshot.child("provider").child(providerId);
                for (DataSnapshot child : accessNode.getChildren()) {
                    if (child.getKey().equals("controller") && child.getValue(Boolean.class)) {
                        addSummary(snapshot.child("schedule").child("start-date").getValue(String.class),
                                snapshot.child("schedule").child("end-date").getValue(String.class));
                        i++;
                    } else if (child.getKey().equals("trigger") && child.getValue(Boolean.class)) {
                        addTrigger();
                        i++;
                    } else if (child.getKey().equals("summary") && child.getValue(Boolean.class)) {
                        addCharts();
                        i++;
                    }
                }
                if (i == 0) {
                    TextView noData = new TextView(getContext());
                    noData.setText("No data shared");
                    noData.setTextSize(24);
                    layout.addView(noData, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addTrigger() {
        TextView triggerTitle = new TextView(getContext());
        triggerTitle.setText("Triggers");
        triggerTitle.setTextSize(24);
        triggerTitle.setPadding(0, 16, 0, 0);
        layout.addView(triggerTitle, 0);
        TextView triggers = new TextView(getContext());
        triggers.setText("Exercise, Cold Air, Dust/Pets, Smoke, Illness, Perfume/Cleaners/Strong Odors");
        triggers.setTextSize(16);
        triggers.setPadding(32, 0, 0, 0);
        layout.addView(triggers, 1);
    }

    private void addSummary(String start, String end) {
        TextView summaryTitle = new TextView(getContext());
        summaryTitle.setText("Controller Adherence Summary");
        summaryTitle.setTextSize(24);
        summaryTitle.setPadding(0, 16, 0, 0);
        layout.addView(summaryTitle, 0);

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        int days = ((int) ChronoUnit.DAYS.between(startDate, endDate)) + 1;

        TextView scheduleStart = new TextView(getContext());
        scheduleStart.setText("Start of schedule: " + start);
        scheduleStart.setTextSize(16);
        scheduleStart.setPadding(32, 0, 0, 0);
        layout.addView(scheduleStart, 1);

        TextView scheduleEnd = new TextView(getContext());
        scheduleEnd.setText("End of schedule: " + end);
        scheduleEnd.setTextSize(16);
        scheduleEnd.setPadding(32, 0, 0,0);
        layout.addView(scheduleEnd, 2);

        TextView adherence = new TextView(getContext());
        adherence.setTextSize(16);
        adherence.setPadding(32, 0, 0, 0);
        layout.addView(adherence, 3);


        DatabaseReference childref = myref.child("medicineLogs");
        end += "T23:59:59.999999";
        childref.orderByChild("date").startAt(start).endAt(end).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("child-id").getValue(String.class).equals(childId) && !dataSnapshot.child("rescue").getValue(Boolean.class)) {
                        count++;
                    }
                }
                adherence.setText("Adherence: " + (count * 100 / days) + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addCharts() {
        TextView pefTitle  = new TextView(getContext());
        pefTitle.setText("Average PEF Summary Chart");
        pefTitle.setTextSize(24);
        pefTitle.setPadding(0, 16, 0, 0);
        layout.addView(pefTitle, 0);

        LocalDate end = LocalDate.now().plusDays(1);
        String startDate = end.minusMonths(3).toString();
        String endDate = end.toString();


        myref.child("zone").orderByChild("date").startAt(startDate).endAt(endDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> labels = new ArrayList<>();
                ArrayList<Float> values = new ArrayList<>();
                int count = 0;
                int index = 0;
                Float average = 0.0f;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.child("child-id").getValue(String.class).equals(childId)) {
                        String date = childSnapshot.child("date").getValue(String.class).split("T")[0];
                        if (!labels.contains(date)) {
                            labels.add(date);
                            values.add(childSnapshot.child("count").getValue(Float.class));
                            count = 1;
                            index = labels.size() - 1;
                        } else {
                            average = (values.get(index)*count + childSnapshot.child("count").getValue(Float.class)) / (count+1);
                            count++;
                            values.set(index, average);
                        }
                    }
                }
                LineChartFragment fragment = LineChartFragment.newInstance("Average PEF Summary Charts", labels, values);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.ChildLayout, fragment)
                        .commit();
                TextView test = new TextView(getContext());
                test.setText("TEST");
                layout.addView(test);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}