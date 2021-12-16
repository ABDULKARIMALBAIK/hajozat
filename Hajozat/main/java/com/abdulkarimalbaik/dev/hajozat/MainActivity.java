package com.abdulkarimalbaik.dev.hajozat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.Brand;
import com.facebook.FacebookSdk;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.disposables.CompositeDisposable;
import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn , btnSignUp;
    CompositeDisposable compositeDisposable;
    IHajoztAPI api;
    AlertDialog dialog;

    TextView txtHajozat;
    believe.cht.fadeintextview.TextView txtslogan;
    KenBurnsView imgMainActivity;
    ShimmerLayout shimmerLayout;

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

        setContentView(R.layout.activity_main);

        initViews();

        FacebookSdk.sdkInitialize(MainActivity.this);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        //int API
        api = Common.getAPI();

        compositeDisposable = new CompositeDisposable();

        //init Paper
        Paper.init(this);

        //Check Remember Check Box
        String email = Paper.book().read("EMAIL");
        String pwd = Paper.book().read("PASSWORD");

        if (email != null && pwd != null){

            if (!email.isEmpty() && !pwd.isEmpty()){

                if (Common.isConnectionToInternet(MainActivity.this))
                    startLogin(email,pwd);
                else
                    Toast.makeText(this, "Please your connection interent !!1", Toast.LENGTH_SHORT).show();
            }

        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this , SignUp.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this , SignIn.class));
            }
        });

        printKeyHash();

    }

    private void initViews() {
        txtHajozat = (TextView)findViewById(R.id.title);
        Typeface typeface = Typeface.createFromAsset(getAssets() , "fonts/nabilla.TTF");
        txtHajozat.setTypeface(typeface);

        txtslogan = (believe.cht.fadeintextview.TextView)findViewById(R.id.txtslogan);
        txtslogan.setText(getResources().getString(R.string.slogan));
        txtslogan.setLetterDuration(200);

        imgMainActivity = (KenBurnsView)findViewById(R.id.imgMainActivity);
        imgMainActivity.setImageDrawable(getResources().getDrawable(R.drawable.background_app));

        shimmerLayout = (ShimmerLayout)findViewById(R.id.shimmerLayout);
        shimmerLayout.startShimmerAnimation();
    }

    private void printKeyHash() {

        try {

            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo("com.abdulkarimalbaik.dev.hajozat",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures){

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash" , Base64.encodeToString(md.digest() , Base64.DEFAULT));

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtslogan.setText(getResources().getString(R.string.slogan));
        txtslogan.setLetterDuration(200);

        shimmerLayout.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        shimmerLayout.stopShimmerAnimation();
    }

    @Override
    protected void onDestroy() {

        File dir = new File(Common.getPathImages());

        if (dir.isDirectory()) {

            String[] children = dir.list();

            if (children != null){

                for (int i = 0; i < children.length; i++)
                {
                    new File(dir, children[i]).delete();
                }
            }

        }

        super.onDestroy();
    }

    private void startLogin(String email, String pwd) {

        dialog = new SpotsDialog(this);
        dialog.show();
        dialog.setMessage("Please waiting....");
        dialog.setCancelable(false);

        api.brandLogin(email, pwd)
                .enqueue(new Callback<Brand>() {
                    @Override
                    public void onResponse(Call<Brand> call, Response<Brand> response) {

                        dialog.dismiss();
                        Common.currentBrand = response.body();
                        startActivity(new Intent(MainActivity.this, HotelActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Brand> call, Throwable t) {

                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Server is close\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
