package com.abdulkarimalbaik.dev.hajozat.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.AnalyzeDataYear;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BookingFragment extends Fragment {

    BarChart barChart;
    public static BookingFragment instance = null;

    List<AnalyzeDataYear> analyzes;
    CompositeDisposable compositeDisposable;
    IHajoztAPI api;

    public static BookingFragment getInstance(){

        if (instance == null)
            instance = new BookingFragment();

        return instance;
    }

    public List<AnalyzeDataYear> getAnalyzes() {
        return analyzes;
    }

    public void setAnalyzes(List<AnalyzeDataYear> analyzes) {
        this.analyzes = analyzes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        barChart = (BarChart)view.findViewById(R.id.bookingsChart);

        compositeDisposable = new CompositeDisposable();
        api = Common.getAPI();

        if (Common.isConnectionToInternet(container.getContext())){

            getData();
        }
        else
            Toast.makeText(container.getContext(), "Please check your connection internet !", Toast.LENGTH_SHORT).show();

        return view;
    }

    private void setData() {

        ArrayList<BarEntry> data = new ArrayList<>();

        for (int i = 0; i < analyzes.size(); i++) {

            float value = Float.parseFloat(analyzes.get(i).getData());
            data.add(new BarEntry(i+1 , (int)value));

        }

        BarDataSet dataSet = new BarDataSet(data , "Data Set");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setDrawValues(true);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateY(500);
    }

    private void getData() {

        compositeDisposable.add(api.BrandBookingsByYear(Common.getToken(),"2020" , Common.currentBrand.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AnalyzeDataYear>>() {
                    @Override
                    public void accept(List<AnalyzeDataYear> analyzeDataYears) throws Exception {

                        if (analyzeDataYears != null && analyzeDataYears.size() != 0){

                            analyzes = analyzeDataYears;
                            barChart.getDescription().setEnabled(false);
                            setData();
                            barChart.setFitBars(true);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getContext(), "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

}
