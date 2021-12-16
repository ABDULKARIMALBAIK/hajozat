package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class RoomAllType {

    @Nullable
    public String Type;

    public RoomAllType() {
    }

    public RoomAllType(String type) {
        Type = type;
    }
    @Nullable
    public String getType() {
        return Type;
    }
    @Nullable
    public void setType(String type) {
        Type = type;
    }
}
