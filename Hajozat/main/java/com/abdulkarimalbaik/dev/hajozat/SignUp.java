package com.abdulkarimalbaik.dev.hajozat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.Brand;
import com.abdulkarimalbaik.dev.hajozat.Model.BrandRegisterId;
import com.abdulkarimalbaik.dev.hajozat.Model.ReCaptchaResponse;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {

    private static final int REQUEST_PAYMENT_CODE = 7777;
    MaterialEditText edtName, edtEmail, edtPassword, edtPhoneNumber, edtTitlePage, edtBrandName, edtDescription;
    Button btnSignUp;
    AlertDialog spotsDialog;
    Location lastLocation;

    CompositeDisposable compositeDisposable;
    IHajoztAPI api;
    IHajoztAPI apiScalars;

    double lat , lng;

    String token = Common.Tokenization_Key_Braintree;

    Map<String,String> params;
    String amount;

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

        setContentView(R.layout.activity_sign_up);

        initViews();

        Typeface typeface = Typeface.DEFAULT;
        edtPassword.setTypeface(typeface);

        api = Common.getAPI();
        apiScalars = Common.getScalarsAPI();
        compositeDisposable = new CompositeDisposable();

        Dexter.withActivity(SignUp.this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION ,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()){
                            getLocationBrand();
                        }
                        else
                            Toast.makeText(SignUp.this, "You can't use location service", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
        .check();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectionToInternet(SignUp.this)){

                    if (checkEdits()){

                        validate_reCaptcha();
                    }
                }
                else
                    Toast.makeText(SignUp.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAYMENT_CODE){
            if (resultCode == RESULT_OK){

                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();

                amount = "1000";
                params = new HashMap<>();

                params.put("amount" , amount);
                params.put("nonce" , strNonce);

                sendPayment();

            }
            else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();

            else {

                Exception error = (Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e("PAYMENT_ERROR" , error.getMessage());
            }
        }
    }

    private void sendPayment() {

        apiScalars.payment(params.get("nonce") , params.get("amount"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.isSuccessful()){

                            Toast.makeText(SignUp.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                            register(edtName.getText().toString(),
                                    edtEmail.getText().toString(),
                                    edtPassword.getText().toString(),
                                    edtPhoneNumber.getText().toString(),
                                    edtTitlePage.getText().toString(),
                                    edtBrandName.getText().toString(),
                                    edtDescription.getText().toString());

                        }
                        else {

                            Toast.makeText(SignUp.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                        }

                        Log.d("PAYMENT_INFO" , response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("PAYMENT_INFO" , t.getMessage());
                    }
                });
    }

    private void validate_reCaptcha() {

        SafetyNet.getClient(SignUp.this)
                .verifyWithRecaptcha(Common.SITE_KEY_RECAPTCHA)
                .addOnSuccessListener(this , new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {

                        if (!recaptchaTokenResponse.getTokenResult().isEmpty())
                            verifyTokenOnServer(recaptchaTokenResponse.getTokenResult());
                        else
                            Toast.makeText(SignUp.this, "Token result is null", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(this , new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e instanceof ApiException){

                            ApiException apiException = (ApiException)e;
                            Log.d("ERROR_reCaptcha" , "ERROR" + CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                            Toast.makeText(SignUp.this, "1- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Log.d("ERROR" , "UnKnown error");
                            Toast.makeText(SignUp.this, "2- " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(SignUp.this, "reCaptcha is verified", Toast.LENGTH_SHORT).show();

                            //Payment
                            DropInRequest dropInRequest = new DropInRequest().clientToken(token);
                            startActivityForResult(dropInRequest.getIntent(SignUp.this) , REQUEST_PAYMENT_CODE);

                        }
                        else
                            Toast.makeText(SignUp.this,"3- " +  response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<ReCaptchaResponse> call, Throwable t) {

                        waitingDialog.dismiss();
                        Toast.makeText(SignUp.this, "4- " +  t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ERROR" , t.getMessage());
                    }
                });
    }

    private void getLocationBrand() {

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation == null)
            lastLocation = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (lastLocation != null){

            lat = (float) lastLocation.getLatitude();
            lng = (float) lastLocation.getLongitude();
        }
        else {

            lat = 1.442;
            lng =  1.636;
        }
    }

    private void register(String name , String email, String password, String phoneNumber , String titlePage, String brandName, String description) {

        spotsDialog = new SpotsDialog(this);
        spotsDialog.show();
        spotsDialog.setMessage("Please waiting....");
        spotsDialog.setCancelable(false);

        api.registerBrand(name , email , phoneNumber , password , brandName , titlePage , description , lat , lng)
                .enqueue(new Callback<BrandRegisterId>() {
                    @Override
                    public void onResponse(Call<BrandRegisterId> call, Response<BrandRegisterId> response) {

                        int id = response.body().getId();

                        spotsDialog.dismiss();
                        Toast.makeText(SignUp.this, "Your account is created Successfully, id:" + id, Toast.LENGTH_SHORT).show();
                        verifyBrandAccount(id);
                    }

                    @Override
                    public void onFailure(Call<BrandRegisterId> call, Throwable t) {

                        spotsDialog.dismiss();
                        Toast.makeText(SignUp.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("myError",t.getMessage());
                    }
                });
    }

    private void verifyBrandAccount(final int brand_id) {

        AlertDialog alertDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Verify Account")
                .setMessage("Enter verify code here :")
                .setCancelable(false)
                .setIcon(R.drawable.ic_security_powerful_24dp);

        View verify_view = getLayoutInflater().inflate(R.layout.send_report_layout, null);
        builder.setView(verify_view);

        final MaterialEditText edtVerify = (MaterialEditText)verify_view.findViewById(R.id.edtReport);

        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                if (edtVerify.getText().toString().isEmpty()) {

                    Toast.makeText(SignUp.this, "Please fill the field!", Toast.LENGTH_SHORT).show();
                    verifyBrandAccount(brand_id);
                }
                else if (!Common.isConnectionToInternet(SignUp.this)) {

                    Toast.makeText(SignUp.this, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
                    verifyBrandAccount(brand_id);
                }
                else {

                    spotsDialog = new SpotsDialog(SignUp.this);
                    spotsDialog.show();
                    spotsDialog.setMessage("Please waiting....");
                    spotsDialog.setCancelable(false);

                    api.BrandVerify(edtVerify.getText().toString() , brand_id)
                            .enqueue(new Callback<Brand>() {
                                @Override
                                public void onResponse(Call<Brand> call, Response<Brand> response) {

                                    spotsDialog.dismiss();
                                    Common.currentBrand = response.body();
                                    startActivity(new Intent(SignUp.this , HotelActivity.class));
                                    finish();
                                }
                                @Override
                                public void onFailure(Call<Brand> call, Throwable t) {

                                    spotsDialog.dismiss();
                                    Toast.makeText(SignUp.this, "Server is close !\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        alertDialog = builder.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.powerfulColor));
    }

    private boolean checkEdits() {

        if (edtName.getText().toString().isEmpty() ||
                edtEmail.getText().toString().isEmpty() ||
                edtPassword.getText().toString().isEmpty() ||
                edtPhoneNumber.getText().toString().isEmpty() ||
                edtTitlePage.getText().toString().isEmpty() ||
                edtBrandName.getText().toString().isEmpty() ||
                edtDescription.getText().toString().isEmpty()){

            Toast.makeText(this, "Please fill all of fields !", Toast.LENGTH_SHORT).show();
            return false;

        }
        else if (!edtEmail.getText().toString().contains("@gmail") &&
                !edtEmail.getText().toString().contains("@yahoo") &&
                !edtEmail.getText().toString().contains("@outlook")){

            Toast.makeText(this, "Use @gmail or @outlook or @yahoo to your email !", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    private void initViews() {

        edtName = (MaterialEditText)findViewById(R.id.edtName);
        edtEmail = (MaterialEditText)findViewById(R.id.edtEmail);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);
        edtPhoneNumber = (MaterialEditText)findViewById(R.id.edtPhoneNumber);
        edtTitlePage = (MaterialEditText)findViewById(R.id.edtTitlePage);
        edtBrandName = (MaterialEditText)findViewById(R.id.edtBrandName);
        edtDescription = (MaterialEditText)findViewById(R.id.edtDescription);
        btnSignUp =(Button)findViewById(R.id.btnSignUp);
    }
}
