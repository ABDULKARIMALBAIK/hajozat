package com.abdulkarimalbaik.dev.hajozat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.HotelAdapterSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.Remote.RetrofitClient;
import com.github.clans.fab.FloatingActionButton;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
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

public class HotelSearchActivity extends AppCompatActivity {

    RecyclerView recyclerHotel;
    MaterialSearchBar materialSearchBar;
    FloatingActionButton fabFilter;

    HotelAdapterSearchFilter adapter;
    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    List<String> suggestList = new ArrayList<>();
    List<HotelSearchFilter> hotelList;

    int sizeAdapter;
    boolean isHotelsLoaded = false;


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

        setContentView(R.layout.activity_hotel_search);

        initViews();

        api = Common.getAPI();
        api = RetrofitClient.createNewRetrofit(Common.BASE_URL).create(IHajoztAPI.class);
        compositeDisposable = new CompositeDisposable();

        //Make sure you move this function after database is getInstance()
        if (Common.isConnectionToInternet(getBaseContext())){

            loadHotels();
        }
        else {
            Toast.makeText(getBaseContext(), "Please check your connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }

        materialSearchBar.setHint("Enter your Hotel...");
        //materialSearchBar.setSpeechMode(false);   //no  need , becuz we already defined it at XML


        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //When user type their text , we will change suggest list
                List<String> suggest = new ArrayList<>();
                for (String search :  suggestList){

                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search Bar is close
                //Restore original adapter
                if (!enabled){

                    if (hotelList != null){
                        if (hotelList.size() > 0){

                            adapter = new HotelAdapterSearchFilter(HotelSearchActivity.this , hotelList);
                            recyclerHotel.setAdapter(adapter);
                            startRecyclerViewAnimation();
                        }

                    }
                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish
                //Show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HotelSearchActivity.this , HotelFilterActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isHotelsLoaded){

            if (Common.hotelsFiltered != null) {
                adapter = new HotelAdapterSearchFilter(HotelSearchActivity.this , Common.hotelsFiltered);
                Common.hotelsFiltered = null;
            }
            else
                adapter = new HotelAdapterSearchFilter(HotelSearchActivity.this , hotelList);

            recyclerHotel.setAdapter(adapter);
            startRecyclerViewAnimation();
            loadSuggest();
        }

    }

    private void startSearch(CharSequence text) {

        compositeDisposable.add(api.getBrandHotelsSearch(Common.getToken() , String.valueOf(text).toLowerCase() , Common.currentBrand.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<HotelSearchFilter>>() {
            @Override
            public void accept(List<HotelSearchFilter> hotels) throws Exception {

                adapter = new HotelAdapterSearchFilter(HotelSearchActivity.this , hotels);
                recyclerHotel.setAdapter(adapter);
                startRecyclerViewAnimation();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(HotelSearchActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

    }

    private void loadSuggest() {

        for (HotelSearchFilter hotel : hotelList){
            suggestList.add(hotel.getHotel_name()); //add name of hotel to suggest list
        }

        materialSearchBar.setLastSuggestions(suggestList);
    }

    private void loadHotels() {

//        compositeDisposable.add(api.getBrandHotel(Common.getToken() , Common.currentBrand.getId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(new Consumer<Response<List<Hotel>>>() {
//                    @Override
//                    public void accept(Response<List<Hotel>> listResponse) throws Exception {
//
//                        //hotelList = hotels;
//                        hotelList = listResponse.body();
//
//                        if (Common.hotelsFiltered != null) {
//                            adapter = new HotelAdapter(HotelSearchActivity.this , Common.hotelsFiltered);
//                            Common.hotelsFiltered = null;
//                        }
//                        else
//                            adapter = new HotelAdapter(HotelSearchActivity.this ,listResponse.body());
//
//                        recyclerHotel.setAdapter(adapter);
//                        startRecyclerViewAnimation();
//
//                        loadSuggest();
//                    }
//                })
//                .subscribe());

        compositeDisposable.add(api.getBrandHotel_Search(Common.getToken() , Common.currentBrand.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<List<HotelSearchFilter>>() {
                    @Override
                    public void accept(List<HotelSearchFilter> listResponse) throws Exception {

//                        sizeAdapter = listResponse.size();
//
//                        if (sizeAdapter > 0){
//
//                            hotelList = listResponse;
//
//                            adapter = new HotelAdapterSearchFilter(HotelSearchActivity.this , listResponse);
//                            recyclerHotel.setAdapter(adapter);
//                            startRecyclerViewAnimation();
//
//                            loadSuggest();
//
//                        }
//                        else
//                            loadHotels();


                            //hotelList = hotels;

                        sizeAdapter = listResponse.size();

                        if (sizeAdapter > 0){

                            hotelList = listResponse;

                            if (Common.hotelsFiltered != null) {
                                adapter = new HotelAdapterSearchFilter(HotelSearchActivity.this , Common.hotelsFiltered);
                                Common.hotelsFiltered = null;
                            }
                            else
                                adapter = new HotelAdapterSearchFilter(HotelSearchActivity.this ,hotelList);

                            recyclerHotel.setAdapter(adapter);
                            startRecyclerViewAnimation();

                            loadSuggest();

                            isHotelsLoaded = true;
                        }
                        else
                            loadHotels();
                    }
                })
                .subscribe());

//        compositeDisposable.add(api.getBrandHotel(Common.getToken() , Common.currentBrand.getId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<Response<List<Hotel>>, Object>() {
//                    @Override
//                    public Object apply(Response<List<Hotel>> listResponse) throws Exception {
//
//                        //hotelList = hotels;
//                        hotelList = listResponse.body();
//
//                        if (Common.hotelsFiltered != null) {
//                            adapter = new HotelAdapter(HotelSearchActivity.this , Common.hotelsFiltered);
//                            Common.hotelsFiltered = null;
//                        }
//                        else
//                            adapter = new HotelAdapter(HotelSearchActivity.this ,listResponse.body());
//
//                        recyclerHotel.setAdapter(adapter);
//                        startRecyclerViewAnimation();
//
//
//                        return null;
//                    }
//                })
//                .subscribe());
//
//        sizeAdapter = adapter.getItemCount();


    }

    private void startRecyclerViewAnimation() {

        Context context = recyclerHotel.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recyclerHotel.setHasFixedSize(true);
        recyclerHotel.setLayoutManager(new LinearLayoutManager(this));

        //Set Animation
        recyclerHotel.setLayoutAnimation(controller);
        //recyclerHotel.getAdapter().notifyDataSetChanged();
        recyclerHotel.scheduleLayoutAnimation();
    }

    private void initViews() {

        recyclerHotel = (RecyclerView)findViewById(R.id.recycler_hotel_search);
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.hotel_searchBar);
        fabFilter = (FloatingActionButton)findViewById(R.id.fabHotelSearch);
    }
}
