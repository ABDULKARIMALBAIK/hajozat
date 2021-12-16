package com.abdulkarimalbaik.dev.hajozat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.FacilityAdapter;
import com.abdulkarimalbaik.dev.hajozat.Adapter.RuleAdapter;
import com.abdulkarimalbaik.dev.hajozat.Adapter.SliderImagesAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelFacility;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelRule;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelType;
import com.abdulkarimalbaik.dev.hajozat.Model.ImageHR;
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
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HotelDetailActivity extends AppCompatActivity {

    FloatingActionButton fabHotelEdit;
    SliderView sliderView;
    KenBurnsView kenBurnsView;
    TextView txtCity , txtType , txtLocation , txtHost;
    RatingBar hotelRating;
    RecyclerView recyclerViewFacility , recyclerViewRules;
    Button btnShowLocation;
    Toolbar toolbar;

    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    FacilityAdapter facilityAdapter;
    RuleAdapter ruleAdapter;

    int hotelId;
    String hotelImage;

    double lat , lng;
    String name;

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

        setContentView(R.layout.activity_hotel_detail);

        initViews();
        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();


        //toolbar.setTitle("Hotel Details");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));


        hotelId = getIntent().getExtras().getInt("Hotel_Id");

        //Make sure you move this function after database is getInstance()
        if (Common.isConnectionToInternet(getBaseContext())){

            loadHotelData();
        }
        else {
            Toast.makeText(getBaseContext(), "Please check your connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }

        fabHotelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Make sure you move this function after database is getInstance()
                if (Common.isConnectionToInternet(getBaseContext())){

                    Intent intent = new Intent(HotelDetailActivity.this , HotelEditActivity.class);
                    intent.putExtra("Hotel_Name" , String.valueOf(kenBurnsView.getContentDescription()));
                    intent.putExtra("Hotel_Image" , hotelImage);
                    intent.putExtra("Hotel_Id" , hotelId);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your connection !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //This code to Google Map
//                Intent intentMap = new Intent(HotelDetailActivity.this , HotelMapActivity.class);
//                intentMap.putExtra("Lat" , lat);
//                intentMap.putExtra("Lng" , lng);
//                intentMap.putExtra("Name" , name);
//                startActivity(intentMap);

                //This code to ArcGIS Map
                Intent intentMap = new Intent(HotelDetailActivity.this , ArcGISMapActivity.class);
                intentMap.putExtra("Lat" , lat);
                intentMap.putExtra("Lng" , lng);
                intentMap.putExtra("Name" , name);
                startActivity(intentMap);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        sliderView.startAutoCycle();

        if (Common.isConnectionToInternet(HotelDetailActivity.this)) {

            loadHotelData();
        }
        else
            Toast.makeText(this, "Server is close !", Toast.LENGTH_SHORT).show();
    }

    private void loadHotelData() {

        loadType();
        loadImages();
        loadFacility();
        loadRules();
    }

    private void loadRules() {

        compositeDisposable.add(api.BrandHotelDetailsRules(Common.getToken() , hotelId)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<HotelRule>>() {
            @Override
            public void accept(List<HotelRule> rules) throws Exception {

                ruleAdapter = new RuleAdapter(HotelDetailActivity.this , rules);
                recyclerViewRules.setAdapter(ruleAdapter);
                startRecyclerViewRulesAnimation();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(HotelDetailActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

    }

    private void loadFacility() {

        compositeDisposable.add(api.BrandHotelDetailsFacility(Common.getToken() , hotelId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<HotelFacility>>() {
        @Override
        public void accept(List<HotelFacility> facilities) throws Exception {

            facilityAdapter = new FacilityAdapter(HotelDetailActivity.this , facilities);
            recyclerViewFacility.setAdapter(facilityAdapter);
            startRecyclerViewFacilityAnimation();
        }
    }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            Toast.makeText(HotelDetailActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }));

}

    private void loadType() {

        compositeDisposable.add(api.BrandHotelDetailsType(Common.getToken() , hotelId)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<HotelType>() {
            @Override
            public void accept(HotelType hotelType) throws Exception {

                txtCity.setText(hotelType.getCity());
                txtLocation.setText("Lat: " + hotelType.getLat() + "   Lng:" + hotelType.getLng());
                txtType.setText(hotelType.getType());
                hotelRating.setRating(Float.parseFloat(String.valueOf(hotelType.getStar())));
                kenBurnsView.setContentDescription(hotelType.getHotel());
                toolbar.setTitle(hotelType.getHotel());
                txtHost.setText(hotelType.getHost());

                setSupportActionBar(toolbar);

                lat = hotelType.getLat();
                lng = hotelType.getLng();
                name = hotelType.getHotel();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(HotelDetailActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void loadImages() {

        compositeDisposable.add(api.BrandHotelDetailsImages(Common.getToken() , hotelId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<ImageHR>>() {
            @Override
            public void accept(List<ImageHR> images) throws Exception {

                if (images.get(0).getImage_Path().contains("drive")){

                    hotelImage = images.get(0).getImage_Path();

                    Picasso.get()
                            .load(images.get(0).getImage_Path())
                            .resize(400 , 400)
                            .into(kenBurnsView);
                }
                else {

                    hotelImage = Common.BASE_URL + "photos/" + images.get(0).getImage_Path();

                    Picasso.get()
                            .load(Common.BASE_URL + "photos/" + images.get(0).getImage_Path())
                            .resize(400 , 400)
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
                Toast.makeText(HotelDetailActivity.this, "Server is close!\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initViews() {

        fabHotelEdit = (FloatingActionButton)findViewById(R.id.fabEditHotel);
        sliderView = (SliderView) findViewById(R.id.imageSlider);
        kenBurnsView = (KenBurnsView)findViewById(R.id.imageHotel);
        txtCity = (TextView)findViewById(R.id.txtCity);
        txtType = (TextView)findViewById(R.id.txtType);
        txtLocation = (TextView)findViewById(R.id.txtLocation);
        hotelRating = (RatingBar)findViewById(R.id.hotelRating);
        recyclerViewFacility = (RecyclerView)findViewById(R.id.recycler_facility);
        recyclerViewRules = (RecyclerView)findViewById(R.id.recycler_rules);
        txtHost = (TextView)findViewById(R.id.txtHost);
        btnShowLocation = (Button)findViewById(R.id.btnShowLocation);
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

    private void startRecyclerViewRulesAnimation(){

        Context context = recyclerViewRules.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recyclerViewRules.setHasFixedSize(true);
        recyclerViewRules.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));

        //Set Animation
        recyclerViewRules.setLayoutAnimation(controller);
        recyclerViewRules.getAdapter().notifyDataSetChanged();
        recyclerViewRules.scheduleLayoutAnimation();
    }
}
