package com.abdulkarimalbaik.dev.hajozat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Adapter.FacilityAdapter;
import com.abdulkarimalbaik.dev.hajozat.Adapter.RuleAdapter;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Common.ProgressRequestBody;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Interface.UploadCallBack;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelFacility;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelRule;
import com.abdulkarimalbaik.dev.hajozat.Model.NameC_HT_HH;
import com.abdulkarimalbaik.dev.hajozat.Model.NewHotel;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
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

public class HotelAddActivity extends AppCompatActivity implements UploadCallBack {

    private static final int PICK_FILE_REQUEST = 1000;

    KenBurnsView kenBurnsView;
    Button btnAddImage , btnAddFacility , btnAddRule , btnAddHotel;
    RatingBar ratingHotel;
    MaterialSpinner spinnerCity , spinnerType , spinnerHost;
    MaterialEditText edtName , edtLat , edtLng;
    RecyclerView recyclerViewFacility , recyclerViewRules;
    ElegantNumberButton checkIn , checkOut;

    IHajoztAPI api;
    CompositeDisposable compositeDisposable;
    Uri selectedFileUri;
    FacilityAdapter facilityAdapter;
    RuleAdapter ruleAdapter;

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

        setContentView(R.layout.activity_hotel_add);

        //Init Views and connection to rest api
        initViews();
        api = Common.getAPI();
        compositeDisposable = new CompositeDisposable();

        //Get new hotel's id and put it in hotelId
        if (Common.isConnectionToInternet(HotelAddActivity.this)){
            insertNewHotel();
        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();


        //Init spinnerCity and spinnerType and recyclerFacility and recyclerRule
        if (Common.isConnectionToInternet(HotelAddActivity.this)){

            loadSpinnerCity();
            loadSpinnerType();
            loadSpinnerHost();
            loadFacility();
            loadRules();

        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();


        //Add new image
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(HotelAddActivity.this)){

                    startUpload();
                }
                else
                    Toast.makeText(HotelAddActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });

        //Add new facility
        btnAddFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(HotelAddActivity.this)){

                    facilityAddShow();
                }
                else
                    Toast.makeText(HotelAddActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });


        //Add new rule
        btnAddRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(HotelAddActivity.this)){

                    ruleAddShow();
                }
                else
                    Toast.makeText(HotelAddActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });

        //Add new Hotel
        btnAddHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(HotelAddActivity.this)){

                    AddHotelData();
                }
                else
                    Toast.makeText(HotelAddActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog alertDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(HotelAddActivity.this)
                .setMessage("Do you want cancel the process ?")
                .setTitle("Cancel process")
                .setIcon(R.drawable.ic_arrow_left_black_48dp)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteNewHotel();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        alertDialog = builder.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.powerfulColor));

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

    private void loadSpinnerHost() {

        compositeDisposable.add(api.getHotelHost(Common.getToken())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<NameC_HT_HH>>() {
                    @Override
                    public void accept(List<NameC_HT_HH> hostTypes) throws Exception {

                        List<String> hostStrings = new ArrayList<>();

                        for (int i = 0; i < hostTypes.size(); i++) {

                            hostStrings.add(hostTypes.get(i).getName());
                        }

                        spinnerHost.setItems(hostStrings);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(HotelAddActivity.this, "Server is close !\n"+ throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void AddHotelData() {

        api.BrandHotelEditType(Common.getToken(),
                ratingHotel.getRating(),
                spinnerCity.getSelectedIndex()+1,
                spinnerType.getSelectedIndex()+1,
                spinnerHost.getSelectedIndex()+1,
                Integer.parseInt(checkIn.getNumber()),
                Integer.parseInt(checkOut.getNumber()),
                edtName.getText().toString(),
                Float.parseFloat(edtLat.getText().toString()),
                Float.parseFloat(edtLng.getText().toString()),
                Common.currentBrand.getId(),
                hotelId)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Toast.makeText(HotelAddActivity.this, "Hotel is created successfully !", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(HotelAddActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void ruleAddShow() {

        AlertDialog alertDialog;

        View view = getLayoutInflater().inflate(R.layout.add_rule_layout , null);
        final MaterialEditText edtRuleName = (MaterialEditText)view.findViewById(R.id.edtRuleName);

        AlertDialog.Builder builder = new AlertDialog.Builder(HotelAddActivity.this)
                .setMessage("Fill the fields :")
                .setTitle("Add Rule")
                .setIcon(R.drawable.ic_add_circle_outline_power_24dp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        api.BrandHotelAddRules(Common.getToken(),
                                hotelId,
                                edtRuleName.getText().toString())
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        Toast.makeText(HotelAddActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                        ruleAdapter.addItem(new HotelRule(edtRuleName.getText().toString()));
                                    }
                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                        Toast.makeText(HotelAddActivity.this, "Server is close !" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });



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

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
    }

    private void facilityAddShow() {

        AlertDialog alertDialog;

        View view = getLayoutInflater().inflate(R.layout.add_facility_layout , null);
        final MaterialEditText edtFacilityName = (MaterialEditText)view.findViewById(R.id.edtFacilityName);
        final MaterialEditText edtFacilityNumber = (MaterialEditText)view.findViewById(R.id.edtFacilityNumber);

        AlertDialog.Builder builder = new AlertDialog.Builder(HotelAddActivity.this)
                .setMessage("Fill the fields :")
                .setTitle("Add Facility")
                .setIcon(R.drawable.ic_add_circle_outline_power_24dp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        api.BrandHotelAddFacility(Common.getToken(),
                                hotelId,
                                edtFacilityName.getText().toString(),
                                Integer.parseInt(edtFacilityNumber.getText().toString()))
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        Toast.makeText(HotelAddActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                        facilityAdapter.addItem(new HotelFacility(edtFacilityName.getText().toString()));

                                    }
                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                        Toast.makeText(HotelAddActivity.this, "Server is close !" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


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

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
    }

    private void uploadFile() {

        if (selectedFileUri != null){

            File file = FileUtils.getFile(this , selectedFileUri);  //hotel image

            String fileName = new StringBuilder(UUID.randomUUID().toString().substring(0 , 7))
                    .append("_Hotel")
                    .append(hotelId)
                    .append(FileUtils.getExtension(file.toString())).toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(file , HotelAddActivity.this);

            final MultipartBody.Part body =  MultipartBody.Part.createFormData("uploaded_file" , fileName , requestFile);

            final MultipartBody.Part id =  MultipartBody.Part.createFormData("id" , String.valueOf(hotelId));

            new Thread(new Runnable() {
                @Override
                public void run() {

                    api.BrandHotelUploadImage(Common.getToken() ,id , body)
                            .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            Toast.makeText(HotelAddActivity.this, response.body() , Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(HotelAddActivity.this, "Server is close !" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }).start();
        }
    }

    private void startUpload() {

        Dexter.withActivity(HotelAddActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){

                            startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent() , "Select a Picture"),
                                    PICK_FILE_REQUEST);
                        }
                        else
                            Toast.makeText(HotelAddActivity.this, "You can't use access to gallery !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
        .check();
    }

    private void loadRules() {

        compositeDisposable.add(api.BrandHotelDetailsRules(Common.getToken() , hotelId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<HotelRule>>() {
                    @Override
                    public void accept(List<HotelRule> rules) throws Exception {

                        ruleAdapter = new RuleAdapter(HotelAddActivity.this , rules);
                        recyclerViewRules.setAdapter(ruleAdapter);
                        startRecyclerViewRulesAnimation();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(HotelAddActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadFacility() {

        compositeDisposable.add(api.BrandHotelDetailsFacility(Common.getToken() , hotelId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<HotelFacility>>() {
                    @Override
                    public void accept(List<HotelFacility> facilities) throws Exception {

                        facilityAdapter = new FacilityAdapter(HotelAddActivity.this , facilities);
                        recyclerViewFacility.setAdapter(facilityAdapter);
                        startRecyclerViewFacilityAnimation();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(HotelAddActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadSpinnerType() {

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

                        spinnerType.setItems(typeStrings);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(HotelAddActivity.this, "Server is close!\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadSpinnerCity() {

        compositeDisposable.add(api.getCity(Common.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NameC_HT_HH>>() {
                    @Override
                    public void accept(List<NameC_HT_HH> cities) throws Exception {

                        List<String> cityStrings = new ArrayList<>();

                        for (int i = 0; i < cities.size(); i++) {

                            cityStrings.add(cities.get(i).getName());
                        }

                        spinnerCity.setItems(cityStrings);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(HotelAddActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void deleteNewHotel() {

        api.BrandDeleteNewHotel(Common.getToken() , hotelId)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Toast.makeText(HotelAddActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(HotelAddActivity.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                back();
            }
        } , 3000);


    }

    private void back() {
        super.onBackPressed();
    }

    private void insertNewHotel() {

        api.BrandAddNewHotel(Common.getToken())
                .enqueue(new Callback<NewHotel>() {
                    @Override
                    public void onResponse(Call<NewHotel> call, Response<NewHotel> response) {

                        hotelId = response.body().getId();
                    }

                    @Override
                    public void onFailure(Call<NewHotel> call, Throwable t) {

                    }
                });
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

    private void initViews() {

        kenBurnsView = (KenBurnsView)findViewById(R.id.imgHotel);
        btnAddImage = (Button)findViewById(R.id.btnAddImage);
        btnAddFacility = (Button)findViewById(R.id.btnAddFacility);
        btnAddRule = (Button)findViewById(R.id.btnAddRule);
        btnAddHotel = (Button)findViewById(R.id.btnAddHotel);
        ratingHotel = (RatingBar)findViewById(R.id.hotelRating);
        spinnerCity = (MaterialSpinner)findViewById(R.id.spinner_city);
        spinnerType = (MaterialSpinner)findViewById(R.id.spinner_hotel_type);
        recyclerViewFacility = (RecyclerView)findViewById(R.id.recycler_facility);
        recyclerViewRules = (RecyclerView)findViewById(R.id.recycler_rules);
        spinnerHost = (MaterialSpinner)findViewById(R.id.spinner_host_type);
        checkIn = (ElegantNumberButton)findViewById(R.id.check_in);
        checkOut = (ElegantNumberButton)findViewById(R.id.check_out);
        edtLat = (MaterialEditText)findViewById(R.id.edtLat);
        edtLng = (MaterialEditText)findViewById(R.id.edtLng);
        edtName = (MaterialEditText)findViewById(R.id.edtName);
    }

    @Override
    public void onProgressUpdate(int pertantage) {

    }
}
