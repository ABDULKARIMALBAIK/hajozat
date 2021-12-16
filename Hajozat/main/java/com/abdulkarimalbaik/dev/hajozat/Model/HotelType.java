package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class HotelType {

    @Nullable
    private String city;
    @Nullable
    private String hotel;
    @Nullable
    private double Star;
    @Nullable
    private double Lat;
    @Nullable
    private double Lng;
    @Nullable
    private String type;
    @Nullable
    private String host;

    public HotelType() {
    }

    public HotelType(String city, String hotel, double star, double lat, double lng, String type, String host) {
        this.city = city;
        this.hotel = hotel;
        Star = star;
        Lat = lat;
        Lng = lng;
        this.type = type;
        this.host = host;
    }

    @Nullable
    public String getCity() {
        return city;
    }
    @Nullable
    public void setCity(String city) {
        this.city = city;
    }
    @Nullable
    public String getHotel() {
        return hotel;
    }
    @Nullable
    public void setHotel(String hotel) {
        this.hotel = hotel;
    }
    @Nullable
    public double getStar() {
        return Star;
    }
    @Nullable
    public void setStar(double star) {
        Star = star;
    }
    @Nullable
    public double getLat() {
        return Lat;
    }
    @Nullable
    public void setLat(double lat) {
        Lat = lat;
    }
    @Nullable
    public double getLng() {
        return Lng;
    }
    @Nullable
    public void setLng(double lng) {
        Lng = lng;
    }
    @Nullable
    public String getType() {
        return type;
    }
    @Nullable
    public void setType(String type) {
        this.type = type;
    }
    @Nullable
    public String getHost() {
        return host;
    }
    @Nullable
    public void setHost(String host) {
        this.host = host;
    }
}
