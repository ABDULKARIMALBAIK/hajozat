package com.abdulkarimalbaik.dev.hajozat.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abdulkarimalbaik.dev.hajozat.Interface.IClickItem;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.github.abdularis.piv.VerticalScrollParallaxImageView;

import androidx.recyclerview.widget.RecyclerView;

public class HotelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener{

    public TextView name , city , rooms;
    public ImageView  share_image;
    public RatingBar rating;
    public VerticalScrollParallaxImageView image;

    public RelativeLayout view_background;
    public RelativeLayout view_foreground;

    private IClickItem iClickItem;

    public void setiClickItem(IClickItem iClickItem) {
        this.iClickItem = iClickItem;
    }

    public HotelViewHolder(View itemView) {
        super(itemView);

        name = (TextView)itemView.findViewById(R.id.hotel_name);
        city = (TextView)itemView.findViewById(R.id.hotel_city);
        rooms = (TextView)itemView.findViewById(R.id.hotel_rooms);
        image = (VerticalScrollParallaxImageView)itemView.findViewById(R.id.hotel_image);
        rating = (RatingBar)itemView.findViewById(R.id.hotel_rating);
        share_image = (ImageView)itemView.findViewById(R.id.share_image);
        view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground = (RelativeLayout)itemView.findViewById(R.id.view_foreground);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

        iClickItem.onClick(v , getAdapterPosition() , false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select the action");
        menu.add(0 , 0 , getAdapterPosition() , "DELETE");
    }
}
