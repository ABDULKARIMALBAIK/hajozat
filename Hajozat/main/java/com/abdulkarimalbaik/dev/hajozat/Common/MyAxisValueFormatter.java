package com.abdulkarimalbaik.dev.hajozat.Common;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter{

    String[] values;

    public MyAxisValueFormatter(String[] values) {
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return values[(int)value];
    }
}
