package com.abdulkarimalbaik.dev.hajozat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.FacilityRoomAdapter;
import com.abdulkarimalbaik.dev.hajozat.Adapter.SliderImagesAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.FacilityRoom;
import com.abdulkarimalbaik.dev.hajozat.Model.ImageHR;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomType;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomTypeBook;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RoomDetailsActivity extends AppCompatActivity {

    TextView txtPrice , txtVisitorNumber , txtSpace , txtPeopleNumber , txtType , txtFeatures;
    KenBurnsView kenBurnsView;
    FloatingActionButton fabRoom;
    RecyclerView recyclerViewFacility;
    SliderView sliderView;
    Toolbar toolbar;

    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    FacilityRoomAdapter adapter;

    int roomId , roomTypeId , hotelId;

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

        setContentView(R.layout.activity_room_details);

        initViews();
        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();


        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        initVariables();

        //Make sure you move this function after database is getInstance()
        if (Common.isConnectionToInternet(getBaseContext())){

            loadRoomData();
        }
        else {
            Toast.makeText(getBaseContext(), "Please check your connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }

        fabRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RoomDetailsActivity.this , RoomEditActivity.class);
                intent.putExtra("Room_Id" , roomId);
                intent.putExtra("Room_Type_Id" , String.valueOf(roomTypeId));
                intent.putExtra("hotel_id" ,  String.valueOf(hotelId));
                startActivity(intent);
            }
        });

    }

    private void initVariables() {
        if (getIntent().getExtras().getString("Room_Id") != null)
            roomId = Integer.parseInt(getIntent().getExtras().getString("Room_Id"));
        else
            Toast.makeText(this, "RoomId is null !!!", Toast.LENGTH_SHORT).show();

        if (getIntent().getExtras().getString("Room_Type_Id") != null)
            roomTypeId = Integer.parseInt(getIntent().getExtras().getString("Room_Type_Id"));
        else
            Toast.makeText(this, "RoomTypeId is null !!!", Toast.LENGTH_SHORT).show();

        if (getIntent().getExtras().getString("hotel_id") != null)
            hotelId = Integer.parseInt(getIntent().getExtras().getString("hotel_id"));
        else
            Toast.makeText(this, "RoomTypeId is null !!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sliderView.startAutoCycle();

        if (Common.isConnectionToInternet(getBaseContext())){

            loadRoomData();
        }
        else {
            Toast.makeText(getBaseContext(), "Please check your connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void loadRoomData() {

        loadType();
        loadTypeBooking();
        loadImages();
        loadFacility();

    }

    private void loadFacility() {

        compositeDisposable.add(api.getFacilityRoomById(Common.getToken() , roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FacilityRoom>>() {
                    @Override
                    public void accept(List<FacilityRoom> facilityRooms) throws Exception {

                        adapter = new FacilityRoomAdapter(RoomDetailsActivity.this , facilityRooms);
                        recyclerViewFacility.setAdapter(adapter);
                        startRecyclerViewFacilityAnimation();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(RoomDetailsActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void loadType() {

        compositeDisposable.add(api.BrandRoomDetailsType(Common.getToken() , roomId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<RoomType>() {
                    @Override
                    public void accept(RoomType roomType) throws Exception {

                        txtPrice.setText(String.valueOf(roomType.getPrice()));
                        txtSpace.setText(String.valueOf(roomType.getSpace()));
                        txtFeatures.setText(roomType.getFeatures());
                        txtType.setText(roomType.getType());
                        txtPeopleNumber.setText(String.valueOf(roomType.getPeople_Number()));
                        kenBurnsView.setContentDescription("Room " + roomType.getId());
                        toolbar.setTitle("Room " + roomType.getId());
                        setSupportActionBar(toolbar);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(RoomDetailsActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadTypeBooking() {

        api.BrandRoomDetailsTypeBooking(Common.getToken() , roomId)
                .enqueue(new Callback<RoomTypeBook>() {
                    @Override
                    public void onResponse(Call<RoomTypeBook> call, Response<RoomTypeBook> response) {

                        RoomTypeBook roomTypeBook = response.body();
                        if (roomTypeBook != null)
                            if (roomTypeBook.getCount_booking() > 0)
                                txtVisitorNumber.setText(String.valueOf(roomTypeBook.getCount_booking()));
                    }

                    @Override
                    public void onFailure(Call<RoomTypeBook> call, Throwable t) {
                        Toast.makeText(RoomDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void loadImages() {

        compositeDisposable.add(api.BrandRoomDetailsImages(Common.getToken() , roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ImageHR>>() {
                    @Override
                    public void accept(List<ImageHR> images) throws Exception {

                        //hotelImage = images.get(0);

                        if (images.get(0).getImage_Path().contains("drive")){

                            Picasso.get()
                                    .load(images.get(0).getImage_Path())
                                    .into(kenBurnsView);
                        }
                        else {
                            Picasso.get()
                                    .load(Common.BASE_URL + "photos/" + images.get(0).getImage_Path())
                                    .into(kenBurnsView);
                        }


                        sliderView.setSliderAdapter(new SliderImagesAdapter(getApplicationContext() , images));

                        sliderView.setIndicatorAnimation(IndicatorAnimations.DROP);
                        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEOUTROTATIONTRANSFORMATION);
                        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        sliderView.setIndicatorSelectedColor(Color.WHITE);
                        sliderView.setIndicatorUnselectedColor(Color.GRAY);
                        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                        sliderView.startAutoCycle();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(RoomDetailsActivity.this, "Server is close!\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void initViews() {

        txtPrice = (TextView)findViewById(R.id.txtPrice);
        txtVisitorNumber = (TextView)findViewById(R.id.txtVisitorNumber);
        txtSpace = (TextView)findViewById(R.id.txtSpace);
        txtPeopleNumber = (TextView)findViewById(R.id.txtPeopleNumber);
        txtType = (TextView)findViewById(R.id.txtType);
        txtFeatures = (TextView)findViewById(R.id.txtFeatures);
        kenBurnsView = (KenBurnsView)findViewById(R.id.imageRoom);
        fabRoom = (FloatingActionButton)findViewById(R.id.fabEditRoom);
        recyclerViewFacility = (RecyclerView)findViewById(R.id.recycler_facility);
        sliderView = (SliderView)findViewById(R.id.imageSlider);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void startRecyclerViewFacilityAnimation() {

        Context context = recyclerViewFacility.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recyclerViewFacility.setHasFixedSize(true);
        recyclerViewFacility.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));

        //Set Animation
        recyclerViewFacility.setLayoutAnimation(controller);
        recyclerViewFacility.getAdapter().notifyDataSetChanged();
        recyclerViewFacility.scheduleLayoutAnimation();
    }
}
