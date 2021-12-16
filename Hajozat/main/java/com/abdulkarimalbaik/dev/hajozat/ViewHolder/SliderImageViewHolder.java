package com.abdulkarimalbaik.dev.hajozat.ViewHolder;

import android.view.View;
import android.widget.ImageView;

import com.abdulkarimalbaik.dev.hajozat.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderImageViewHolder extends SliderViewAdapter.ViewHolder{

    public ImageView sliderImage;
    public View itemView;

    public SliderImageViewHolder(View itemView) {
        super(itemView);

        sliderImage = (ImageView)itemView.findViewById(R.id.image_slider_item);
        this.itemView = itemView;
    }
}
