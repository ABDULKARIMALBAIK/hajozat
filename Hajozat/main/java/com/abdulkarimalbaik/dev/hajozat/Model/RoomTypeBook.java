package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class RoomTypeBook {

    @Nullable
    public int Id;
    @Nullable
    public int count_booking;

    public RoomTypeBook() {
    }

    public RoomTypeBook(int Id, int count_booking) {
        this.Id = Id;
        this.count_booking = count_booking;
    }

    @Nullable
    public int getId() {
        return Id;
    }

    @Nullable
    public void setId(int Id) {
        this.Id = Id;
    }

    @Nullable
    public int getCount_booking() {
        return count_booking;
    }

    @Nullable
    public void setCount_booking(int count_booking) {
        this.count_booking = count_booking;
    }

}
