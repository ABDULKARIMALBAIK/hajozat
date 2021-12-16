package com.abdulkarimalbaik.dev.hajozat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.RoomAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Common.RecyclerItemTouchHelper;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Interface.RecyclerItemTouchHelperListener;
import com.abdulkarimalbaik.dev.hajozat.Model.Room;
import com.abdulkarimalbaik.dev.hajozat.ViewHolder.RoomViewHolder;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  , RecyclerItemTouchHelperListener {

    DrawerLayout drawer;
    RecyclerView recyclerRoom;
    RoomAdapter adapter;
    FloatingActionButton fabRoom;
    RelativeLayout rootLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView txtName , txtEmail;
    CircleImageView img_avatar;

    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    int hotel_id;

    List<Room> roomList;

    Goldfinger goldfinger;

    LoginButton loginButton;
    CallbackManager callbackManager;

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

        setContentView(R.layout.activity_room);

        initView();
        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();

        Paper.init(this);

        callbackManager = CallbackManager.Factory.create();

        //Swipe to delete item
        //Very important put code ItemTouchHelper....  AFTER init RecyclerView
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0 ,
                ItemTouchHelper.LEFT,
                this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerRoom);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Rooms");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        //Init Navication Drawer

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(RoomActivity.this);


        //Set Data of brand on navigation
        View headerView = navigationView.getHeaderView(0);
        txtName = (TextView)headerView.findViewById(R.id.brand_name);
        txtEmail = (TextView)headerView.findViewById(R.id.brand_email);
        img_avatar = (CircleImageView)headerView.findViewById(R.id.brand_image);


        if (getIntent().getExtras().getString("hotel_id") != null)
            hotel_id = Integer.parseInt(getIntent().getExtras().getString("hotel_id"));
        else
            Toast.makeText(this, "Hotel_Id is null !!!", Toast.LENGTH_SHORT).show();



        //Make sure you move this function after database is getInstance()
        if (Common.isConnectionToInternet(getBaseContext())){

            loadRooms();
        }
        else {
            Toast.makeText(getBaseContext(), "Please check your connection !!!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Fab room was clicked
        fabRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent roomAddIntent = new Intent(RoomActivity.this , RoomAddActivity.class);
                roomAddIntent.putExtra("hotel_id" , String.valueOf(hotel_id));
                startActivity(roomAddIntent);
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Common.isConnectionToInternet(getBaseContext())){

                    loadRooms();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Toast.makeText(getBaseContext(), "Please check your connection !!!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode , resultCode , data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        recyclerRoom = (RecyclerView)findViewById(R.id.recycler_rooms);
        fabRoom = (FloatingActionButton)findViewById(R.id.fabRoom);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Common.isConnectionToInternet(RoomActivity.this))
            loadRooms();
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals("DELETE")){

            deleteRoom(Integer.parseInt(roomList.get(item.getOrder()).getId()));
        }

        return super.onContextItemSelected(item);
    }

    private void deleteRoom(int roomId) {

        api.deleteRoom(Common.getToken() , roomId)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Toast.makeText(RoomActivity.this, response.body(), Toast.LENGTH_SHORT).show();

                        //Refresh data
                        loadRooms();
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Toast.makeText(RoomActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadRooms() {

        compositeDisposable.add(api.getBrandRoom(Common.getToken() , hotel_id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Room>>() {
            @Override
            public void accept(List<Room> rooms) throws Exception {

                if (rooms != null){

                    roomList = rooms;

                    adapter = new RoomAdapter(RoomActivity.this , rooms , hotel_id);
                    recyclerRoom.setAdapter(adapter);
                    startRecyclerViewAnimation();


                    //set information
                    txtName.setText(Common.currentBrand.getName());
                    txtEmail.setText(Common.currentBrand.getEmail());

                    if (Common.currentBrand.getImage().contains("drive")){

                        Picasso.get().load(Common.currentBrand.getImage()).into(img_avatar);
                    }
                    else {

                        Picasso.get().load(Common.BASE_URL + "photos/" + Common.currentBrand.getImage()).into(img_avatar);
                    }

                }
                else
                    loadRooms();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(RoomActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void startRecyclerViewAnimation() {

        Context context = recyclerRoom.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(
                context , R.anim.layout_fall_down);

        recyclerRoom.setHasFixedSize(true);
        recyclerRoom.setItemViewCacheSize(20);
        recyclerRoom.setDrawingCacheEnabled(true);
        recyclerRoom.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerRoom.setLayoutManager(new LinearLayoutManager(this));

        //Set Animation
        recyclerRoom.setLayoutAnimation(controller);
        recyclerRoom.getAdapter().notifyDataSetChanged();
        recyclerRoom.scheduleLayoutAnimation();
    }

    private void showFingerprint() {

        if (Common.isConnectionToInternet(RoomActivity.this)){

            String hasFingerprint = Paper.book().read("FINGERPRINT");

            if (hasFingerprint == null){

                final AlertDialog alertDialog;
                View view = getLayoutInflater().inflate(R.layout.fingerprint_layout , null);

                AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this)
                        .setTitle("Add Fingerprint")
                        .setMessage("Add fingerprint for upcomming login :")
                        .setIcon(R.drawable.ic_security_powerful_24dp)
                        .setCancelable(false)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                goldfinger.cancel();
                                dialog.dismiss();
                            }
                        });

                builder.setView(view);
                alertDialog = builder.show();

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.powerfulColor));

                final String fingerprintCode = UUID.randomUUID().toString().substring(0 , 8);

                startFingerprint(fingerprintCode , alertDialog);

            }
            else {

                Toast.makeText(this, "You added fingerprint already !", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();

    }

    private void startFingerprint(final String fingerprintCode , final AlertDialog alertDialog) {

        Goldfinger.Builder goldBuilder = new Goldfinger.Builder(RoomActivity.this);
        goldfinger = goldBuilder.build();

        if (goldfinger.hasFingerprintHardware()){

            if (goldfinger.hasEnrolledFingerprint()){

                goldfinger.encrypt(Common.KEY_NAME, fingerprintCode, new Goldfinger.Callback() {
                    @Override
                    public void onSuccess(final String value) {

                        api.BrandAddFingerprint(Common.getToken() , value , Common.currentBrand.getId())
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        Toast.makeText(RoomActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                        Paper.book().write("FINGERPRINT", value);

                                        alertDialog.dismiss();

                                    }
                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                        alertDialog.dismiss();
                                        goldfinger.cancel();
                                        Toast.makeText(RoomActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                    @Override
                    public void onError(Error error) {

                        alertDialog.dismiss();
                        goldfinger.cancel();
                        Toast.makeText(RoomActivity.this, "ERROR: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else{
                goldfinger.cancel();
                Toast.makeText(this, "You did't enrolled fingerprint !", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            goldfinger.cancel();
            Toast.makeText(this, "Your device doesn't support fingerprint !", Toast.LENGTH_SHORT).show();
        }

    }

    private void facebookLogin() {

        if (Common.isConnectionToInternet(this)){

            final AlertDialog dialogShow;

            View view = getLayoutInflater().inflate(R.layout.facebook_register_layout  , null);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(RoomActivity.this)
                    .setMessage("Use facebook login to easy login in next time :")
                    .setTitle("Facebook Login")
                    .setIcon(R.drawable.ic_facebook_power_clipbord_24dp)
                    .setCancelable(false)
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.setView(view);
            dialogShow = alertDialog.show();

            dialogShow.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
            dialogShow.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.powerfulColor));

            loginButton = (LoginButton)view.findViewById(R.id.facebookLogin);

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    dialogShow.dismiss();

                    String facebookId = loginResult.getAccessToken().getUserId();
                    Toast.makeText(RoomActivity.this, "UserId: " + facebookId, Toast.LENGTH_SHORT).show();
                    Log.d("UserId" , facebookId);

                    final android.app.AlertDialog dialog = new SpotsDialog(RoomActivity.this);
                    dialog.show();
                    dialog.setMessage("Please waiting....");
                    dialog.setCancelable(false);

                    api.BrandAddFacebook(Common.getToken() , facebookId , Common.currentBrand.getId())
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    dialog.dismiss();
                                    Toast.makeText(RoomActivity.this, response.body(), Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                    dialog.dismiss();
                                    Toast.makeText(RoomActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                @Override
                public void onCancel() {

                    Toast.makeText(RoomActivity.this, "Prcoess canceled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {

                    Toast.makeText(RoomActivity.this, "ERROR: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hotel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_report) {

            AlertDialog alertDialog;

            View view = getLayoutInflater().inflate(R.layout.send_report_layout , null);

            final MaterialEditText edtReport = (MaterialEditText)view.findViewById(R.id.edtReport);

            AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this)
                    .setTitle("Report")
                    .setMessage("Write your issue :")
                    .setIcon(R.drawable.ic_info_powerful_24dp)
                    .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (Common.isConnectionToInternet(RoomActivity.this))
                                sendReport(edtReport.getText().toString());
                            else
                                Toast.makeText(RoomActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false);

            builder.setView(view);
            alertDialog = builder.show();

            alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
            alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
        }
        else if (id == R.id.action_search){

            Intent roomIntent = new Intent(RoomActivity.this , RoomSearchActivity.class);
            roomIntent.putExtra("Hotel_Id" , hotel_id);
            startActivity(roomIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendReport(String context) {

        api.BrandSendReport(Common.getToken() , Common.currentBrand.getId() , context)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Toast.makeText(RoomActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Toast.makeText(RoomActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.hotel_action)
            finish();
        else if (id == R.id.profile_action)
            startActivity(new Intent(RoomActivity.this , ProfileActivity.class));
        else if (id == R.id.about_action)
            startActivity(new Intent(RoomActivity.this , AboutActivity.class));
        else if(id == R.id.analyze_action)
            startActivity(new Intent(RoomActivity.this , AnalyzeActivity.class));
        else if (id == R.id.room_booking_action)
            startActivity(new Intent(RoomActivity.this , BookingRoomActivity.class));
        else if (id == R.id.fingerprint_action){

            showFingerprint();
        }
        else if (id == R.id.facebook_action){

            facebookLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof RoomViewHolder){

            String name = ((RoomAdapter)recyclerRoom.getAdapter()).getItem(position).getId();
            final Room deleteItem = ((RoomAdapter)recyclerRoom.getAdapter()).getItem(viewHolder.getAdapterPosition());

//            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());
            //new Database(getBaseContext()).removeFromFavorites(deleteItem.getFoodId() , Common.currentUser.getPhone());
            deleteRoom(Integer.parseInt(deleteItem.getId()));


            //Make Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout ,"Room " +  name + " is removed from favorites !" , Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    adapter.restoreItem(deleteItem ,deleteIndex);
//                    new Database(getBaseContext()).addToFavorites(deleteItem);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}
