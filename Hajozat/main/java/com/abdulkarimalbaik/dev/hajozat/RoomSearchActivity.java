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

import com.abdulkarimalbaik.dev.hajozat.Adapter.RoomAdapteSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomSearchFilter;
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

public class RoomSearchActivity extends AppCompatActivity {

    RecyclerView recyclerRoom;
    MaterialSearchBar materialSearchBar;
    FloatingActionButton fabFilter;

    RoomAdapteSearchFilter adapter;
    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    List<String> suggestList = new ArrayList<>();
    List<RoomSearchFilter> roomList;

    int hotelId;
    boolean isRoomsLoaded = false;

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

        setContentView(R.layout.activity_room_search);

        initViews();

        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();

        hotelId = getIntent().getExtras().getInt("Hotel_Id");

        //Make sure you move this function after database is getInstance()
        if (Common.isConnectionToInternet(getBaseContext())){

            loadRooms();
        }
        else {
            Toast.makeText(getBaseContext(), "Please check your connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }

        materialSearchBar.setHint("Enter your Room...");
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

                    if (roomList != null){
                        if (roomList.size() > 0){

                            adapter = new RoomAdapteSearchFilter(RoomSearchActivity.this , roomList , hotelId);
                            recyclerRoom.setAdapter(adapter);
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

                Intent roomFilter = new Intent(RoomSearchActivity.this , RoomFilterActivity.class);
                roomFilter.putExtra("HOTEL" , hotelId);
                startActivity(roomFilter);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isRoomsLoaded){

            if (Common.roomFiltered != null) {
                adapter = new RoomAdapteSearchFilter(RoomSearchActivity.this , Common.roomFiltered , hotelId);
                Common.roomFiltered = null;
            }
            else
                adapter = new RoomAdapteSearchFilter(RoomSearchActivity.this , roomList , hotelId);

            recyclerRoom.setAdapter(adapter);
            startRecyclerViewAnimation();
            loadSuggest();
        }

    }

    private void startSearch(CharSequence text) {

        compositeDisposable.add(api.getBrandRoomsSearch(Common.getToken() , Integer.parseInt(String.valueOf(text)) , hotelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RoomSearchFilter>>() {
                    @Override
                    public void accept(List<RoomSearchFilter> rooms) throws Exception {

                        adapter = new RoomAdapteSearchFilter(RoomSearchActivity.this , rooms , hotelId);
                        recyclerRoom.setAdapter(adapter);
                        startRecyclerViewAnimation();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(RoomSearchActivity.this, "Server is close !" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void loadSuggest() {

        for (RoomSearchFilter room : roomList){
            suggestList.add(room.getId());
        }

        materialSearchBar.setLastSuggestions(suggestList);
    }

    private void initViews() {

        recyclerRoom = (RecyclerView)findViewById(R.id.recycler_room_search);
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.room_searchBar);
        fabFilter = (FloatingActionButton)findViewById(R.id.fabRoomSearch);
    }

    private void loadRooms() {

        compositeDisposable.add(api.getBrandRoom_Search(Common.getToken() , hotelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RoomSearchFilter>>() {
                    @Override
                    public void accept(List<RoomSearchFilter> rooms) throws Exception {

                        roomList = rooms;

                        if (Common.roomFiltered != null) {
                            adapter = new RoomAdapteSearchFilter(RoomSearchActivity.this , Common.roomFiltered , hotelId);
                            Common.roomFiltered = null;
                        }
                        else
                            adapter = new RoomAdapteSearchFilter(RoomSearchActivity.this , rooms , hotelId);

                        recyclerRoom.setAdapter(adapter);
                        startRecyclerViewAnimation();

                        loadSuggest();

                        isRoomsLoaded = true;

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(RoomSearchActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void startRecyclerViewAnimation() {

        Context context = recyclerRoom.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_fall_down);

        recyclerRoom.setHasFixedSize(true);
        recyclerRoom.setLayoutManager(new LinearLayoutManager(this));

        //Set Animation
        recyclerRoom.setLayoutAnimation(controller);
        recyclerRoom.getAdapter().notifyDataSetChanged();
        recyclerRoom.scheduleLayoutAnimation();
    }
}
