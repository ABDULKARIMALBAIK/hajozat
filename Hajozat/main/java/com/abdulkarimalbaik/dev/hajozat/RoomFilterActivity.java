package com.abdulkarimalbaik.dev.hajozat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.FacilityAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelFacility;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomAllType;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomSearchFilter;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

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

public class RoomFilterActivity extends AppCompatActivity {

    RecyclerView recyclerFeatures;
    MaterialEditText edtPrice;
    MaterialSpinner spinnerFamilyType , spinnerSpace;
    Button btnOK;
    ElegantNumberButton number_guests;

    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    FacilityAdapter adapter;

    int fromSpace , toSpace;
    List<RoomAllType> localTypes;

    int hotelId;

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
        
        setContentView(R.layout.activity_room_filter);

        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();
        
        initViews();
        initSpinnerSpace();

        hotelId = getIntent().getExtras().getInt("HOTEL");


        if (Common.isConnectionToInternet(RoomFilterActivity.this)){

            loadFacility();
            initFamilyType();
        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(RoomFilterActivity.this)) 
                    setData();
                else
                    Toast.makeText(RoomFilterActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFamilyType() {

        compositeDisposable.add(api.getRoomType(Common.getToken())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<RoomAllType>>() {
            @Override
            public void accept(List<RoomAllType> types) throws Exception {

                //citySelected = cities.get(0).getName();

                List<String> typeStrings = new ArrayList<>();

                for (int i = 0; i < types.size(); i++) {

                    typeStrings.add(types.get(i).getType());
                }

                spinnerFamilyType.setItems(typeStrings);
                localTypes = types;
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(RoomFilterActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void setData() {

        getSpaces();

        compositeDisposable.add(api.getBrandRoomFilters(Common.getToken(),
                Float.parseFloat(edtPrice.getText().toString()),
                fromSpace,
                toSpace,
                Integer.parseInt(number_guests.getNumber()),
                getFamilyType(),
                getFacilities(),
                hotelId,
                Common.currentBrand.getId()
                )
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<List<RoomSearchFilter>>() {
            @Override
            public void accept(List<RoomSearchFilter> rooms) throws Exception {

                Common.roomFiltered = rooms;
                Toast.makeText(RoomFilterActivity.this, "Rooms is filtered !", Toast.LENGTH_SHORT).show();
                finish();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(RoomFilterActivity.this, "Server is close @\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

         private String getFamilyType() {

            for (int i = 0; i < localTypes.size(); i++) {

                if (spinnerSpace.getSelectedIndex() == i)
                    return localTypes.get(i).getType();
            }
            return localTypes.get(0).getType();
        }

        private void getSpaces() {

            switch (spinnerSpace.getSelectedIndex()){
                case 0:{
                    fromSpace = 50;
                    toSpace = 100;
                    break;
                }
                case 1:{
                    fromSpace = 100;
                    toSpace = 150;
                    break;
                }
                case 2:{
                    fromSpace = 150;
                    toSpace = 200;
                    break;
                }
                case 3:{
                    fromSpace = 200;
                    toSpace = 300;
                    break;
                }
                case 4:{
                    fromSpace = 300;
                    toSpace = 400;
                    break;
                }
                case 5:{
                    fromSpace = 400;
                    toSpace = 500;
                    break;
                }
                default:{
                    fromSpace = 50;
                    toSpace = 100;
                    break;
                }
            }
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

            return  data.substring(0 , data.length() - 1);
        }

        private void loadFacility() {

            compositeDisposable.add(api.getFacilityRoomFilter(Common.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<HotelFacility>>() {
                        @Override
                        public void accept(List<HotelFacility> facilities) throws Exception {

                            adapter = new FacilityAdapter(RoomFilterActivity.this , facilities);
                            recyclerFeatures.setAdapter(adapter);
                            startRecyclerViewAnimation();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(RoomFilterActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }

        private void startRecyclerViewAnimation() {

        Context context = recyclerFeatures.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_slide_right);

        recyclerFeatures.setHasFixedSize(true);
        recyclerFeatures.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));

        //Set Animation
        recyclerFeatures.setLayoutAnimation(controller);
        recyclerFeatures.getAdapter().notifyDataSetChanged();
        recyclerFeatures.scheduleLayoutAnimation();
    }

    private void initSpinnerSpace() {

        List<String> spaces = new ArrayList<>();
        spaces.add("50 ~ 100");
        spaces.add("100 ~ 150");
        spaces.add("150 ~ 200");
        spaces.add("200 ~ 300");
        spaces.add("300 ~ 400");
        spaces.add("400 ~ 500");

        spinnerSpace.setItems(spaces);
    }

    private void initViews() {
        
        recyclerFeatures = (RecyclerView)findViewById(R.id.recycler_features);
        edtPrice = (MaterialEditText)findViewById(R.id.edtPrice);
        spinnerFamilyType = (MaterialSpinner)findViewById(R.id.spinner_family_type);
        number_guests = (ElegantNumberButton) findViewById(R.id.number_guests);
        spinnerSpace = (MaterialSpinner)findViewById(R.id.spinner_space);
        btnOK = (Button)findViewById(R.id.btnRoomFilterOK); 
    }
}
