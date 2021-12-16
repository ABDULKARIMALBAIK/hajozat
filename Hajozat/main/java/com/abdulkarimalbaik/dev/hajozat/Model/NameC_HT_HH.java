package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class NameC_HT_HH {

    @Nullable
    String Name;

    public NameC_HT_HH() {
    }

    public NameC_HT_HH(String name) {
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
