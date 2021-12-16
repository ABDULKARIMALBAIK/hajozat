package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class RoomSearchFilter {

    @Nullable
    public String Id;
    @Nullable
    public String room_type;
    @Nullable
    public String Price;
    @Nullable
    public String Image_Path;

    public RoomSearchFilter() {
    }

    public RoomSearchFilter(String id, String room_type, String price, String image_Path) {
        Id = id;
        this.room_type = room_type;
        Price = price;
        Image_Path = image_Path;
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
    public String getRoom_type() {
        return room_type;
    }
    @Nullable
    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }
    @Nullable
    public String getPrice() {
        return Price;
    }
    @Nullable
    public void setPrice(String price) {
        Price = price;
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
