package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class BrandRegisterId {

    @Nullable
    public int Id;

    public BrandRegisterId() {
    }

    public BrandRegisterId(int id) {
        Id = id;
    }

    @Nullable
    public int getId() {
        return Id;
    }
    @Nullable
    public void setId(int id) {
        Id = id;
    }
}
