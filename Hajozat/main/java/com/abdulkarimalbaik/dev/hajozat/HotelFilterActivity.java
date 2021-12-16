package com.abdulkarimalbaik.dev.hajozat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.FacilityAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelFacility;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.Model.NameC_HT_HH;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HotelFilterActivity extends AppCompatActivity {

    RecyclerView recycler_facility;
    MaterialSpinner spinnerHotelType , spinnerCity , spinnerHost;
    Button btnOK;
    RatingBar rating;

    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    FacilityAdapter adapter;
    Map<String,String> map_city;

    String citySelected;

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

        setContentView(R.layout.activity_hotel_filter);

        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();
        map_city = new HashMap<>();

        initViews();

        if (Common.isConnectionToInternet(HotelFilterActivity.this)){

            loadFacilities();
            loadCities();
            loadHotelType();
            loadHost();
        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();


        spinnerCity.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                citySelected = map_city.get(String.valueOf(position));
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(HotelFilterActivity.this)){

                    filterData();
                }
                else
                    Toast.makeText(HotelFilterActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void filterData() {

        String facility_name = getFacilities();

        compositeDisposable.add(api.getBrandHotelsFilters(Common.getToken() , rating.getRating(), citySelected , spinnerHotelType.getSelectedIndex()+1, facility_name , spinnerHost.getSelectedIndex()+1 , Common.currentBrand.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<HotelSearchFilter>>() {
            @Override
            public void accept(List<HotelSearchFilter> hotels) throws Exception {

                Common.hotelsFiltered = hotels;
                Toast.makeText(HotelFilterActivity.this, "Hotels is filtered !", Toast.LENGTH_SHORT).show();
                finish();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(HotelFilterActivity.this, "Server is close !" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private String getFacilities() {

        List<HotelFacility> facilities = adapter.getFacilities();
        List<Integer> item_selected = adapter.getItems_seleceted();

        String data = "";
        for (int i = 0; i < facilities.size(); i++) {
            for (int j = 0; j < item_selected.size(); j++) {
                if (item_selected.get(j) == i){   //This item is selected !

                    data += facilities.get(i).getName();
                    data += "/";
                }
            }
        }

        if (data.length() > 0)
            return  data.substring(0 , data.length() - 1);
        else
            return  "";
    }

    private void loadHost() {

        compositeDisposable.add(api.getHotelHost(Common.getToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<NameC_HT_HH>>() {
                    @Override
                    public void accept(List<NameC_HT_HH> hosts) throws Exception {

                        List<String> hostStrings = new ArrayList<>();

                        for (int i = 0; i < hosts.size(); i++) {

                            hostStrings.add(hosts.get(i).getName());
                        }

                        try{
                            spinnerHost.setItems(hostStrings);
                        }
                        catch (Exception e){
                            Toast.makeText(HotelFilterActivity.this, "Error: " + e.getMessage() , Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(HotelFilterActivity.this, "Server is close !" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadHotelType() {

        compositeDisposable.add(api.getHotelType(Common.getToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<NameC_HT_HH>>() {
                    @Override
                    public void accept(List<NameC_HT_HH> types) throws Exception {

                        List<String> typeStrings = new ArrayList<>();

                        for (int i = 0; i < types.size(); i++) {

                            typeStrings.add(types.get(i).getName());
                        }

                        try{
                            spinnerHotelType.setItems(typeStrings);
                        }
                        catch (Exception e){
                            Toast.makeText(HotelFilterActivity.this, "Error: " + e.getMessage() , Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(HotelFilterActivity.this, "Server is close !" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadCities() {

        compositeDisposable.add(api.getCity(Common.getToken())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<NameC_HT_HH>>() {
            @Override
            public void accept(List<NameC_HT_HH> cities) throws Exception {

                citySelected = cities.get(0).getName();

                List<String> cityStrings = new ArrayList<>();

                for (int i = 0; i < cities.size(); i++) {

                    cityStrings.add(cities.get(i).getName());
                }

                try{

                    for (int i = 0; i < cities.size(); i++) {
                        map_city.put(String.valueOf(i) , cities.get(i).getName());
                    }
                    spinnerCity.setItems(cityStrings);
                }
                catch (Exception e){
                    Toast.makeText(HotelFilterActivity.this, "Error: " + e.getMessage() , Toast.LENGTH_SHORT).show();
                }

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(HotelFilterActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void loadFacilities() {

        compositeDisposable.add(api.getFacility(Common.getToken())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<HotelFacility>>() {
            @Override
            public void accept(List<HotelFacility> facilities) throws Exception {

                adapter = new FacilityAdapter(HotelFilterActivity.this , facilities);
                recycler_facility.setAdapter(adapter);
                startRecyclerViewAnimation();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(HotelFilterActivity.this, "Server is close !" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initViews() {

        recycler_facility = (RecyclerView)findViewById(R.id.recycler_facility);
        spinnerCity = (MaterialSpinner)findViewById(R.id.spinner_city);
        spinnerHotelType = (MaterialSpinner)findViewById(R.id.spinner_hotel_type);
        spinnerHost = (MaterialSpinner)findViewById(R.id.spinner_host);
        btnOK = (Button)findViewById(R.id.btnHotelFilterOK);
        rating = (RatingBar)findViewById(R.id.hotel_rating);
    }

    private void startRecyclerViewAnimation() {

        Context context = recycler_facility.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recycler_facility.setHasFixedSize(true);
        recycler_facility.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));

        //Set Animation
        recycler_facility.setLayoutAnimation(controller);
        recycler_facility.getAdapter().notifyDataSetChanged();
        recycler_facility.scheduleLayoutAnimation();
    }
}
