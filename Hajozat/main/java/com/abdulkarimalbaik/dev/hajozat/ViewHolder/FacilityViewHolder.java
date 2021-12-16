package com.abdulkarimalbaik.dev.hajozat.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.abdulkarimalbaik.dev.hajozat.Interface.IClickItem;
import com.abdulkarimalbaik.dev.hajozat.R;

import androidx.recyclerview.widget.RecyclerView;

public class FacilityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtFacilityName;
    private IClickItem iClickItem;

    public void setiClickItem(IClickItem iClickItem) {
        this.iClickItem = iClickItem;
    }

    public FacilityViewHolder(View itemView) {
        super(itemView);

        txtFacilityName = (TextView)itemView.findViewById(R.id.facilityName);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        iClickItem.onClick(v , getAdapterPosition() , false);
    }
}
