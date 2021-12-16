package com.abdulkarimalbaik.dev.hajozat.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.AnalyzeDataPercent;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TopBookingFragment extends Fragment {

    PieChart pieChart;
    public static TopBookingFragment instance = null;

    List<AnalyzeDataPercent> analyzes;
    CompositeDisposable compositeDisposable;
    IHajoztAPI api;

    public static TopBookingFragment getInstance(){

        if (instance == null)
            instance = new TopBookingFragment();

        return instance;
    }

    public List<AnalyzeDataPercent> getAnalyzes() {
        return analyzes;
    }

    public void setAnalyzes(List<AnalyzeDataPercent> analyzes) {
        this.analyzes = analyzes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_top_booking, container, false);

        compositeDisposable = new CompositeDisposable();
        api = Common.getAPI();

        pieChart = (PieChart)view.findViewById(R.id.topBookingChart);

        if (Common.isConnectionToInternet(container.getContext())){

            getData();
        }
        else
            Toast.makeText(container.getContext(), "Please Check your connection internet !", Toast.LENGTH_SHORT).show();

        return view;
    }

    private void getData() {

        compositeDisposable.add(api.BrandTopHotelBookings(Common.getToken() , Common.currentBrand.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AnalyzeDataPercent>>() {
                    @Override
                    public void accept(List<AnalyzeDataPercent> analyzeDataPercents) throws Exception {

                        if (analyzeDataPercents != null && analyzeDataPercents.size() != 0){

                            analyzes = analyzeDataPercents;
                            completeLoadProcess();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getContext(), "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void completeLoadProcess() {

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5f , 10f , 5f , 5f);
        pieChart.setDragDecelerationFrictionCoef(0.99f);
        //pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(60f);
        //pieChart.setTransparentCircleRadius(90f);

        ArrayList<PieEntry> data = new ArrayList<>();

        for (int i = 0; i < analyzes.size(); i++) {
            data.add(new PieEntry(Float.parseFloat(analyzes.get(i).getPercent()) , analyzes.get(i).getName()));
        }

        PieDataSet dataSet = new PieDataSet(data , "Bookings");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.GREEN);

        pieChart.setData(pieData);
        pieChart.animateXY(2000 , 2000);
        pieChart.invalidate();

    }

}
