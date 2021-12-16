package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class ImageHR {

    @Nullable
    public String Image_Path;

    public ImageHR() {
    }

    public ImageHR(String image_Path) {
        Image_Path = image_Path;
    }

    @Nullable
    public String getImage_Path() {
        return Image_Path;
    }
    @Nullable
    public void setImage_Path(String image_Path) {
        Image_Path = image_Path;
    }
}
