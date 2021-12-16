package com.abdulkarimalbaik.dev.hajozat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.FacilityRoomAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Common.ProgressRequestBody;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Interface.UploadCallBack;
import com.abdulkarimalbaik.dev.hajozat.Model.FacilityRoom;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RoomEditActivity extends AppCompatActivity implements UploadCallBack{

    private static final int PICK_FILE_REQUEST = 1000;
    MaterialEditText edtPrice , edtSpace , edtType , edtFeatures;
    ElegantNumberButton peopleNumberButton;
    RecyclerView recyclerViewFacility;
    KenBurnsView kenBurnsView;
    Button btnEditRoom , btnAddImage;

    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    Uri selectedFileUri;
    FacilityRoomAdapter facilityRoomAdapter;

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

        setContentView(R.layout.activity_room_edit);

        initViews();
        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();

        //Get roomId , roomTypeId , hotelId
        if (getIntent().getExtras().getInt("Room_Id") != 0)
            roomId = getIntent().getExtras().getInt("Room_Id");
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


        //Load Facility
        //Init RecyclerView facility
        if (Common.isConnectionToInternet(RoomEditActivity.this)){
            loadFacility();
        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();


        //btnAddImage was clicked
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(RoomEditActivity.this)){

                    startUpload();
                }
                else
                    Toast.makeText(RoomEditActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });

        btnEditRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(RoomEditActivity.this)){

                    addRoomData();
                    addFacilitySelected();
                    finish();
                }
                else
                    Toast.makeText(RoomEditActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST){
            if (resultCode == RESULT_OK){
                if (data != null){

                    selectedFileUri = data.getData();
                    if (selectedFileUri != null && !selectedFileUri.getPath().isEmpty()){

                        kenBurnsView.setImageURI(selectedFileUri);
                        uploadFile();
                    }
                    else
                        Toast.makeText(this, "you can't upload file to server !!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void addFacilitySelected() {

        final List<Integer> facilityRooms = facilityRoomAdapter.getFacilitySeleceted();

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < facilityRooms.size(); i++) {

                    api.BrandRoomAddFacility(Common.getToken() , roomId , facilityRooms.get(i))
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    Toast.makeText(RoomEditActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                    Toast.makeText(RoomEditActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).start();
    }

    private void addRoomData() {

        api.BrandRoomTypeEdit(Common.getToken(),
                edtType.getText().toString(),
                Integer.parseInt(edtSpace.getText().toString()),
                edtFeatures.getText().toString(),
                Integer.parseInt(peopleNumberButton.getNumber()),
                Float.parseFloat(edtPrice.getText().toString()),
                roomTypeId)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Toast.makeText(RoomEditActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Toast.makeText(RoomEditActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadFile() {

        if (selectedFileUri != null){

            File file = FileUtils.getFile(this , selectedFileUri);  //hotel image

            String fileName = new StringBuilder(UUID.randomUUID().toString().substring(0 , 7))
                    .append("_Room")
                    .append(roomId)
                    .append(FileUtils.getExtension(file.toString())).toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(file , RoomEditActivity.this);

            final MultipartBody.Part body =  MultipartBody.Part.createFormData("uploaded_file" , fileName , requestFile);

            final MultipartBody.Part room_id =  MultipartBody.Part.createFormData("room_id" , String.valueOf(roomId));

            final MultipartBody.Part hotel_id =  MultipartBody.Part.createFormData("hotel_id" , String.valueOf(hotelId));

            new Thread(new Runnable() {
                @Override
                public void run() {

//                    compositeDisposable.add(api.BrandRoomUploadImage(Common.getToken() , hotel_id , room_id , body)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribeOn(Schedulers.io())
//                            .subscribe(new Consumer<String>() {
//                                @Override
//                                public void accept(String result) throws Exception {
//
//                                    Toast.makeText(RoomEditActivity.this, "Image is uploaded !", Toast.LENGTH_SHORT).show();
//                                }
//                            }, new Consumer<Throwable>() {
//                                @Override
//                                public void accept(Throwable throwable) throws Exception {
//                                    Toast.makeText(RoomEditActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }));


                    api.BrandRoomUploadImage(Common.getToken() , hotel_id , room_id , body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    Toast.makeText(RoomEditActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                    Toast.makeText(RoomEditActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            }).start();
        }
    }

    private void startUpload() {

        Dexter.withActivity(RoomEditActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){

                            startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent() , "Select a Picture"),
                                    PICK_FILE_REQUEST);
                        }
                        else
                            Toast.makeText(RoomEditActivity.this, "You can't use access to gallery !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
        .check();
    }

    private void startRecyclerViewAnimation() {

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

    private void loadFacility() {

        compositeDisposable.add(api.getFacilityRoom(Common.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FacilityRoom>>() {
                    @Override
                    public void accept(List<FacilityRoom> facilityRooms) throws Exception {

                        facilityRoomAdapter = new FacilityRoomAdapter(RoomEditActivity.this , facilityRooms);
                        recyclerViewFacility.setAdapter(facilityRoomAdapter);
                        startRecyclerViewAnimation();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(RoomEditActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void initViews() {

        edtPrice = (MaterialEditText)findViewById(R.id.edtPrice);
        edtSpace = (MaterialEditText)findViewById(R.id.edtSpace);
        edtType = (MaterialEditText)findViewById(R.id.edtType);
        edtFeatures = (MaterialEditText)findViewById(R.id.edtFeatures);
        btnAddImage = (Button)findViewById(R.id.btnAddImage);
        btnEditRoom = (Button)findViewById(R.id.btnEditRoom);
        recyclerViewFacility = (RecyclerView)findViewById(R.id.recycler_facility);
        peopleNumberButton = (ElegantNumberButton)findViewById(R.id.number_people);
        kenBurnsView = (KenBurnsView) findViewById(R.id.imgRoom);
    }

    @Override
    public void onProgressUpdate(int pertantage) {

    }
}
