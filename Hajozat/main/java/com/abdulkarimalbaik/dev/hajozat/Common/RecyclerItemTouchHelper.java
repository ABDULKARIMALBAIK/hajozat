package com.abdulkarimalbaik.dev.hajozat.Common;

import android.graphics.Canvas;
import android.view.View;

import com.abdulkarimalbaik.dev.hajozat.Interface.RecyclerItemTouchHelperListener;
import com.abdulkarimalbaik.dev.hajozat.ViewHolder.HotelViewHolder;
import com.abdulkarimalbaik.dev.hajozat.ViewHolder.RoomViewHolder;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        if (listener != null)
            listener.onSwiped(viewHolder , direction , viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        if (viewHolder instanceof HotelViewHolder){

            View foregroundView = ((HotelViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }
        else if (viewHolder instanceof RoomViewHolder){

            View foregroundView = ((RoomViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (viewHolder instanceof HotelViewHolder){

            View foregroundView = ((HotelViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c , recyclerView , foregroundView , dX , dY , actionState , isCurrentlyActive);
        }
        else if (viewHolder instanceof RoomViewHolder){

            View foregroundView = ((RoomViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c , recyclerView , foregroundView , dX , dY , actionState , isCurrentlyActive);
        }

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (viewHolder != null){

            if (viewHolder instanceof HotelViewHolder){

                View foregroundView = ((HotelViewHolder)viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundView);
            }
            else if (viewHolder instanceof RoomViewHolder){

                View foregroundView = ((RoomViewHolder)viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundView);
            }

        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (viewHolder instanceof HotelViewHolder){

            View foregroundView = ((HotelViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c , recyclerView , foregroundView , dX , dY , actionState , isCurrentlyActive);
        }
        else  if (viewHolder instanceof RoomViewHolder){

            View foregroundView = ((RoomViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c , recyclerView , foregroundView , dX , dY , actionState , isCurrentlyActive);
        }

    }
}