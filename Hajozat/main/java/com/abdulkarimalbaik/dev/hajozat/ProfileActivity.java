package com.abdulkarimalbaik.dev.hajozat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Common.ProgressRequestBody;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Interface.UploadCallBack;
import com.abdulkarimalbaik.dev.hajozat.Model.Profile;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
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

public class ProfileActivity extends AppCompatActivity implements UploadCallBack {

    private static final int PICK_FILE_REQUEST = 1000;
    ImageView imgManager;
    TextView txtManagerName , txtHotelNumber , txtRoomNumber , txtJoinTime , txtLocation , txtSlogan , txtDescritopn;
    RatingBar profileRating;

    CompositeDisposable compositeDisposable;
    IHajoztAPI api;

    Uri selectedFileUri;

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

        setContentView(R.layout.activity_profile);

        initViews();

        compositeDisposable = new CompositeDisposable();
        api = Common.getAPI();


        if (Common.isConnectionToInternet(ProfileActivity.this)){

            loadData();
        }
        else
            Toast.makeText(this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();


        imgManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(ProfileActivity.this)){

                    startUpload();
                }
                else
                    Toast.makeText(ProfileActivity.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();

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

                        imgManager.setImageURI(selectedFileUri);
                        uploadFile();
                    }
                    else
                        Toast.makeText(this, "you can't upload file to server !!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadFile() {

        if (selectedFileUri != null){

            File file = FileUtils.getFile(this , selectedFileUri);

            String fileName = new StringBuilder(UUID.randomUUID().toString().substring(0 , 7))
                    .append("_brand")
                    .append(Common.currentBrand.getId())
                    .append(FileUtils.getExtension(file.toString())).toString();

            ProgressRequestBody requestFile = new ProgressRequestBody(file , ProfileActivity.this);

            final MultipartBody.Part body =  MultipartBody.Part.createFormData("uploaded_file" , fileName , requestFile);

            final MultipartBody.Part id =  MultipartBody.Part.createFormData("id" , String.valueOf(Common.currentBrand.getId()));

            new Thread(new Runnable() {
                @Override
                public void run() {

                    api.uploadAvatarBrand(Common.getToken() , id , body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    Toast.makeText(ProfileActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                    Toast.makeText(ProfileActivity.this, "Server is close !" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }).start();
        }
    }

    private void startUpload() {

        Dexter.withActivity(ProfileActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){

                            startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent() , "Select a Picture"),
                                    PICK_FILE_REQUEST);
                        }
                        else
                            Toast.makeText(ProfileActivity.this, "You can't use access to gallery !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
    }

    private void loadData() {

        compositeDisposable.add(api.BrandProfile(Common.getToken() , Common.currentBrand.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Profile>() {
            @Override
            public void accept(Profile profile) throws Exception {

                if (profile.getImage() != null){

                    if (profile.getImage().contains("drive"))
                        Picasso.get().load(profile.getImage()).into(imgManager);
                    else
                        Picasso.get().load(Common.BASE_URL + "photos/" + profile.getImage()).into(imgManager);
                }

                if (profile.getManager_Name() != null)
                    txtManagerName.setText(profile.getManager_Name());
                if (profile.getH_counts() != 0)
                    txtHotelNumber.setText(String.valueOf(profile.getH_counts()));
                if (profile.getR_counts() != 0)
                    txtRoomNumber.setText(String.valueOf(profile.getR_counts()));
                if (profile.getDate_Join() != null)
                    txtJoinTime.setText(profile.getDate_Join());
                if (profile.getLat() != 0.0 && profile.getLng() != 0.0)
                    txtLocation.setText("Lat: " + profile.getLat() + "    " + "Lng: " + profile.getLng());
                if (profile.getSlogan() != null)
                    txtSlogan.setText(profile.getSlogan());
                if (profile.getDescription() != null)
                    txtDescritopn.setText(profile.getDescription());
                if (profile.getAvg_ratings() != 0.0)
                    profileRating.setRating(Float.parseFloat(String.valueOf(profile.getAvg_ratings())));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(ProfileActivity.this, "Server is close !\n" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initViews() {

        imgManager = (ImageView)findViewById(R.id.imgManager);
        txtManagerName = (TextView)findViewById(R.id.txtManagerName);
        txtHotelNumber = (TextView)findViewById(R.id.txtHotelNumber);
        txtRoomNumber = (TextView)findViewById(R.id.txtRoomNumber);
        txtJoinTime = (TextView)findViewById(R.id.txtJoinTime);
        txtLocation = (TextView)findViewById(R.id.txtLocation);
        txtSlogan = (TextView)findViewById(R.id.txtSlogan);
        txtDescritopn = (TextView)findViewById(R.id.txtDescription);
        profileRating = (RatingBar)findViewById(R.id.profileRating);
    }

    @Override
    public void onProgressUpdate(int pertantage) {

    }
}
