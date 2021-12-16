package com.abdulkarimalbaik.dev.hajozat.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abdulkarimalbaik.dev.hajozat.Interface.IClickItem;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.github.abdularis.piv.VerticalScrollParallaxImageView;

import androidx.recyclerview.widget.RecyclerView;

public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener{

    public TextView txtName , txtPrice;
    public ImageView imgShare;
    public VerticalScrollParallaxImageView imgRoom;


    public RelativeLayout view_background;
    public RelativeLayout view_foreground;

    private IClickItem iClickItem;

    public void setiClickItem(IClickItem iClickItem) {
        this.iClickItem = iClickItem;
    }

    public RoomViewHolder(View itemView) {
        super(itemView);

        txtName = (TextView)itemView.findViewById(R.id.room_name);
        txtPrice = (TextView)itemView.findViewById(R.id.room_price);
        imgRoom = (VerticalScrollParallaxImageView)itemView.findViewById(R.id.room_image);
        imgShare = (ImageView)itemView.findViewById(R.id.share_image);
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
