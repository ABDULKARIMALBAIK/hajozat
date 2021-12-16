package com.abdulkarimalbaik.dev.hajozat.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.abdulkarimalbaik.dev.hajozat.R;

import androidx.recyclerview.widget.RecyclerView;

public class RoomTypeViewHolder extends RecyclerView.ViewHolder {

    public TextView txtRoomTypeId;

    public RoomTypeViewHolder(View itemView) {
        super(itemView);

        txtRoomTypeId = (TextView)itemView.findViewById(R.id.room_type_id);
    }
}
