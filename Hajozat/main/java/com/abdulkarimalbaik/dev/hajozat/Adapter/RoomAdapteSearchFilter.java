package com.abdulkarimalbaik.dev.hajozat.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Interface.IClickItem;
import com.abdulkarimalbaik.dev.hajozat.Model.RoomSearchFilter;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.abdulkarimalbaik.dev.hajozat.RoomDetailsActivity;
import com.abdulkarimalbaik.dev.hajozat.ViewHolder.RoomViewHolder;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapteSearchFilter extends RecyclerView.Adapter<RoomViewHolder> {

    private Activity activity;
    private List<RoomSearchFilter> roomList;
    private int hotelId;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    public RoomAdapteSearchFilter(Activity activity, List<RoomSearchFilter> roomList , int hotelId) {
        this.activity = activity;
        this.roomList = roomList;
        this.hotelId = hotelId;

        //Init FaceBook
        callbackManager  = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(activity);

    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.room_item , parent , false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, final int position) {

        holder.txtName.setText(roomList.get(position).getId());
        holder.txtPrice.setText(roomList.get(position).getPrice() + " $");

        if (roomList.get(position).getImage_Path().contains("drive")){

            Picasso.get()
                    .load(roomList.get(position).getImage_Path())
                    .placeholder(R.color.powerfulColor)
                    .into(holder.imgRoom);
        }
        else {

            Picasso.get()
                    .load(Common.BASE_URL + "photos/"+ roomList.get(position).getImage_Path())
                    .placeholder(R.color.powerfulColor)
                    .into(holder.imgRoom);
        }

        //final Bitmap bitmap = ((BitmapDrawable)holder.imgRoom.getDrawable()).getBitmap();

        holder.imgShare.setOnClickListener(new View.OnClickListener() {  //Maybe conflect between listener and below listener
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog;

                View view = LayoutInflater.from(activity).inflate(R.layout.select_share_mode , null);

                LinearLayout facebook = (LinearLayout)view.findViewById(R.id.select_facebook);
                LinearLayout whatsApp = (LinearLayout)view.findViewById(R.id.select_whatsapp);
                LinearLayout messenger = (LinearLayout)view.findViewById(R.id.select_messenger);
                LinearLayout instagram = (LinearLayout)view.findViewById(R.id.select_instagram);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                        .setTitle("Share")
                        .setMessage("Share with :")
                        .setIcon(activity.getResources().getDrawable(R.drawable.ic_share_power_24dp))
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);

                builder.setView(view);
                alertDialog = builder.show();

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.powerfulColor));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.powerfulColor));

                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Common.isConnectionToInternet(activity)){

                            Picasso.get()
                                    .load(roomList.get(position).getImage_Path())
                                    .resize(400 ,400)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                            shareFacebook(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                        }
                        else
                            Toast.makeText(activity, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
                    }
                });
                whatsApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.isConnectionToInternet(activity)){

                            Picasso.get()
                                    .load(roomList.get(position).getImage_Path())
                                    .resize(400 ,400)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                            shareWhatsapp(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });

                        }
                        else
                            Toast.makeText(activity, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
                    }
                });
                messenger.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.isConnectionToInternet(activity)){

                            Picasso.get()
                                    .load(roomList.get(position).getImage_Path())
                                    .resize(400 ,400)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                            shareMessenger(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                        }
                        else
                            Toast.makeText(activity, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
                    }
                });
                instagram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.isConnectionToInternet(activity)){

                            Picasso.get()
                                    .load(roomList.get(position).getImage_Path())
                                    .resize(400 ,400)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                            shareInstagram(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                        }
                        else
                            Toast.makeText(activity, "Please check your connection internet !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.setiClickItem(new IClickItem() {
            @Override
            public void onClick(View view, int position, boolean isLongClicked) {

                if (hotelId != 0){

                    Intent roomIntent = new Intent(activity , RoomDetailsActivity.class);
                    roomIntent.putExtra("Room_Id" , roomList.get(position).getId());
                    roomIntent.putExtra("Room_Type_Id" , roomList.get(position).getRoom_type());
                    roomIntent.putExtra("hotel_id" , String.valueOf(hotelId));
                    activity.startActivity(roomIntent);
                }
            }
        });
    }

    private void shareWhatsapp(final Bitmap bitmap) {

        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            String imagePath =
                                    Common.getPathImages() +
                                            "/" + UUID.randomUUID().toString() + "_profile.png";

                            Uri data;
                            File file = Common.createFile(bitmap);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

                                data = FileProvider.getUriForFile(activity , "com.abdulkarimalbaik.dev.hajozat.myprovider", file);
                                activity.grantUriPermission(activity.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                            else
                                data = Uri.fromFile(file);

                            Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                            if (intent != null) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.setType("image/png");
                                shareIntent.setPackage("com.whatsapp");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, data);

                                activity.startActivity(shareIntent);

                            } else {
                                Toast.makeText(activity, "Please download Whatsapp app !", Toast.LENGTH_SHORT).show();
                            }

                        } else
                            Toast.makeText(activity, "Permission denied !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
    }

    private void shareMessenger(final Bitmap bitmap) {

        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            String imagePath =
                                    Common.getPathImages() +
                                            "/" + UUID.randomUUID().toString() + "_profile.png";

                            Uri data;
                            File file = Common.createFile(bitmap);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

                                data = FileProvider.getUriForFile(activity, "com.abdulkarimalbaik.dev.hajozat.myprovider", file);
                                activity.grantUriPermission(activity.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                            else
                                data = Uri.fromFile(file);

                            Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                            if (intent != null) {
                                Intent sendIntent = new Intent();
                                sendIntent.setType("image/png");
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_STREAM, data);
                                sendIntent.setPackage("com.facebook.orca");

                                activity.startActivity(sendIntent);
                            } else {
                                Toast.makeText(activity, "Please download Messenger app !", Toast.LENGTH_SHORT).show();
                            }

                        } else
                            Toast.makeText(activity, "Permission denied !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
    }

    private void shareInstagram(final Bitmap bitmap) {

        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            Uri data;
                            File file = Common.createFile(bitmap);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

                                data = FileProvider.getUriForFile(activity, "com.abdulkarimalbaik.dev.hajozat.myprovider", file);
                                activity.grantUriPermission(activity.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                            else
                                data = Uri.fromFile(file);

                            Intent intent = activity.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                            if (intent != null) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.setType("image/*");
                                shareIntent.setPackage("com.instagram.android");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, data);

                                activity.startActivity(shareIntent);

                            } else {
                                Toast.makeText(activity, "Please download Instagram app !", Toast.LENGTH_SHORT).show();
                            }

                        } else
                            Toast.makeText(activity, "Permission denied !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
    }

    private void shareFacebook(final Bitmap bitmap) {

        Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            String imagePath =
                                    Common.getPathImages() +
                                            "/" + UUID.randomUUID().toString() + "_profile.png";

                            SharePhoto sharePhoto = new SharePhoto.Builder()
                                    .setBitmap(bitmap)
                                    .build();
                            if (ShareDialog.canShow(SharePhotoContent.class)){

                                SharePhotoContent content = new SharePhotoContent.Builder()
                                        .addPhoto(sharePhoto)
                                        .build();

                                shareDialog.show(content);

                                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                                    @Override
                                    public void onSuccess(Sharer.Result result) {
                                        Toast.makeText(activity, "Share successful !", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancel() {
                                        Toast.makeText(activity, "Share cancelled !", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(FacebookException error) {
                                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                        else
                            Toast.makeText(activity, "Permission denied !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
