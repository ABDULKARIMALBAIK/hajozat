package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class AnalyzeDataPercent {

    @Nullable
    public String Id;
    @Nullable
    public String Name;
    @Nullable
    public String percent;

    public AnalyzeDataPercent() {
    }

    public AnalyzeDataPercent(String id, String name, String percent) {
        Id = id;
        Name = name;
        this.percent = percent;
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
    public String getName() {
        return Name;
    }
    @Nullable
    public void setName(String name) {
        Name = name;
    }
    @Nullable
    public String getPercent() {
        return percent;
    }
    @Nullable
    public void setPercent(String percent) {
        this.percent = percent;
    }
}
