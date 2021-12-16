package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class RoomData {

    @Nullable
    private int Room_Id , Room_Type_Id;

    public RoomData() {
    }

    public RoomData(int room_Id, int room_Type_Id) {
        Room_Id = room_Id;
        Room_Type_Id = room_Type_Id;
    }

    @Nullable
    public int getRoom_Id() {
        return Room_Id;
    }
    @Nullable
    public void setRoom_Id(int room_Id) {
        Room_Id = room_Id;
    }
    @Nullable
    public int getRoom_Type_Id() {
        return Room_Type_Id;
    }
    @Nullable
    public void setRoom_Type_Id(int room_Type_Id) {
        Room_Type_Id = room_Type_Id;
    }
}
