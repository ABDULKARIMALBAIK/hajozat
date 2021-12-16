package com.abdulkarimalbaik.dev.hajozat.Common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Interface.IGeoCoordinates;
import com.abdulkarimalbaik.dev.hajozat.Interface.IHajoztAPI;
import com.abdulkarimalbaik.dev.hajozat.Model.Brand;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.Remote.RetrofitClient;
import com.abdulkarimalbaik.dev.hajozat.Remote.RetrofitMapClient;
import com.abdulkarimalbaik.dev.hajozat.Remote.RetrofitScalarsClient;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class Common {

    public static final String BASE_URL = ".........................................";
    public static final String SITE_KEY_RECAPTCHA = ".........................................";
    public static final String Tokenization_Key_Braintree = ".......................................";
    public static final String KEY_NAME = "............................";
    public static final String baseUrlMap = "....................................";
    public static final String API_KEY_MAPS = "...........................................";
    //public static final String BASE_URL = "................................................";
    public static Brand currentBrand;
    public static int lastNewBooking;
    public static List<HotelSearchFilter> hotelsFiltered = null;
    public static List<RoomSearchFilter> roomFiltered = null;

    public static IHajoztAPI getAPI(){

        return RetrofitClient.getInstance(BASE_URL).create(IHajoztAPI.class);
    }

    public static IHajoztAPI getScalarsAPI(){

        return RetrofitScalarsClient.getInstance(BASE_URL).create(IHajoztAPI.class);
    }

    public static IGeoCoordinates getGeoCodeService(){

        return RetrofitMapClient.getClient(baseUrlMap).create(IGeoCoordinates.class);
    }

    public static String getToken(){

        if (currentBrand.getToken() != null)
            return "Bearer " + currentBrand.getToken();
        else{
            Log.d("Token Error: " , "Token is null !!!!!!!");
            return null;
        }

    }

    public static boolean isConnectionToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){

                for (int i = 0; i < info.length; i++) {

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap , int newWidth , int newHeight){

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth , newHeight , Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0 , pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX ,scaleY , pivotX , pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap , 0 , 0 , new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public static String getPathImages(){

        File file = new File(new StringBuilder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .append("/")
                .append("Hajozat").toString());

        if (!file.isDirectory())
            file.mkdir();

        return new StringBuilder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .append("/")
                .append("Hajozat").toString();
    }

    public static String getPathDocuments(){

        File file = new File(new StringBuilder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getAbsolutePath())
                .append("/")
                .append("Hajozat").toString());

        if (!file.isDirectory())
            file.mkdir();

        return new StringBuilder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getAbsolutePath())
                .append("/")
                .append("Hajozat").toString();
    }

    public static File createFile(Bitmap bitmap){

        //Make sure you set permission Read/Write external storage AND set Provider that exists in Manifest

        String file_path = getPathImages();
        File dir = new File(file_path);
//        if(!dir.exists())
//            dir.mkdirs();
        File file = new File(dir.getAbsoluteFile(), new StringBuilder(UUID.randomUUID().toString()).append(".png").toString());
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public static void createCSVFile(String fileName , Activity activity , Workbook wb){

        String file_path = getPathDocuments();
        File dir = new File(file_path);
        File file = new File(dir.getAbsoluteFile(),
                new StringBuilder(UUID.randomUUID().toString().substring(0 , 6))
                        .append(fileName).append(".xls").toString());

        Log.d("3-" , "OK");


        FileOutputStream outputStream = null;
        Log.d("4-" , "OK");

        try {

            outputStream = new FileOutputStream(file);
            outputStream.flush();
            wb.write(outputStream);
            Toast.makeText(activity.getApplicationContext(),"Data is exported successfully",Toast.LENGTH_LONG).show();
            Log.d("5-" , "OK");
        }
        catch (java.io.IOException e) {

            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(),"ERROR" + e.getMessage(),Toast.LENGTH_LONG).show();

            try {
                outputStream.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(activity.getApplicationContext(),"ERROR" + e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {

        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }




}
