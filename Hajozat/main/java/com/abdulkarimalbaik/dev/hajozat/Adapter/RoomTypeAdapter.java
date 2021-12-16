package com.abdulkarimalbaik.dev.hajozat.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulkarimalbaik.dev.hajozat.R;
import com.abdulkarimalbaik.dev.hajozat.ViewHolder.RoomTypeViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeViewHolder> {

    Context context;
    List<Integer> ids;

    public RoomTypeAdapter(Context context, List<Integer> ids) {
        this.context = context;
        this.ids = ids;
    }

    @NonNull
    @Override
    public RoomTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.room_type_item , parent , false);
        return new RoomTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomTypeViewHolder holder, int position) {

        holder.txtRoomTypeId.setText(String.valueOf(ids.get(position)));
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }
}
