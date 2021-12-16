package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class HotelRule {

    @Nullable
    public String Desciption;

    public HotelRule() {
    }

    public HotelRule(String desciption) {
        Desciption = desciption;
    }

    @Nullable
    public String getDesciption() {
        return Desciption;
    }
    @Nullable
    public void setDesciption(String desciption) {
        Desciption = desciption;
    }
}
