package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class AnalyzeDataYear {

    @Nullable
    private String month , Id , data;

    public AnalyzeDataYear() {
    }

    public AnalyzeDataYear(String month, String id, String data) {
        this.month = month;
        Id = id;
        this.data = data;
    }

    @Nullable
    public String getMonth() {
        return month;
    }
    @Nullable
    public void setMonth(String month) {
        this.month = month;
    }
    @Nullable
    public String getId() {
        return Id;
    }
    @Nullable
    public void setId(String id) {
        Id = id;
    }
    @Nullable
    public String getData() {
        return data;
    }
    @Nullable
    public void setData(String data) {
        this.data = data;
    }
}
