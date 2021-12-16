package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class PasswordBrand {

    @Nullable
    public String Password;

    public PasswordBrand() {
    }

    public PasswordBrand(String password) {
        Password = password;
    }

    @Nullable
    public String getPassword() {
        return Password;
    }
    @Nullable
    public void setPassword(String password) {
        Password = password;
    }
}
