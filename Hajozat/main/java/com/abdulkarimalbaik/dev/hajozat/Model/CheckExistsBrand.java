package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class CheckExistsBrand {

    @Nullable
    private boolean exists;
    @Nullable
    private String error_msg;

    public CheckExistsBrand() {
    }

    @Nullable
    public boolean isExists() {
        return exists;
    }
    @Nullable
    public void setExists(boolean exists) {
        this.exists = exists;
    }
    @Nullable
    public String getError_msg() {
        return error_msg;
    }
    @Nullable
    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
