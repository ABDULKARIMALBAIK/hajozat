package com.abdulkarimalbaik.dev.hajozat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulkarimalbaik.dev.hajozat.Common.Common;
import com.abdulkarimalbaik.dev.hajozat.Model.ImageHR;
import com.abdulkarimalbaik.dev.hajozat.R;
import com.abdulkarimalbaik.dev.hajozat.ViewHolder.SliderImageViewHolder;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderImagesAdapter extends SliderViewAdapter<SliderImageViewHolder> {

    private Context context;
    private List<ImageHR> imageHRs;

    public SliderImagesAdapter(Context context, List<ImageHR> imageHRs) {
        this.context = context;
        this.imageHRs = imageHRs;
    }

    @Override
    public SliderImageViewHolder onCreateViewHolder(ViewGroup parent) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, null);
        return new SliderImageViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderImageViewHolder viewHolder, int position) {

        if (imageHRs.get(position).getImage_Path().contains("drive")){

            Glide.with(viewHolder.itemView)
                    .load(imageHRs.get(position).getImage_Path())
                    .fitCenter()
                    .placeholder(R.color.powerfulColor)
                    .into(viewHolder.sliderImage);
        }
        else {

            Glide.with(viewHolder.itemView)
                    .load(Common.BASE_URL + "photos/" + imageHRs.get(position).getImage_Path())
                    .fitCenter()
                    .placeholder(R.color.powerfulColor)
                    .into(viewHolder.sliderImage);
        }

    }

    @Override
    public int getCount() {
        return imageHRs.size();
    }



    public void renewItems(List<ImageHR> sliderItems) {
        this.imageHRs = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.imageHRs.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(ImageHR sliderItem) {
        this.imageHRs.add(sliderItem);
        notifyDataSetChanged();
    }
}
