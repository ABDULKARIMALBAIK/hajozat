package com.abdulkarimalbaik.dev.hajozat.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.abdulkarimalbaik.dev.hajozat.BookingRoomActivity;
import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Common.NotificationHelper;
import com.abdulkarimalbaik.dev.hajozat.Model.CheckNewBooking;
import com.abdulkarimalbaik.dev.hajozat.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckNewBookingsActivity extends Service {

    Timer timer;

    public CheckNewBookingsActivity() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                checkNewBookings();
            }
        } , 10000 , 15000);


        return super.onStartCommand(intent, flags, startId);
    }

    private void checkNewBookings() {

        Common.getAPI().BrandCheckNewBookings(Common.getToken() , Common.currentBrand.getId())
                .enqueue(new Callback<CheckNewBooking>() {
                    @Override
                    public void onResponse(Call<CheckNewBooking> call, Response<CheckNewBooking> response) {

                        if (response.body() != null){

                            int newBookings = response.body().count_bookings;

                            if (newBookings > Common.lastNewBooking){

                                sendNotificationToBrand(
                                        "Hi " + Common.currentBrand.getManager_Name() + ", you have new bookings " + (newBookings - Common.lastNewBooking) ,
                                        "New Bookings !");

                                Common.lastNewBooking = newBookings;

                            }
//                            else {
//
//                                sendNotificationToBrand(
//                                        "Hi " + Common.currentBrand.getManager_Name() + ", no bookings was occurred " + newBookings,
//                                        "No changes !");
//                            }


                        }
                        else
                            Log.d("ERROR" , "Yes");
                    }
                    @Override
                    public void onFailure(Call<CheckNewBooking> call, Throwable t) {

                        Log.d("ERROR" , t.getMessage());
                    }
                });


    }

    private void sendNotificationToBrand(String message , String title) {

        //Send Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificationAPI26(message , title);
        else
            sendNotification(message , title);

    }

    private void sendNotification(String message , String title) {

        Intent intent = new Intent(this , BookingRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0 , intent ,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_hajozati)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0  , builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotificationAPI26(String message , String title) {

        PendingIntent pendingIntent;   //Message to open alarm system app
        NotificationHelper helper;
        Notification.Builder builder;  //Object Notification

        //Create message
        Intent intent = new Intent(this , BookingRoomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //if activity is created before => clear alarm system activity and create new activity and make it in top
        pendingIntent = PendingIntent.getActivity(this , 0 , intent ,
                PendingIntent.FLAG_UPDATE_CURRENT); //if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //set default sound of system
        helper = new NotificationHelper(this);
        builder = helper.getAlarmSystemChannelNotification(title,
                message,
                pendingIntent,
                defaultSoundUri);

        //Show notification
        helper.getManager().notify(new Random().nextInt() , builder.build());

    }

}
