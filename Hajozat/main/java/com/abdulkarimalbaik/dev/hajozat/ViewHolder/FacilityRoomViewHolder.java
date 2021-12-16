package com.abdulkarimalbaik.dev.hajozat.ViewHolder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abdulkarimalbaik.dev.hajozat.Interface.IClickItem;
import com.abdulkarimalbaik.dev.hajozat.R;

import androidx.recyclerview.widget.RecyclerView;

public class FacilityRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtFacilityName , txtFacilityNumber;
    public LinearLayout relativeLayout;
    private IClickItem clickItem;

    public void setClickItem(IClickItem clickItem) {
        this.clickItem = clickItem;
    }

    public FacilityRoomViewHolder(View itemView) {
        super(itemView);

        txtFacilityName = (TextView)itemView.findViewById(R.id.facilityName);
        txtFacilityNumber = (TextView)itemView.findViewById(R.id.facilityNumber);
        relativeLayout = (LinearLayout)itemView.findViewById(R.id.relative_root);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        clickItem.onClick(v , getAdapterPosition() , false);
    }
}
