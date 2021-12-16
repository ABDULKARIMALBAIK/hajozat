package com.abdulkarimalbaik.dev.hajozat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Common.DirectionJSONParser;
import com.abdulkarimalbaik.dev.hajozat.Interface.IGeoCoordinates;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST = 1000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1001;
    private GoogleMap mMap;

    private static int UPDATE_INTERVAL = 1000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private LocationRequest mLocationRequest;
    private IGeoCoordinates api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        api = Common.getGeoCodeService();

        if (ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestRuntimePermission();
        }
        else {

            if (checkPlayServices()){

                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        displayLocation();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient != null){

            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case LOCATION_PERMISSION_REQUEST :{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    if (checkPlayServices()){

                        buildGoogleApiClient();
                        createLocationRequest();

                        displayLocation();
                    }
                }
                break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        displayLocation();
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestRuntimePermission();
        }
        else {

            double latitude , longitude;

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null){

                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
            else {

                latitude = Common.currentBrand.getLat();
                longitude = Common.currentBrand.getLng();

                Toast.makeText(this, "Couldn't get the location !", Toast.LENGTH_SHORT).show();
            }

            LatLng yourLocation = new LatLng(latitude , longitude);

            try{
                //Add marker in your location and move the camera
                mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Location")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                //After add Marker for your location , Add Marker for this Order and draw route
                drawRoute(yourLocation);
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "You can't use location service !", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void requestRuntimePermission() {

        if (Build.VERSION.SDK_INT >= 23)
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            }, LOCATION_PERMISSION_REQUEST);
    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){

                GooglePlayServicesUtil.getErrorDialog(resultCode , this , PLAY_SERVICES_RESOLUTION_REQUEST).show();

            }
            else{
                Toast.makeText(this, "this device is not support", Toast.LENGTH_SHORT).show();
                finish();
            }

            return false;
        }

        return true;
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient , mLocationRequest , this);


    }


    private void drawRoute(final LatLng yourLocation) {

        try {


            LatLng hotelLocation;
            hotelLocation = new LatLng(getIntent().getExtras().getDouble("Lat") ,
                    getIntent().getExtras().getDouble("Lng"));

            Bitmap bitmap = BitmapFactory.decodeResource(getResources() , R.drawable.ic_home_power_24dp);
            bitmap = Common.scaleBitmap(bitmap , 70 , 70);

            MarkerOptions marker = new MarkerOptions()
                    .title("Hotel of " + getIntent().getExtras().getString("Name"))
                    .position(hotelLocation);

            mMap.addMarker(marker).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            //.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                    .add(yourLocation , hotelLocation)
                                    .width(10)
                                    .color(Color.RED)
                                    .geodesic(true));
            //we are in syria , so i used rather than api.getDirections(yourLocation.latitude + "," + yourLocation.longitude, ...
            // i used api.getDirections(yourLocation.latitude + "," + yourLocation.longitude,

            //Draw route
            api.getDirections(yourLocation.latitude + "," + yourLocation.longitude,
                    hotelLocation.latitude + "," + hotelLocation.longitude,
                    Common.API_KEY_MAPS)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            new ParseTask().execute(response.body().toString());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ParseTask extends AsyncTask<String , Integer , List<List<HashMap<String , String>>>> {

        ProgressDialog progressDialog = new ProgressDialog(HotelMapActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Please waiting...");
            progressDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jsonObject;
            List<List<HashMap<String , String>>> routes = null;

            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                routes =  parser.parse(jsonObject);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);

            progressDialog.dismiss();

            ArrayList points = null;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < lists.size(); i++) {

                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String , String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++) {

                    HashMap<String , String> point = path.get(j);

                    double lat =  Double.parseDouble(point.get("lat"));
                    double lng =  Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat , lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }
            mMap.addPolyline(lineOptions);
        }
    }
}
