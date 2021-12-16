package com.abdulkarimalbaik.dev.hajozat.Model;

import io.reactivex.annotations.Nullable;

public class CheckNewBooking {

    @Nullable
    public int count_bookings;

    public CheckNewBooking() {
    }

    public CheckNewBooking(int count_bookings) {
        this.count_bookings = count_bookings;
    }

    @Nullable
    public int getCount_bookings() {
        return count_bookings;
    }
    @Nullable
    public void setCount_bookings(int count_bookings) {
        this.count_bookings = count_bookings;
    }

}
