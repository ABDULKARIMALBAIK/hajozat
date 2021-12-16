package com.abdulkarimalbaik.dev.hajozat.Adapter;

import android.content.Context;

import com.abdulkarimalbaik.dev.hajozat.Fragments.BookingFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.PaymentsFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.RatingFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.TopBookingFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.TopPriceFragment;
import com.abdulkarimalbaik.dev.hajozat.Fragments.TopRatingFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AnalyzeFragmentAdapter extends FragmentPagerAdapter {

    Context context;

    public AnalyzeFragmentAdapter(FragmentManager fm , Context context) {
        super(fm);

        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position){
            case 0:{

                return PaymentsFragment.getInstance();
            }

            case 1:{

                return TopPriceFragment.getInstance();
            }

            case 2:{

                return BookingFragment.getInstance();
            }

            case 3:{

                return TopBookingFragment.getInstance();
            }

            case 4:{

                return RatingFragment.getInstance();
            }

            case 5:{

                return TopRatingFragment.getInstance();
            }
            default:{

                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//
//        switch (position){
//
//            case 0:{
//
//                return "Payments";
//            }
//
//            case 1:{
//
//                return "Top Payments";
//            }
//
//            case 2:{
//
//                return "Bookings";
//            }
//
//            case 3:{
//
//                return "Top Bookings";
//            }
//
//            case 4:{
//
//                return "Ratings";
//            }
//
//            case 5:{
//
//                return "Top Ratings";
//            }
//            default:{
//
//                return null;
//            }
//        }
//    }

}
