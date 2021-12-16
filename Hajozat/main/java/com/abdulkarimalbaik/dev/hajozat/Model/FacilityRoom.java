package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class FacilityRoom {

    @Nullable
    private String Id , Number , Name;

    public FacilityRoom() {
    }

    public FacilityRoom(String id, String number, String name) {
        Id = id;
        Number = number;
        Name = name;
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
    public String getNumber() {
        return Number;
    }
    @Nullable
    public void setNumber(String number) {
        Number = number;
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
