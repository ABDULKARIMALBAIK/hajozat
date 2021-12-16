package com.abdulkarimalbaik.dev.hajozat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulkarimalbaik.dev.hajozat.Interface.IClickItem;
import com.abdulkarimalbaik.dev.hajozat.Model.FacilityRoom;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.abdulkarimalbaik.dev.hajozat.ViewHolder.FacilityRoomViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FacilityRoomAdapter extends RecyclerView.Adapter<FacilityRoomViewHolder> {

    private Context context;
    private List<FacilityRoom> facilityRooms;
    private List<Integer> facilitySeleceted;
    private List<Integer> itemSelected;

    public FacilityRoomAdapter(Context context, List<FacilityRoom> facilityRooms) {
        this.context = context;
        this.facilityRooms = facilityRooms;
        facilitySeleceted = new ArrayList<>();
        itemSelected = new ArrayList<>();
    }

    public List<Integer> getFacilitySeleceted() {
        return facilitySeleceted;
    }

    public List<Integer> getItemSelected() {
        return itemSelected;
    }

    @NonNull
    @Override
    public FacilityRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.facility_room_item , parent , false);
        return new FacilityRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityRoomViewHolder holder, int position) {

        //Fill txtFacilityName
        holder.txtFacilityName.setText(facilityRooms.get(position).getName());

        //Fill txtFacilityNumber
        if (facilityRooms.get(position).getNumber() != null){
            holder.txtFacilityNumber.setText(facilityRooms.get(position).getNumber());
        }
        else
            holder.txtFacilityNumber.setVisibility(View.GONE);

        //Fill background_app
        boolean isSelected = false;

        for (int i = 0; i < itemSelected.size(); i++) {
            if (itemSelected.get(i) == position){   ////Item is selected

                holder.relativeLayout.setBackgroundResource(R.drawable.design_card4);
                holder.txtFacilityName.setTextColor(context.getResources().getColor(android.R.color.white));
                holder.txtFacilityNumber.setTextColor(context.getResources().getColor(android.R.color.white));
                isSelected = true;
            }
        }
        if (!isSelected){
            holder.relativeLayout.setBackgroundResource(R.drawable.design_card3);
            holder.txtFacilityName.setTextColor(context.getResources().getColor(R.color.powerfulColor));
            holder.txtFacilityNumber.setTextColor(context.getResources().getColor(R.color.powerfulColor));
        }


        holder.setClickItem(new IClickItem() {
            @Override
            public void onClick(View view, int position, boolean isLongClicked) {

                boolean isSelected = false;

                for (int i = 0; i < itemSelected.size(); i++) {

                    if (itemSelected.get(i) == position){  //Item is exists in the list
                        itemSelected.remove(i);
                        facilitySeleceted.remove(i);
                        isSelected = true;
                        notifyDataSetChanged();
                    }
                }
                //This mean the item isn't exists in the list
                if (!isSelected){
                    itemSelected.add(position);
                    facilitySeleceted.add(Integer.parseInt(facilityRooms.get(position).getId()));
                    notifyDataSetChanged();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return facilityRooms.size();
    }
}
