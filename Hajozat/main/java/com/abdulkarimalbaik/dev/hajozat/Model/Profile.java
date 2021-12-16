package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class Profile {

    @Nullable
    public int Id;
    @Nullable
    public String Manager_Name;
    @Nullable
    public String Image;
    @Nullable
    public int Verified;
    @Nullable
    public double avg_ratings;
    @Nullable
    public int h_counts;
    @Nullable
    public int r_counts;
    @Nullable
    public String Date_Join;
    @Nullable
    public double Lat;
    @Nullable
    public double Lng;
    @Nullable
    public String Slogan;
    @Nullable
    public String Description;

    public Profile() {
    }

    public Profile(int id, String manager_Name, String image, int verified, double avg_ratings, int h_counts, int r_counts, String date_Join, double lat, double lng, String slogan, String description) {
        Id = id;
        Manager_Name = manager_Name;
        Image = image;
        Verified = verified;
        this.avg_ratings = avg_ratings;
        this.h_counts = h_counts;
        this.r_counts = r_counts;
        Date_Join = date_Join;
        Lat = lat;
        Lng = lng;
        Slogan = slogan;
        Description = description;
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
    public String getManager_Name() {
        return Manager_Name;
    }
    @Nullable
    public void setManager_Name(String manager_Name) {
        Manager_Name = manager_Name;
    }
    @Nullable
    public String getImage() {
        return Image;
    }
    @Nullable
    public void setImage(String image) {
        Image = image;
    }
    @Nullable
    public int getVerified() {
        return Verified;
    }
    @Nullable
    public void setVerified(int verified) {
        Verified = verified;
    }
    @Nullable
    public double getAvg_ratings() {
        return avg_ratings;
    }
    @Nullable
    public void setAvg_ratings(double avg_ratings) {
        this.avg_ratings = avg_ratings;
    }
    @Nullable
    public int getH_counts() {
        return h_counts;
    }
    @Nullable
    public void setH_counts(int h_counts) {
        this.h_counts = h_counts;
    }
    @Nullable
    public int getR_counts() {
        return r_counts;
    }
    @Nullable
    public void setR_counts(int r_counts) {
        this.r_counts = r_counts;
    }
    @Nullable
    public String getDate_Join() {
        return Date_Join;
    }
    @Nullable
    public void setDate_Join(String date_Join) {
        Date_Join = date_Join;
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
    public String getSlogan() {
        return Slogan;
    }
    @Nullable
    public void setSlogan(String slogan) {
        Slogan = slogan;
    }
    @Nullable
    public String getDescription() {
        return Description;
    }
    @Nullable
    public void setDescription(String description) {
        Description = description;
    }
}
