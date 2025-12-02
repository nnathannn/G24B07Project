package com.example.smartair;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class LineChartFragment extends Fragment {

    private LineChart lineChart;
    private List<String> labels = new ArrayList<>();
    private List<Float> values = new ArrayList<>();
    private String chartTitle = "";

    public LineChartFragment() {
    }

    public static LineChartFragment newInstance(String chartTitle, ArrayList<String> labels, ArrayList<Float> values) {
        LineChartFragment fragment = new LineChartFragment();
        Bundle args = new Bundle();
        args.putString("chartTitle", chartTitle);
        args.putStringArrayList("labels", labels);
        args.putSerializable("values", values);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_line_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = view.findViewById(R.id.lineChart);

        if (getArguments() != null) {
            chartTitle = getArguments().getString("chartTitle");
            labels = getArguments().getStringArrayList("labels");
            values = (ArrayList<Float>) getArguments().getSerializable("values");
        }

        setupChart();
    }

    private void setupChart() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            entries.add(new Entry(i, values.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, chartTitle);
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
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

LineChartFragment fragment = LineChartFragment.newInstance("Medicine Logs", labels, values);
getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit();
*/