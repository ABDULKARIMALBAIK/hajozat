package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class Hotel {

    @Nullable
    public String city_name;
    @Nullable
    public String hotel_name;
    @Nullable
    public String Star;
    @Nullable
    public String Id;
    @Nullable
    public String Image_Path;

    public Hotel() {
    }

    public Hotel(String city_name, String hotel_name, String star, String id, String image_Path) {
        this.city_name = city_name;
        this.hotel_name = hotel_name;
        Star = star;
        Id = id;
        Image_Path = image_Path;
    }

    @Nullable
    public String getCity_name() {
        return city_name;
    }
    @Nullable
    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
    @Nullable
    public String getHotel_name() {
        return hotel_name;
    }
    @Nullable
    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }
    @Nullable
    public String getStar() {
        return Star;
    }
    @Nullable
    public void setStar(String star) {
        Star = star;
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
    public String getImage_Path() {
        return Image_Path;
    }
    @Nullable
    public void setImage_Path(String image_Path) {
        Image_Path = image_Path;
    }
}
