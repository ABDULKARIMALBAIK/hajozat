package com.abdulkarimalbaik.dev.hajozat;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.RoomAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.Room;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BookingRoomActivity extends AppCompatActivity {

    RecyclerView recyclerBookingRoom;
    RoomAdapter adapter;

    CompositeDisposable compositeDisposable;
    IHajoztAPI api;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Note: add this code before setContentView method
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/indie_flower.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_booking_room);

        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();

        recyclerBookingRoom = (RecyclerView)findViewById(R.id.recycler_booking_rooms);

        if (Common.isConnectionToInternet(BookingRoomActivity.this)){

            loadBookingHotel();
        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
    }

    private void loadBookingHotel() {

        compositeDisposable.add(api.getBrandBookingRooms(Common.getToken() , Common.currentBrand.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Room>>() {
            @Override
            public void accept(List<Room> rooms) throws Exception {

                adapter = new RoomAdapter(BookingRoomActivity.this , rooms , 0);
                recyclerBookingRoom.setAdapter(adapter);
                startRecyclerViewAnimation();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(BookingRoomActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void startRecyclerViewAnimation() {

        Context context = recyclerBookingRoom.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_fall_down);

        recyclerBookingRoom.setHasFixedSize(true);
        recyclerBookingRoom.setLayoutManager(new LinearLayoutManager(this));

        //Set Animation
        recyclerBookingRoom.setLayoutAnimation(controller);
        recyclerBookingRoom.getAdapter().notifyDataSetChanged();
        recyclerBookingRoom.scheduleLayoutAnimation();
    }
}
