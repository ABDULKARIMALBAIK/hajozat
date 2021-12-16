package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class HotelFacility {

    @Nullable
    public String Name;

    public HotelFacility() {
    }

    public HotelFacility(String name) {
        Name = name;
    }

    @Nullable
    public String getName() {
        return Name;
    }
    @Nullable
    public void setName(String name) {
        Name = name;
    }
}
