package com.abdulkarimalbaik.dev.hajozat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulkarimalbaik.dev.hajozat.Interface.IClickItem;
import com.abdulkarimalbaik.dev.hajozat.Model.HotelFacility;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.abdulkarimalbaik.dev.hajozat.ViewHolder.FacilityViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityViewHolder> {

    private Context context;
    private List<HotelFacility> facilities;
    private List<Integer> items_seleceted;

    public FacilityAdapter(Context context, List<HotelFacility> facilities) {
        this.context = context;
        this.facilities = facilities;
        items_seleceted = new ArrayList<>();
    }

    public List<HotelFacility> getFacilities() {
        return facilities;
    }

    public List<Integer> getItems_seleceted() {
        return items_seleceted;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.facility_item , parent , false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {

        holder.txtFacilityName.setText(facilities.get(position).getName());

        boolean isSelected = false;

        for (int i = 0; i < items_seleceted.size(); i++) {
            if (items_seleceted.get(i) == position){   ////Item is selected

                holder.txtFacilityName.setBackgroundResource(R.drawable.design_card4);
                holder.txtFacilityName.setTextColor(context.getResources().getColor(android.R.color.white));
                isSelected = true;
            }
        }

        if (!isSelected){
            holder.txtFacilityName.setBackgroundResource(R.drawable.design_card3);
            holder.txtFacilityName.setTextColor(context.getResources().getColor(R.color.powerfulColor));
        }

        holder.setiClickItem(new IClickItem() {
            @Override
            public void onClick(View view, int position, boolean isLongClicked) {

                boolean isSelected = false;

                for (int i = 0; i < items_seleceted.size(); i++) {

                    if (items_seleceted.get(i) == position){  //Item is exists in the list , it is selected before ,so remove it
                        items_seleceted.remove(i);
                        isSelected = true;
                        notifyDataSetChanged();
                    }
                }
                //This mean the item isn't exists in the list
                if (!isSelected){
                    items_seleceted.add(position);
                    notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    public void addItem(HotelFacility item){

        facilities.add(item);
        notifyDataSetChanged();
    }
}
