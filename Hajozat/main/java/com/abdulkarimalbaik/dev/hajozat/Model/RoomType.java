package com.abdulkarimalbaik.dev.hajozat.Model;


import io.reactivex.annotations.Nullable;

public class RoomType {

    @Nullable
    private int  Id , Space , People_Number;
    @Nullable
    private double Price;
    @Nullable
    private String Features , Type;

    public RoomType() {
    }

    public RoomType(int id, int space, int people_Number, double price, String features, String type) {
        Id = id;
        Space = space;
        People_Number = people_Number;
        Price = price;
        Features = features;
        Type = type;
    }

    @Nullable
    public int getId() {
        return Id;
    }
    @Nullable
    public void setId(int id) {
        Id = id;
    }
    @Nullable
    public int getSpace() {
        return Space;
    }
    @Nullable
    public void setSpace(int space) {
        Space = space;
    }
    @Nullable
    public int getPeople_Number() {
        return People_Number;
    }
    @Nullable
    public void setPeople_Number(int people_Number) {
        People_Number = people_Number;
    }
    @Nullable
    public double getPrice() {
        return Price;
    }
    @Nullable
    public void setPrice(double price) {
        Price = price;
    }
    @Nullable
    public String getFeatures() {
        return Features;
    }
    @Nullable
    public void setFeatures(String features) {
        Features = features;
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
