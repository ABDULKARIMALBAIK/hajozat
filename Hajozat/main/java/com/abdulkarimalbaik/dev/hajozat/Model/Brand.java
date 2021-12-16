package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class Brand {

    @Nullable
    public int Id;
    @Nullable
    public String Name;
    @Nullable
    public String Email;
    @Nullable
    public String Phone_Number;
    @Nullable
    public String Password;
    @Nullable
    public String Manager_Name;
    @Nullable
    public String Slogan;
    @Nullable
    public String Description;
    @Nullable
    public double Lat;
    @Nullable
    public double Lng;
    @Nullable
    public int Verified;
    @Nullable
    public String Image;
    @Nullable
    public String Date_Join;
    @Nullable
    public String Token;

    public Brand() {
    }

    public Brand(int id, String name, String email, String phone_Number, String password, String manager_Name, String slogan, String description, double lat, double lng, int verified, String image, String date_Join, String token) {
        Id = id;
        Name = name;
        Email = email;
        Phone_Number = phone_Number;
        Password = password;
        Manager_Name = manager_Name;
        Slogan = slogan;
        Description = description;
        Lat = lat;
        Lng = lng;
        Verified = verified;
        Image = image;
        Date_Join = date_Join;
        Token = token;
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
    public String getName() {
        return Name;
    }
    @Nullable
    public void setName(String name) {
        Name = name;
    }
    @Nullable
    public String getEmail() {
        return Email;
    }
    @Nullable
    public void setEmail(String email) {
        Email = email;
    }
    @Nullable
    public String getPhone_Number() {
        return Phone_Number;
    }
    @Nullable
    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }
    @Nullable
    public String getPassword() {
        return Password;
    }
    @Nullable
    public void setPassword(String password) {
        Password = password;
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
    public int getVerified() {
        return Verified;
    }
    @Nullable
    public void setVerified(int verified) {
        Verified = verified;
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
    public String getDate_Join() {
        return Date_Join;
    }
    @Nullable
    public void setDate_Join(String date_Join) {
        Date_Join = date_Join;
    }
    @Nullable
    public String getToken() {
        return Token;
    }
    @Nullable
    public void setToken(String token) {
        Token = token;
    }
}
