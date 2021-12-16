package com.abdulkarimalbaik.dev.hajozat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.Brand;
import com.abdulkarimalbaik.dev.hajozat.Model.PasswordBrand;
import com.abdulkarimalbaik.dev.hajozat.Model.ReCaptchaResponse;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import co.infinum.goldfinger.Error;
import co.infinum.goldfinger.Goldfinger;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {

    MaterialEditText edtEmail, edtPassword;
    Button btnSignIn;
    CheckBox ckbRememberMe;
    TextView txtForgetPassword;
    RelativeLayout root;
    ImageView fingerLogin;

    IHajoztAPI api;
    IHajoztAPI scalarsAPI;
    CompositeDisposable compositeDisposable;

    AlertDialog dialog;
    Goldfinger goldfinger;

    CallbackManager callbackManager;
    ImageView facebookLogin;

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

        setContentView(R.layout.activity_sign_in);

        initViews();

        Typeface typeface = Typeface.DEFAULT;
        edtPassword.setTypeface(typeface);

        Paper.init(this);

        api = Common.getAPI();
        scalarsAPI = Common.getScalarsAPI();
        compositeDisposable = new CompositeDisposable();

        callbackManager = CallbackManager.Factory.create();


        fingerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(SignIn.this)){

                    String fingerprintCode = Paper.book().read("FINGERPRINT");

                    if (fingerprintCode != null){

                        final androidx.appcompat.app.AlertDialog alertDialog;
                        View view = getLayoutInflater().inflate(R.layout.fingerprint_layout , null);

                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SignIn.this)
                                .setTitle("Fingerprint Login")
                                .setMessage("Use fingerprint to login :")
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

                        fingerprintLogin(alertDialog , fingerprintCode);
                    }
                    else
                        Toast.makeText(SignIn.this, "Sign Up and add fingerprint authentication", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(SignIn.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();


            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showForgetPwd();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Save user & password
                if (ckbRememberMe.isChecked()) {

                    Paper.book().write("EMAIL", edtEmail.getText().toString());
                    Paper.book().write("PASSWORD", edtPassword.getText().toString());
                }

                if (Common.isConnectionToInternet(SignIn.this)) {
                    if (!edtEmail.getText().toString().isEmpty() && !edtPassword.getText().toString().isEmpty()) {
                        if (edtEmail.getText().toString().contains("@gmail") ||
                                edtEmail.getText().toString().contains("@outlook") ||
                                edtEmail.getText().toString().contains("@yahoo")) {

                            validate_reCaptcha();


                        } else
                            Toast.makeText(SignIn.this, "put @gmail or @outlook or @yahoo", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SignIn.this, "Please fill all of fielads !", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(SignIn.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });

        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(SignIn.this)){

                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                    
                    if (isLoggedIn){

                        String facebook_Id = accessToken.getUserId();

                        Toast.makeText(SignIn.this, "UserId: " + facebook_Id, Toast.LENGTH_SHORT).show();

                        api.BrandFacebookLogin(facebook_Id)
                                .enqueue(new Callback<Brand>() {
                                    @Override
                                    public void onResponse(Call<Brand> call, Response<Brand> response) {

                                        Common.currentBrand = response.body();
                                        startActivity(new Intent(SignIn.this, HotelActivity.class));
                                        finish();
                                    }
                                    @Override
                                    public void onFailure(Call<Brand> call, Throwable t) {

                                        Toast.makeText(SignIn.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                        Toast.makeText(SignIn.this, "Sign Up and Add facebook account in upcomming login", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(SignIn.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode , resultCode , data);
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void fingerprintLogin(final androidx.appcompat.app.AlertDialog alertDialog , final String fingerprintCode) {

        Log.d("fingerprinttttt" , " " + fingerprintCode);

        Goldfinger.Builder builder = new Goldfinger.Builder(SignIn.this);
        goldfinger = builder.build();

        if (goldfinger.hasFingerprintHardware()){

            if (goldfinger.hasEnrolledFingerprint()){

                goldfinger.decrypt(Common.KEY_NAME, fingerprintCode , new Goldfinger.Callback() {
                    @Override
                    public void onSuccess(String value) {

                        api.BrandFingerPrintLogin(fingerprintCode)
                                .enqueue(new Callback<Brand>() {
                                    @Override
                                    public void onResponse(Call<Brand> call, Response<Brand> response) {

                                        alertDialog.dismiss();
                                        Common.currentBrand = response.body();
                                        startActivity(new Intent(SignIn.this, HotelActivity.class));
                                        finish();

                                    }
                                    @Override
                                    public void onFailure(Call<Brand> call, Throwable t) {

                                        alertDialog.dismiss();
                                        goldfinger.cancel();
                                        Toast.makeText(SignIn.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                    @Override
                    public void onError(Error error) {

                        alertDialog.dismiss();
                        goldfinger.cancel();
                        Toast.makeText(SignIn.this, error.toString(), Toast.LENGTH_SHORT).show();
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

    private void validate_reCaptcha() {

        SafetyNet.getClient(SignIn.this)
                .verifyWithRecaptcha(Common.SITE_KEY_RECAPTCHA)
                .addOnSuccessListener(this , new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {

                        if (!recaptchaTokenResponse.getTokenResult().isEmpty())
                            verifyTokenOnServer(recaptchaTokenResponse.getTokenResult());
                        else
                            Toast.makeText(SignIn.this, "Token result is null", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(this , new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e instanceof ApiException){

                            ApiException apiException = (ApiException)e;
                            Log.d("ERROR_reCaptcha" , "ERROR" + CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                            Toast.makeText(SignIn.this, "1- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Log.d("ERROR" , "UnKnown error");
                            Toast.makeText(SignIn.this, "2- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }});
    }

    private void verifyTokenOnServer(String tokenResult) {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait...");
        waitingDialog.setCancelable(false);

        api.validate(tokenResult)
                .enqueue(new Callback<ReCaptchaResponse>() {
                    @Override
                    public void onResponse(Call<ReCaptchaResponse> call, Response<ReCaptchaResponse> response) {

                        waitingDialog.dismiss();

                        if (response.body().isSuccess()){

                            Toast.makeText(SignIn.this, "reCaptcha is verified", Toast.LENGTH_SHORT).show();
                            //startLoginPage(LoginType.PHONE);
                            login(edtEmail.getText().toString(), edtPassword.getText().toString());
                        }
                        else
                            Toast.makeText(SignIn.this,"3- " +  response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<ReCaptchaResponse> call, Throwable t) {

                        waitingDialog.dismiss();
                        Toast.makeText(SignIn.this, "4- " +  t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR" , t.getMessage());
                    }
                });
    }

    private void showForgetPwd() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setMessage("Enter your Data :")
                .setCancelable(false)
                .setIcon(R.drawable.ic_security_powerful_24dp);

        View forgot_view = getLayoutInflater().inflate(R.layout.forget_password_layout, null);
        builder.setView(forgot_view);

        final MaterialEditText edtEmail = (MaterialEditText) forgot_view.findViewById(R.id.edtEmail);
        final MaterialEditText edtPhone = (MaterialEditText) forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtBrandName = (MaterialEditText) forgot_view.findViewById(R.id.edtBrandName);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                if (edtEmail.getText().toString().isEmpty() ||
                        edtPhone.getText().toString().isEmpty() ||
                        edtBrandName.getText().toString().isEmpty()) {

                    Toast.makeText(SignIn.this, "Please fill all of fields !", Toast.LENGTH_SHORT).show();

                }
                else if (!edtEmail.getText().toString().contains("@gmail") &&
                        !edtEmail.getText().toString().contains("@outlook") &&
                        !edtEmail.getText().toString().contains("@yahoo")) {

                    Toast.makeText(SignIn.this, "Use @gmail or @outlook or @yahoo for your Email !", Toast.LENGTH_SHORT).show();
                }
                else if (!Common.isConnectionToInternet(SignIn.this)) {

                    Toast.makeText(SignIn.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
                }
                else {

                    String email = edtEmail.getText().toString();
                    String phoneNumber = edtPhone.getText().toString();
                    String managerName = edtBrandName.getText().toString();

                    api.brandForgetPassword(email , phoneNumber , managerName)
                            .enqueue(new Callback<PasswordBrand>() {
                                @Override
                                public void onResponse(Call<PasswordBrand> call, Response<PasswordBrand> response) {

                                    Snackbar.make(root, "Your Password:  " + response.body().getPassword(), Snackbar.LENGTH_LONG).show();

                                }
                                @Override
                                public void onFailure(Call<PasswordBrand> call, Throwable t) {

                                    Toast.makeText(SignIn.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.show();

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
    }

    private void login(final String email, final String password) {

        dialog = new SpotsDialog(this);
        dialog.show();
        dialog.setMessage("Please waiting....");
        dialog.setCancelable(false);

        api.brandLogin(email, password)
                .enqueue(new Callback<Brand>() {
                    @Override
                    public void onResponse(Call<Brand> call, Response<Brand> response) {

                        dialog.dismiss();
                        Common.currentBrand = response.body();
                        startActivity(new Intent(SignIn.this, HotelActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Brand> call, Throwable t) {

                        dialog.dismiss();
                        Toast.makeText(SignIn.this, "Server is close\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void initViews() {

        edtEmail = (MaterialEditText) findViewById(R.id.edtEmail);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        ckbRememberMe = (CheckBox) findViewById(R.id.ckbRemember);
        txtForgetPassword = (TextView) findViewById(R.id.txtForgotPwd);
        root = (RelativeLayout) findViewById(R.id.root);
        fingerLogin = (ImageView)findViewById(R.id.fingerLogin);
        facebookLogin = (ImageView)findViewById(R.id.facebookLogin);
    }
}
