package com.example.smartair;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class BarChartFragment extends Fragment {

    private BarChart barChart;
    private String chartTitle;
    private ArrayList<String> labels;
    private ArrayList<Float> values;

    public static BarChartFragment newInstance(String chartTitle, ArrayList<String> labels, ArrayList<Float> values) {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();
        args.putString("chartTitle", chartTitle);
        args.putStringArrayList("labels", labels);
        args.putSerializable("values", values);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bar_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = view.findViewById(R.id.barChart);

        if (getArguments() != null) {
            chartTitle = getArguments().getString("chartTitle");
            labels = getArguments().getStringArrayList("labels");
            values = (ArrayList<Float>) getArguments().getSerializable("values");
        }

        setupChart();
    }

    private void setupChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            entries.add(new BarEntry(i, values.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, chartTitle);
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.another_blue));
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = Math.round(value);
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                } else {
                    return "";
                }
            }
        });

        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }
}

/*
Example of use:
ArrayList<String> labels = new ArrayList<>();
ArrayList<Float> values = new ArrayList<>();

labels.add("2025-12-01");
labels.add("2025-12-02");
values.add(3f);
values.add(7f);

BarChartFragment fragment = BarChartFragment.newInstance("Medicine Logs", labels, values);
getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit();
*/