package com.abdulkarimalbaik.dev.hajozat.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Common.MyAxisValueFormatter;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.AnalyzeDataYear;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RatingFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {

    LineChart lineChart;
    public static RatingFragment instance = null;

    List<AnalyzeDataYear> analyzes;
    CompositeDisposable compositeDisposable;
    IHajoztAPI api;

    public static RatingFragment getInstance(){

        if (instance == null)
            instance = new RatingFragment();

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
        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        lineChart = (LineChart)view.findViewById(R.id.ratingsChart);

        compositeDisposable = new CompositeDisposable();
        api = Common.getAPI();

        if (Common.isConnectionToInternet(container.getContext())){

            getData();
        }
        else
            Toast.makeText(container.getContext(), "Please check your connection internet !", Toast.LENGTH_SHORT).show();

        return view;
    }

    private void getData() {

        compositeDisposable.add(api.BrandRatingsByYear(Common.getToken() , "2020" , Common.currentBrand.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AnalyzeDataYear>>() {
                    @Override
                    public void accept(List<AnalyzeDataYear> analyzeDataYears) throws Exception {

                        if (analyzeDataYears != null && analyzeDataYears.size() != 0){

                            analyzes = analyzeDataYears;
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

        lineChart.setOnChartGestureListener(RatingFragment.this);
        lineChart.setOnChartValueSelectedListener(RatingFragment.this);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        LimitLine upperLimit = new LimitLine(280 , "Cool");
        upperLimit.setLineWidth(2f);
        upperLimit.enableDashedLine(10f , 10f , 0f);
        upperLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upperLimit.setTextSize(15f);

        LimitLine lowerLimit = new LimitLine(260f , "Bad");
        upperLimit.setLineWidth(2f);
        upperLimit.enableDashedLine(10f , 10f , 10f);
        upperLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        upperLimit.setTextSize(15f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upperLimit);
        leftAxis.addLimitLine(lowerLimit);
        leftAxis.setAxisMaximum(500f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f , 10f , 0f);
        leftAxis.setDrawGridLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> data = new ArrayList<>();

        for (int i = 0; i < analyzes.size(); i++) {
            data.add(new Entry(i + 1 , Float.parseFloat(analyzes.get(i).getData())));
        }

        LineDataSet dataSet = new LineDataSet(data , "Data Set");
        dataSet.setFillAlpha(110);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLUE);

        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(dataSet);

        LineData lineData = new LineData(iLineDataSets);
        lineChart.setData(lineData);

        String [] values = new String[]{analyzes.get(0).getMonth(),
                analyzes.get(1).getMonth(),
                analyzes.get(2).getMonth(),
                analyzes.get(3).getMonth(),
                analyzes.get(4).getMonth(),
                analyzes.get(5).getMonth(),
                analyzes.get(6).getMonth(),
                analyzes.get(7).getMonth(),
                analyzes.get(8).getMonth(),
                analyzes.get(9).getMonth(),
                analyzes.get(10).getMonth(),
                analyzes.get(11).getMonth()};

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new MyAxisValueFormatter(values));
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.d("onChartGestureStart :" , "X: " + me.getX() + " , Y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.d("onChartGestureEnd :" , "X: " + me.getX() + " , Y: " + me.getY());
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
