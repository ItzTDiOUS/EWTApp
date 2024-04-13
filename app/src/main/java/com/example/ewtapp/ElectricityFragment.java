package com.example.ewtapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ElectricityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ElectricityFragment extends Fragment {
    private BarChart barChart;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ElectricityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ElectricityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ElectricityFragment newInstance(String param1, String param2) {
        ElectricityFragment fragment = new ElectricityFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_electricity, container, false);
// Find the BarChart
        barChart = view.findViewById(R.id.barChart);


        // Get X-axis
        XAxis xAxis = barChart.getXAxis();
        // Set custom X-axis labels
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getYourListOfDays()));


        ImageView vectorImageView = view.findViewById(R.id.vector);

        // Set click listener for the ImageView
        vectorImageView.setOnClickListener(v -> {
            // Start MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        // TODO: Fetch electricity usage data from Firebase Realtime Database
        // Example: replace "yourFirebaseDataFetchingMethod()" with your actual method
        List<BarEntry> entries = yourFirebaseDataFetchingMethod();

        if (entries.isEmpty()) {
            Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
        } else {
            setupBarChart(entries);
        }

        return view;
    }

    private List<BarEntry> yourFirebaseDataFetchingMethod() {
        // TODO: Implement this method to fetch data from Firebase
        // You should return a list of BarEntry objects with your actual data
        // For example:
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 50));  // Replace with actual data
        entries.add(new BarEntry(1, 30));
        entries.add(new BarEntry(2, 80));
        entries.add(new BarEntry(3, 50));
        entries.add(new BarEntry(4, 34));
        entries.add(new BarEntry(5, 91));
        entries.add(new BarEntry(6, 63));
        // ...

        return entries;
    }

    private void setupBarChart(List<BarEntry> entries) {
        BarDataSet barDataSet = new BarDataSet(entries, "Power Consumption");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.setTouchEnabled(true);
        int darkBlueColor = ContextCompat.getColor(requireContext(), R.color.darkblue);

        barDataSet.setColor(darkBlueColor);
        // Customize the appearance of the chart
        // For example, you can customize the X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0f);
        xAxis.setLabelCount(entries.size());

        barChart.getAxisRight().setEnabled(false);
        // Refresh the chart
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }

    private List<String> getYourListOfDays() {
        List<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thu");
        days.add("Fri");
        days.add("Sat");
        days.add("Sun");
        return days;
    }

}