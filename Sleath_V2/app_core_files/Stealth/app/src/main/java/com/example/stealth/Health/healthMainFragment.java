package com.example.stealth.Health;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.stealth.R;
import com.example.stealth.databinding.FragmentActivityBinding;
import com.example.stealth.models.profileModels;
import com.example.stealth.models.stepsModels;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class healthMainFragment extends Fragment {
    FragmentActivityBinding binding;
    private com.example.stealth.models.profileModels profileModels;
    boolean dialogShown;
    private float floatWeight,floatHeight,floatBmi;
    ArrayList<BarEntry> yAxisStepsEntries;
    ArrayList<String> xAxisStepsEntries;
    ArrayList<stepsModels> stepsArrayList = new ArrayList<>();
    private String[] StepSpinner = {"Week","Month","All time"};
    private FirebaseAuth uAuth;
    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentActivityBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.StepCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stepsDialog();

            }
        });
        binding.HeartRateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                heartRateDialog();
            }
        });
        binding.SleepCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sleepDialog();
            }
        });
        binding.WeightCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightDialog();
            }
        });
        weightInfo();
        getBmiInfo();

    }
    private void weightInfo(){
        uAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.child(uAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    if(snapshot.hasChild("weight")){
                        String weight = snapshot.child("weight").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //////////////////////start of steps function////////////////////////////////////////////////////////
    private void stepsDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.steps);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ImageView back = dialog.findViewById(R.id.button_back);
        BarChart stepsChart = dialog.findViewById(R.id.stepsChart);
        StepsBarChartData();
        yAxisStepsEntries = new ArrayList<>();
        xAxisStepsEntries = new ArrayList<>();
        for(int i = 0;i<stepsArrayList.size();i++){
            String xData = stepsArrayList.get(i).getStepsXAxis();
            int yData = stepsArrayList.get(i).getStepsYAxis();
            yAxisStepsEntries.add(new BarEntry(i,yData));
            xAxisStepsEntries.add(xData);
            BarDataSet barDataSet = new BarDataSet(yAxisStepsEntries,"steps");
            barDataSet.setColors(Color.WHITE);
            customBarChartRender barChartRender = new customBarChartRender(stepsChart,stepsChart.getAnimator(), stepsChart.getViewPortHandler());
            barChartRender.setRadius(30);
            stepsChart.setRenderer(barChartRender);
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(.3f);
            barData.setValueTextColor(Color.WHITE);
            stepsChart.setData(barData);
            XAxis xAxis = stepsChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisStepsEntries));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setDrawAxisLine(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(xAxisStepsEntries.size());
            stepsChart.getAxisLeft().setTextColor(getResources().getColor(R.color.white));
            stepsChart.getAxisRight().setDrawAxisLine(false);
            stepsChart.getAxisLeft().setDrawAxisLine(false);
            stepsChart.getXAxis().setDrawAxisLine(false);
            stepsChart.getXAxis().setDrawGridLines(false);
            stepsChart.getAxisLeft().setDrawGridLines(false);
            stepsChart.getAxisRight().setDrawGridLines(false);
            stepsChart.animateY(2000);
            stepsChart.invalidate();
            Spinner spinner = dialog.findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> stepDropDown = ArrayAdapter.createFromResource(getActivity(),R.array.stepDropDownItems, android.R.layout.simple_spinner_item);
            stepDropDown.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(stepDropDown);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    private void StepsBarChartData(){
        stepsArrayList.clear();
        stepsArrayList.add(new stepsModels("steps",2000));
        stepsArrayList.add(new stepsModels("calories",500));
        stepsArrayList.add(new stepsModels("distance",1000));
    }
    //////////////////////////////end of steps functions////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ////////////////////////start of heart rate function ///////////////////////////////
    private void heartRateDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.heartrate);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ImageView back = dialog.findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
    ////////////////////////end of heart rate function///////////////////
    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////start of sleep function/////////////////////////////
    private void sleepDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sleep);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ImageView back = dialog.findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    //////////////////////////////end of the sleep function////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////start of weight function//////////////////////////////
    private void weightDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.weight);
        TextView weightText = dialog.findViewById(R.id.weightText);
        TextView userBmi = dialog.findViewById(R.id.bmiText);
        TextView userBodyType = dialog.findViewById(R.id.bodyTypeText);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ImageView back = dialog.findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    private void  getBmiInfo() {
        databaseReference.child(uAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    if (snapshot.hasChild("weight") && snapshot.hasChild("height")) {
                        String weight = snapshot.child("weight").getValue().toString();
                        String height = snapshot.child("height").getValue().toString();
                        floatWeight = Float.parseFloat(weight);
                        floatHeight = Float.parseFloat(height) / 100;
                        floatBmi = Math.round(floatWeight / (floatHeight * floatHeight));
                        binding.userBMI.setText(floatBmi + "");
                        if (floatBmi < 16) {
                            //binding.userBodyType.setText("Severe Thinness");

                        } else if (floatBmi < 16.9 && floatBmi > 16) {
                            //binding.userBodyType.setText("Moderate Thinness");

                        } else if (floatBmi < 18.4 && floatBmi > 17) {
                            //binding.userBodyType.setText("Mild Thinness");
                        } else if (floatBmi < 24.9 && floatBmi > 18.5) {
                            //binding.userBodyType.setText("Normal");
                        } else if (floatBmi < 29.9 && floatBmi > 25) {
                            //binding.userBodyType.setText("Overweight");
                        } else if (floatBmi < 34.9 && floatBmi > 30) {
                            //binding.userBodyType.setText("Obese Class I");
                        } else {
                            //binding.userBodyType.setText("Obese Class II");
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}